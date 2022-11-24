package com.hillwar.testapplication

import android.app.Application
import androidx.room.Room
import com.hillwar.testapplication.room.AppDatabase


class MyApplication: Application() {

    private var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database")
            .build()
    }

    fun getDatabase(): AppDatabase? {
        return database
    }

    companion object {
        lateinit var instance: MyApplication
            private set
        var id = 0L
    }
}