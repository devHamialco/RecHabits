package com.rechabits.app.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rechabits.app.data.db.dao.ScheduleDao
import com.rechabits.app.data.db.entity.ScheduleEntity
import com.rechabits.app.data.reminder.ReminderReceiver
import com.rechabits.app.domain.model.Schedule
import java.util.Calendar
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class ReminderRepository @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context // Add this annotation
) {
    suspend fun getAllEnabledSchedules(): List<Schedule> {
        return scheduleDao.getAllEnabled().map { it.toDomain() }
    }

    suspend fun getSchedulesForHabit(habitId: Long): List<Schedule> {
        return scheduleDao.getByHabitId(habitId).map { it.toDomain() }
    }

    suspend fun saveSchedule(schedule: Schedule) {
        scheduleDao.insert(schedule.toEntity())
    }

    suspend fun deleteSchedulesForHabit(habitId: Long) {
        scheduleDao.deleteByHabitId(habitId)
    }

    suspend fun registerAllAlarms() {
        val schedules = scheduleDao.getAllEnabled()
        schedules.forEach { schedule ->
            registerAlarm(schedule)
        }
    }

    suspend fun registerAlarmForSchedule(schedule: ScheduleEntity) {
        registerAlarm(schedule)
    }

    fun cancelAlarmForSchedule(scheduleId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun registerAlarm(schedule: ScheduleEntity) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_HABIT_ID, schedule.habitId)
            putExtra(ReminderReceiver.EXTRA_SCHEDULE_ID, schedule.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, schedule.hour)
            set(Calendar.MINUTE, schedule.minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun ScheduleEntity.toDomain() = Schedule(
        id = id,
        habitId = habitId,
        hour = hour,
        minute = minute,
        enabled = enabled
    )

    private fun Schedule.toEntity() = ScheduleEntity(
        id = id,
        habitId = habitId,
        hour = hour,
        minute = minute,
        enabled = enabled
    )
}
