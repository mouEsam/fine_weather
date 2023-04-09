package com.iti.fineweather.features.weather.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iti.fineweather.core.helpers.InternetFetchException
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.weather.models.RemoteWeatherResponse
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.isA
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import java.io.IOException
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var weatherRemoteService: WeatherRemoteService

    @Mock
    private lateinit var weatherData: RemoteWeatherResponse

    @Before
    fun setup() {
        reset(weatherRemoteService, weatherData)
    }

    @Test
    fun getWeatherData_serverAvailable_returnsLoaded() = runTest {
        whenever(weatherRemoteService.getWeather(
            latitude = any(),
            longitude = any(),
            language = anyOrNull(),
            units = anyOrNull(),
            excludes = anyOrNull(),
        )).doReturn(weatherData)
        val repository = WeatherRepository(weatherRemoteService, testDispatcher)

        val result = repository.getWeatherData(latitude = 0.0, longitude = 0.0, Locale.getDefault())

        assertThat(result, isA(Resource.Success.Remote::class.java))
    }

    @Test
    fun getWeatherData_serverUnAvailable_returnsError() = runTest {
        doAnswer { throw IOException() }.whenever(weatherRemoteService).getWeather(
            latitude = any(),
            longitude = any(),
            language = anyOrNull(),
            units = anyOrNull(),
            excludes = anyOrNull(),
        )
        val repository = WeatherRepository(weatherRemoteService, testDispatcher)
        val result = repository.getWeatherData(
            latitude = 0.0,
            longitude = 0.0,
            locale = Locale.getDefault(),
        )
        assertThat(result, isA(Resource.Error::class.java))
        assertThat(result.error, isA(InternetFetchException::class.java))
    }
}