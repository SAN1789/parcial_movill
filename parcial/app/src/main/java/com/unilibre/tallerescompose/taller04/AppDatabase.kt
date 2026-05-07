package com.unilibre.tallerescompose.taller04

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Main Database class for the application.
 */
@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
