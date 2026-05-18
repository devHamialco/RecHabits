package com.rechabits.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rechabits.app.data.db.entity.CompletionEntity

@Dao
interface CompletionDao {
    @Query("SELECT * FROM completions WHERE habitId = :habitId AND date = :date")
    suspend fun getCompletionsForDate(habitId: Long, date: String): List<CompletionEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM completions WHERE habitId = :habitId AND date = :date")
    suspend fun getTotalAmountForDate(habitId: Long, date: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: CompletionEntity)

    @Query("DELETE FROM completions WHERE habitId = :habitId")
    suspend fun deleteByHabitId(habitId: Long)
}
