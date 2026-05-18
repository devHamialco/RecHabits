package com.rechabits.app.presentation.ui.reminder

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rechabits.app.data.reminder.ReminderReceiver
import com.rechabits.app.presentation.ui.theme.RechaBitsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderActivity : ComponentActivity() {

    private val viewModel: ReminderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        val habitId = intent.getLongExtra(ReminderReceiver.EXTRA_HABIT_ID, -1L)
        val habitName = intent.getStringExtra(ReminderReceiver.EXTRA_HABIT_NAME) ?: ""
        val iconId = intent.getStringExtra(ReminderReceiver.EXTRA_ICON_ID) ?: "water_drop"

        setContent {
            RechaBitsTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Habit icon (discrete - only icon, no text)
                    Text(
                        text = getIconForHabit(iconId),
                        fontSize = 96.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = {
                            if (habitId != -1L) {
                                viewModel.completeHabit(habitId)
                            }
                            finish()
                        },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Hecho", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            finish()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Posponer 5 min", fontSize = 18.sp)
                    }
                }
            }
        }
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
}
