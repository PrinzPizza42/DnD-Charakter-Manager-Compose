package main

import Data.Read
import Main.Inventory
import Main.ItemClasses.*
import Main.ItemClasses.Weapons.LongRangeWeapon
import Main.ItemClasses.Weapons.ShortRangeWeapon
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import main.InventoryDisplay.displayInv
import main.InventoryDisplay.showItemDisplayStructure
import main.ScrollDisplay.scrollDisplay
import main.InvSelector.inventorySelector
import main.TabSelector.displayTabSelector
import org.jetbrains.skiko.Cursor

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
            val currentItems = selectedInventory.value?.items ?: ArrayList<Item?>()
            val processedItems = if (showSortedInv.value) {
                currentItems.sortedWith(compareBy { item -> typePriority[item::class] ?: Int.MAX_VALUE })
            } else {
                currentItems
            }
            processedItems.take(totalSlots) + List(totalSlots - processedItems.size) { EmptySlot() }
        }
    }

    //if index -1 add first
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

    val removeItem: (Item) -> Unit = { item ->
        selectedInventory.value?.let { inv ->
            val newItems = ArrayList(inv.items)
            val newInv = Inventory(inv)
            newItems.remove(item)
            newInv.items.clear()
            newInv.items.addAll(newItems)
            selectedInventory.value = newInv
            println("removed item " + item.name)
        }
    }

    //if index is -1 add last
    val addItemAtIndex: (Item, Item) -> Unit = { item, hoveredItem ->
        selectedInventory.value?.let { inv ->
            val newItems = ArrayList(inv.items)
            val newInv = Inventory(inv)
            if(hoveredItem is EmptySlot) {
                newItems.addLast(item)
            }
            else {
                val dropIndex = newItems.indexOf(hoveredItem)
                if (dropIndex != -1) {
                    newItems.add(dropIndex, item)
                } else {
                    newItems.addLast(item)
                }
            }
            newInv.items.clear()
            newInv.items.addAll(newItems)
            selectedInventory.value = newInv
        }
    }

    if(showInvSelector.value) inventorySelector(selectedInventory)

    if(!showInvSelector.value) {
        Box(Modifier.fillMaxSize()) {
            Row(Modifier
                .fillMaxSize()
            ) {
                val showInvAnimationEndInv = remember { mutableStateOf(true) }
                val weightInv = animateFloatAsState(
                    if(showInventory.value) 1f else 0.01f,
                    label = "weight",
                    finishedListener = {
                        showInvAnimationEndInv.value = showInventory.value
                    }
                )

                val showInvAnimationEndSpells = remember { mutableStateOf(true) }
                val weightSpells = animateFloatAsState(
                    if(showScrollPanel.value) 1f else 0.01f,
                    label = "weight",
                    finishedListener = {
                        showInvAnimationEndSpells.value = showScrollPanel.value
                    }
                )

                val modifierInv = if(showInvAnimationEndInv.value) Modifier.weight(weightInv.value) else Modifier.width(0.dp)
                val modifierSpells = if(showInvAnimationEndSpells.value) Modifier.weight(weightSpells.value) else Modifier.width(0.dp)

                displayTabSelector(showInventory, showScrollPanel, selectedInventory, showInvAnimationEndInv, showInvAnimationEndSpells)

                displayInv(selectedInventory, modifierInv, showItemDisplay, itemDisplayItem, showSortedInv, items, totalSlots, 100.dp, updateInventory, refreshInv, removeItem, addItemAtIndex)

                scrollDisplay(modifierSpells, selectedInventory.value!!, showScrollPanel)

                val backGroundColor = animateColorAsState(
                    if(!showInvAnimationEndSpells.value && !showInvAnimationEndInv.value) Color.Gray else Color.Black,
                    label = "background color",
                    animationSpec = tween(300)
                )

                if(!showInvAnimationEndSpells.value && !showInvAnimationEndInv.value) {
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(backGroundColor.value)
                    ) {
                        Text(
                            text = "Keine Panels ausgewählt",
                            modifier =  Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = 40.sp
                        )
                    }
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
