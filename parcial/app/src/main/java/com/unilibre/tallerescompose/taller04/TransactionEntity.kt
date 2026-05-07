package com.unilibre.tallerescompose.taller04

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a financial transaction.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis()
)
