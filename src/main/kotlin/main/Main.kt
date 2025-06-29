package main

import Data.Read
import Main.Inventory
import Main.ItemClasses.Item
import Main.Spell
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
import main.InvSelector.inventorySelector
import main.TabSelector.displayTabSelector

@Composable
@Preview
fun App() {
    val showItemDisplay = remember { mutableStateOf(false) }
    val itemDisplayItem = remember { mutableStateOf<Item?>(null) }

    val refreshTrigger = remember { mutableStateOf(0) }
    val selectedInventory = remember { mutableStateOf<Inventory?>(null) }

    val showInventory = remember { mutableStateOf(true) }
    val showScrollPanel = remember { mutableStateOf(true) }

    val showSortedInv = remember { mutableStateOf(false) }

    val showInvSelector = remember(refreshTrigger.value, selectedInventory.value) { mutableStateOf(selectedInventory.value == null) }

    if(showInvSelector.value) inventorySelector(selectedInventory, showInvSelector)

    if(!showInvSelector.value) {
        Box(Modifier.fillMaxSize()) {
            Row(Modifier
                .fillMaxSize()
            ) {
                val modifier = Modifier.weight(1f)

                displayTabSelector(showInventory, showScrollPanel, selectedInventory)

                if(selectedInventory.value != null && showInventory.value) {
                    displayInv(selectedInventory as MutableState<Inventory>, modifier, showItemDisplay, itemDisplayItem, refreshTrigger, showSortedInv)
                }

                if(showScrollPanel.value) {
                    scrollDisplay(modifier, selectedInventory.value!!, refreshTrigger, showScrollPanel)
                }

            }
            if (showItemDisplay.value) {
                showItemDisplayStructure(itemDisplayItem, selectedInventory, showItemDisplay, refreshTrigger)
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
