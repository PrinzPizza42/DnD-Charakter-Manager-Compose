package main

import Data.Read
import Main.Inventory
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import main.InventoryDisplay.displayEmptyDisplay
import main.InventoryDisplay.displayInv
import main.ScrollDisplay.scrollDisplay
import main.Sidebar.sidebar

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val selectedInventory = remember { mutableStateOf<Inventory?>(null) }

    Row(Modifier
        .fillMaxSize()

    ) {
        sidebar(selectedInventory = selectedInventory)
        val modifier = Modifier.weight(1f)

        if(selectedInventory.value != null) {
            displayInv(selectedInventory.value!!, modifier)
        }
        else {
            displayEmptyDisplay(modifier)
        }

        scrollDisplay(modifier)
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
