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

class QuantumOverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: Bubble? = null
    private var params: WindowManager.LayoutParams? = null
    private var expanded = false
    private var appForeground = false
    private var startX = 0
    private var startY = 0

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL, "Quantum floating assistant", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val notification = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, CHANNEL).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Quantum активен")
                .setContentText("Плавающий остров включён. Микрофон не слушает.")
                .setOngoing(true).build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Quantum активен").setOngoing(true).build()
        }
        if (Build.VERSION.SDK_INT >= 34) startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        else startForeground(NOTIFICATION_ID, notification)
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
        if (bubble != null || !android.provider.Settings.canDrawOverlays(this)) return
        val view = Bubble(this)
        bubble = view
        val lp = WindowManager.LayoutParams(dp(if (expanded) 224 else 58), dp(58),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dp(8)
            alpha = .82f
        }
        params = lp
        view.onTap = { x ->
            if (!expanded) { expanded = true; resize() }
            else if (x > view.width * .58f) openAssistant()
            else { expanded = false; resize() }
        }
        view.onDragStart = { params?.let { startX = it.x; startY = it.y } }
        view.onDrag = { dx, dy ->
            params?.let {
                it.gravity = Gravity.TOP or Gravity.START
                it.x = (startX + dx.toInt()).coerceAtLeast(0)
                it.y = (startY + dy.toInt()).coerceAtLeast(dp(4))
                try { wm.updateViewLayout(view, it) } catch (_: Exception) { }
            }
        }
        try { wm.addView(view, lp) } catch (_: Exception) { bubble = null; params = null }
    }

    private fun resize() {
        val view = bubble ?: return
        val lp = params ?: return
        lp.width = dp(if (expanded) 224 else 58)
        lp.height = dp(58)
        lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        lp.x = 0
        lp.y = dp(if (expanded) 12 else 8)
        lp.alpha = if (expanded) 1f else .82f
        try { wm.updateViewLayout(view, lp); view.invalidate() } catch (_: Exception) { }
    }

    private fun openAssistant() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        expanded = false
        resize()
    }

    private fun removeBubble() {
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) { } }
        bubble = null
        params = null
    }

    override fun onDestroy() { removeBubble(); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    companion object {
        const val ACTION_APP_FOREGROUND = "com.quantum.assistant.overlay.APP_FOREGROUND"
        const val ACTION_APP_BACKGROUND = "com.quantum.assistant.overlay.APP_BACKGROUND"
        const val ACTION_STOP = "com.quantum.assistant.overlay.STOP"
        private const val CHANNEL = "quantum_overlay"
        private const val NOTIFICATION_ID = 731
    }
}

private class Bubble(context: android.content.Context) : View(context) {
    private val bg = Paint(3).apply { color = Color.rgb(5, 14, 22) }
    private val edge = Paint(3).apply { color = Color.argb(110, 53, 187, 213); style = Paint.Style.STROKE; strokeWidth = dp(1f) }
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
        val cy = height / 2f
        val rect = RectF(1f, 1f, width - 1f, height - 1f)
        canvas.drawRoundRect(rect, height / 2f, height / 2f, bg)
        canvas.drawRoundRect(rect, height / 2f, height / 2f, edge)
        canvas.drawCircle(dp(29f), cy, dp(17f), cyan)
        canvas.drawCircle(dp(29f), cy, dp(10f), bg)
        canvas.drawCircle(dp(29f), cy, dp(4f), white)
        if (width > dp(100f)) {
            canvas.drawText("Quantum", dp(56f), cy + dp(4f), white)
            canvas.drawText("Готов к работе", dp(56f), cy + dp(19f), muted)
            val x = width - dp(31f)
            canvas.drawCircle(x, cy, dp(19f), cyan)
            val mic = Paint(3).apply { color = Color.rgb(7, 21, 34); style = Paint.Style.STROKE; strokeWidth = dp(2f); strokeCap = Paint.Cap.ROUND }
            canvas.drawRoundRect(RectF(x-dp(4f),cy-dp(10f),x+dp(4f),cy+dp(3f)),dp(4f),dp(4f),mic)
            canvas.drawArc(RectF(x-dp(9f),cy-dp(7f),x+dp(9f),cy+dp(10f)),0f,180f,false,mic)
            canvas.drawLine(x,cy+dp(10f),x,cy+dp(14f),mic)
        }
    }
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> { downX=e.rawX; downY=e.rawY; dragging=false; onDragStart?.invoke(); return true }
            MotionEvent.ACTION_MOVE -> {
                val dx=e.rawX-downX; val dy=e.rawY-downY
                if (abs(dx)>dp(8f)||abs(dy)>dp(8f)) dragging=true
                if (dragging) onDrag?.invoke(dx,dy)
                return true
            }
            MotionEvent.ACTION_UP -> { if (!dragging) onTap?.invoke(e.x); performClick(); return true }
        }
        return true
    }
    override fun performClick(): Boolean { super.performClick(); return true }
    private fun dp(v: Float) = v * resources.displayMetrics.density
}
