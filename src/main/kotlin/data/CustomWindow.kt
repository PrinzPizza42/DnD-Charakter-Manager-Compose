package data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import data.WindowManager.LocalWindow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CustomWindow @OptIn(ExperimentalUuidApi::class) constructor(
    val uuid: Uuid = Uuid.random(),
    val title: MutableState<String>,
    val onCloseRequest: () -> Unit,
    val icon: Painter,
    val openTab: MutableState<Boolean>? = null,
    val openWindow: MutableState<Boolean> = mutableStateOf(true)
) {
    @OptIn(ExperimentalUuidApi::class)
    var content: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize().background(Color.White, RoundedCornerShape(10.dp))) {
            Text("Window ID: $uuid")
        }
    }

    @Composable
    fun draw() {
        Window(
            onCloseRequest = {
                onCloseRequest()
                openTab?.value = true
                openWindow.value = false
                WindowManager.removeWindow(this)
            },
            title = title.value,
            icon = icon,
            visible = if(openTab != null) !openTab.value && openWindow.value else openWindow.value
        ) {
            CompositionLocalProvider(LocalWindow provides window) {
                content()
            }
        }
    }

    fun close() {
        WindowManager.removeWindow(this)
        openWindow.value = false
    }
}