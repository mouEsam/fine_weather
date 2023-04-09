package com.iti.fineweather.features.alerts.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iti.fineweather.core.helpers.InvalidStateException
import com.iti.fineweather.core.helpers.MissingValueException
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.repositories.WeatherAlertsRepository
import com.iti.fineweather.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class NewWeatherAlertViewModelTest {

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var alertsRepository: WeatherAlertsRepository

    @Test
    fun getAlert_initially_returnsNull() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = viewModel.alert.first()

        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun getOperationState_initially_returnInitial() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = viewModel.operationState.first()

        assertThat(result, isA(UiState.Initial::class.java))
    }

    @Test
    fun newAlert_addsAlertState() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        viewModel.newAlert()
        advanceUntilIdle()

        val result = viewModel.alert.first()

        assertThat(result, `is`(notNullValue()))
    }

    @Test
    fun submit_emitsLoading() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = async { viewModel.operationState.drop(1).first() }

        advanceUntilIdle()
        viewModel.submit()

        assertThat(result.await(), isA(UiState.Loading::class.java))
    }

    @Test
    fun submit_noAlert_fails() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.submit()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Error::class.java))
        assertThat(resultState.error, isA(InvalidStateException::class.java))
    }

    @Test
    fun submit_invalidAlert_fails() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.submit()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Error::class.java))
        assertThat(resultState.error, isA(MissingValueException::class.java))
    }

    @Test
    fun submit_validAlert_success() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        viewModel.updateStartDate(LocalDate.now())
        viewModel.updateTime(LocalTime.now())
        viewModel.updateAlarmEnabled(true)

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.submit()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }

    @Test
    fun validate_missingRepetitionType_returnsFalse() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateStartDate(LocalDate.now())
        viewModel.updateTime(LocalTime.now())
        viewModel.updateAlarmEnabled(true)

        advanceUntilIdle()
        val result = viewModel.validate()

        assertThat(result, `is`(false))
    }

    @Test
    fun validate_missingStartDate_returnsFalse() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        viewModel.updateTime(LocalTime.now())
        viewModel.updateAlarmEnabled(true)

        advanceUntilIdle()
        val result = viewModel.validate()

        assertThat(result, `is`(false))
    }

    @Test
    fun validate_missingTime_returnsFalse() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        viewModel.updateStartDate(LocalDate.now())
        viewModel.updateAlarmEnabled(true)

        advanceUntilIdle()
        val result = viewModel.validate()

        assertThat(result, `is`(false))
    }

    @Test
    fun validate_endBeforeStart_returnsFalse() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        viewModel.updateStartDate(LocalDate.now().plusDays(1))
        viewModel.updateEndDate(LocalDate.now())
        viewModel.updateTime(LocalTime.now())
        viewModel.updateAlarmEnabled(true)

        advanceUntilIdle()
        val result = viewModel.validate()

        assertThat(result, `is`(false))
    }

    @Test
    fun validate_missingAlarmState_returnsFalse() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        viewModel.updateStartDate(LocalDate.now())
        viewModel.updateTime(LocalTime.now())

        advanceUntilIdle()
        val result = viewModel.validate()

        assertThat(result, `is`(false))
    }

    @Test
    fun validate_validAlert_success() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        viewModel.updateStartDate(LocalDate.now())
        viewModel.updateTime(LocalTime.now())
        viewModel.updateAlarmEnabled(true)

        advanceUntilIdle()
        val result = viewModel.validate()

        assertThat(result, `is`(true))
    }

    @Test
    fun resetError() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = async { viewModel.operationState.drop(3).first() }

        advanceUntilIdle()
        viewModel.submit()
        advanceUntilIdle()
        viewModel.resetError()
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Initial::class.java))
    }

    @Test
    fun update_emitLoading() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = async { viewModel.operationState.drop(1).first() }

        advanceUntilIdle()
        viewModel.update { it }
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loading::class.java))
    }

    @Test
    fun update_noAlert_fails() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.update { it }
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Error::class.java))
        assertThat(resultState.error, isA(InvalidStateException::class.java))
    }

    @Test
    fun update_withAlert_success() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.update { it }
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }

    @Test
    fun updateRepetitionType() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.updateRepetitionType(RepetitionType.SINGLE)
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }

    @Test
    fun updateAlarmEnabled() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.updateAlarmEnabled(true)
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }

    @Test
    fun updateTime() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.updateTime(LocalTime.now())
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }

    @Test
    fun updateStartDate() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.updateStartDate(LocalDate.now())
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }

    @Test
    fun updateEndDate() = runTest {
        val viewModel = NewWeatherAlertViewModel(alertsRepository)
        viewModel.newAlert()

        val result = async { viewModel.operationState.drop(2).first() }

        advanceUntilIdle()
        viewModel.updateEndDate(LocalDate.now())
        advanceUntilIdle()

        val resultState = result.await()
        assertThat(resultState, isA(UiState.Loaded::class.java))
    }
}