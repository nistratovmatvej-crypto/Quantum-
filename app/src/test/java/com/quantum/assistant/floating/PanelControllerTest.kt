package com.quantum.assistant.floating

import com.quantum.assistant.audio.MicAvailability
import com.quantum.assistant.audio.MicListener
import com.quantum.assistant.audio.MicSession
import com.quantum.assistant.audio.MicStartResult
import com.quantum.assistant.audio.NoOpMicSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeMic(
    override val availability: MicAvailability = MicAvailability.Available,
    override val reportsSpeechActivity: Boolean = true,
    private val failOnStart: String? = null
) : MicSession {
    var running = false
    var startCount = 0
    var listener: MicListener? = null
    override fun start(listener: MicListener): MicStartResult {
        startCount++
        failOnStart?.let { return MicStartResult.Failed(it) }
        running = true; this.listener = listener; return MicStartResult.Started
    }
    override fun stop() { running = false }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PanelControllerTest {
    private enum class Ev(val fire: (PanelController) -> Boolean) {
        STAR({ it.onStarTap() }), MIC({ it.onMicTap() }), STOP({ it.onStopListening() }),
        TRANSCRIPT({ it.onTranscriptFinalized() }), PROPOSE({ it.onActionProposed() }),
        READY_SPEAK({ it.onResponseReady(true) }), READY_QUIET({ it.onResponseReady(false) }),
        APPROVE_SPEAK({ it.onApprove(true) }), APPROVE_QUIET({ it.onApprove(false) }), REJECT({ it.onReject() }),
        SPEAK_DONE({ it.onSpeakingFinished() }), FAIL({ it.onFailure("boom") }), DISMISS({ it.onDismissError() }), CLOSE({ it.onClose() })
    }

    private val valid: Map<Pair<PanelState, Ev>, PanelState> = mapOf(
        (PanelState.COLLAPSED to Ev.STAR) to PanelState.EXPANDED_IDLE,
        (PanelState.EXPANDED_IDLE to Ev.MIC) to PanelState.LISTENING,
        (PanelState.LISTENING to Ev.STOP) to PanelState.EXPANDED_IDLE,
        (PanelState.LISTENING to Ev.TRANSCRIPT) to PanelState.PROCESSING,
        (PanelState.PROCESSING to Ev.PROPOSE) to PanelState.AWAITING_CONFIRMATION,
        (PanelState.PROCESSING to Ev.READY_SPEAK) to PanelState.SPEAKING,
        (PanelState.PROCESSING to Ev.READY_QUIET) to PanelState.EXPANDED_IDLE,
        (PanelState.AWAITING_CONFIRMATION to Ev.APPROVE_SPEAK) to PanelState.SPEAKING,
        (PanelState.AWAITING_CONFIRMATION to Ev.APPROVE_QUIET) to PanelState.EXPANDED_IDLE,
        (PanelState.AWAITING_CONFIRMATION to Ev.REJECT) to PanelState.EXPANDED_IDLE,
        (PanelState.SPEAKING to Ev.SPEAK_DONE) to PanelState.EXPANDED_IDLE,
        (PanelState.ERROR to Ev.DISMISS) to PanelState.EXPANDED_IDLE
    ) + listOf(PanelState.EXPANDED_IDLE, PanelState.LISTENING, PanelState.PROCESSING,
        PanelState.AWAITING_CONFIRMATION, PanelState.SPEAKING, PanelState.ERROR).map { (it to Ev.CLOSE) to PanelState.COLLAPSED } +
        listOf(PanelState.EXPANDED_IDLE, PanelState.LISTENING, PanelState.PROCESSING,
            PanelState.AWAITING_CONFIRMATION, PanelState.SPEAKING).map { (it to Ev.FAIL) to PanelState.ERROR }

    private fun controller(scope: TestScope, mic: MicSession = FakeMic()) = PanelController(scope, mic)

    private fun PanelController.drive(target: PanelState): PanelController {
        when (target) {
            PanelState.COLLAPSED -> Unit
            PanelState.EXPANDED_IDLE -> onStarTap()
            PanelState.LISTENING -> { onStarTap(); onMicTap() }
            PanelState.PROCESSING -> { drive(PanelState.LISTENING); onTranscriptFinalized() }
            PanelState.AWAITING_CONFIRMATION -> { drive(PanelState.PROCESSING); onActionProposed() }
            PanelState.SPEAKING -> { drive(PanelState.PROCESSING); onResponseReady(true) }
            PanelState.ERROR -> { drive(PanelState.EXPANDED_IDLE); onFailure("boom") }
        }
        assertEquals(target, state.value)
        return this
    }

    @Test fun everyStateEventPairBehavesAsTableSays() {
        for (from in PanelState.values()) for (ev in Ev.values()) {
            val scope = TestScope()
            val c = controller(scope).drive(from)
            val expected = valid[from to ev]
            val accepted = ev.fire(c)
            assertEquals("$from + $ev accepted?", expected != null, accepted)
            assertEquals("$from + $ev", expected ?: from, c.state.value)
        }
    }

    @Test fun firstTapExpandsWithMicOff() {
        val mic = FakeMic(); val c = controller(TestScope(), mic)
        c.onStarTap()
        assertEquals(PanelState.EXPANDED_IDLE, c.state.value)
        assertEquals(0, mic.startCount); assertFalse(mic.running)
    }

    @Test fun micStartsOnlyOnSecondTap() {
        val mic = FakeMic(); val c = controller(TestScope(), mic)
        assertFalse(c.onMicTap()); assertEquals(0, mic.startCount)
        c.onStarTap(); c.onMicTap()
        assertEquals(1, mic.startCount); assertTrue(mic.running)
    }

    @Test fun manualStopStopsMicAndReturnsToIdle() {
        val mic = FakeMic(); val c = controller(TestScope(), mic).drive(PanelState.LISTENING)
        c.onStopListening()
        assertEquals(PanelState.EXPANDED_IDLE, c.state.value); assertFalse(mic.running)
    }

    @Test fun closeCollapsesAndStopsMicFromEveryActiveState() {
        for (s in PanelState.values().filter { it != PanelState.COLLAPSED }) {
            val mic = FakeMic(); val c = controller(TestScope(), mic).drive(s)
            c.onClose()
            assertEquals(s.name, PanelState.COLLAPSED, c.state.value); assertFalse(s.name, mic.running)
        }
    }

    @Test fun previewTimeoutCollapsesAfterFifteenSecondsWhenCaptureUnavailable() {
        val scope = TestScope(); val c = PanelController(scope, NoOpMicSession).drive(PanelState.EXPANDED_IDLE)
        assertFalse(c.isCaptureLive); assertFalse(c.timeoutIsSilenceDetection)
        c.onMicTap(); scope.runCurrent()
        assertEquals(PanelState.ERROR, c.state.value)
    }

    @Test fun speechActivityResetsTimeout() {
        val scope = TestScope(); val mic = FakeMic(); val c = PanelController(scope, mic).drive(PanelState.LISTENING)
        assertTrue(c.timeoutIsSilenceDetection)
        scope.runCurrent(); scope.advanceTimeBy(10_000)
        mic.listener!!.onSpeechDetected(); scope.runCurrent()
        scope.advanceTimeBy(10_000); scope.runCurrent()
        assertEquals(PanelState.LISTENING, c.state.value)
        scope.advanceTimeBy(6_000); scope.runCurrent()
        assertEquals(PanelState.COLLAPSED, c.state.value); assertFalse(mic.running)
    }

    @Test fun liveMicWithoutSpeechDetectionNeverTimesOut() {
        val scope = TestScope()
        val mic = object : MicSession {
            override val availability = MicAvailability.Available
            override val reportsSpeechActivity = false
            override fun start(listener: MicListener) = MicStartResult.Started
            override fun stop() = Unit
        }
        val c = controller(scope, mic).drive(PanelState.LISTENING)
        assertFalse(c.timeoutIsSilenceDetection)
        scope.advanceTimeBy(60_000); scope.runCurrent()
        assertEquals(PanelState.LISTENING, c.state.value)
    }

    @Test fun reEnteringListeningDoesNotStackTimers() {
        val scope = TestScope(); val c = PanelController(scope, NoOpMicSession).drive(PanelState.EXPANDED_IDLE)
        c.onMicTap()
        assertEquals(PanelState.ERROR, c.state.value)
    }

    @Test fun micStartFailureGoesToErrorWithMessage() {
        val c = controller(TestScope(), FakeMic(failOnStart = "denied")).drive(PanelState.EXPANDED_IDLE)
        c.onMicTap()
        assertEquals(PanelState.ERROR, c.state.value); assertEquals("denied", c.errorMessage.value)
        c.onDismissError()
        assertEquals(PanelState.EXPANDED_IDLE, c.state.value); assertNull(c.errorMessage.value)
    }

    @Test fun thrownMicStartExceptionBecomesErrorState() {
        val mic = object : MicSession {
            override val availability = MicAvailability.Available
            override val reportsSpeechActivity = true
            override fun start(listener: MicListener): MicStartResult = error("permission bridge crashed")
            override fun stop() = Unit
        }
        val c = controller(TestScope(), mic).drive(PanelState.EXPANDED_IDLE)
        assertTrue(c.onMicTap())
        assertEquals(PanelState.ERROR, c.state.value)
        assertEquals("permission bridge crashed", c.errorMessage.value)
    }

    @Test fun micErrorWhileListeningGoesToError() {
        val mic = FakeMic(); val c = controller(TestScope(), mic).drive(PanelState.LISTENING)
        mic.listener!!.onError("interrupted")
        assertEquals(PanelState.ERROR, c.state.value); assertFalse(mic.running)
    }

    @Test fun transcriptFromMicMovesToProcessing() {
        val mic = FakeMic(); val c = controller(TestScope(), mic).drive(PanelState.LISTENING)
        mic.listener!!.onTranscript("hello")
        assertEquals(PanelState.PROCESSING, c.state.value); assertFalse(mic.running)
        assertEquals("hello", c.transcript.value)
    }

    @Test fun noOpMicReportsUnavailable() {
        assertTrue(NoOpMicSession.availability is MicAvailability.Unavailable)
        assertFalse(NoOpMicSession.reportsSpeechActivity)
    }
}
