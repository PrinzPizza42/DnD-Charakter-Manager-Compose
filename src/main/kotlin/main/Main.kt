package main

import Data.Read
import Main.Inventory
import Main.ItemClasses.Item
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import main.InventoryDisplay.displayEmptyDisplay
import main.InventoryDisplay.displayInv
import main.InventoryDisplay.showItemDisplayStructure
import main.ScrollDisplay.scrollDisplay
import main.Sidebar.sidebar

@Composable
@Preview
fun App() {
    val selectedInventory = remember { mutableStateOf<Inventory?>(null) }

    val showItemDisplay = remember { mutableStateOf(false) }
    val itemDisplayItem = remember { mutableStateOf<Item?>(null) }

    val refreshTrigger = remember { mutableStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        Row(Modifier
            .fillMaxSize()
        ) {
            sidebar(selectedInventory = selectedInventory)
            val modifier = Modifier.weight(1f)

            if(selectedInventory.value != null) {
                displayInv(selectedInventory.value!!, modifier, showItemDisplay, itemDisplayItem, refreshTrigger)
            }
            else {
                displayEmptyDisplay(modifier)
            }

            scrollDisplay(modifier)


        }
        if (showItemDisplay.value) {
            showItemDisplayStructure(itemDisplayItem, selectedInventory, showItemDisplay, refreshTrigger)
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
