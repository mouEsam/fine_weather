package com.iti.fineweather.features.alerts.repositories

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iti.fineweather.core.helpers.InvalidStateException
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.services.local.WeatherAlertScheduler
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsDAO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.AdditionalMatchers
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherAlertsRepositoryTest {

    companion object {
        val EXISTING_ID = UUID.randomUUID()
        val NON_EXISTING_ID = UUID.randomUUID()
        val MOCK_USER_ALERT = UserWeatherAlert(
            startDate = LocalDate.now(),
            repetitionType = RepetitionType.DAILY,
            alarmEnabled = false,
            time = LocalTime.now(),
        )
    }

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var weatherAlertsDAO: WeatherAlertsDAO

    @Mock
    private lateinit var weatherAlertScheduler: WeatherAlertScheduler

    private lateinit var repository: WeatherAlertsRepository

    @Before
    fun setUp() = runBlocking {

        doReturn(flowOf(MOCK_USER_ALERT)).whenever(weatherAlertsDAO).getById(EXISTING_ID)
        doReturn(emptyFlow<UserWeatherAlert>()).whenever(weatherAlertsDAO).getById(NON_EXISTING_ID)
        doReturn(flowOf(listOf(MOCK_USER_ALERT))).whenever(weatherAlertsDAO).getAllActive()

        doReturn(null).whenever(weatherAlertsDAO).insertAll(anyVararg<UserWeatherAlert>())
        whenever(weatherAlertsDAO.insertAll(MOCK_USER_ALERT)).then { throw SQLiteConstraintException() }

        whenever(weatherAlertsDAO.updateAll(anyVararg<UserWeatherAlert>())).then {
            val alert = (it.arguments.first() as Array<*>).first() as UserWeatherAlert
            if (alert.id != MOCK_USER_ALERT.id) {
                throw SQLiteConstraintException()
            }
            Unit
        }

        repository = WeatherAlertsRepository(
            weatherAlertsDAO = weatherAlertsDAO,
            weatherAlertScheduler = weatherAlertScheduler,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun getWeatherAlertsFlow_getAllAlerts() = runTest {
        val result = repository.weatherAlertsFlow.first()
        assertThat(result, `is`(Resource.Success.Local(listOf(MOCK_USER_ALERT))))
    }

    @Test
    fun getAlert_existingId_returnsAlert() = runTest {
        val result = repository.getAlert(EXISTING_ID).first()
        assertThat(result, `is`(Resource.Success.Local(MOCK_USER_ALERT)))
    }

    @Test(expected = NoSuchElementException::class)
    fun getAlert_existingId_fails() = runTest {
        repository.getAlert(NON_EXISTING_ID).first()
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addAlert_existingAlert_failure() = runTest {
        repository.addAlert(MOCK_USER_ALERT)
    }

    @Test
    fun addAlert_newAlert_success() = runTest {
        val weatherAlert = mock<UserWeatherAlert>()

        repository.addAlert(weatherAlert)

        verify(weatherAlertsDAO, times(1)).insertAll(weatherAlert)
    }

    @Test
    fun removeAlert_existingAlert_success() = runTest {
        repository.removeAlert(MOCK_USER_ALERT)

        verify(weatherAlertsDAO, times(1)).updateAll(anyVararg<UserWeatherAlert>())
    }

    @Test(expected = SQLiteConstraintException::class)
    fun removeAlert_newAlert_failure() = runTest {
        val weatherAlert = MOCK_USER_ALERT.copy(id = UUID.randomUUID())

        repository.removeAlert(weatherAlert)
    }

    @Test(expected = InvalidStateException::class)
    fun removeAlert_removedAlert_failure() = runTest {
        val weatherAlert = mock<UserWeatherAlert>()
        doReturn(LocalDateTime.now()).whenever(weatherAlert).deletedAt

        repository.removeAlert(weatherAlert)
    }

    @Test
    fun setExhausted_existingAlert_success() = runTest {
        repository.setExhausted(MOCK_USER_ALERT)

        verify(weatherAlertsDAO, times(1)).updateAll(anyVararg<UserWeatherAlert>())
    }

    @Test(expected = SQLiteConstraintException::class)
    fun setExhausted_newAlert_failure() = runTest {
        val weatherAlert = MOCK_USER_ALERT.copy(id = UUID.randomUUID())

        repository.setExhausted(weatherAlert)
    }

    @Test(expected = InvalidStateException::class)
    fun setExhausted_exhaustedAlert_failure() = runTest {
        val weatherAlert = mock<UserWeatherAlert>()
        doReturn(true).whenever(weatherAlert).exhausted

        repository.setExhausted(weatherAlert)
    }

    @Test
    fun updateAlertAlarmEnabled_existingAlert_success() = runTest {
        repository.updateAlertAlarmEnabled(MOCK_USER_ALERT, true)

        verify(weatherAlertsDAO, times(1)).updateAll(anyVararg<UserWeatherAlert>())
    }

    @Test(expected = SQLiteConstraintException::class)
    fun updateAlertAlarmEnabled_newAlert_failure() = runTest {
        val weatherAlert = MOCK_USER_ALERT.copy(id = UUID.randomUUID())

        repository.updateAlertAlarmEnabled(weatherAlert, true)
    }
}