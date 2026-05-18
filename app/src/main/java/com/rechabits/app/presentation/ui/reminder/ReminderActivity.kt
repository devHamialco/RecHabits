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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
        val scheduleId = intent.getLongExtra(ReminderReceiver.EXTRA_SCHEDULE_ID, -1L)

        setContent {
            RechaBitsTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Habit icon placeholder
                    Icon(
                        painter = painterResource(android.R.drawable.ic_dialog_info),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "\u23F0",
                        fontSize = 48.sp
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
}
