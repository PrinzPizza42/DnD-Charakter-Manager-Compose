package data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import disk.ImageLoader
import data.CustomWindow
import kotlin.uuid.ExperimentalUuidApi

object WindowManager {
    val mainWindowTitle = mutableStateOf("DnD-Fight-Manager-KMP")
    val iconRessource = ImageLoader.loadImageFromResources("icon.png").get().toPainter()
    val windowList = mutableStateListOf<CustomWindow>()

    @OptIn(ExperimentalUuidApi::class)
    fun openNewWindow(
        onCloseRequest: () -> Unit,
        content: @Composable (() -> Unit)? = null,
        icon: Painter = iconRessource,
        title: MutableState<String> = mainWindowTitle
    ): CustomWindow {
        val newWindow = CustomWindow(
            onCloseRequest = onCloseRequest,
            icon = icon,
            title = title,
        )
        if(content != null) newWindow.content = content

        windowList.add(newWindow)

        return newWindow
    }

    fun removeWindow(window: CustomWindow) {
        windowList.remove(window)
    }
}