package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worksheets")
data class WorksheetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val topic: String,
    val difficulty: String,
    val createdAt: Long = System.currentTimeMillis(),
    val rawInput: String,
    val questionsJson: String,
    val targetGrammarRules: String,
    val isFavorite: Boolean = false
)
