package main

import Main.Inventory
import Main.ItemClasses.Consumable
import Main.ItemClasses.EmptySlot
import Main.ItemClasses.Item
import Main.ItemClasses.Miscellaneous
import Main.ItemClasses.Potion
import Main.ItemClasses.Weapons.ShortRangeWeapon
import Main.ItemClasses.Weapons.Weapon
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import java.util.UUID

object InventoryDisplay {

    @Composable
    fun displayEmptyDisplay(modifier: Modifier) {
        Box (modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
        ) {
            Text("Wähle ein Inventar aus")
        }
        //TODO set closed backpack image here and no content (maybe with "Wähle einen Charakter aus" text with blurred background?)
    }

    @Composable
    fun displayInv(inv: MutableState<Inventory>, modifier: Modifier, showItemDisplay: MutableState<Boolean>, itemDisplayItem: MutableState<Item?>, refreshTrigger: MutableState<Int>) {
        //TODO set opened backpack image as background here (maybe split in three pieces so it does not stretch when the width changes?)
        //TODO set randomly selected scenery behind backpack
        val int = refreshTrigger.value

        Box(modifier.fillMaxHeight()) {
            Column (modifier
                .fillMaxHeight()
            ) {
                sceneryAndBackPackTop(showItemDisplay)
                backPack(inv, refreshTrigger, showItemDisplay, itemDisplayItem)
            }
        }
    }

    @Composable
    fun sceneryAndBackPackTop(showItemDisplay: MutableState<Boolean>) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            //Background
            Box(
                Modifier
                    .zIndex(0f)
                    .background(Color.Green)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
            }

            //Foreground
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .zIndex(1f)
                        .weight(1f)
                        .fillMaxWidth()
                )
                {
                }
                Box(
                    Modifier
                        .zIndex(1f)
                        .background(Color.LightGray)
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
                {
                }
            }

            //Sorting
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .zIndex(2f)
                        .weight(1f)
                        .fillMaxWidth()
                )
                {
                }
                Box(
                    Modifier
                        .zIndex(2f)
                        .background(Color.Transparent)
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
                {
                    val options = listOf("Eigene Sortierung", "Nach Klasse")
                    var selectedOption by remember { mutableStateOf(options[0]) }
                    Row {
                        options.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f)
                            ) {
                                RadioButton(
                                    selected = (option == selectedOption),
                                    onClick = { selectedOption = option }
                                )
                                Text(option)
                            }
                        }

                        Button(
                            onClick = {
                                println("adding item")
                                showItemDisplay.value = true
                            },
                            content = {
                                Text("+")
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .height(75.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun showItemDisplayStructure(itemDisplayItem: MutableState<Item?>, selectedInventory: MutableState<Inventory?>, showItemDisplay: MutableState<Boolean>, refreshTrigger: MutableState<Int>) {
        val classes = listOf("Nahkampf-Waffe", "Fernkampf-Waffe", "Verbrauchbares", "Trank", "Verschiedenes")
        var selectedClass by remember { mutableStateOf(classes[0]) }
        val hasSelected = remember { mutableStateOf(false) }

        val int = refreshTrigger.value

        //InvDisplay overlay
        Box(
            Modifier
                .zIndex(10f)
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        showItemDisplay.value = false
                        itemDisplayItem.value = null
                        refreshTrigger.value++
                        itemDisplayItem.value = itemDisplayItem.value
                    })
                }
        ) {
            //ItemDisplay
            Box(
                Modifier
                    .align(Alignment.Center)
                    .zIndex(11f)
                    .size(1200.dp, 700.dp)
            ) {
                //Background
                Row(
                    Modifier
                        .zIndex(11f)
                        .background(Color.White)
                        .fillMaxSize()
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                    ) {

                    }

                    Box(
                        Modifier
                            .weight(1f)
                    ) {
                    }
                }

                //Foreground
                Row(
                    Modifier
                        .zIndex(12f)
                        .fillMaxSize()
                ) {
                    //Item stats
                    Box(
                        Modifier
                            .weight(1f)
                    ) {
                        //Item Create
                        if (itemDisplayItem.value == null && !hasSelected.value) {
                            Column(
                                Modifier
                                    .align(Alignment.Center)
                                    .pointerInput(Unit) {
                                        detectTapGestures {}
                                    }
                            ) {
                                Text("Wähle eine Klasse für dein neues Item aus")
                                Column() {
                                    classes.forEach { option ->
                                        Row {
                                            RadioButton(
                                                selected = (option == selectedClass),
                                                onClick = {
                                                    selectedClass = option
                                                    hasSelected.value = true
                                                    //create an empty item
                                                    when (selectedClass) {
                                                        "Nahkampf-Waffe" -> itemDisplayItem.value = ShortRangeWeapon("", "", 1, 1, 1, "")
                                                        "Fernkampf-Waffe" -> itemDisplayItem.value = ShortRangeWeapon("", "", 1, 1, 1, "")
                                                        "Verbrauchbares" -> itemDisplayItem.value = Consumable("", "", 1, 1, 1)
                                                        "Trank" -> itemDisplayItem.value = Potion("", "", 1, 1, 1)
                                                        "Verschiedenes" -> itemDisplayItem.value = Miscellaneous("", "", 1, 1, 1)
                                                    }
                                                    selectedInventory.value?.items?.add(0, itemDisplayItem.value!!)

                                                    refreshTrigger.value++
                                                }
                                            )
                                            Text(option)
                                        }
                                    }
                                }
                            }
                        }
                        //Normal Display
                        else if (itemDisplayItem.value != null) {
                            key(itemDisplayItem.value) {
                                Column(
                                    Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                ) {

                                    //Name
                                    val nameInput =
                                        remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.name)) }
                                    TextField(
                                        value = nameInput.value,
                                        onValueChange = {
                                            nameInput.value = it
                                            itemDisplayItem.value!!.name = it.text
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        label = {
                                            Text("Name")
                                        },
                                        singleLine = true,
                                    )

                                    //Description
                                    val descInput =
                                        remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.description)) }
                                    TextField(
                                        value = descInput.value,
                                        onValueChange = {
                                            descInput.value = it
                                            itemDisplayItem.value!!.description = it.text
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        label = {
                                            Text("Beschreibung")
                                        },
                                        singleLine = true,
                                    )

                                    //Damage
                                    if (itemDisplayItem.value is Weapon) {
                                        val weapon: Weapon = itemDisplayItem.value as Weapon
                                        val dmgInput = remember { mutableStateOf(TextFieldValue(weapon.damage)) }
                                        TextField(
                                            value = dmgInput.value,
                                            onValueChange = {
                                                dmgInput.value = it
                                                weapon.damage = it.text
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            label = {
                                                Text("Schaden")
                                            },
                                            singleLine = true,
                                        )
                                        itemDisplayItem.value = weapon
                                    }

                                    //Weight
                                    val weightInput =
                                        remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.weight.toString())) }
                                    val validWeight = remember { mutableStateOf(true) }
                                    var weightModifier: Modifier = Modifier
                                    if (!validWeight.value) weightModifier = Modifier.background(Color.Red)
                                    TextField(
                                        value = weightInput.value,
                                        onValueChange = {
                                            weightInput.value = it
                                            try {
                                                val weight = it.text.toInt()
                                                itemDisplayItem.value!!.weight = weight
                                                validWeight.value = true
                                            } catch (e: NumberFormatException) {
                                                println("Could not get int from " + it.text)
                                                validWeight.value = false
                                            }
                                        },
                                        modifier = weightModifier
                                            .fillMaxWidth(),
                                        label = {
                                            Text("Gewicht")
                                        },
                                        singleLine = true,
                                    )

                                    //Value
                                    val valueInput =
                                        remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.valueInGold.toString())) }
                                    val validValue = remember { mutableStateOf(true) }
                                    var valueModifier: Modifier = Modifier
                                    if (!validValue.value) valueModifier = Modifier.background(Color.Red)
                                    TextField(
                                        value = valueInput.value,
                                        onValueChange = {
                                            valueInput.value = it
                                            try {
                                                val value = it.text.toInt()
                                                itemDisplayItem.value!!.valueInGold = value
                                                validValue.value = true
                                            } catch (e: NumberFormatException) {
                                                println("Could not get int from " + it.text)
                                                validValue.value = false
                                            }
                                        },
                                        modifier = valueModifier
                                            .fillMaxWidth(),
                                        label = {
                                            Text("Wert in Gold")
                                        },
                                        singleLine = true,
                                    )

                                    //Amount
                                    val amountInput =
                                        remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.amount.toString())) }
                                    val validAmount = remember { mutableStateOf(true) }
                                    var amountModifier: Modifier = Modifier
                                    if (!validAmount.value) amountModifier = Modifier.background(Color.Red)
                                    TextField(
                                        value = amountInput.value,

                                        onValueChange = {
                                            amountInput.value = it
                                            try {
                                                val amount = it.text.toInt()
                                                itemDisplayItem.value!!.amount = amount
                                                validAmount.value = true
                                            } catch (e: NumberFormatException) {
                                                println("Could not get int from " + it.text)
                                                validAmount.value = false
                                            }
                                        },
                                        modifier = amountModifier
                                            .fillMaxWidth(),
                                        label = {
                                            Text("Menge")
                                        },
                                        singleLine = true,
                                    )
                                }
                            }
                        } else println("Something went wrong")
                    }

                    //Item image
                    Box(
                        Modifier
                            .weight(1f)
                    ) {

                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun backPack(
        inv: MutableState<Inventory>,
        refreshTrigger: MutableState<Int>,
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>
    ) {
        var mousePosition by remember { mutableStateOf(Offset.Zero) }
        val overIndex = remember { mutableStateOf<Int?>(null) }
        val hitItem = remember { mutableStateOf<Item?>(null) }
        val draggedIndex = remember { mutableStateOf<Int?>(null) }
        val draggedItem = remember { mutableStateOf<Item?>(null) }
        val itemCoordinates = remember { mutableStateMapOf<UUID, LayoutCoordinates>() }
        val boxCoords  = remember { mutableStateOf<LayoutCoordinates?>(null) }

        val totalSlots = 30
        val itemSize = 100.dp

        val items = remember(inv.value, refreshTrigger.value) {
            mutableStateListOf<Item?>().apply {
                addAll(inv.value.items.take(totalSlots))
                repeat(totalSlots - size) {
                    add(EmptySlot())
                }
            }
        }

        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    boxCoords.value = coords
                }
                .pointerMoveFilter(
                    onMove = {
                        mousePosition = it
                        false
                    }
                )
        ) {
            val columns = (maxWidth / itemSize).toInt()
            val rows = totalSlots / columns
            Column(modifier = Modifier.fillMaxWidth()) {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until columns) {
                            val index = row * columns + col
                            val item: Item? = items[index]

                            Box(
                                modifier = Modifier
                                    .size(itemSize)
//                                    .background(Color.Gray.copy(alpha = 0.1f))
                            ) {
                                if (item != null) {
                                    invItem(
                                        item = item,
                                        index = index,
                                        items = items,
                                        draggedItem = draggedItem,
                                        draggedIndex = draggedIndex,
                                        overIndex = overIndex,
                                        itemCoordinates = itemCoordinates,
                                        inv = inv,
                                        refreshTrigger = refreshTrigger,
                                        mousePosition = mousePosition,
                                        boxCoords = boxCoords,
                                        hitItem = hitItem,
                                        showItemDisplay = showItemDisplay,
                                        itemDisplayItem = itemDisplayItem
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun invItem(
        item: Item,
        index: Int,
        items: SnapshotStateList<Item?>,
        draggedItem: MutableState<Item?>,
        draggedIndex: MutableState<Int?>,
        overIndex: MutableState<Int?>,
        itemCoordinates: MutableMap<UUID, LayoutCoordinates>,
        inv: MutableState<Inventory>,
        refreshTrigger: MutableState<Int>,
        mousePosition: Offset,
        boxCoords: MutableState<LayoutCoordinates?>,
        hitItem: MutableState<Item?>,
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>
    ) {
        val backGroundColor = remember { mutableStateOf(Color.LightGray) }
        val positionRelativeToBox = remember { mutableStateOf<Offset?>(null) }
        val localCoordinates = mutableStateOf<LayoutCoordinates?>(null)
        val size = remember { mutableStateOf<Size?>(null) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .onGloballyPositioned { coords ->
                    localCoordinates.value = coords
                    val cachedCoords = coords.localPositionOf(boxCoords.value!!, Offset.Zero)
                    positionRelativeToBox.value = Offset(cachedCoords.x * -1, cachedCoords.y * -1)
                    size.value = coords.size.toSize()
                    itemCoordinates[item.uuid] = coords
                    if(Rect(positionRelativeToBox.value!!, size.value!!).contains(mousePosition) && item != hitItem.value) {
                        backGroundColor.value = Color.Red
                    }
                    else if(item == hitItem.value) {
                        backGroundColor.value = Color.Blue
                    }
                    else backGroundColor.value = Color.LightGray
                }
                .background(backGroundColor.value)
                .pointerInput(Unit) {
                    detectTapGestures( onTap = {
                        refreshTrigger.value++
                        itemDisplayItem.value = item
                        showItemDisplay.value = true
                    })
                }
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = {
//                            if(item !is EmptySlot) {
//                                draggedItem.value = item
//                                draggedIndex.value = index
//                                println("Drag started at " + draggedIndex.value)
//                            }
//                        },
//                        onDragEnd = {
//                            if(item !is EmptySlot) {
//                                println("Drag ended at " + overIndex.value)
//                                if(overIndex.value != null) {
//                                    val target = overIndex.value
//                                    if (target != null && target != index && target in 0..30 && hitItem.value != null && draggedItem.value != null && draggedItem.value !is EmptySlot && target < items.size) {
//                                        items[target] = draggedItem.value
//                                        items[index] = hitItem.value
//                                        println("Item " + draggedItem.value!!.name + " was moved from index " + index + " to " + target)
//                                        inv.value.items[target] = draggedItem.value
//                                        inv.value.items[index] = hitItem.value
//
//                                        inv.value = inv.value.copy()
//                                        refreshTrigger.value++
//                                    }
//                                    draggedItem.value = null
//                                    draggedIndex.value = null
//                                    overIndex.value = null
//                                    draggedItem.value = null
//                                    hitItem.value = null
//                                }
//                                else println("Drag ended at invalid target")
//                            }
//                        },
//                        onDragCancel = {
//                            //TODO reset item to draggedIndex
//                            println("Drag cancelled")
//                            draggedItem.value = null
//                            draggedIndex.value = null
//                            overIndex.value = null
//                            draggedItem.value = null
//                        },
//                        onDrag = { change, _ ->
//                            if(item !is EmptySlot) {
//                                change.consume()
//
//                                val localPos = change.position // relative to Composable
//                                val absolutePos = localCoordinates.value?.localToRoot(localPos) // absolute in Root
//
//                                if(absolutePos != null && boxCoords != null) {
//                                    val hitUUID = itemCoordinates.entries.find { (_, coords) ->
//                                        coords.isAttached && coords.boundsInRoot().contains(absolutePos)
//                                    }?.key
//
//                                    try {
//                                        val potentialItem = items.find { it!!.uuid == hitUUID }
//
//                                        if(potentialItem != null) {
//                                            hitItem.value = potentialItem
//                                            if (hitItem != null && item != hitItem) {
//                                                overIndex.value = items.indexOf(hitItem.value)
//                                            }
//                                            else overIndex.value = null
//                                        }
//                                        else overIndex.value = null
//                                    }
//                                    catch (e: NullPointerException) {
//                                        println("NPM with key " + hitUUID.toString())
//                                    }
//                                }
//                            }
//                        }
//                    )
//                }
        ) {
            if(item !is EmptySlot) {
                Column(
                    Modifier
                        .fillMaxSize()
                ) {
                    //Name
                    Text(
                        item.name,
                        Modifier
                            .fillMaxWidth()
                            .weight(5f)
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        //Filler
                        Box(
                            Modifier.weight(4f)
                        ) {}
                        //Amount
                        Text(
                            item.amount.toString(),
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

//    @Composable
//    fun invItem(item: Item, draggedIndex: MutableState<Int?>, draggedItem: MutableState<Item?>, overIndex: MutableState<Int?>, index: Int, items: SnapshotStateList<Item?>, itemCoordinates: SnapshotStateMap<UUID, LayoutCoordinates>, inv: MutableState<Inventory>, refreshTrigger: MutableState<Int>) {
//        val localCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }
//        val targetColor = if (overIndex.value == index) Color.Yellow else Color.Green
//        val animatedColor by animateColorAsState(targetColor, label = "BackgroundColor")
//        val int = refreshTrigger.value
//        val inventory = inv.value
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .aspectRatio(1f)
//                .background(animatedColor)
//                .onGloballyPositioned { coords ->
//                    localCoordinates.value = coords
//                    itemCoordinates[item.uuid] = coords
//                }
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = {
//                            draggedItem.value = item
//                            overIndex.value = items.indexOf(item)
//                            println("Set draggedItem " + draggedItem.value!!.name)
//                        },
//                        onDragEnd = {
//                            draggedIndex.value = null
//                            overIndex.value = null
//                            draggedItem.value = null
//                        },
//                        onDragCancel = {
//                            draggedIndex.value = null
//                            overIndex.value = null
//                            draggedItem.value = null
//                        },
//                        onDrag = { change, _ ->
//                            change.consume()
//
//                            val localPos = change.position // relative to Composable
//                            val absolutePos = localCoordinates.value?.localToRoot(localPos) // absolute in Root
//                            println("Absolute pos of item " + item.name + ": $absolutePos")
//
//                            if(absolutePos != null) {
//                                val hitKey = itemCoordinates.entries.find { (_, coords) ->
//                                    coords.isAttached && coords.boundsInRoot().contains(absolutePos)
//                                }?.key
//
//                                val hitItem = items.find { it!!.uuid == hitKey }
//
//                                if (hitItem != null && item != hitItem) {
//                                    overIndex.value = items.indexOf(hitItem)
//                                    val previousIndex = items.indexOf(draggedItem.value)
//                                    val aimedIndex = items.indexOf(hitItem)
//                                    val dragged = draggedItem.value ?: return@detectDragGestures
//                                    println("Dragged " + dragged.name + " to " + hitItem.name)
//                                    println("Changed index of " + dragged.name + " from " + previousIndex + " to " + aimedIndex)
//                                    println("ItemsList before:")
//                                    items.forEach { item ->
//                                        println("   ${items.indexOf(item)}. ${item.name} ")
//                                    }
//                                    items.remove(dragged)
//                                    items.add(aimedIndex, dragged)
//                                    println("ItemsList after:")
//                                    items.forEach { item ->
//                                        println("   ${items.indexOf(item)}. ${item.name} ")
//                                    }
//                                    inv.value.items.clear()
//                                    inv.value.items.addAll(items)
//                                    refreshTrigger.value++
//                                }
//                            }
//                        }
//                    )
//                }
//        )
//        {
//            Column(
//                Modifier
//                    .fillMaxSize()
//            ) {
//                //Name
//                Text(
//                    item.name,
//                    Modifier
//                        .fillMaxWidth()
//                        .weight(5f)
//                )
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                ) {
//                    //Filler
//                    Box(
//                        Modifier.weight(4f)
//                    ) {}
//                    //Amount
//                    Text(
//                        item.amount.toString(),
//                        Modifier.weight(1f)
//                    )
//                }
//            }
//        }
//    }
}
