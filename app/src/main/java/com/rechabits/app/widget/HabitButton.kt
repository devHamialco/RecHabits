package com.rechabits.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import com.rechabits.app.data.db.RechaBitsDatabase
import com.rechabits.app.data.repository.HabitRepository
import com.rechabits.app.domain.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Retrieve habitId from parameters
        val habitId = parameters[HabitIdKey] ?: return

        withContext(Dispatchers.IO) {
            val db = RechaBitsDatabase.getDatabase(context)
            val repo = HabitRepository(db.habitDao(), db.completionDao())
            repo.completeHabit(habitId)
        }
        RechaBitsWidget().update(context, glanceId)
    }

    companion object {
        val HabitIdKey = ActionParameters.Key<Long>("habitId")
    }
}

@Composable
fun HabitButton(habit: Habit) {
    val icon = getIconForHabit(habit.iconId)

    Box(
        modifier = GlanceModifier
            .size(60.dp)
            .clickable(
                onClick = actionRunCallback<HabitClickAction>(
                    parameters = actionParametersOf(HabitClickAction.HabitIdKey to habit.id)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = icon,
                style = TextStyle(fontSize = 24.sp)
            )
            Text(
                text = habit.name.take(3).uppercase(),
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(android.graphics.Color.GRAY)
                )
            )
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