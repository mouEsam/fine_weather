package com.iti.fineweather.core.controllers

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class FineWeatherApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }

}