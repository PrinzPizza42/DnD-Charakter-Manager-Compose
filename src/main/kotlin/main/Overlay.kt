package main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

object Overlay {
    val activeOverlay = mutableStateOf<(@Composable () -> Unit)?>(null)
    val closeOverlay: () -> Unit = { activeOverlay.value = null }
    val showOverlay: (@Composable () -> Unit) -> Unit = { content ->
        activeOverlay.value = content
    }
}