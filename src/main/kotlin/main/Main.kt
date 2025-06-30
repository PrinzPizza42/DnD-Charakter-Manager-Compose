package main

import Data.Read
import Main.Inventory
import Main.ItemClasses.*
import Main.ItemClasses.Weapons.LongRangeWeapon
import Main.ItemClasses.Weapons.ShortRangeWeapon
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

    val totalSlots = 50

    val refreshInv = remember { mutableStateOf(false) }


    val typePriority = mapOf(
        ShortRangeWeapon::class to 0,
        LongRangeWeapon::class to 1,
        Potion::class to 2,
        Consumable::class to 3,
        Miscellaneous::class to 4
    )

    val items by remember(selectedInventory.value?.uuid, selectedInventory.value, showSortedInv.value) {
        derivedStateOf {
            val currentItems = selectedInventory.value?.items ?: emptyList()
            val processedItems = if (showSortedInv.value) {
                currentItems.sortedWith(compareBy { item -> typePriority[item::class] ?: Int.MAX_VALUE })
            } else {
                currentItems
            }
            processedItems.take(totalSlots) + List(totalSlots - processedItems.size) { EmptySlot() }
        }
    }

    val updateInventory: (Item) -> Unit = { updatedItem ->
        selectedInventory.value?.let { inv ->
            val newItems = ArrayList(inv.items)
            val index = newItems.indexOfFirst { it.uuid == updatedItem.uuid }
            val newInv = Inventory(inv)
            if (index != -1) {
                newItems[index] = updatedItem
            } else {
                newItems.add(0, updatedItem)
            }
            newInv.items.clear()
            newInv.items.addAll(newItems)
            selectedInventory.value = newInv
        }
    }

    if(showInvSelector.value) inventorySelector(selectedInventory, showInvSelector)

    if(!showInvSelector.value) {
        Box(Modifier.fillMaxSize()) {
            Row(Modifier
                .fillMaxSize()
            ) {
                val modifier = Modifier.weight(1f)

                displayTabSelector(showInventory, showScrollPanel, selectedInventory)

                if(selectedInventory.value != null && showInventory.value) {
                    displayInv(selectedInventory, modifier, showItemDisplay, itemDisplayItem, showSortedInv, items, totalSlots, 100.dp, updateInventory, refreshInv)
                }

                if(showScrollPanel.value) {
                    scrollDisplay(modifier, selectedInventory.value!!, showScrollPanel)
                }
            }
            if (showItemDisplay.value) {
                showItemDisplayStructure(itemDisplayItem, showItemDisplay, updateInventory, refreshInv)
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
