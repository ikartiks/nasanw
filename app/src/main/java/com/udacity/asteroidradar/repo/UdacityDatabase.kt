package com.udacity.asteroidradar.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.Asteroid

@Database(entities = [Asteroid::class], version = 1, exportSchema = true)
abstract class UdacityDatabase : RoomDatabase() {
    abstract fun asteroidDao(): AsteroidDao


    companion object {
        fun create(context: Context): UdacityDatabase =
            Room.databaseBuilder(context, UdacityDatabase::class.java, "udacity")
                .build()

        @Volatile
        private var instance: UdacityDatabase? = null // Singleton instantiation

        fun getInstance(context: Context): UdacityDatabase {
            return instance ?: synchronized(this) {
                instance ?: create(context).also { instance = it }
            }
        }
    }

    fun clearAll() {
        asteroidDao().clear()
    }
}