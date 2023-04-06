package com.iti.fineweather.features.common.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import timber.log.Timber

class ActionLock internal constructor(
    val isLocked: Boolean,
    val lock: (suspend () -> Unit) -> Unit,
)

private val LocalLock: ProvidableCompositionLocal<ActionLock> = compositionLocalOf { error("not provided") }

@Composable
fun ActionLock(
    modifier: Modifier = Modifier,
    content: @Composable (ActionLock) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isLocked by remember { mutableStateOf(false) }
    val lock by remember {
        derivedStateOf {
            ActionLock(
                isLocked = isLocked,
                lock = { action ->
                   coroutineScope.launch {
                       isLocked = true
                       try {
                           action()
                       } catch (e: Exception) {
                          Timber.e(e)
                       } finally {
                           isLocked = false
                       }
                   }
                },
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