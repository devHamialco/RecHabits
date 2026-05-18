package com.rechabits.app.data.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.rechabits.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var autoDismissJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val habitId = intent?.getLongExtra(EXTRA_HABIT_ID, -1L) ?: -1L
        val habitName = intent?.getStringExtra(EXTRA_HABIT_NAME) ?: ""
        val iconId = intent?.getStringExtra(EXTRA_ICON_ID) ?: "water_drop"

        showOverlay(habitId, habitName, iconId)

        // Auto-dismiss after 30 seconds
        autoDismissJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        autoDismissJob?.cancel()
    }

    private fun showOverlay(habitId: Long, habitName: String, iconId: String) {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
            setBackgroundColor(android.graphics.Color.parseColor("#FF5722"))
            elevation = 16f
        }

        // Icon
        val iconText = TextView(this).apply {
            text = getIconForHabit(iconId)
            textSize = 48f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }
        layout.addView(iconText)

        // Habit name (discrete - only shown in overlay, not in widget)
        val nameText = TextView(this).apply {
            text = habitName
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }
        layout.addView(nameText)

        // Done button
        val doneButton = Button(this).apply {
            text = "Hecho"
            setOnClickListener {
                // Complete the habit
                CoroutineScope(Dispatchers.IO).launch {
                    val db = com.rechabits.app.data.db.RechaBitsDatabase.getDatabase(this@OverlayService)
                    val repo = com.rechabits.app.data.repository.HabitRepository(
                        db.habitDao(),
                        db.completionDao()
                    )
                    repo.completeHabit(habitId)
                }
                stopSelf()
            }
        }
        layout.addView(doneButton)

        // Snooze button
        val snoozeButton = Button(this).apply {
            text = "Posponer 5 min"
            setOnClickListener {
                stopSelf()
            }
        }
        layout.addView(snoozeButton)

        val params = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            gravity = Gravity.CENTER
            format = PixelFormat.TRANSLUCENT
        }

        overlayView = layout
        windowManager?.addView(layout, params)
    }

    private fun removeOverlay() {
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                // View may already be removed
            }
        }
        overlayView = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RechaBits Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification for overlay service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RechaBits")
            .setContentText("Recordatorio activo")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun getIconForHabit(iconId: String): String {
        return when (iconId) {
            "water_drop" -> "\uD83D\uDCA7"
            "run" -> "\uD83C\uDFC3\u200D\u2640\uFE0F"
            "apple" -> "\uD83C\uDF4E"
            "tooth" -> "\uD83E\uDBB7"
            "moon" -> "\uD83C\uDF19"
            "sun" -> "\u2600\uFE0F"
            "cream" -> "\uD83E\uDDF4"
            "shield" -> "\uD83D\uDEE1\uFE0F"
            "plant" -> "\uD83C\uDF31"
            else -> "\uD83D\uDCA7"
        }
    }

    companion object {
        private const val CHANNEL_ID = "rechabits_overlay_channel"
        private const val NOTIFICATION_ID = 1

        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_NAME = "extra_habit_name"
        const val EXTRA_ICON_ID = "extra_icon_id"
    }
}
