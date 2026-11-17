package com.quantum.assistant.floating

import com.quantum.assistant.audio.MicAvailability
import com.quantum.assistant.audio.MicListener
import com.quantum.assistant.audio.MicSession
import com.quantum.assistant.audio.MicStartResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PanelState { COLLAPSED, EXPANDED_IDLE, LISTENING, PROCESSING, AWAITING_CONFIRMATION, SPEAKING, ERROR }

/**
 * Floating star/panel state machine (ARCHITECTURE.md). Pure Kotlin; call from the main thread only.
 *
 * Valid transitions (everything else is ignored and the event method returns false):
 *   COLLAPSED             --starTap-->        EXPANDED_IDLE
 *   EXPANDED_IDLE         --micTap-->         LISTENING (or ERROR if the mic fails to start)
 *   LISTENING             --stop-->           EXPANDED_IDLE
 *   LISTENING             --transcript-->     PROCESSING
 *   LISTENING             --timeout-->        COLLAPSED
 *   PROCESSING            --actionProposed--> AWAITING_CONFIRMATION
 *   PROCESSING            --responseReady-->  SPEAKING | EXPANDED_IDLE
 *   AWAITING_CONFIRMATION --approve-->        SPEAKING | EXPANDED_IDLE
 *   AWAITING_CONFIRMATION --reject-->         EXPANDED_IDLE
 *   SPEAKING              --finished-->       EXPANDED_IDLE
 *   any non-COLLAPSED     --failure-->        ERROR (ERROR itself excluded)
 *   ERROR                 --dismiss-->        EXPANDED_IDLE
 *   any non-COLLAPSED     --close-->          COLLAPSED
 *
 * Timer rules: at most one timer job exists. It runs only while LISTENING, and only if it is honest to do so:
 * (a) capture is unavailable, so LISTENING is a labelled preview, or (b) the mic really reports speech activity.
 * A live mic without speech-activity reporting gets NO timer, so it can never cut off someone who is speaking.
 */
class PanelController(
    private val scope: CoroutineScope,
    private val mic: MicSession,
    private val timeoutSeconds: Int = 15
) {
    private val _state = MutableStateFlow(PanelState.COLLAPSED)
    val state: StateFlow<PanelState> = _state.asStateFlow()

    private val _secondsLeft = MutableStateFlow(timeoutSeconds)
    val secondsLeft: StateFlow<Int> = _secondsLeft.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** True if real capture is running/available; false means LISTENING is only a UI preview. */
    val isCaptureLive: Boolean get() = mic.availability is MicAvailability.Available

    /** True only when the countdown is genuine silence detection driven by real speech activity. */
    val timeoutIsSilenceDetection: Boolean get() = isCaptureLive && mic.reportsSpeechActivity

    val unavailableReason: String? get() = (mic.availability as? MicAvailability.Unavailable)?.reason

    private var timer: Job? = null

    private val micListener = object : MicListener {
        override fun onSpeechDetected() { if (_state.value == PanelState.LISTENING) restartTimer() }
        override fun onTranscript(text: String) { onTranscriptFinalized() }
        override fun onError(reason: String) { onFailure(reason) }
    }

    fun onStarTap(): Boolean = move(PanelState.COLLAPSED, PanelState.EXPANDED_IDLE)

    fun onMicTap(): Boolean {
        if (_state.value != PanelState.EXPANDED_IDLE) return false
        if (isCaptureLive) {
            val result = try {
                mic.start(micListener)
            } catch (t: Throwable) {
                MicStartResult.Failed(t.message ?: "Microphone startup failed.")
            }
            if (result is MicStartResult.Failed) {
                safeStopMic()
                enter(PanelState.ERROR, result.reason)
                return true
            }
        }
        enter(PanelState.LISTENING)
        if (!isCaptureLive || mic.reportsSpeechActivity) restartTimer()
        return true
    }

    fun onStopListening(): Boolean {
        if (_state.value != PanelState.LISTENING) return false
        stopAudioAndTimer(); enter(PanelState.EXPANDED_IDLE); return true
    }

    fun onTranscriptFinalized(): Boolean {
        if (_state.value != PanelState.LISTENING) return false
        stopAudioAndTimer(); enter(PanelState.PROCESSING); return true
    }

    fun onActionProposed(): Boolean = move(PanelState.PROCESSING, PanelState.AWAITING_CONFIRMATION)

    fun onResponseReady(speak: Boolean): Boolean =
        move(PanelState.PROCESSING, if (speak) PanelState.SPEAKING else PanelState.EXPANDED_IDLE)

    fun onApprove(speak: Boolean): Boolean =
        move(PanelState.AWAITING_CONFIRMATION, if (speak) PanelState.SPEAKING else PanelState.EXPANDED_IDLE)

    fun onReject(): Boolean = move(PanelState.AWAITING_CONFIRMATION, PanelState.EXPANDED_IDLE)

    fun onSpeakingFinished(): Boolean = move(PanelState.SPEAKING, PanelState.EXPANDED_IDLE)

    fun onFailure(reason: String): Boolean {
        val s = _state.value
        if (s == PanelState.COLLAPSED || s == PanelState.ERROR) return false
        stopAudioAndTimer(); enter(PanelState.ERROR, reason); return true
    }

    fun onDismissError(): Boolean = move(PanelState.ERROR, PanelState.EXPANDED_IDLE)

    /** Close from any state. Always stops audio and timer, even if already collapsed (defensive). */
    fun onClose(): Boolean {
        stopAudioAndTimer()
        if (_state.value == PanelState.COLLAPSED) return false
        enter(PanelState.COLLAPSED); return true
    }

    private fun move(from: PanelState, to: PanelState): Boolean {
        if (_state.value != from) return false
        enter(to); return true
    }

    private fun enter(to: PanelState, error: String? = null) {
        _errorMessage.value = if (to == PanelState.ERROR) error else null
        _state.value = to
    }

    private fun stopAudioAndTimer() {
        timer?.cancel(); timer = null
        _secondsLeft.value = timeoutSeconds
        safeStopMic()
    }

    private fun safeStopMic() {
        try {
            mic.stop()
        } catch (t: Throwable) {
            // Cleanup must not crash the UI or prevent the state machine from closing.
            _errorMessage.value = t.message ?: "Microphone cleanup failed."
        }
    }

    private fun restartTimer() {
        timer?.cancel()
        timer = scope.launch {
            var left = timeoutSeconds
            while (left > 0) { _secondsLeft.value = left; delay(1_000); left-- }
            if (_state.value == PanelState.LISTENING) {
                stopAudioAndTimer(); enter(PanelState.COLLAPSED)
            }
        }
    }
}
