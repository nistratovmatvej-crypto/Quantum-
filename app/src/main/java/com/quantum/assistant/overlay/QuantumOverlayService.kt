package com.quantum.assistant.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.quantum.assistant.MainActivity
import com.quantum.assistant.R
import kotlin.math.abs

/**
 * User-enabled persistent Quantum bubble. It is intentionally silent: tapping its
 * mic affordance opens the app, where Android's normal runtime microphone permission
 * and the existing SpeechRecognizer flow are used.
 */
class QuantumOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var bubble: QuantumOverlayView? = null
    private var params: WindowManager.LayoutParams? = null
    private var expanded = false
    private var appForeground = false
    private var x0 = 0f
    private var y0 = 0f
    private var startX = 0
    private var startY = 0

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_APP_FOREGROUND -> { appForeground = true; removeBubble() }
            ACTION_APP_BACKGROUND -> { appForeground = false; showBubble() }
            ACTION_STOP -> { stopSelf(); return START_NOT_STICKY }
            else -> if (!appForeground) showBubble()
        }
        return START_STICKY
    }

    private fun showBubble() {
        if (bubble != null) return
        val view = QuantumOverlayView(this) { openAssistant() }
        bubble = view
        val width = dp(if (expanded) 224 else 58)
        val height = dp(if (expanded) 64 else 58)
        val lp = WindowManager.LayoutParams(
            width, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dp(8)
            alpha = if (expanded) 1f else 0.82f
        }
        params = lp
        view.onTap = { x ->
            if (!expanded) {
                expanded = true
                resizeBubble()
            } else if (x > view.width * 0.58f) {
                openAssistant()
            } else {
                expanded = false
                resizeBubble()
            }
        }
        view.onDragStart = {
            params?.let { startX = it.x; startY = it.y }
        }
        view.onDrag = { dx, dy ->
            params?.let {
                it.gravity = Gravity.TOP or Gravity.START
                it.x = (startX + dx).coerceAtLeast(0)
                it.y = (startY + dy).coerceAtLeast(dp(4))
                try { windowManager.updateViewLayout(view, it) } catch (_: Exception) { }
            }
        }
        try { windowManager.addView(view, lp) } catch (_: Exception) { bubble = null; params = null }
    }

    private fun resizeBubble() {
        val view = bubble ?: return
        val lp = params ?: return
        lp.width = dp(if (expanded) 224 else 58)
        lp.height = dp(if (expanded) 64 else 58)
        lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        lp.x = 0
        lp.y = dp(if (expanded) 12 else 8)
        lp.alpha = if (expanded) 1f else 0.82f
        try { windowManager.updateViewLayout(view, lp); view.invalidate() } catch (_: Exception) { }
    }

    private fun openAssistant() {
        val launch = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_START_MIC, true)
        }
        startActivity(launch)
        expanded = false
        resizeBubble()
    }

    private fun removeBubble() {
        bubble?.let { try { windowManager.removeView(it) } catch (_: Exception) { } }
        bubble = null
        params = null
    }

    override fun onDestroy() {
        removeBubble()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID, "Quantum floating assistant", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Quantum активен")
                .setContentText("Плавающий остров включён. Микрофон не слушает.")
                .setContentIntent(open).setOngoing(true).build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Quantum активен").setContentText("Плавающий остров включён.")
                .setContentIntent(open).setOngoing(true).build()
        }
    }

    companion object {
        const val ACTION_APP_FOREGROUND = "com.quantum.assistant.overlay.APP_FOREGROUND"
        const val ACTION_APP_BACKGROUND = "com.quantum.assistant.overlay.APP_BACKGROUND"
        const val ACTION_STOP = "com.quantum.assistant.overlay.STOP"
        private const val CHANNEL_ID = "quantum_overlay"
        private const val NOTIFICATION_ID = 731
    }
}

private class QuantumOverlayView(
    context: android.content.Context,
    private val openMic: () -> Unit
) : View(context) {
    private val bg = Paint(3).apply { color = Color.rgb(5, 14, 22) }
    private val stroke = Paint(3).apply { color = Color.argb(110, 53, 187, 213); style = Paint.Style.STROKE; strokeWidth = dp(1f) }
    private val cyan = Paint(3).apply { color = Color.rgb(53, 187, 213) }
    private val white = Paint(3).apply { color = Color.rgb(244, 243, 239); textSize = dp(13f); typeface = android.graphics.Typeface.create("sans-serif-medium", 0) }
    private val muted = Paint(3).apply { color = Color.rgb(145, 168, 181); textSize = dp(10f) }
    var onTap: ((Float) -> Unit)? = null
    var onDragStart: (() -> Unit)? = null
    var onDrag: ((Float, Float) -> Unit)? = null
    private var downX = 0f
    private var downY = 0f
    private var dragging = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = height / 2f
        val rect = RectF(1f, 1f, width - 1f, height - 1f)
        canvas.drawRoundRect(rect, r, r, bg)
        canvas.drawRoundRect(rect, r, r, stroke)
        val cx = dp(29f)
        val cy = height / 2f
        canvas.drawCircle(cx, cy, dp(17f), cyan)
        canvas.drawCircle(cx, cy, dp(10f), bg)
        canvas.drawCircle(cx, cy, dp(4f), white)
        if (width > dp(100f)) {
            canvas.drawText("Quantum", dp(56f), cy + dp(4f), white)
            canvas.drawText("Готов к работе", dp(56f), cy + dp(19f), muted)
            val micX = width - dp(31f)
            canvas.drawCircle(micX, cy, dp(19f), cyan)
            val mic = Paint(3).apply { color = Color.rgb(7, 21, 34); strokeWidth = dp(2f); style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
            canvas.drawRoundRect(RectF(micX-dp(4f),cy-dp(10f),micX+dp(4f),cy+dp(3f)),dp(4f),dp(4f),mic)
            canvas.drawArc(RectF(micX-dp(9f),cy-dp(7f),micX+dp(9f),cy+dp(10f)),0f,180f,false,mic)
            canvas.drawLine(micX,cy+dp(10f),micX,cy+dp(14f),mic)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX; downY = event.rawY; dragging = false; onDragStart?.invoke(); return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - downX
                val dy = event.rawY - downY
                if (abs(dx) > dp(8f) || abs(dy) > dp(8f)) dragging = true
                if (dragging) onDrag?.invoke(dx.toInt().toFloat(), dy.toInt().toFloat())
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!dragging) onTap?.invoke(event.x)
                performClick()
                return true
            }
        }
        return true
    }

    override fun performClick(): Boolean { super.performClick(); return true }
    private fun dp(value: Float) = value * resources.displayMetrics.density
}
