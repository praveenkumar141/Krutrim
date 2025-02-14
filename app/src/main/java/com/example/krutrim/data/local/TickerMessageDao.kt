package com.example.krutrim.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TickerMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: TickerMessageEntity)

    @Query("SELECT * FROM ticker_messages ORDER BY id DESC LIMIT 10")
    suspend fun getLatestMessages(): List<TickerMessageEntity>
}