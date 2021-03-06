package com.udacity.asteroidradar.repo

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

fun <T> LiveData<T>.observeX(owner: LifecycleOwner, observer: (T?) -> Unit) =
    observe(owner, Observer<T> { v -> observer.invoke(v) })

fun <X, Y> LiveData<X>.map(func: (X) -> Y) =
    Transformations.map(this, func)