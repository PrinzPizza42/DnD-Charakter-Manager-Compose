package main

import Data.Read
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import main.Sidebar.sidebar

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    Row() {
        sidebar()

        MaterialTheme {
            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }
        }
    }

}

fun main() = application {
    Read.readData();

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1920.dp, 1200.dp)
        ),
        title = "DnD-Charakter-Manager",
        icon = null
    ) {
        App()
    }
}
