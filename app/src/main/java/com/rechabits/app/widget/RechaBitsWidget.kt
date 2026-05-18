package com.rechabits.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.TextUnit
import androidx.glance.unit.TextUnitType
import androidx.glance.unit.dp
import com.rechabits.app.data.db.RechaBitsDatabase
import com.rechabits.app.data.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class RechaBitsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val habits = withContext(Dispatchers.IO) {
            val db = RechaBitsDatabase.getDatabase(context)
            val repo = HabitRepository(db.habitDao(), db.completionDao())
            repo.getAllActive().first()
        }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (habits.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Agregá hábitos desde la app",
                            style = TextStyle(
                                color = ColorProvider(android.graphics.Color.GRAY),
                                fontSize = TextUnit(14f, TextUnitType.Sp)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "RechaBits",
                        style = TextStyle(
                            color = ColorProvider(android.graphics.Color.BLACK),
                            fontSize = TextUnit(16f, TextUnitType.Sp)
                        )
                    )
                    
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        habits.forEach { habit ->
                            HabitButton(habit = habit)
                        }
                    }
                }
            }
        }
    }
}
