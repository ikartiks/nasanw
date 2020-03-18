package com.udacity.asteroidradar.executors

import androidx.annotation.NonNull
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DiskIOThreadExecutor : Executor {

    private val mDiskIO: Executor

    init {
        mDiskIO = Executors.newSingleThreadExecutor()
    }

    override fun execute(@NonNull runnable: Runnable) {
        mDiskIO.execute(runnable)
    }
}