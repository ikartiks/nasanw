package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.*
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.repo.FetchAsteroidWorker
import com.udacity.asteroidradar.repo.Repository
import com.udacity.asteroidradar.repo.UdacityDatabase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    fun fetchAsteroids() {
        val constraints =  Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()
        val uploadWorkRequest = PeriodicWorkRequestBuilder<FetchAsteroidWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("fetchAsteroids",ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest)
    }

    private var liveData: LiveData<List<Asteroid>>? = null
    private val mediatorLiveData = MediatorLiveData<List<Asteroid>>()

    fun getAsteroids(owner: LifecycleOwner): LiveData<List<Asteroid>> {
        liveData?.removeObservers(owner)
        val roomDatabase = UdacityDatabase.getInstance(app.applicationContext)
        liveData?.let { mediatorLiveData.removeSource(it) }
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        liveData = roomDatabase.asteroidDao().load(dateFormat.format(currentTime))
            .apply {
                mediatorLiveData.addSource(this) { resource ->
                    mediatorLiveData.value = resource
                }
            }
        return mediatorLiveData
    }

    fun fetchTodaysImage(imageView: ImageView) {
        val sharedPref = app.applicationContext.getSharedPreferences("preferences",Context.MODE_PRIVATE)
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val key = dateFormat.format(currentTime)
        if(sharedPref.contains(key)){
            Picasso.with(imageView.context).load(sharedPref.getString(key,"")).into(imageView)
            return
        }
        val repo = Repository(app.applicationContext)
        repo.fetchTodaysImage {
            if (it.mediaType == "image"){
                val editor = sharedPref.edit()
                Picasso.with(imageView.context).load(it.url).into(imageView)
                editor.putString(key,it.url)
                editor.apply()
            }

        }
    }


}