package com.quantum.assistant.floating

import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.quantum.assistant.MainActivity
import com.quantum.assistant.R
import com.quantum.assistant.ui.theme.QuantumColors

/**
 * User-enabled system-wide Quantum Island.
 * It is deliberately a small, non-focusable overlay; tapping it opens the app.
 * Microphone capture remains in the Activity, where Android runtime permission
 * can be requested explicitly.
 */
class QuantumIslandService : Service() {
    private lateinit var windowManager: WindowManager
    private var island: IslandView? = null
    private var params: WindowManager.LayoutParams? = null
    private var compact = false
    private var expanded = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Quantum Island активен")
            .setContentText("Нажми на остров, чтобы открыть помощника")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else startForeground(NOTIFICATION_ID, notification)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> { removeIsland(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); return START_NOT_STICKY }
            ACTION_COMPACT -> setCompact(true)
            ACTION_RESTORE -> setCompact(false)
            ACTION_START, null -> showIsland()
        }
        return START_STICKY
    }

    private fun showIsland() {
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        if (island == null) {
            val view = IslandView(this)
            view.setOnLongClickListener {\n                removeIsland()\n                stopForeground(STOP_FOREGROUND_REMOVE)\n                stopSelf()\n                true\n            }\n            view.setOnClickListener {
                if (expanded) {
                    expanded = false
                    animateSize(false)
                } else {
                    expanded = true
                    compact = false
                    animateSize(true)
                    val open = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(open)
                }
            }
            island = view
            params = WindowManager.LayoutParams(
                dp(132), dp(46), WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = dp(8)
            }
            try { windowManager.addView(view, params) } catch (_: Exception) { island = null; stopSelf() }
        }
        setCompact(false)
    }

    private fun setCompact(value: Boolean) {
        compact = value
        if (island == null) { if (!value) showIsland(); return }
        val view = island ?: return
        val p = params ?: return
        view.setCompact(value)
        view.alpha = if (value) 0.62f else 1f
        p.y = dp(if (value) 2 else 8)
        try { windowManager.updateViewLayout(view, p) } catch (_: Exception) { }
        if (value) animateSize(false) else if (expanded) animateSize(true)
    }

    private fun animateSize(toExpanded: Boolean) {
        val view = island ?: return
        val p = params ?: return
        val targetW = dp(if (toExpanded && !compact) 222 else 132)
        val targetH = dp(if (toExpanded && !compact) 58 else 46)
        val fromW = p.width
        val fromH = p.height
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 240L
            addUpdateListener { a ->
                val f = a.animatedValue as Float
                p.width = (fromW + (targetW - fromW) * f).toInt()
                p.height = (fromH + (targetH - fromH) * f).toInt()
                try { windowManager.updateViewLayout(view, p) } catch (_: Exception) { cancel() }
            }
            start()
        }
    }

    private fun removeIsland() {
        island?.let { try { windowManager.removeView(it) } catch (_: Exception) { } }
        island = null
        params = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL, "Quantum Island", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    override fun onDestroy() { removeIsland(); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.quantum.assistant.island.START"
        const val ACTION_COMPACT = "com.quantum.assistant.island.COMPACT"
        const val ACTION_RESTORE = "com.quantum.assistant.island.RESTORE"
        const val ACTION_STOP = "com.quantum.assistant.island.STOP"
        private const val CHANNEL = "quantum_island"
        private const val NOTIFICATION_ID = 7301
    }
}

private class IslandView : View {
    private val bg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 1.2f }
    private val cyan = Paint(Paint.ANTI_ALIAS_FLAG)
    private val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(244,243,239); textSize = 13f; typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL) }
    private val muted = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(145,168,181); textSize = 10f }
    private val rect = RectF()
    private var compact = false
    private val density get() = resources.displayMetrics.density

    constructor(context: android.content.Context) : super(context) {
        isClickable = true
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    fun setCompact(value: Boolean) { compact = value; invalidate() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val d = density
        rect.set(1f*d, 1f*d, width-1f*d, height-1f*d)
        bg.shader = LinearGradient(0f,0f,width.toFloat(),height.toFloat(),
            intArrayOf(android.graphics.Color.argb(if(compact) 190 else 238, 7,21,34), android.graphics.Color.argb(if(compact) 190 else 246, 13,36,53)), null, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(rect, height/2f, height/2f, bg)
        edge.color = android.graphics.Color.argb(if(compact) 90 else 155, 53,187,213)
        canvas.drawRoundRect(rect, height/2f, height/2f, edge)
        val cx = 22f*d
        val cy = height/2f
        cyan.color = android.graphics.Color.rgb(53,187,213)
        cyan.style = Paint.Style.STROKE
        cyan.strokeWidth = 2f*d
        canvas.drawCircle(cx,cy,10f*d,cyan)
        canvas.drawCircle(cx,cy,5.5f*d,cyan)
        cyan.style = Paint.Style.FILL
        canvas.drawCircle(cx,cy,2f*d,cyan)
        if (!compact) {
            canvas.drawText("QUANTUM", 42f*d, cy-1f*d, white)
            canvas.drawText("Готов к работе", 42f*d, cy+13f*d, muted)
            canvas.drawCircle(width-24f*d, cy, 12f*d, cyan)
            white.textSize = 12f*d
            canvas.drawText("›", width-27f*d, cy+4f*d, white)
            white.textSize = 13f
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> { animate().scaleX(.97f).scaleY(.97f).setDuration(90).start(); return true }
            MotionEvent.ACTION_UP -> { animate().scaleX(1f).scaleY(1f).setDuration(150).start(); performClick(); return true }
            MotionEvent.ACTION_CANCEL -> { animate().scaleX(1f).scaleY(1f).setDuration(150).start(); return true }
        }
        return true
    }

    override fun performClick(): Boolean { super.performClick(); return callOnClick() }
}