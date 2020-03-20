package com.udacity.asteroidradar.repo

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.executors.AppExecutors
import com.udacity.asteroidradar.executors.DiskIOThreadExecutor
import com.udacity.asteroidradar.executors.MainThreadExecutor

class FetchAsteroidWorker(private val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams){

    override fun doWork(): Result {
        var result = Result.retry()
        val repo = Repository(appContext)
        repo.fetchAsteroidsFromWorker {
            it?.let {
                val roomDatabase = UdacityDatabase.getInstance(appContext)
                val appExecutors = AppExecutors(DiskIOThreadExecutor(), MainThreadExecutor())
                result = Result.success()
                appExecutors.diskIO().execute {
                    roomDatabase.clearAll()
                    roomDatabase.asteroidDao().insertAsteroids(*it.toTypedArray())
                }
            }
        }
        return result
    }
}