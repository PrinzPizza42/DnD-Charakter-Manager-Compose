package main

import Data.ImageLoader
import Data.Read
import Main.Inventory
import Main.ItemClasses.*
import Main.ItemClasses.Weapons.LongRangeWeapon
import Main.ItemClasses.Weapons.ShortRangeWeapon
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
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

@Composable
@Preview
fun App(window: ComposeWindow) {
    val showItemDisplay = remember { mutableStateOf(false) }
    val itemDisplayItem = remember { mutableStateOf<Item?>(null) }

    val selectedInventory = remember { mutableStateOf<Inventory?>(null) }

    val showInventory = remember { mutableStateOf(true) }
    val showScrollPanel = remember { mutableStateOf(true) }

    val showSortedInv = remember { mutableStateOf(false) }

    val showInvSelector = remember(selectedInventory.value) { mutableStateOf(selectedInventory.value == null) }

    val totalSlots = 50

    val refreshInv = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val typePriority = mapOf(
        ShortRangeWeapon::class to 0,
        LongRangeWeapon::class to 1,
        Armor::class to 2,
        Potion::class to 3,
        Consumable::class to 4,
        Miscellaneous::class to 5
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
        val sectionSwitch = remember { mutableStateOf(true) } // true = inv & spells; false = char details

        Box(Modifier.fillMaxSize()) {
            // Inv & Spells
            section(
                showInventory,
                showScrollPanel,
                {
                    displayInv(
                        selectedInventory,
                        Modifier.fillMaxSize(),
                        showItemDisplay,
                        itemDisplayItem,
                        showSortedInv,
                        items,
                        totalSlots,
                        100.dp,
                        updateInventory,
                        refreshInv,
                        removeItem,
                        addItemAtIndex,
                        window
                    )
                },
                {
                    scrollDisplay(
                        Modifier.fillMaxSize(),
                        selectedInventory.value!!,
                        showScrollPanel
                    )
                },
                {
                    displayTabSelector(showInventory, showScrollPanel, selectedInventory)
                }
            )

            if (showItemDisplay.value) {
                showItemDisplayStructure(itemDisplayItem, showItemDisplay, updateInventory, refreshInv, focusManager, window)
            }
        }
    }
}

@Composable
fun section(
    showTab1: MutableState<Boolean>,
    showTab2: MutableState<Boolean>,
    contentTab1: @Composable () -> Unit,
    contentTab2: @Composable () -> Unit,
    contentTabSelector: @Composable () -> Unit
) {
    val animationSpec = tween<Float>(300, 0)
    val tab1Weight by animateFloatAsState(
        targetValue = if (showTab1.value) 1f else 0.0001f,
        animationSpec = animationSpec
    )
    val tab2Weight by animateFloatAsState(
        targetValue = if (showTab2.value) 1f else 0.0001f,
        animationSpec = animationSpec
    )
    val emptyWeight by animateFloatAsState(
        targetValue = if (!showTab1.value && !showTab2.value) 1f else 0.0001f,
        animationSpec = animationSpec
    )

    Row(Modifier
        .fillMaxSize()
    ) {
        contentTabSelector()

        Box(Modifier.weight(tab1Weight)) {
            contentTab1()
        }

        Box(
            Modifier
                .weight(emptyWeight)
        ) {
            if (emptyWeight > 0.01f) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {
                    Text(
                        text = "Keine Panels ausgewählt",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp
                    )
                }
            }
        }

        Box(Modifier.weight(tab2Weight)) {
            contentTab2()
        }
    }
}

fun main() = application {
    val icon = remember { ImageLoader.loadImageFromResources("icon.png").get().toPainter() }

    Read.readData()

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1920.dp, 1200.dp)
        ),
        title = "DnD-Charakter-Manager",
        icon = icon
    ) {
        App(window)
    }
}

@Composable
fun getFloatInputOverlay(
    modifier: Modifier,
    startValue: Float,
    text: String,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier
    ) {
        val input = remember { mutableStateOf(TextFieldValue(startValue.toString())) }
        var isError by remember { mutableStateOf(false) }

        TextField(
            value = input.value,
            onValueChange = {
                input.value = it
                isError = it.text.toFloatOrNull() == null
            },
            modifier = Modifier
                .fillMaxWidth()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.Enter -> {
                                if(!isError) onConfirm(input.value.text.toFloat())
                                true
                            }
                            Key.Escape -> {
                                onDismiss()
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                }
            ,
            label = {
                Text(text)
            },
            singleLine = true,
            isError = isError
        )
    }
}
