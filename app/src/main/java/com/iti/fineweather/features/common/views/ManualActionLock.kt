package com.iti.fineweather.features.common.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class ManualActionLock internal constructor(
    val isLocked: Boolean,
    val lock: () -> Unit,
    val unLock: () -> Unit,
)

private val LocalLock: ProvidableCompositionLocal<ManualActionLock> = compositionLocalOf { error("not provided") }

@Composable
fun ManualActionLock(
    modifier: Modifier = Modifier,
    content: @Composable (ManualActionLock) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isLocked by remember { mutableStateOf(false) }
    val lock by remember {
        derivedStateOf {
            ManualActionLock(
                isLocked = isLocked,
                lock = { isLocked = true },
                unLock = { isLocked = false },
            )
        }
    }

    CompositionLocalProvider(
        LocalLock provides lock,
    ) {
        Box(
            modifier = Modifier.clickable(
                enabled = isLocked,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            )
        ) {
            content(lock)
        }
    }
}