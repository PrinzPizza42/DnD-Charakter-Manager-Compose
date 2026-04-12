package main.ui

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import data.ImageLoader
import main.CharacterManager
import main.DropdownString
import main.Inventory
import main.itemClasses.Armor
import main.itemClasses.ArmorClasses
import main.itemClasses.Consumable
import main.itemClasses.EmptySlot
import main.itemClasses.Item
import main.itemClasses.Miscellaneous
import main.itemClasses.Potion
import main.itemClasses.weapons.LongRangeWeapon
import main.itemClasses.weapons.ShortRangeWeapon
import main.itemClasses.weapons.Weapon
import main.StepShifterIntBig
import main.StepShifterIntSmall
import main.getFloatInputOverlay
import main.loadPainterFromFile
import org.jetbrains.skiko.Cursor
import java.awt.FileDialog
import java.io.File
import java.util.UUID

object InventoryDisplay {
    val showSortedInv = mutableStateOf(false)

    @Composable
    fun displayInv(
        inv: MutableState<Inventory?>,
        modifier: Modifier,
        window: ComposeWindow
    ) {
        val slotSize = remember { mutableStateOf(100.dp) }

        Box(
            modifier = modifier
        ) {
            Column {
                sceneryAndBackPackTop(inv, slotSize, window)
                backPack(slotSize, window)
            }
        }
    }

    @Composable
    fun showItemDisplayStructure(
        item: MutableState<Item?>,
        window: ComposeWindow
    ) {
        val classes = listOf("Nahkampf-Waffe", "Fernkampf-Waffe", "Verbrauchsgegenstände", "Rüstung", "Trank", "Verschiedenes")
        val selectedClass = remember { mutableStateOf(classes[0]) }
        val hasSelected = remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        //InvDisplay overlay
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
        ) {
            val itemDisplayWith: Dp by remember(this.maxWidth) { mutableStateOf(if (this.maxWidth > 1000.dp) 1000.dp else this.maxWidth) }
            //ItemDisplay
            Box(
                Modifier
                    .align(Alignment.Center)
                    .zIndex(11f)
                    .size(itemDisplayWith, 700.dp)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Escape || keyEvent.key == Key.Enter) {
                            focusManager.clearFocus()
                            true
                        } else false
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                val itemDisplayBackGround =
                    remember { ImageLoader.loadImageFromResources("itemDisplayBackGround.png").get().toPainter() }
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
                        itemDisplayStats(item, hasSelected, classes, selectedClass, reloadKey)
                    }

                    //Item image
                    Box(Modifier.weight(1f)) {
                        itemDisplayImage(item, window, reloadKey)
                    }
                }
            }
        }
    }

    @Composable
    fun itemDisplayStats(
        itemDisplayItem: MutableState<Item?>,
        hasSelected: MutableState<Boolean>,
        classes: List<String>,
        selectedClass: MutableState<String>,
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
                                CharacterManager.selectedInventory.value!!.addItem(itemDisplayItem.value!!)
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
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text("Name")
                    },
                    singleLine = true,
                )

                //Description
                val descInput = remember { mutableStateOf(TextFieldValue(itemDisplayItem.value!!.description)) }
                TextField(
                    value = descInput.value,
                    onValueChange = {
                        descInput.value = it
                        itemDisplayItem.value!!.description = it.text
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
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
                            CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
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

                if (itemDisplayItem.value is Armor) {
                    //Armor Value
                    val armor: Armor = itemDisplayItem.value as Armor
                    val armorValue = remember { mutableStateOf(armor.armorValue) }
                    val armorValueRange = IntRange(0, 20)

                    StepShifterIntSmall(
                        "Rüstungswert:",
                        armorValueRange,
                        armorValue,
                        {
                            armor.armorValue += 1
                            CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                            println("Increased 1 to ${armor.armorValue}")
                        },
                        { 
                            armor.armorValue -= 1 
                            CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                        }
                    )

                    //Armor Class
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("Rüstungsklasse:", modifier = Modifier.width(150.dp))
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            DropdownString(
                                null,
                                ArmorClasses.entries.map { it.name },
                                mutableStateOf(armor.armorClass.toString()),
                                { newClass ->
                                    armor.armorClass = ArmorClasses.valueOf(newClass)
                                    CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                                    println("Armor class value changed to ${armor.armorClass}")
                                }
                            )

                        }
                    }
                }

                //Weight
                val weightValue = remember { mutableStateOf(itemDisplayItem.value!!.weight) }
                val weightRange = IntRange(0, 500)

                StepShifterIntBig(
                    "Gewicht:",
                    weightRange,
                    weightValue,
                    { increase -> 
                        itemDisplayItem.value!!.weight += increase 
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    },
                    { decrease -> 
                        itemDisplayItem.value!!.weight -= decrease 
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    }
                )

                //Value
                val valueValue = remember { mutableStateOf(itemDisplayItem.value!!.valueInGold) }
                val valueRange = IntRange(0, 1000)

                StepShifterIntBig(
                    "Wert in Gold:",
                    valueRange,
                    valueValue,
                    { increase -> 
                        itemDisplayItem.value!!.valueInGold += increase 
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    },
                    { decrease -> 
                        itemDisplayItem.value!!.valueInGold -= decrease 
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    }
                )

                //Amount
                val amountValue = remember { mutableStateOf(itemDisplayItem.value!!.amount) }
                val amountRange = IntRange(0, 500)

                StepShifterIntBig(
                    "Menge:",
                    amountRange,
                    amountValue,
                    { increase -> 
                        itemDisplayItem.value!!.amount += increase 
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    },
                    { decrease -> 
                        itemDisplayItem.value!!.amount -= decrease 
                        CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                    }
                )

                //Equipped
                val equipped = remember(
                    itemDisplayItem.value,
                    itemDisplayItem.value!!.equipped
                ) { mutableStateOf(itemDisplayItem.value!!.equipped) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Ausgerüstet", Modifier.width(150.dp))
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Checkbox(
                            checked = equipped.value,
                            onCheckedChange = {
                                itemDisplayItem.value!!.equipped = it
                                equipped.value = it
                                CharacterManager.selectedInventory.value?.notifyItemChanged(itemDisplayItem.value!!)
                            },
                            modifier = Modifier
                                .width(30.dp)
                                .padding(horizontal = 10.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Yellow,
                                uncheckedColor = Color.Black,
                                checkmarkColor = Color.Black
                            )
                        )
                    }
                }

                //Image reset
                Text(
                    "Bild zurücksetzen",
                    Modifier.clickable(
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun itemDisplayImage(
        item: MutableState<Item?>,
        window: ComposeWindow,
        reloadKey: MutableState<Int>
    ) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            if (item.value == null) {
                Text("No image for this item found")
            } else {
                key(reloadKey.value) {
                    val painter: Painter = remember(item.value!!.icon) { item.value!!.icon.toPainter() }
                    var showPopUp by remember { mutableStateOf(false) }
                    var path by remember { mutableStateOf("") }
                    val selectedFile: MutableState<File?> = remember { mutableStateOf(null) }
                    if (showPopUp) {
                        Popup(
                            onDismissRequest = { showPopUp = false },
                            alignment = Alignment.Center,
                            properties = PopupProperties(focusable = true)
                        ) {
                            Column(
                                Modifier
                                    .shadow(10.dp, RoundedCornerShape(10.dp))
                                    .background(
                                        Color.White,
                                        androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                    )
                                    .width(600.dp)
                                    .height(100.dp)
                            ) {
                                Row {
                                    val availableFiles: SnapshotStateList<File?> = remember { mutableStateListOf(null) }

                                    TextField(
                                        value = path,
                                        onValueChange = {
                                            path = it
                                        },
                                        modifier = Modifier
                                            .weight(3f),
                                        label = {
                                            Text("Pfad zum Ordner der Datei")
                                        },
                                        placeholder = {
                                            Text("Pfad")
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                    )

                                    val expanded = remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = expanded.value,
                                        onExpandedChange = {
                                            expanded.value = !expanded.value
                                        }
                                    ) {
                                        Button(
                                            content = { Text(if (selectedFile.value != null) selectedFile.value!!.name else "/") },
                                            onClick = {
                                                availableFiles.clear()
                                                availableFiles.addAll(getFilesInDirectory(path).toMutableStateList())
                                            }
                                        )
                                        ExposedDropdownMenu(
                                            modifier = Modifier.width(450.dp),
                                            expanded = expanded.value,
                                            onDismissRequest = {
                                                expanded.value = false
                                            }
                                        ) {
                                            for (availableFile in availableFiles) {
                                                if (availableFile != null) availableFilesDropDownMenuItem(
                                                    availableFile,
                                                    expanded,
                                                    selectedFile
                                                )
                                            }
                                        }
                                    }
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Button(
                                        onClick = {
                                            setImage(path, selectedFile.value!!.name, item, reloadKey)
                                            showPopUp = false
                                        }
                                    ) {
                                        Text("Übernehmen")
                                    }
                                }
                            }
                        }
                    }
                    Image(
                        painter = painter,
                        contentDescription = "item icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable(
                                onClick = {
                                    var directory = ""
                                    var preFile = ""

                                    val isWindows = System.getProperty("os.name").contains("Windows", ignoreCase = true)

                                    if (isWindows) {
                                        val dialog = FileDialog(window, "Wähle eine Datei", FileDialog.LOAD)
                                        dialog.isVisible = true

                                        directory = dialog.directory
                                        preFile = dialog.file
                                    } else {
                                        showPopUp = true
                                    }

                                    if (isWindows) {
                                        try {
                                            setImage(directory, preFile, item, reloadKey)
                                        } catch (e: NullPointerException) {
                                            println("Could not get image from filepicker")
                                            e.printStackTrace()
                                        }
                                    } else showPopUp = true
                                    showPopUp = true
                                }
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    fun getFilesInDirectory(directoryPath: String): List<File> {
        val directory = File(directoryPath)

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        return directory.listFiles()?.filter { it.isFile && (it.extension.contains("png") || it.extension.contains("jpg") || it.extension.contains("svg")) } ?: emptyList()
    }

    fun setImage(directory: String, preFile: String, item: MutableState<Item?>, reloadKey: MutableState<Int>) {
        if (directory != null && preFile != null) {
            val file = File(directory, preFile)
            val uuid = UUID.randomUUID().toString()
            val finalFileName = "${file.nameWithoutExtension}_${uuid}.${file.extension}"
            ImageLoader.copyImageToUserImagesFolder(file, finalFileName)
            item.value!!.userIconName = finalFileName
            reloadKey.value++
        }
    }

    @Composable
    fun availableFilesDropDownMenuItem(file: File, expanded: MutableState<Boolean>, selectedFile: MutableState<File?>) {
        val path = file.toPath().toString()

        DropdownMenuItem(
            onClick = {
                selectedFile.value = file
                expanded.value = false
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var painter by remember(path) { mutableStateOf(loadPainterFromFile(path)) }

                if (painter != null) {
                    Image(
                        painter = painter!!,
                        contentDescription = "loaded image",
                        modifier = Modifier
                            .size(90.dp)
                            .padding(5.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "File not found icon",
                        modifier = Modifier
                            .size(90.dp)
                            .padding(5.dp)
                    )
                }
                Text(modifier = Modifier.padding(5.dp), text = file.name)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun sceneryAndBackPackTop(
        inv: MutableState<Inventory?>,
        slotSize: MutableState<Dp>,
        window: ComposeWindow
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
                val backPackTopOpenLeft =
                    remember { ImageLoader.loadImageFromResources("backPackTopOpenLeft.png").get().toPainter() }
                Image(
                    backPackTopOpenLeft,
                    contentDescription = "Backpack top left",
                    modifier = Modifier.weight(1f),
                    contentScale = ContentScale.FillBounds
                )
                val backPackTopOpenMiddle =
                    remember { ImageLoader.loadImageFromResources("backPackTopOpenMiddle.png").get().toPainter() }
                Image(
                    backPackTopOpenMiddle,
                    contentDescription = "Backpack top middle",
                    modifier = Modifier.width(180.dp),
                    contentScale = ContentScale.FillBounds
                )
                val backPackTopOpenRight =
                    remember { ImageLoader.loadImageFromResources("backPackTopOpenRight.png").get().toPainter() }
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
                                modifier = Modifier.width(150.dp),
                            )

                            val backGroundColor =
                                remember { lerp(Color.Transparent, Color.Black, 0.2f) }

                            Column(
                                Modifier
                                    .size(150.dp, 100.dp)
                                    .background(
                                        backGroundColor,
                                        androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                                    )
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
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

                            val items = CharacterManager.selectedInventory.value!!.items
                            val backPackWeight = remember(items) {
                                mutableStateOf(items.toMutableList().sumOf { it.weight * it.amount }.toFloat())
                            }
                            var backPackWeightUIValue by remember { mutableStateOf(inv.value!!.maxCarryingCapacity) }

                            Box(
                                Modifier
                                    .onClick {
                                        Overlay.showOverlay({
                                            val weightChangerColor = remember {
                                                lerp(
                                                    Color.Transparent,
                                                    Color.White,
                                                    0.9f
                                                )
                                            }

                                            getFloatInputOverlay(
                                                Modifier
                                                    .background(
                                                        weightChangerColor,
                                                        androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                                                    )
                                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(5.dp)),
                                                inv.value!!.maxCarryingCapacity,
                                                "Maximalgewicht",
                                                onConfirm = { value ->
                                                    println(value.toString())
                                                    Overlay.closeOverlay()
                                                    inv.value!!.maxCarryingCapacity = value
                                                    backPackWeightUIValue = value
                                                    println("confirmed")
                                                },
                                                onDismiss = {
                                                    Overlay.closeOverlay()
                                                    println("dismissed")
                                                }
                                            )
                                        })
                                    }
                            ) {
                                backPackTopValue(modifier, backPackWeight, backPackWeightUIValue, "Gewicht")
                            }

                            //BackPack value
                            val backPackValue = remember(items) {
                                mutableStateOf(
                                    items.toMutableList().sumOf { it!!.valueInGold * it.amount }.toFloat()
                                )
                            }
                            backPackTopValue(modifier, backPackValue, null, "Wert in Gold")

                            Button(
                                onClick = {
                                    println("adding item")
                                    Overlay.showOverlay({
                                        showItemDisplayStructure(
                                            mutableStateOf(null),
                                            window
                                        )
                                    })
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
        val valueCorrelatingColor by remember(value, maxValue) {
            mutableStateOf(
                if (maxValue == null) Color.DarkGray
                else lerp(Color.DarkGray, Color.Red, value.value / maxValue)
            )
        }

        val backGroundColor = remember { lerp(Color.Transparent, Color.Black, 0.2f) }
        val textColor = remember { Color.White }

        Column(
            modifier
                .background(backGroundColor, androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
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
                val text: String = "${value.value}" + if (maxValue != null) "/${maxValue}" else ""
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
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
                        .shadow(0.dp, ambientColor = Color.White, spotColor = Color.Black)
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun backPack(
        slotSize: MutableState<Dp>,
        window: ComposeWindow
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

        Box(Modifier.fillMaxSize()) {
            //Background
            val backPackBackgroundOpen =
                remember { ImageLoader.loadImageFromResources("backPackBackgroundOpen.jpg").get().toPainter() }
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
                val inv = CharacterManager.selectedInventory.value!!
                val displayItems = if (showSortedInv.value) {
                    inv.getItemsSortedByClass()
                } else {
                    inv.items
                }

                val slotPadding = remember { 4.dp }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = slotSize.value + slotPadding),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        items = displayItems,
                        key = { item -> item.uuid }
                    ) { item ->
                        Box(
                            Modifier
                                .animateItem()
                        ) {
                            invSlot(
                                item,
                                draggedItem,
                                slotSize,
                                dragMode,
                                window
                            )
                        }
                    }
                }
            }

            //Overlay
            if (draggedItem.value != null) {
                val density = LocalDensity.current
                val scaleFactor = density.density
                val overlayBackGroundColor by animateColorAsState(
                    targetValue = if (draggedItem.value != null) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(
                        alpha = 0f
                    ),
                    animationSpec = tween(durationMillis = 500)
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .background(overlayBackGroundColor)
                ) {
                    Column {
                        //Placeholder
                        Box(Modifier.weight(1f))

                        //Delete Button
                        Box(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            val deleteIconRed =
                                remember { ImageLoader.loadImageFromResources("deleteIconRed.png").get().toPainter() }
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
                val borderColor = remember(draggedItem.value?.equipped) {
                    mutableStateOf(
                        if (draggedItem.value!! is EmptySlot) Color.Black.copy(alpha = 0.1f) else if (!draggedItem.value!!.equipped) Color.Black.copy(
                            alpha = 0.3f
                        ) else Color.Yellow.copy(alpha = 0.7f)
                    )
                }

                val boxShape = remember(draggedItem.value?.equipped) {
                    mutableStateOf(
                        if (!draggedItem.value!!.equipped) androidx.compose.foundation.shape.RoundedCornerShape(10.dp) else CutCornerShape(
                            10.dp
                        )
                    )
                }

                //ItemDisplay Overlay
                Box(
                    Modifier
                        .zIndex(2f)
                        .fillMaxSize()
                        .offset(
                            (draggedItemOffset.value.x / scaleFactor).dp,
                            (draggedItemOffset.value.y / scaleFactor).dp
                        )
                ) {
                    Column {
                        Box(
                            Modifier
                                .size(100.dp)
                                .shadow(10.dp, shape = boxShape.value, clip = false)
                                .background(
                                    color = lerp(Color.Transparent, Color.Black, 0.1f),
                                    shape = boxShape.value
                                )
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
                                        .background(
                                            color = lerp(
                                                Color.Transparent,
                                                Color.White,
                                                0.8f
                                            ), shape = RoundedCornerShape(15.dp)
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
                                        Modifier.weight(4f)
                                    )
                                    //Amount
                                    Text(
                                        draggedItem.value!!.amount.toString(),
                                        Modifier
                                            .padding(5.dp, 0.dp)
                                            .background(
                                                color = lerp(
                                                    Color.Transparent,
                                                    Color.White,
                                                    0.8f
                                                ), shape = CircleShape
                                            )
                                            .padding(10.dp, 0.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            "Klicke um das item einzuordnen",
                            Modifier.padding(8.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun invSlot(
        item: Item?,
        draggedItem: MutableState<Item?>,
        slotSize: MutableState<Dp>,
        dragMode: MutableState<Boolean>,
        window: ComposeWindow,
    ) {
        val inv = CharacterManager.selectedInventory

        val mutation = item?.mutationCount ?: 0

        val backGroundColor = remember(item, mutation) {
            mutableStateOf(
                if (item != null && item !is EmptySlot) lerp(
                    Color.Transparent,
                    Color.Black,
                    0.1f
                ) else Color.LightGray.copy(alpha = 0.2f)
            )
        }

        if (item != null) {
            val boxShape = remember(item, mutation, item.equipped) {
                mutableStateOf(
                    if (!item.equipped) androidx.compose.foundation.shape.RoundedCornerShape(10.dp) else androidx.compose.foundation.shape.CutCornerShape(
                        10.dp
                    )
                )
            }
            var isHovered by remember { mutableStateOf(false) }
            val borderColor = remember(item, mutation, item.equipped, dragMode.value, isHovered) {
                mutableStateOf(
                    if (dragMode.value && isHovered) {
                        Color.Red
                    } else if (item is EmptySlot) {
                        Color.Black.copy(alpha = 0.1f)
                    } else if (!item.equipped) {
                        Color.Black.copy(
                            alpha = 0.3f
                        )
                    } else Color.Yellow.copy(alpha = 0.7f)
                )
            }

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
                    .shadow(elevation, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp), clip = false)
                    .background(backGroundColor.value, shape = boxShape.value)
                    .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                println("clicked item " + item.name)
                                if (dragMode.value) {
                                    println("drop item before " + item.name)
                                    inv.value!!.addItemAtIndex(draggedItem.value!!, item)
                                    draggedItem.value = null
                                    dragMode.value = false
                                } else if (item !is EmptySlot) {
                                    Overlay.showOverlay({
                                        showItemDisplayStructure(mutableStateOf(item), window)
                                    })
                                }
                            },
                            onLongPress = {
                                if (item !is EmptySlot && draggedItem.value == null) {
                                    draggedItem.value = item
                                    dragMode.value = true
                                    inv.value!!.removeItem(item)
                                }
                            }
                        )
                    }
                    .pointerHoverIcon(
                        if (item !is EmptySlot) PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)) else PointerIcon(
                            org.jetbrains.skiko.Cursor(Cursor.DEFAULT_CURSOR)
                        )
                    )
            ) {
                if (item !is EmptySlot) {
                    Box(Modifier.padding(3.dp))
                    {
                        //BackgroundIcon
                        val icon = remember(item, mutation, item.icon) { item.icon.toPainter() }
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
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp)
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
        }
 else {
            Box(
                Modifier
                    .size(100.dp)
                    .background(
                        backGroundColor.value.copy(alpha = 0.5f),
                        androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                    )
            )
        }
    }

    @Composable
    fun getRandomSceneryImage() {
        val imagePath = remember { "sceneryImages/" + (1..7).random() + ".jpeg" }
        return Image(
            painterResource(imagePath),
            "Scenery Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}