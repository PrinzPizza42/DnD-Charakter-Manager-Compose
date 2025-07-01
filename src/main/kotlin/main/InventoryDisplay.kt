package main

import Main.Inventory
import Main.ItemClasses.Consumable
import Main.ItemClasses.EmptySlot
import Main.ItemClasses.Item
import Main.ItemClasses.Miscellaneous
import Main.ItemClasses.Potion
import Main.ItemClasses.Weapons.LongRangeWeapon
import Main.ItemClasses.Weapons.ShortRangeWeapon
import Main.ItemClasses.Weapons.Weapon
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jetbrains.skiko.Cursor
import java.util.*

object InventoryDisplay {
    @Composable
    fun displayInv(
        inv: MutableState<Inventory?>,
        modifier: Modifier,
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>,
        showSortedInv: MutableState<Boolean>,
        items: List<Item?>,
        totalSlots: Int,
        itemSize: Dp,
        onItemChanged: (Item) -> Unit,
        refreshInv: MutableState<Boolean>,
        removeItem: (Item) -> Unit,
        addItemAtIndex: (Item, Item) -> Unit,
    ) {
        //TODO set randomly selected scenery behind backpack

        Box(modifier.fillMaxHeight()) {
            Column (modifier
                .fillMaxHeight()
            ) {
                sceneryAndBackPackTop(showItemDisplay, showSortedInv, refreshInv)
                backPack(inv, showItemDisplay, itemDisplayItem, showSortedInv, items, onItemChanged, refreshInv, removeItem, addItemAtIndex)
            }
        }
    }

    @Composable
    fun sceneryAndBackPackTop(showItemDisplay: MutableState<Boolean>, showSortedInv: MutableState<Boolean>, refreshInv: MutableState<Boolean>) {
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
                                    onClick = {
                                        selectedOption = option
                                        showSortedInv.value = (option == "Nach Klasse")
                                        println("selected option: $selectedOption") //TODO remove
                                        println("showSortedInv: ${showSortedInv.value}") //TODO remove
                                    }
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
                                .width(75.dp)
                                .height(75.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun showItemDisplayStructure(
        itemDisplayItem: MutableState<Item?>,
        showItemDisplay: MutableState<Boolean>,
        onItemChanged: (Item) -> Unit,
        refreshInv: MutableState<Boolean>
    ) {
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
                        if(showItemDisplay.value) {
                            showItemDisplay.value = false
                            itemDisplayItem.value?.let {
                                onItemChanged(it)
                                refreshInv.value = true
                            }
                            itemDisplayItem.value = null
                            println("Detected tap outside")
                        }
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
                                                        "Fernkampf-Waffe" -> itemDisplayItem.value = LongRangeWeapon("", "", 1, 1, 1, "")
                                                        "Verbrauchbares" -> itemDisplayItem.value = Consumable("", "", 1, 1, 1)
                                                        "Trank" -> itemDisplayItem.value = Potion("", "", 1, 1, 1)
                                                        "Verschiedenes" -> itemDisplayItem.value = Miscellaneous("", "", 1, 1, 1)
                                                    }
                                                    println("Created ${itemDisplayItem.value}")
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
        inv: MutableState<Inventory?>,
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>,
        showSortedInv: MutableState<Boolean>,
        items: List<Item?>,
        onItemChanged: (Item) -> Unit,
        refreshInv: MutableState<Boolean>,
        removeItem: (Item) -> Unit,
        addItemAtIndex: (Item, Item) -> Unit,
    ) {
        val draggedItem = remember { mutableStateOf<Item?>(null) }
        val draggedItemCoords = remember { mutableStateOf(0) }
        val dragHoveredOver = remember { mutableStateOf<Item?>(null) }

        val typePriority = mapOf(
            ShortRangeWeapon::class to 0,
            LongRangeWeapon::class to 1,
            Potion::class to 2,
            Consumable::class to 3,
            Miscellaneous::class to 4
        )
        Box(Modifier
            .fillMaxSize()
        ) {
            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .zIndex(0f)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    contentPadding = PaddingValues(4.dp),
                ) {
                    items(
                        items = if(showSortedInv.value) {
                            if(refreshInv.value) {
                                println("refreshed inv because of manual refresh")
                                refreshInv.value = false
                            }

                            println("loading items")
                            items.sortedBy { item -> typePriority[item!!::class] ?: Int.MAX_VALUE}
                        }
                        else {
                            if(refreshInv.value) {
                                println("refreshed inv because of manual refresh")
                                refreshInv.value = false
                            }
                            items
                        },
                        key = { item -> item?.uuid ?: UUID.randomUUID() }
                    ) { item: Item? ->
                        invSlot(item, showItemDisplay, itemDisplayItem, onItemChanged, items, draggedItem, dragHoveredOver, removeItem, addItemAtIndex)
                    }
                }
            }

            if(draggedItem.value != null) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Column(
                        Modifier
                            .background(Color.Red)
                    ) {
                        Text("Coordinates: " + draggedItemCoords.value.toString())
                        Text("Hovered over item: " + dragHoveredOver.value?.name)

                        Text("Klicke um das item einzuordnen")

                        //TODO add delete item button at the bottom which only closes the overlay
                    }
                }
                Box(
                    Modifier
                        .zIndex(2f)
                        .size(100.dp)
                        .offset(10.dp)
                ) {
                    Box(
                        Modifier
                            .background(Color.LightGray.copy(alpha = 1f), shape = RoundedCornerShape(10.dp))
                    ) {

                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun invSlot(
        item: Item?,
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>,
        onItemChanged: (Item) -> Unit,
        items: List<Item?>,
        draggedItem: MutableState<Item?>,
        dragHoveredOver: MutableState<Item?>,
        removeItem: (Item) -> Unit,
        addItemAtIndex: (Item, Item) -> Unit
    ) {
        val backGroundColor = remember { mutableStateOf(if(item !is EmptySlot) Color.LightGray else Color.LightGray.copy(alpha = 0.2f)) }

        if(item != null) {
            val boxShape = remember(item.equipped) { mutableStateOf(if(!item.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)) }
            var isHovered by remember { mutableStateOf(false) }
            val borderColor = remember(item.equipped) { mutableStateOf(Color.Transparent)  }

            if(dragHoveredOver.value != null && dragHoveredOver.value!! == item) remember(item.equipped) {
                borderColor.value = Color.Red
            }
            else {
                 borderColor.value = if(item is EmptySlot) Color.Black.copy(alpha = 0.1f) else if(!item.equipped) Color.Black.copy(alpha = 0.3f) else Color.Yellow.copy(alpha = 0.7f)
            }

            val scale by animateFloatAsState(
                targetValue = if (isHovered) 1.08f else 1f,
                animationSpec = tween(durationMillis = 150)
            )

            val elevation by animateDpAsState(
                targetValue = if (isHovered) 6.dp else if(item !is EmptySlot) 2.dp else 0.dp,
                animationSpec = tween(durationMillis = 150)
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .pointerMoveFilter(
                        onEnter = {
                            if(item !is EmptySlot) {
                                isHovered = true
                                if(draggedItem.value != null) dragHoveredOver.value = item
                            }
                            false
                        },
                        onExit = {
                            if(item !is EmptySlot) {
                                isHovered = false
                                if (draggedItem.value != null) dragHoveredOver.value = null
                            }
                            false
                        }
                    )
                    .graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                    }
                    .shadow(elevation, shape = RoundedCornerShape(8.dp), clip = false)
                    .background(backGroundColor.value, shape = boxShape.value)
                    .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                println("clicked item " + item.name)
                                if(draggedItem.value != null) {
                                    println("drop item before " + item.name)
                                    addItemAtIndex(draggedItem.value!!, item)
                                    draggedItem.value = null
                                    dragHoveredOver.value = null
                                }
                                else if(item !is EmptySlot) {
                                    itemDisplayItem.value = item
                                    showItemDisplay.value = true
                                }
                            },
                            onDoubleTap = {
                                if(item !is EmptySlot) {
                                    println("press " + item.name)
                                    item.equipped = !item.equipped
                                    onItemChanged(item)
                                }
                            },
                            onLongPress = {
                                if(item !is EmptySlot) {
                                    println("longpress " + item.name)
                                    draggedItem.value = item
                                    removeItem(item)
                                }
                            }
                        )
                    }
                    .pointerHoverIcon(if(item !is EmptySlot) PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor( Cursor.HAND_CURSOR)) else PointerIcon(
                        Cursor(Cursor.DEFAULT_CURSOR)
                    ))
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
                                .weight(3f)
                        )
                        Text(when(item) {
                            is LongRangeWeapon -> "longRangeWeapon"
                            is ShortRangeWeapon -> "shortRangeWeapon"
                            is Miscellaneous -> "miscellaneous"
                            is Potion -> "potion"
                            is Consumable -> "consumable"
                            else -> "no class"
                        },
                            Modifier
                                .fillMaxWidth()
                                .weight(1f))
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
        else {
            Box(Modifier
                .size(100.dp)
                .background(backGroundColor.value.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            )
        }
    }
}
