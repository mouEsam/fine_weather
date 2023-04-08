package com.iti.fineweather.features.common.di

import com.google.gson.*
import com.iti.fineweather.features.common.helpers.connectivity.ConnectivityHelper
import com.iti.fineweather.features.common.helpers.connectivity.ConnectivityHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
interface UtilsModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun provideConnectivityHelper(helper: ConnectivityHelperImpl): ConnectivityHelper

}
