package com.quantum.assistant.audio

/** Whether real microphone capture can be started by this implementation. */
sealed interface MicAvailability {
    data object Available : MicAvailability
    data class Unavailable(val reason: String) : MicAvailability
}

sealed interface MicStartResult {
    data object Started : MicStartResult
    data class Failed(val reason: String) : MicStartResult
}

/** Callbacks a real capture implementation delivers to the panel state machine (always on the main thread). */
interface MicListener {
    /** Real voice-activity detection fired. Resets the silence countdown. */
    fun onSpeechDetected()
    /** A finalized transcript is ready. */
    fun onTranscript(text: String)
    /** Capture failed or was interrupted (permission revoked, audio focus lost, ...). */
    fun onError(reason: String)
}

/**
 * Boundary between the panel state machine and real audio capture.
 * A future implementation must request RECORD_AUDIO at point of use inside/before [start].
 */
interface MicSession {
    val availability: MicAvailability

    /** True only if this implementation really detects speech activity and calls [MicListener.onSpeechDetected]. */
    val reportsSpeechActivity: Boolean

    /** Only called when [availability] is [MicAvailability.Available]. */
    fun start(listener: MicListener): MicStartResult

    /** Must be idempotent and safe to call when not started. */
    fun stop()
}

/** Alpha 0.1 implementation: states plainly that capture is unavailable and never touches audio hardware. */
object NoOpMicSession : MicSession {
    override val availability = MicAvailability.Unavailable("Microphone capture is not implemented in Alpha 0.1.")
    override val reportsSpeechActivity = false
    override fun start(listener: MicListener): MicStartResult = MicStartResult.Failed((availability).reason)
    override fun stop() = Unit
}
