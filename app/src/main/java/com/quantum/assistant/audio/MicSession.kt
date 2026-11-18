package com.quantum.assistant.audio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import java.util.Locale

sealed interface MicAvailability {
    data object Available : MicAvailability
    data class Unavailable(val reason: String) : MicAvailability
}

sealed interface MicStartResult {
    data object Started : MicStartResult
    data class Failed(val reason: String) : MicStartResult
}

interface MicListener {
    fun onSpeechDetected()
    fun onTranscript(text: String)
    fun onError(reason: String)
}

interface MicSession {
    val availability: MicAvailability
    val reportsSpeechActivity: Boolean
    fun start(listener: MicListener): MicStartResult
    fun stop()
}

/**
 * Uses Android's installed speech-recognition service. It starts only after the user
 * taps the microphone and grants RECORD_AUDIO. The system recognition service may
 * require internet depending on the device and installed language pack.
 */
class AndroidSpeechRecognizerSession(context: Context) : MicSession {
    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private var active = false

    override val availability: MicAvailability =
        if (SpeechRecognizer.isRecognitionAvailable(appContext)) MicAvailability.Available
        else MicAvailability.Unavailable("На устройстве нет службы распознавания речи.")

    override val reportsSpeechActivity: Boolean = true

    override fun start(listener: MicListener): MicStartResult {
        if (availability !is MicAvailability.Available) {
            return MicStartResult.Failed((availability as MicAvailability.Unavailable).reason)
        }
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return MicStartResult.Failed("Нет разрешения на микрофон. Разреши доступ и попробуй снова.")
        }
        return try {
            stop()
            val speech = SpeechRecognizer.createSpeechRecognizer(appContext)
            recognizer = speech
            speech.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { active = true }
                override fun onBeginningOfSpeech() { listener.onSpeechDetected() }
                override fun onRmsChanged(rmsdB: Float) {
                    if (rmsdB > 2.0f) listener.onSpeechDetected()
                }
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onError(error: Int) {
                    active = false
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Ошибка аудиосистемы."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Android не выдал доступ к микрофону."
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Служба распознавания недоступна. Проверь интернет."
                        SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Не удалось разобрать речь. Попробуй ещё раз."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Служба распознавания занята."
                        else -> "Распознавание речи завершилось с ошибкой ($error)."
                    }
                    listener.onError(message)
                    stop()
                }
                override fun onResults(results: Bundle?) {
                    active = false
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (text.isNullOrBlank()) listener.onError("Речь не распознана. Попробуй ещё раз.")
                    else listener.onTranscript(text)
                    stop()
                }
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, appContext.packageName)
            }
            speech.startListening(intent)
            MicStartResult.Started
        } catch (t: Throwable) {
            stop()
            MicStartResult.Failed(t.message ?: "Не удалось запустить микрофон.")
        }
    }

    override fun stop() {
        val current = recognizer
        recognizer = null
        active = false
        if (current != null) {
            try { current.cancel() } catch (_: Throwable) { }
            try { current.destroy() } catch (_: Throwable) { }
        }
    }
}

/** Explicit fallback for tests or devices without a recognition service. */
object NoOpMicSession : MicSession {
    override val availability = MicAvailability.Unavailable("Распознавание речи недоступно на этом устройстве.")
    override val reportsSpeechActivity = false
    override fun start(listener: MicListener): MicStartResult =
        MicStartResult.Failed((availability as MicAvailability.Unavailable).reason)
    override fun stop() = Unit
}
