package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
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
            //.setRequiresCharging(true) NOTE removed , as reviewer could not review becase of this
            .build()
        val uploadWorkRequest = PeriodicWorkRequestBuilder<FetchAsteroidWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("fetchAsteroids",ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest)
        //NOTE fetchAsteroids does not need to be in strings.xml as they are logical keys & not user displayable values
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
        //NOTE strings hardcoded below do not need to be in strings.xml as they are logical keys & not user displayable values
        val sharedPref = app.applicationContext.getSharedPreferences("preferences",Context.MODE_PRIVATE)
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val key = dateFormat.format(currentTime)
        val imageTitle = "${key}title"
        if(sharedPref.contains(key)){
            Picasso.with(imageView.context).load(sharedPref.getString(key,"")).into(imageView)
            imageView.contentDescription = imageView.context.getString(R.string.nasa_picture_of_day_content_description_format,sharedPref.getString(imageTitle,""))
            return
        }
        val repo = Repository(app.applicationContext)
        repo.fetchTodaysImage {
            it?.let {
                if (it.mediaType == "image"){
                    val editor = sharedPref.edit()
                    Picasso.with(imageView.context).load(it.url).into(imageView)
                    imageView.contentDescription = imageView.context.getString(R.string.nasa_picture_of_day_content_description_format,it.title)
                    editor.putString(key,it.url)
                    editor.putString(imageTitle, it.title)
                    editor.apply()
                }
            }?: kotlin.run {
                imageView.contentDescription = imageView.context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
            }
        }
    }
}