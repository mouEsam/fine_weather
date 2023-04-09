package com.iti.fineweather.features.alerts.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iti.fineweather.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class NewWeatherAlertViewModelTest {

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun getAlert() {
    }

    @Test
    fun getOperationState() {
    }

    @Test
    fun newAlert() {
    }

    @Test
    fun submit() {
    }

    @Test
    fun validate() {
    }

    @Test
    fun resetError() {
    }

    @Test
    fun updateRepetitionType() {
    }

    @Test
    fun updateAlarmEnabled() {
    }

    @Test
    fun updateTime() {
    }

    @Test
    fun updateStartDate() {
    }

    @Test
    fun updateEndDate() {
    }
}