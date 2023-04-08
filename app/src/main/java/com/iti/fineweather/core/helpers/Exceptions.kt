package com.iti.fineweather.core.helpers

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.fineweather.R


abstract class AppException : Exception() {
    @get:Composable
    abstract val error: String
}

data class InvalidStateException(@StringRes val errorRes: Int) : AppException() {
    override val error: String
        @Composable
        get() = stringResource(errorRes)
}

data class InternetFetchException(@StringRes val fetchedResourceName: Int) : AppException() {
    override val error: String
        @Composable
        get() = stringResource(R.string.error_internet_fetch, stringResource(fetchedResourceName))
}

data class MissingValueException(@StringRes val valueName: Int) : AppException() {
    override val error: String
        @Composable
        get() = stringResource(R.string.error_missing_value, stringResource(valueName))
}
