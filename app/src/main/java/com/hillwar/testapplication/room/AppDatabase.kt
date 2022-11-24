package com.hillwar.testapplication.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [PairQA::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pairDao(): PairDao?
}