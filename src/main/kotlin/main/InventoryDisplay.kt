package main

import Data.ImageLoader
import Main.Inventory
import Main.ItemClasses.*
import Main.ItemClasses.Weapons.LongRangeWeapon
import Main.ItemClasses.Weapons.ShortRangeWeapon
import Main.ItemClasses.Weapons.Weapon
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jetbrains.skiko.Cursor
import java.awt.FileDialog
import java.io.File
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
        window: ComposeWindow,
    ) {
        val slotSize = remember { mutableStateOf(100.dp) }

        Box(
            modifier = modifier
        ) {
            Column{
                sceneryAndBackPackTop(showItemDisplay, showSortedInv, refreshInv, items, inv, slotSize)
                backPack(showItemDisplay, itemDisplayItem, showSortedInv, items, removeItem, addItemAtIndex, slotSize)
            }
        }
    }

    @Composable
    fun showItemDisplayStructure(
        itemDisplayItem: MutableState<Item?>,
        showItemDisplay: MutableState<Boolean>,
        onItemChanged: (Item) -> Unit,
        refreshInv: MutableState<Boolean>,
        focusManager: FocusManager,
        window: ComposeWindow
    ) {
        val classes = listOf("Nahkampf-Waffe", "Fernkampf-Waffe", "Verbrauchsgegenstände", "Rüstung", "Trank", "Verschiedenes")
        val selectedClass = remember { mutableStateOf(classes[0]) }
        val hasSelected = remember { mutableStateOf(false) }

        //InvDisplay overlay
        BoxWithConstraints (
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
                            println("Detected tap outside, closing item display")
                        }
                    })
                }
        ) {
            val itemDisplayWith: Dp by remember(this.maxWidth) { mutableStateOf(if(this.maxWidth > 1000.dp) 1000.dp else this.maxWidth) }
            //ItemDisplay
            Box(
                Modifier
                    .align(Alignment.Center)
                    .zIndex(11f)
                    .size(itemDisplayWith, 700.dp)
                    .onKeyEvent { keyEvent ->
                        if(keyEvent.key == Key.Escape || keyEvent.key == Key.Enter && showItemDisplay.value) {
                            focusManager.clearFocus()
                            println("cleared focus")
                            true
                        }
                        else false
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            println("detected tap inside, clearing focus")
                            focusManager.clearFocus()
                        })
                    }
            ) {
                val itemDisplayBackGround = remember { ImageLoader.loadImageFromResources("itemDisplayBackGround.png").get().toPainter() }
                Image(
                    itemDisplayBackGround,
                    "itemDisplayBackGround",
                    Modifier
                        .zIndex(11f)
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                //Foreground
                Row(
                    Modifier
                        .zIndex(12f)
                        .fillMaxSize()
                        .padding(62.dp, 15.dp)
                ) {
                    val reloadKey = remember { mutableStateOf(0) }

                    //Item stats
                    Box(Modifier.weight(1f)) {
                        itemDisplayStats(showItemDisplay, itemDisplayItem, hasSelected, classes, selectedClass, refreshInv, onItemChanged, reloadKey)
                    }

                    //Item image
                    Box(Modifier.weight(1f)) {
                        itemDisplayImage(showItemDisplay, itemDisplayItem, window, reloadKey)
                    }
                }
            }
        }
    }

    @Composable
    fun itemDisplayStats(
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>,
        hasSelected: MutableState<Boolean>,
        classes: List<String>,
        selectedClass: MutableState<String>,
        refreshInv: MutableState<Boolean>,
        onItemChanged: (Item) -> Unit,
        reloadKey: MutableState<Int>
    ) {
        Row(
            Modifier
                .fillMaxSize()
        ) {
            //Item Create
            if (itemDisplayItem.value == null && !hasSelected.value) {
                itemDisplayStatsCreateDisplay(classes, selectedClass, hasSelected, itemDisplayItem)
            }
            //Normal Display
            else if (itemDisplayItem.value != null) {
                itemDisplayStatsNormalDisplay(itemDisplayItem, reloadKey)
            } else println("Something went wrong")
        }
    }

    @Composable
    fun itemDisplayStatsCreateDisplay(
        classes: List<String>,
        selectedClass: MutableState<String>,
        hasSelected: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>
    ) {
        Column(
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures {}
                }
        ) {
            Text("Wähle eine Klasse für dein neues Item aus")
            Column {
                classes.forEach { option ->
                    Row {
                        RadioButton(
                            selected = (option == selectedClass.value),
                            onClick = {
                                selectedClass.value = option
                                hasSelected.value = true
                                //create an empty item
                                when (selectedClass.value) {
                                    "Nahkampf-Waffe" -> itemDisplayItem.value = ShortRangeWeapon("", "", 1, 1, 1, "")
                                    "Fernkampf-Waffe" -> itemDisplayItem.value = LongRangeWeapon("", "", 1, 1, 1, "")
                                    "Verbrauchsgegenstände" -> itemDisplayItem.value = Consumable("", "", 1, 1, 1)
                                    "Rüstung" -> itemDisplayItem.value = Armor("", "", 1, 1, 1, 10, ArmorClasses.MEDIUM)
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

    @Composable
    fun itemDisplayStatsNormalDisplay(itemDisplayItem: MutableState<Item?>, reloadKey: MutableState<Int>) {
        key(itemDisplayItem.value) {
            Column(
                Modifier
                    .fillMaxSize()
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

                if (itemDisplayItem.value is Weapon) {
                    //Damage
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

                if(itemDisplayItem.value is Armor) {
                    //Armor Value
                    val armor: Armor = itemDisplayItem.value as Armor
                    val armorValueInput = remember { mutableStateOf(TextFieldValue(armor.armorValue.toString())) }
                    var errorValue by remember { mutableStateOf(false) }

                    TextField(
                        value = armorValueInput.value,
                        onValueChange = {
                            armorValueInput.value = it
                            val inputInt = it.text.toIntOrNull()

                            if(inputInt == null) errorValue = true
                            else {
                                errorValue = false
                                armor.armorValue = inputInt
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text("Rüstungswert")
                        },
                        singleLine = true,
                        isError = errorValue
                    )

                    //Armor Class
                    DropdownString(
                        "Rüstungsklasse",
                        ArmorClasses.entries.map { it.name },
                        mutableStateOf(armor.armorClass.toString()),
                        {
                            newClass -> armor.armorClass = ArmorClasses.valueOf(newClass)
                            println("Armor class value changed to ${armor.armorClass}")
                        }
                    )
                }

                //Weight
                val weightInput = remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.weight.toString())) }
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
                val amountInput = remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.amount.toString())) }
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

                //Image reset
                Text("Bild zurücksetzen",
                    Modifier.clickable (
                        onClick = {
                            itemDisplayItem.value!!.userIconName = null
                            reloadKey.value++
                            println("Reset userIconName of item ${itemDisplayItem.value!!.name}")
                        }
                    )
                )
            }
        }
    }

    @Composable
    fun itemDisplayImage(
        showItemDisplay: MutableState<Boolean>,
        item: MutableState<Item?>,
        window: ComposeWindow,
        reloadKey: MutableState<Int>
    ) {

        Box(
            Modifier
                .fillMaxSize()
        ) {
            if(item.value == null) {
                Text("No image for this item found")
            }
            else {
                key(reloadKey.value) {
                    val painter: Painter = remember(item.value!!.icon) { item.value!!.icon.toPainter() }
                    Image(
                        painter = painter,
                        contentDescription = "item icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable(
                                onClick = {
                                    println("Clicked ${item.value!!.name}")

                                    val dialog = FileDialog(window, "Wähle eine Datei", FileDialog.LOAD)

                                    dialog.isVisible = true

                                    val directory = dialog.directory
                                    val preFile = dialog.file

                                    if (directory != null && preFile != null) {
                                        val file = File(directory, preFile)
                                        println("found ${file.absolutePath}")
                                        item.value!!.userIconName = file.absolutePath
                                        reloadKey.value++
                                    }
                                }
                            )
                        ,
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun sceneryAndBackPackTop(
        showItemDisplay: MutableState<Boolean>,
        showSortedInv: MutableState<Boolean>,
        refreshInv: MutableState<Boolean>,
        items: List<Item?>,
        inv: MutableState<Inventory?>,
        slotSize: MutableState<Dp>,
        ) {
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
                val backPackTopOpenLeft = remember { ImageLoader.loadImageFromResources("backPackTopOpenLeft.png").get().toPainter() }
                Image(
                    backPackTopOpenLeft,
                    contentDescription = "Backpack top left",
                    modifier = Modifier.weight(1f),
                    contentScale = ContentScale.FillBounds
                )
                val backPackTopOpenMiddle = remember { ImageLoader.loadImageFromResources("backPackTopOpenMiddle.png").get().toPainter() }
                Image(
                    backPackTopOpenMiddle,
                    contentDescription = "Backpack top middle",
                    modifier = Modifier.width(180.dp),
                    contentScale = ContentScale.FillBounds
                )
                val backPackTopOpenRight = remember { ImageLoader.loadImageFromResources("backPackTopOpenRight.png").get().toPainter() }
                Image(
                    backPackTopOpenRight,
                    contentDescription = "Backpack top right",
                    modifier = Modifier.weight(1f),
                    contentScale = ContentScale.FillBounds
                )
            }

            //BackPack functions
            Column(
                Modifier
                    .zIndex(2f)
                    .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .zIndex(2f)
                        .weight(1.5f)
                        .fillMaxWidth()
                )
                Row(
                    Modifier
                        .zIndex(2f)
                        .weight(1f)
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                    )

                    //Content
                    Box(
                        Modifier
                            .background(Color.Transparent)
                            .weight(5f)
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        val showChangeBackPackWeight = remember { mutableStateOf(false) }

                        if (showChangeBackPackWeight.value) {
                            val weightChangerColor = remember { lerp(Color.Transparent, Color.White, 0.9f) }

                            getFloatInputOverlay(
                                Modifier
                                    .fillMaxSize()
                                    .zIndex(12f)
                                    .background(weightChangerColor, RoundedCornerShape(5.dp))
                                    .clip(RoundedCornerShape(5.dp)),
                                inv.value!!.maxCarryingCapacity,
                                "Maximalgewicht",
                                onConfirm = { value ->
                                    println(value.toString())
                                    showChangeBackPackWeight.value = false
                                    inv.value!!.maxCarryingCapacity = value

                                    println("confirmed")
                                },
                                onDismiss = {
                                    showChangeBackPackWeight.value = false

                                    println("dismissed")
                                }
                            )
                        }


                        val options = listOf("Eigene", "Item-Klasse")
                        var selectedOption by remember { mutableStateOf(options[0]) }
                        val range = remember { 50f.rangeTo(150f) }
                        Row {
                            Slider(
                                value = slotSize.value.value,
                                valueRange = range,
                                onValueChange = {
                                    slotSize.value = it.dp
                                },
                                modifier = Modifier.weight(1f),
                            )

                            val backGroundColor = remember { lerp(Color.Transparent, Color.Black, 0.2f) }

                            Column(
                                Modifier
                                    .size(150.dp, 100.dp)
                                    .background(backGroundColor, RoundedCornerShape(5.dp))
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Text(
                                    text = "Sortierung:",
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                options.forEach { option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        RadioButton(
                                            selected = (option == selectedOption),
                                            onClick = {
                                                selectedOption = option
                                                showSortedInv.value = (option == "Item-Klasse")
                                            },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = Color.LightGray,
                                                unselectedColor = Color.White
                                            )
                                        )
                                        Text(option, color = Color.White)
                                    }
                                }
                            }

                            //BackPack weight
                            val modifier = Modifier.padding(10.dp, 0.dp)

                            val backPackWeight = remember(items) { mutableStateOf(items.toMutableList().sumOf { it!!.weight * it.amount}.toFloat()) }

                            Box(
                                Modifier
                                    .onClick {
                                        showChangeBackPackWeight.value = true
                                    }
                            ) {
                                backPackTopValue(modifier, backPackWeight, inv.value!!.maxCarryingCapacity, "Gewicht")
                            }

                            //BackPack value
                            val backPackValue = remember(items) { mutableStateOf(items.toMutableList().sumOf { it!!.valueInGold * it.amount}.toFloat()) }
                            backPackTopValue(modifier, backPackValue, null, "Wert in Gold")

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
                                    .height(75.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    backgroundColor = lerp(Color.Transparent, Color.Black, 0.2f)
                                )
                            )
                        }
                    }

                    Box(
                        Modifier
                            .weight(1f)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun backPackTopValue(modifier: Modifier, value: MutableState<Float>, maxValue: Float?, title: String) {
        val valueCorrelatingColor by remember(value, maxValue) { mutableStateOf(
            if(maxValue == null) Color.DarkGray
            else lerp(Color.DarkGray, Color.Red, value.value / maxValue)
        )}

        val backGroundColor = remember { lerp(Color.Transparent, Color.Black, 0.2f) }
        val textColor = remember { Color.White }

        Column(
            modifier
                .background(backGroundColor, RoundedCornerShape(5.dp))
                .clip(RoundedCornerShape(5.dp))
                .width(100.dp)
                .height(75.dp)
        ) {
            Text(
                text = "$title:",
                color = textColor,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )

            //Progressbar
            Box(
                Modifier
                    .height(50.dp)
            ) {
                val text: String = "${value.value}" + if(maxValue != null) "/${maxValue}" else ""
                Text(
                    text = text,
                    color = textColor,
                    modifier = Modifier
                        .zIndex(1f)
                        .padding(0.dp, 15.dp, 0.dp, 0.dp)
                        .fillMaxSize(),
                    textAlign = TextAlign.Center,
                )

                LinearProgressIndicator(
                    progress = value.value / (maxValue ?: 1f),
                    color = valueCorrelatingColor,
                    modifier = Modifier
                        .zIndex(0f)
                        .padding(5.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5.dp))
                        .shadow(0.dp, ambientColor = Color.White, spotColor = Color.Black)
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun backPack(
        showItemDisplay: MutableState<Boolean>,
        itemDisplayItem: MutableState<Item?>,
        showSortedInv: MutableState<Boolean>,
        items: List<Item?>,
        removeItem: (Item) -> Unit,
        addItemAtIndex: (Item, Item) -> Unit,
        slotSize: MutableState<Dp>
    ) {
        val dragMode = remember { mutableStateOf(false) }
        val draggedItem = remember { mutableStateOf<Item?>(null) }
        val draggedItemOffset = remember { mutableStateOf(Offset.Zero) }

        val typePriority = mapOf(
            ShortRangeWeapon::class to 0,
            LongRangeWeapon::class to 1,
            Armor::class to 2,
            Potion::class to 3,
            Consumable::class to 4,
            Miscellaneous::class to 5
        )

        Box(Modifier
            .fillMaxSize()
        )
        {
            //Background
            val backPackBackgroundOpen = remember { ImageLoader.loadImageFromResources("backPackBackgroundOpen.jpg").get().toPainter() }
            Image(
                backPackBackgroundOpen,
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
                val sortedItems = remember(items, showSortedInv.value) {
                    if (showSortedInv.value) {
                        items.sortedBy { item -> typePriority[item!!::class] ?: Int.MAX_VALUE }
                    } else {
                        items
                    }
                }

                val slotPadding = remember { 4.dp }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = slotSize.value + slotPadding),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        items = sortedItems,
                        key = { item -> item?.uuid ?: UUID.randomUUID() }
                    ) { item: Item? ->
                        Box(
                            Modifier
                                .animateItem()
                        ) {
                            invSlot(
                                item,
                                showItemDisplay,
                                itemDisplayItem,
                                draggedItem,
                                removeItem,
                                addItemAtIndex,
                                slotSize,
                                dragMode
                            )
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
                            val deleteIconRed = remember { ImageLoader.loadImageFromResources("deleteIconRed.png").get().toPainter() }
                            Image(
                                deleteIconRed,
                                "Delete",
                                Modifier
                                    .padding(10.dp)
                                    .align(Alignment.Center)
                                    .height(50.dp)
                                    .clickable {
                                        println("deleted item " + draggedItem.value!!.name)
                                        draggedItem.value = null
                                        dragMode.value = false
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
                                .shadow(10.dp, shape = boxShape.value, clip = false)
                                .background(color = lerp(Color.Transparent, Color.Black, 0.1f), shape = boxShape.value)
                                .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
                        ) {
                            Box(Modifier.padding(3.dp))
                            {
                                //BackgroundIcon
                                val icon = remember(draggedItem.value!!.icon) { draggedItem.value!!.icon.toPainter() }
                                Image(
                                    icon,
                                    draggedItem.value!!.iconName,
                                    Modifier
                                        .fillMaxSize()
                                )
                                //Name
                                Text(
                                    draggedItem.value!!.name,
                                    Modifier
                                        .padding(5.dp, 0.dp)
                                        .background(color = lerp(Color.Transparent, Color.White, 0.8f), shape = RoundedCornerShape(15.dp))
                                        .padding(10.dp, 0.dp)
                                )
                                Row(
                                    Modifier
                                        .align(Alignment.BottomEnd)
                                        .fillMaxWidth()
                                ) {
                                    //Filler
                                    Box(
                                        Modifier
                                            .weight(4f)
                                    )
                                    //Amount
                                    Text(
                                        draggedItem.value!!.amount.toString(),
                                        Modifier
                                            .padding(5.dp, 0.dp)
                                            .background(color = lerp(Color.Transparent, Color.White, 0.8f), shape = CircleShape)
                                            .padding(10.dp, 0.dp)
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
        draggedItem: MutableState<Item?>,
        removeItem: (Item) -> Unit,
        addItemAtIndex: (Item, Item) -> Unit,
        slotSize: MutableState<Dp>,
        dragMode: MutableState<Boolean>
    ) {
        val backGroundColor = remember { mutableStateOf(if(item !is EmptySlot) lerp(Color.Transparent, Color.Black, 0.1f) else Color.LightGray.copy(alpha = 0.2f)) }

        if (item != null) {
            val boxShape = remember(item.equipped) {
                mutableStateOf(
                    if (!item.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)
                )
            }
            var isHovered by remember { mutableStateOf(false) }
            val borderColor = remember(item.equipped, dragMode.value, isHovered) {
                mutableStateOf(
                    if(dragMode.value && isHovered) {
                        Color.Red
                    } else if (item is EmptySlot) {
                        Color.Black.copy(alpha = 0.1f)
                    } else if (!item.equipped) {
                        Color.Black.copy(
                            alpha = 0.3f
                        )
                    } else Color.Yellow.copy(alpha = 0.7f)
            )}

            val scale by animateFloatAsState(
                targetValue = if (isHovered && item !is EmptySlot && !dragMode.value) 1.08f else 1f,
                animationSpec = tween(durationMillis = 150)
            )

            val elevation by animateDpAsState(
                targetValue = if (isHovered && item !is EmptySlot && !dragMode.value) 6.dp else if (item !is EmptySlot) 2.dp else 0.dp,
                animationSpec = tween(durationMillis = 150)
            )

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(slotSize.value)
                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                    .onPointerEvent(PointerEventType.Exit) { isHovered = false }
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
                                if (dragMode.value) {
                                    println("drop item before " + item.name)
                                    addItemAtIndex(draggedItem.value!!, item)
                                    draggedItem.value = null
                                    dragMode.value = false
                                } else if (item !is EmptySlot) {
                                    itemDisplayItem.value = item
                                    showItemDisplay.value = true
                                }
                            },
                            onLongPress = {
                                if (item !is EmptySlot && draggedItem.value == null) {
                                    println("longpress " + item.name)
                                    draggedItem.value = item
                                    dragMode.value = true
                                    removeItem(item)
                                }
                            }
                        )
                    }
                    .pointerHoverIcon(
                        if (item !is EmptySlot) PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)) else PointerIcon(
                            Cursor(Cursor.DEFAULT_CURSOR)
                        )
                    )
            ) {
                if (item !is EmptySlot) {
                    Box(Modifier.padding(3.dp))
                    {
                        //BackgroundIcon
                        val icon = remember(item.icon) { item.icon.toPainter() }
                        Image(
                            icon,
                            item.iconName,
                            Modifier
                                .fillMaxSize()
                        )
                        //Name
                        Text(
                            item.name,
                            Modifier
                                .padding(5.dp, 0.dp)
                                .background(
                                    color = lerp(Color.Transparent, Color.White, 0.8f),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(10.dp, 0.dp)
                        )
                        Row(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .fillMaxWidth()
                        ) {
                            //Filler
                            Box(
                                Modifier
                                    .weight(4f)
                            )
                            //Amount
                            Text(
                                item.amount.toString(),
                                Modifier
                                    .padding(5.dp, 0.dp)
                                    .background(
                                        color = lerp(Color.Transparent, Color.White, 0.8f),
                                        shape = CircleShape
                                    )
                                    .padding(10.dp, 0.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                Modifier
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
