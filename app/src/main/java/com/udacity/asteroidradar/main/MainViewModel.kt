package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.repo.Repository
import com.udacity.asteroidradar.repo.UdacityDatabase

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    fun fetchAsteroids(){
        val repo = Repository( app.applicationContext)
        repo.fetchAsteroids()
    }

    private var liveData: LiveData<List<Asteroid>>? = null
    private val mediatorLiveData = MediatorLiveData<List<Asteroid>>()

    fun getAsteroids(owner: LifecycleOwner): LiveData<List<Asteroid>> {
        liveData?.removeObservers(owner)
        val roomDatabase = UdacityDatabase.getInstance(app.applicationContext)
        liveData?.let { mediatorLiveData.removeSource(it) }
        liveData = roomDatabase.asteroidDao().load()
            .apply {
                mediatorLiveData.addSource(this) { resource ->
                    mediatorLiveData.value = resource
                }
            }
        return mediatorLiveData
    }
}