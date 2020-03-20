package com.udacity.asteroidradar.repo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroids(vararg users: Asteroid)

    @Query("SELECT * FROM asteroid_table where closeApproachDate >= :today ORDER BY closeApproachDate ASC")
    fun load(today:String): LiveData<List<Asteroid>>

    @Query("DELETE FROM asteroid_table")
    fun clear()
}