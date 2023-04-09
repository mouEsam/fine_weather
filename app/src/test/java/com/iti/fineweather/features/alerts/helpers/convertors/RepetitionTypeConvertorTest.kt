package com.iti.fineweather.features.alerts.helpers.convertors

import com.iti.fineweather.features.alerts.entities.RepetitionType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith


class RepetitionTypeConvertorTest {
    companion object {
        private val TYPE = RepetitionType.DAILY
        private val SUCCESS_NAME = RepetitionType.DAILY.name
        private val FAILURE_NAME = RepetitionType.DAILY.name + "_"
    }

    @Test
    fun toRepetitionType_nameExist_success() {
        val result = RepetitionTypeConvertor.toRepetitionType(SUCCESS_NAME)
        assertThat(result, `is`(TYPE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun toRepetitionType_nameMissing_failure() {
        RepetitionTypeConvertor.toRepetitionType(FAILURE_NAME)
    }

    @Test
    fun fromRepetitionType_getCorrectName() {
        val result = RepetitionTypeConvertor.fromRepetitionType(TYPE)
        assertThat(result, `is`(SUCCESS_NAME))
    }

}