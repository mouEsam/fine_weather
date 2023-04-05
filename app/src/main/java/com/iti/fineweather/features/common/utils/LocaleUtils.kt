package com.iti.fineweather.features.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@Composable
fun rememberLocalizedDateTimeFormatter(pattern: String): DateTimeFormatter {
    val locale = LocalConfiguration.current.locales[0]
    return remember(key1 = pattern) { DateTimeFormatterBuilder().appendPattern(pattern).toFormatter(locale) }
}
