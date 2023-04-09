package com.iti.fineweather.features.alerts.services.local

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import com.iti.fineweather.core.controllers.FineWeatherApp
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.common.services.local.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = FineWeatherApp::class, manifest = Config.NONE)
class WeatherAlertsDAOTest {

    companion object {
        val MOCK_USER_ALERT = UserWeatherAlert(
            createdAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis()),
                ZoneId.systemDefault()
            ),
            startDate = LocalDate.now(),
            repetitionType = RepetitionType.DAILY,
            alarmEnabled = false,
            time = LocalTime.ofSecondOfDay(LocalTime.now().toSecondOfDay().toLong()),
        )
    }

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    private lateinit var weatherAlertsDAO: WeatherAlertsDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<FineWeatherApp>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        weatherAlertsDAO = database.weatherAlertsDao()
    }

    @After
    fun breakDown() {
        database.close()
    }

    @Test
    fun getAll_empty_returnsNone() = runTest {
        val result = weatherAlertsDAO.getAll().first()

        assertThat(result.size, `is`(0))
    }

    @Test
    fun getAll_oneItem_returnsOne() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)

        val result = weatherAlertsDAO.getAll().first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun getAll_oneNotActive_returnsOne() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT.copy(deletedAt = LocalDateTime.now()))

        val result = weatherAlertsDAO.getAll().first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun getAllActive_empty_returnsNone() = runTest {
        val result = weatherAlertsDAO.getAll().first()

        assertThat(result.size, `is`(0))
    }

    @Test
    fun getAllActive_oneItem_returnsOne() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)

        val result = weatherAlertsDAO.getAllActive().first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun getAllActive_oneNotActive_returnsNone() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT.copy(deletedAt = LocalDateTime.now()))

        val result = weatherAlertsDAO.getAllActive().first()
        assertThat(result.size, `is`(0))
    }

    @Test
    fun getById_itemExists_returnsItem() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)

        val result = weatherAlertsDAO.getById(MOCK_USER_ALERT.id).first()
        assertThat(result, `is`(MOCK_USER_ALERT))
    }

    @Test
    fun getById_notExists_returnsNull() = runTest {
        val result = weatherAlertsDAO.getById(MOCK_USER_ALERT.id).firstOrNull()
        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun insertAll_newItem_saved() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)

        val result = weatherAlertsDAO.getAll().first()
        assertThat(result.first(), `is`(MOCK_USER_ALERT))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertAll_existingItem_throws() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)
    }

    @Test
    fun updateAll_existingItem_saved() = runTest {
        weatherAlertsDAO.insertAll(MOCK_USER_ALERT)

        val updated = MOCK_USER_ALERT.copy(exhausted = true)
        weatherAlertsDAO.updateAll(updated)

        val result = weatherAlertsDAO.getAll().first()
        assertThat(result.first(), `is`(updated))
    }

}
