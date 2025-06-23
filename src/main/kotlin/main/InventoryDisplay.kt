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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import net.bytebuddy.pool.TypePool
import org.jetbrains.skiko.Cursor
import java.util.UUID

object InventoryDisplay {

    @Composable
    fun displayEmptyDisplay(modifier: Modifier) {
        Box (modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
        ) {
            Text("Wähle ein Inventar aus")
        }
        //TODO set closed backpack image here and no content (maybe with "Wähle einen Charakter aus" text with blurred background?)
    }

    @Composable
    fun displayInv(inv: MutableState<Inventory>, modifier: Modifier, showItemDisplay: MutableState<Boolean>, itemDisplayItem: MutableState<Item?>, refreshTrigger: MutableState<Int>, showSortedInv: MutableState<Boolean>) {
        //TODO set opened backpack image as background here (maybe split in three pieces so it does not stretch when the width changes?)
        //TODO set randomly selected scenery behind backpack
        val int = refreshTrigger.value

        Box(modifier.fillMaxHeight()) {
            Column (modifier
                .fillMaxHeight()
            ) {
                sceneryAndBackPackTop(showItemDisplay, showSortedInv)
                backPack(inv, refreshTrigger, showItemDisplay, itemDisplayItem, showSortedInv)
            }
        }
    }

    @Composable
    fun sceneryAndBackPackTop(showItemDisplay: MutableState<Boolean>, showSortedInv: MutableState<Boolean>) {
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
        itemDisplayItem: MutableState<Item?>,
        showSortedInv: MutableState<Boolean>
    ) {
        var mousePosition by remember { mutableStateOf(Offset.Zero) }
        val itemCoordinates = remember { mutableStateMapOf<UUID, LayoutCoordinates>() }
        val boxCoords  = remember { mutableStateOf<LayoutCoordinates?>(null) }

        val totalSlots = 30
        val itemSize = 100.dp

        val typePriority = mapOf(
            ShortRangeWeapon::class to 0,
            LongRangeWeapon::class to 1,
            Potion::class to 2,
            Consumable::class to 3,
            Miscellaneous::class to 4
        )

        val items = remember(inv.value, refreshTrigger.value, showSortedInv.value) {
            mutableStateListOf<Item?>().apply {
                if(!showSortedInv.value) {
                    addAll(inv.value.items.take(totalSlots))
                }
                else {
                    addAll(inv.value.items.sortedBy { item -> typePriority[item::class] ?: Int.MAX_VALUE})
                }
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
                            ) {
                                if (item != null) {
                                    invItem(
                                        item = item,
                                        itemCoordinates = itemCoordinates,
                                        refreshTrigger = refreshTrigger,
                                        mousePosition = mousePosition,
                                        boxCoords = boxCoords,
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

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun invItem(
        item: Item,
        itemCoordinates: MutableMap<UUID, LayoutCoordinates>,
        refreshTrigger: MutableState<Int>,
        mousePosition: Offset,
        boxCoords: MutableState<LayoutCoordinates?>,
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>
    ) {
        val backGroundColor = remember { mutableStateOf(Color.LightGray) }
        val boxShape = remember(item.equipped) { mutableStateOf(if(!item.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)) }
        val borderColor = remember(item.equipped) { mutableStateOf(if(!item.equipped) Color.Black.copy(alpha = 0.3f) else Color.Yellow.copy(alpha = 0.7f))}
        val positionRelativeToBox = remember { mutableStateOf<Offset?>(null) }
        val localCoordinates = mutableStateOf<LayoutCoordinates?>(null)
        val size = remember { mutableStateOf<Size?>(null) }
        var isHovered by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isHovered) 1.08f else 1f,
            animationSpec = tween(durationMillis = 150)
        )
        val elevation by animateDpAsState(
            targetValue = if (isHovered) 10.dp else 2.dp,
            animationSpec = tween(durationMillis = 150)
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .pointerMoveFilter(
                    onEnter = {
                        if(item !is EmptySlot) isHovered = true
                        false
                    },
                    onExit = {
                        if(item !is EmptySlot) isHovered = false
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
                            if(item !is EmptySlot) {
                                refreshTrigger.value++
                                itemDisplayItem.value = item
                                showItemDisplay.value = true
                            }
                        },
                        onDoubleTap = {
                            println("press " + item.name)
                            item.equipped = !item.equipped
                            refreshTrigger.value++
                        }
                    )
                }
                .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(if(item !is EmptySlot) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR)))
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
                            .weight(3f) //TODO reset to 5f
                    )
                    Text(when(item) {
                        is LongRangeWeapon -> "longRangeWeapon"
                        is ShortRangeWeapon -> "shortRangeWeapon"
                        is Miscellaneous -> "miscellaneous"
                        is Potion -> "potion"
                        is Consumable -> "consumable"
                        else -> {"no class"}
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
}
