package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorksheetDao {
    @Query("SELECT * FROM worksheets ORDER BY createdAt DESC")
    fun getAllWorksheets(): Flow<List<WorksheetEntity>>

    @Query("SELECT * FROM worksheets WHERE id = :id")
    fun getWorksheetById(id: Long): Flow<WorksheetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorksheet(worksheet: WorksheetEntity): Long

    @Query("DELETE FROM worksheets WHERE id = :id")
    suspend fun deleteWorksheetById(id: Long)

    @Query("UPDATE worksheets SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
}
