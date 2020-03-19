package com.udacity.asteroidradar.executors

import java.util.concurrent.Executor

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
//private val networkIOExecutor: Executor
class AppExecutors(
    private val diskIOExecutor: DiskIOThreadExecutor,
    private val mainThreadExecutor: MainThreadExecutor
) {

    fun diskIO(): Executor {
        return diskIOExecutor
    }

    fun mainThread(): Executor {
        return mainThreadExecutor
    }
}
