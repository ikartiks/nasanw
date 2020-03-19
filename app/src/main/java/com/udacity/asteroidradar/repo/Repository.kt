package com.udacity.asteroidradar.repo

import android.content.Context
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.API
import com.udacity.asteroidradar.api.RetrofitInstance
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.executors.AppExecutors
import com.udacity.asteroidradar.executors.DiskIOThreadExecutor
import com.udacity.asteroidradar.executors.MainThreadExecutor
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class Repository(val context: Context) {

    fun fetchAsteroids() {
        val service: API = RetrofitInstance.retrofitInstance.create(
            API::class.java
        )
        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val startDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = dateFormat.format(calendar.time)
        val call = service.getAsteroidsFor7Days(startDate, endDate)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()!!.string())
                    val arrayList = parseAsteroidsJsonResult(json)
                    val roomDatabase = UdacityDatabase.getInstance(context)
                    val appExecutors = AppExecutors(DiskIOThreadExecutor(), MainThreadExecutor())
                    appExecutors.diskIO().execute {
                        roomDatabase.clearAll()
                        roomDatabase.asteroidDao().insertAsteroids(*arrayList.toTypedArray())
                    }
                }
            }

        })
    }

    fun fetchTodaysImage(callback: (PictureOfDay) -> Unit) {
        val service: API = RetrofitInstance.retrofitInstance.create(
            API::class.java
        )
        val call = service.getPicOfTheDay()

        call.enqueue(object : Callback<PictureOfDay> {
            override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
            }

            override fun onResponse(call: Call<PictureOfDay>, response: Response<PictureOfDay>) {
                if (response.isSuccessful)
                    callback.invoke(response.body()!!)
            }

        })
    }


}