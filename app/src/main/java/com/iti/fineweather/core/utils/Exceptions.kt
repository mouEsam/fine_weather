package com.iti.fineweather.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.AppException


val Exception.error: String
    @Composable
    get() {
        val e = this
        return if (e is AppException) {
            e.error
        } else {
            e.localizedMessage ?: e.message ?: stringResource(R.string.error_unknown)
        }
    }
