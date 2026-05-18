package com.rechabits.app.widget

import android.content.Context
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.TextUnit
import androidx.glance.unit.TextUnitType
import androidx.glance.unit.dp
import com.rechabits.app.data.db.RechaBitsDatabase
import com.rechabits.app.data.repository.HabitRepository
import com.rechabits.app.domain.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitClickAction(
    private val habitId: Long
) : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: androidx.glance.GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.IO) {
            val db = RechaBitsDatabase.getDatabase(context)
            val repo = HabitRepository(db.habitDao(), db.completionDao())
            repo.completeHabit(habitId)
        }
        RechaBitsWidget().update(context, glanceId)
    }
}

@androidx.glance.GlanceComposable
fun HabitButton(habit: Habit) {
    val icon = getIconForHabit(habit.iconId)
    val action = HabitClickAction(habit.id)
    
    Box(
        modifier = GlanceModifier
            .size(60.dp)
            .clickable(onClick = actionRunCallback<HabitClickAction>()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = icon,
                style = TextStyle(fontSize = TextUnit(24f, TextUnitType.Sp))
            )
            Text(
                text = habit.name.take(3).uppercase(),
                style = TextStyle(
                    fontSize = TextUnit(10f, TextUnitType.Sp), 
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
