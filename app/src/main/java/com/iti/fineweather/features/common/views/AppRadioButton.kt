package com.iti.fineweather.features.common.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.iti.fineweather.core.theme.LocalTheme


@Composable
fun AppRadioButton(
    title: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onSelected()
            },
            colors = RadioButtonDefaults.colors(LocalTheme.colors.main),
        )
        Text(
            text = title,
            modifier = Modifier
                .padding(start = LocalTheme.spaces.small)
                .clickable {
                    onSelected()
                }
        )
    }
}