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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
        Box(
            modifier = modifier
        ) {
            Column{
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
                .height(250.dp)
        ) {
            //Background
            Box(
                Modifier
                    .zIndex(0f)
                    .fillMaxSize()
            ) {
                //Scenery
                getRandomSceneryImage()
            }

            //Foreground
            Row(
                Modifier
                    .zIndex(1f)
                    .fillMaxSize()
            ) {
                Image(
                    painterResource("backPackTopOpenLeft.png"),
                    contentDescription = "Backpack top left",
                    modifier = Modifier.weight(1f),
                    contentScale = ContentScale.FillBounds
                )
                Image(
                    painterResource("backPackTopOpenMiddle.png"),
                    contentDescription = "Backpack top middle",
                    modifier = Modifier.width(180.dp),
                    contentScale = ContentScale.FillBounds
                )
                Image(
                    painterResource("backPackTopOpenRight.png"),
                    contentDescription = "Backpack top right",
                    modifier = Modifier.weight(1f),
                    contentScale = ContentScale.FillBounds
                )
            }

            //Sorting
            Column(
                Modifier
                    .zIndex(2f)
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
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color.LightGray,
                                        unselectedColor = Color.White
                                    )
                                )
                                Text(option, color = Color.White)
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

                                    //Equipped
                                    val equipped = remember(itemDisplayItem.value, itemDisplayItem.value!!.equipped) { mutableStateOf(itemDisplayItem.value!!.equipped) }
                                    Row {
                                        Checkbox(
                                            checked = equipped.value,
                                            onCheckedChange = {
                                                itemDisplayItem.value!!.equipped = it
                                                equipped.value = it
                                            },
                                            modifier = Modifier
                                                .width(30.dp)
                                                .padding(horizontal = 10.dp),
                                        )
                                        Text("Ausgerüstet")
                                    }
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

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
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
        val draggedItemOffset = remember { mutableStateOf(Offset.Zero) }
        val dragHoveredOver = remember { mutableStateOf<Item?>(null) }

        val firstEmptySlot = remember(items) {
            derivedStateOf {
                items.firstOrNull { it is EmptySlot }
            }
        }
        val highlightFirstEmptySlot = remember { mutableStateOf(false) }

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
            //Background
            Image(
                painterResource("backPackBackgroundOpen.jpg"),
                "Backpack background",
                Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            //Inv display
            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .zIndex(0f)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val position = event.changes.first().position
                                draggedItemOffset.value = position
                            }
                        }
                    }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 104.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        items = if(showSortedInv.value) {
                            if(refreshInv.value) {
                                refreshInv.value = false
                            }
                            println("loading items")
                            items.sortedBy { item -> typePriority[item!!::class] ?: Int.MAX_VALUE}
                        }
                        else {
                            if(refreshInv.value) {
                                refreshInv.value = false
                            }
                            items
                        },
                        key = { item -> item?.uuid ?: UUID.randomUUID() }
                    ) { item: Item? ->
                        Box(
                            Modifier
                                .animateItem()
                        ) {
                            invSlot(item, showItemDisplay, itemDisplayItem, onItemChanged, items, draggedItem, dragHoveredOver, removeItem, addItemAtIndex, firstEmptySlot, highlightFirstEmptySlot)
                        }
                    }
                }
            }

            //Overlay
            if(draggedItem.value != null) {
                val density = LocalDensity.current
                val scaleFactor = density.density
                val overlayBackGroundColor by animateColorAsState(
                    targetValue = if (draggedItem.value != null) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0f),
                    animationSpec = tween(durationMillis = 500)
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .background(overlayBackGroundColor)
                ) {
                    Column{
                        //Placeholder
                        Box(Modifier.weight(1f))

                        //Delete Button
                        Box(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Image(painterResource("deleteIconRed.svg"), "Delete",
                                Modifier
                                    .padding(10.dp)
                                    .align(Alignment.Center)
                                    .height(50.dp)
                                    .clickable {
                                        println("deleted item " + draggedItem.value!!.name)
                                        draggedItem.value = null
                                        dragHoveredOver.value = null
                                    }
                                    .clipToBounds()
                            )
                        }
                    }
                }
                val borderColor = remember(draggedItem.value?.equipped) {mutableStateOf(if(draggedItem.value!! is EmptySlot) Color.Black.copy(alpha = 0.1f) else if(!draggedItem.value!!.equipped) Color.Black.copy(alpha = 0.3f) else Color.Yellow.copy(alpha = 0.7f))}

                val boxShape = remember(draggedItem.value?.equipped) { mutableStateOf(if(!draggedItem.value!!.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)) }

                //ItemDisplay Overlay
                Box(
                    Modifier
                        .zIndex(2f)
                        .fillMaxSize()
                        .offset((draggedItemOffset.value.x / scaleFactor).dp, (draggedItemOffset.value.y / scaleFactor).dp)
                ) {
                    Column {
                        Box(
                            Modifier
                                .size(100.dp)
                                .shadow(10.dp, shape = RoundedCornerShape(8.dp), clip = false)
                                .background(Color.LightGray.copy(alpha = 1f), shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                            ) {
                                //Name
                                Text(
                                    draggedItem.value!!.name,
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(3f)
                                )
                                Text(when(draggedItem.value!!) {
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
                                        draggedItem.value!!.amount.toString(),
                                        Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        Text("Klicke um das item einzuordnen", Modifier.padding(8.dp), color = Color.White)
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
        addItemAtIndex: (Item, Item) -> Unit,
        firstEmptySlot: State<Item?>,
        highlightFirstEmptySlot: MutableState<Boolean>
    ) {
        val backGroundColor = remember { mutableStateOf(if(item !is EmptySlot) Color.LightGray else Color.LightGray.copy(alpha = 0.2f)) }

        if(item != null) {
            val boxShape = remember(item.equipped) { mutableStateOf(if(!item.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)) }
            var isHovered by remember { mutableStateOf(false) }
            val borderColor = remember(item.equipped) { mutableStateOf(Color.Transparent)  }

            if(dragHoveredOver.value != null) {
                if(dragHoveredOver.value!! is EmptySlot && highlightFirstEmptySlot.value && firstEmptySlot.value == item) {
                    remember(item.equipped) {
                        borderColor.value = Color.Red
                    }
                }
                else if(dragHoveredOver.value!! == item) {
                    if(item !is EmptySlot || firstEmptySlot.value == item) {
                        remember(item.equipped) {
                            borderColor.value = Color.Red
                        }
                    }
                    else {
                        remember(item.equipped) {
                            borderColor.value = Color.Black.copy(alpha = 0.1f)
                        }
                    }
                }
            }
            else {
                borderColor.value = if(item is EmptySlot) Color.Black.copy(alpha = 0.1f) else if(!item.equipped) Color.Black.copy(alpha = 0.3f) else Color.Yellow.copy(alpha = 0.7f)
                highlightFirstEmptySlot.value = false
            }

            val scale by animateFloatAsState(
                targetValue = if (isHovered && item !is EmptySlot) 1.08f else 1f,
                animationSpec = tween(durationMillis = 150)
            )

            val elevation by animateDpAsState(
                targetValue = if (isHovered && item !is EmptySlot) 6.dp else if(item !is EmptySlot) 2.dp else 0.dp,
                animationSpec = tween(durationMillis = 150)
            )

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(100.dp)
                    .pointerMoveFilter(
                        onEnter = {
                            isHovered = true
                            if(draggedItem.value != null && item is EmptySlot) {
                                highlightFirstEmptySlot.value = true
                            }
                            if(draggedItem.value != null) dragHoveredOver.value = item
                            false
                        },
                        onExit = {
                            isHovered = false
                            if(draggedItem.value != null && item is EmptySlot) {
                                highlightFirstEmptySlot.value = false
                            }
                            if (draggedItem.value != null) dragHoveredOver.value = null
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
                            onLongPress = {
                                if(item !is EmptySlot && draggedItem.value == null) {
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

    @Composable
    fun getRandomSceneryImage() {
        return Image(
            painterResource("sceneryImages/" + (1..7).random() + ".jpeg"),
            "Scenery Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
