package main

import Main.Inventory
import Main.ItemClasses.Consumable
import Main.ItemClasses.Item
import Main.ItemClasses.Miscellaneous
import Main.ItemClasses.Potion
import Main.ItemClasses.Weapons.ShortRangeWeapon
import Main.ItemClasses.Weapons.Weapon
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

object InventoryDisplay {
    var removedItemsCount: Int = 0
    var addedItemsCount: Int = 0

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
    fun displayInv(inv: Inventory, modifier: Modifier, showItemDisplay: MutableState<Boolean>, itemDisplayItem: MutableState<Item?>, refreshTrigger: MutableState<Int>) {
        //TODO set opened backpack image as background here (maybe split in three pieces so it does not stretch when the width changes?)
        //TODO set randomly selected scenery behind backpack
        val int = refreshTrigger.value


        Box(modifier.fillMaxHeight()) {
            Column (modifier
                .fillMaxHeight()
            ) {
                sceneryAndBackPackTop(showItemDisplay)
                backPack(inv, refreshTrigger)
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

                    Box() {

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

                                                    selectedInventory.value = selectedInventory.value
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

    @Composable
    fun backPack(inv: Inventory, refreshTrigger: MutableState<Int>) {
        val draggedIndex = remember { mutableStateOf<Int?>(null) }
        val overIndex = remember { mutableStateOf<Int?>(null) }
        val items = remember { mutableStateListOf<Item>() }
        items.clear()
        items.addAll(inv.items)
        println("loaded different inv " + inv.name)
        val itemCoordinates = remember { mutableStateMapOf<Int, LayoutCoordinates>() }

        val int = refreshTrigger.value

        //Background
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Box(
                Modifier
                    .zIndex(1f)
                    .fillMaxSize()
            ) {

            }
            //Foreground
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color.Red)
                    .zIndex(2f),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items.size) { index ->
                    invItem(items[index], draggedIndex, overIndex, index, items, itemCoordinates, inv, refreshTrigger)
                }
            }
        }
    }

    @Composable
    fun invItem(item: Item, draggedIndex: MutableState<Int?>, overIndex: MutableState<Int?>, index: Int, items: SnapshotStateList<Item>, itemCoordinates: SnapshotStateMap<Int, LayoutCoordinates>, inv: Inventory, refreshTrigger: MutableState<Int>) {
        val localCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }
        val targetColor = if (overIndex.value == index) Color.Yellow else Color.Green
        val animatedColor by animateColorAsState(targetColor, label = "BackgroundColor")
        val int = refreshTrigger.value
        val draggedItem = remember { mutableStateOf<Item?>(null) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .background(animatedColor)
                .onGloballyPositioned { coords ->
                    localCoordinates.value = coords
                    itemCoordinates[index] = coords
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            draggedItem.value = item
                            println("Set draggedItem " + draggedItem.value!!.name)
                        },
                        onDragEnd = {
                            draggedIndex.value = null
                            overIndex.value = null
                        },
                        onDragCancel = {
                            draggedIndex.value = null
                            overIndex.value = null
                        },
                        onDrag = { change, _ ->
                            change.consume()

                            val localPos = change.position // relativ to Composable
                            val absolutePos = localCoordinates.value?.localToRoot(localPos) // absolut im Root

                            if(absolutePos != null) {
                                val hitIndex = runCatching {
                                    itemCoordinates.entries.find { (_, coords) ->
                                        coords.isAttached && coords.boundsInRoot().contains(absolutePos)
                                    }?.key
                                }.getOrNull()

                                if (hitIndex != null && items.indexOf(item) != hitIndex) {
                                    val dragged = draggedItem.value ?: return@detectDragGestures
                                    items.remove(dragged)
                                    items.add(hitIndex, dragged)

                                }
                            }
                        }
                    )
                }
        )
        {
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
