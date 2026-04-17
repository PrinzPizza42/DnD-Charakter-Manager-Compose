package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import data.CharacterManager
import data.CustomWindow
import data.ItemDisplayManager
import data.WindowManager
import disk.ImageLoader
import itemClasses.Armor
import itemClasses.ArmorClasses
import itemClasses.Consumable
import itemClasses.Item
import itemClasses.Miscellaneous
import itemClasses.Potion
import itemClasses.weapons.LongRangeWeapon
import itemClasses.weapons.ShortRangeWeapon
import itemClasses.weapons.Weapon
import java.awt.FileDialog
import java.io.File

class ItemDisplay(
    var item: MutableState<Item?> = mutableStateOf<Item?>(null)
) {
    var show by mutableStateOf(false)
    var window by mutableStateOf<CustomWindow?>(null)

    val classes = listOf("Nahkampf-Waffe", "Fernkampf-Waffe", "Verbrauchsgegenstände", "Rüstung", "Trank", "Verschiedenes")
    var selectedClass by mutableStateOf(classes[0])
    var hasSelected by mutableStateOf(false)

    fun draw() {
        if(window == null) {
            window = WindowManager.openNewWindow(
                onCloseRequest = {
                    ItemDisplayManager.removeItemDisplay(this)
                },
                content = { itemDisplayContent() },
                title = mutableStateOf("Item Display: ${item.value?.name}")
            )
        }
    }

    fun close() {
        show = false
        item.value = null
        if(window != null) {
            window!!.close()
            window = null
        }
    }

    @Composable
    private fun itemDisplayContent() {
        val focusManager = LocalFocusManager.current

        LaunchedEffect(CharacterManager.selectedInventory.value) {
            if(CharacterManager.selectedInventory.value == null) close()
        }

        //InvDisplay overlay
        BoxWithConstraints(Modifier.fillMaxSize().background(Color.Gray)) {
            //ItemDisplay
            Box(
                Modifier
                    .align(Alignment.Center)
                    .zIndex(11f)
                    .size(this.maxWidth, this.maxHeight)
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
                val itemDisplayBackGround = remember { ImageLoader.loadImageFromResources("itemDisplayBackGround.png").get().toPainter() }
                Image(
                    itemDisplayBackGround,
                    "itemDisplayBackGround",
                    Modifier
                        .zIndex(0f)
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                //Foreground
                Column(Modifier.zIndex(1f).fillMaxSize()) {
                    Box(Modifier.fillMaxWidth().weight(0.025f))

                    Row(Modifier.weight(1f)) {
                        val reloadKey = remember { mutableStateOf(0) }

                        Box(Modifier.fillMaxHeight().weight(0.14f))

                        //Item stats
                        Box(Modifier.weight(1f)) {
                            itemDisplayStats(reloadKey)
                        }

                        Box(Modifier.fillMaxHeight().weight(0.1f))

                        //Item image
                        Box(Modifier.weight(1f)) {
                            itemDisplayImage(reloadKey)
                        }

                        Box(Modifier.fillMaxHeight().weight(0.14f))
                    }

                    Box(Modifier.fillMaxWidth().weight(0.025f))
                }
            }
        }
    }

    @Composable
    private fun itemDisplayStats(reloadKey: MutableState<Int>) {
        Row(Modifier.fillMaxSize()) {
            //Item Create
            if (item.value == null && !hasSelected) {
                itemDisplayStatsCreateDisplay()
            }
            //Normal Display
            else if (item.value != null) {
                itemDisplayStatsNormalDisplay(reloadKey)
            } else println("Something went wrong")
        }
    }

    @Composable
    private fun itemDisplayStatsCreateDisplay() {
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
                            selected = (option == selectedClass),
                            onClick = {
                                selectedClass = option
                                hasSelected = true
                                //create an empty item
                                when (selectedClass) {
                                    "Nahkampf-Waffe" -> item.value = ShortRangeWeapon("", "", 1, 1, 1, "")
                                    "Fernkampf-Waffe" -> item.value = LongRangeWeapon("", "", 1, 1, 1, "")
                                    "Verbrauchsgegenstände" -> item.value = Consumable("", "", 1, 1, 1)
                                    "Rüstung" -> item.value = Armor("", "", 1, 1, 1, 10, ArmorClasses.MEDIUM)
                                    "Trank" -> item.value = Potion("", "", 1, 1, 1)
                                    "Verschiedenes" -> item.value = Miscellaneous("", "", 1, 1, 1)
                                }
                                println("Created ${item.value}")
                                CharacterManager.selectedInventory.value!!.addItem(item.value!!)
                            }
                        )
                        Text(option)
                    }
                }
            }
        }
    }

    @Composable
    private fun itemDisplayStatsNormalDisplay(reloadKey: MutableState<Int>) {
        key(item) {
            Column(Modifier.fillMaxSize()) {
                //Name
                val nameInput = remember { mutableStateOf(TextFieldValue(item.value!!.name)) }
                TextField(
                    value = nameInput.value,
                    onValueChange = {
                        nameInput.value = it
                        item.value!!.name = it.text
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Name")
                    },
                    singleLine = true,
                )

                //Description
                val descInput = remember { mutableStateOf(TextFieldValue(item.value!!.description)) }
                TextField(
                    value = descInput.value,
                    onValueChange = {
                        descInput.value = it
                        item.value!!.description = it.text
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text("Beschreibung")
                    },
                    singleLine = true,
                )

                if (item.value is Weapon) {
                    //Damage
                    val weapon: Weapon = item.value as Weapon
                    val dmgInput = remember { mutableStateOf(TextFieldValue(weapon.damage)) }
                    TextField(
                        value = dmgInput.value,
                        onValueChange = {
                            dmgInput.value = it
                            weapon.damage = it.text
                            CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text("Schaden")
                        },
                        singleLine = true,
                    )
                    item.value = weapon
                }

                if (item.value is Armor) {
                    //Armor Value
                    val armor: Armor = item.value as Armor
                    val armorValue = remember { mutableStateOf(armor.armorValue) }
                    val armorValueRange = IntRange(0, 20)

                    StepShifterIntSmall(
                        "Rüstungswert:",
                        armorValueRange,
                        armorValue,
                        {
                            armor.armorValue += 1
                            CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                            println("Increased 1 to ${armor.armorValue}")
                        },
                        {
                            armor.armorValue -= 1
                            CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
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
                                    CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                                    println("Armor class value changed to ${armor.armorClass}")
                                }
                            )

                        }
                    }
                }

                //Weight
                val weightValue = remember { mutableStateOf(item.value!!.weight) }
                val weightRange = IntRange(0, 500)

                StepShifterIntBig(
                    "Gewicht:",
                    weightRange,
                    weightValue,
                    { increase ->
                        item.value!!.weight += increase
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    },
                    { decrease ->
                        item.value!!.weight -= decrease
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    }
                )

                //Value
                val valueValue = remember { mutableStateOf(item.value!!.valueInGold) }
                val valueRange = IntRange(0, 1000)

                StepShifterIntBig(
                    "Wert in Gold:",
                    valueRange,
                    valueValue,
                    { increase ->
                        item.value!!.valueInGold += increase
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    },
                    { decrease ->
                        item.value!!.valueInGold -= decrease
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    }
                )

                //Amount
                val amountValue = remember { mutableStateOf(item.value!!.amount) }
                val amountRange = IntRange(0, 500)

                StepShifterIntBig(
                    "Menge:",
                    amountRange,
                    amountValue,
                    { increase ->
                        item.value!!.amount += increase
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    },
                    { decrease ->
                        item.value!!.amount -= decrease
                        CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
                    }
                )

                //Equipped
                val equipped = remember(
                    item.value,
                    item.value!!.equipped
                ) { mutableStateOf(item.value!!.equipped) }
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
                                item.value!!.equipped = it
                                equipped.value = it
                                CharacterManager.selectedInventory.value?.notifyItemChanged(item.value!!)
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
                            item.value!!.userIconName = null
                            reloadKey.value++
                            println("Reset userIconName of item ${item.value!!.name}")
                        }
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun itemDisplayImage(reloadKey: MutableState<Int>) {
        val window = WindowManager.LocalWindow.current

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
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
                                                availableFiles.addAll(
                                                    InventoryDisplay.getFilesInDirectory(path).toMutableStateList()
                                                )
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
                                                if (availableFile != null) InventoryDisplay.availableFilesDropDownMenuItem(
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
                                            InventoryDisplay.setImage(path, selectedFile.value!!.name, item.value, reloadKey)
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
                                            InventoryDisplay.setImage(directory, preFile, item.value, reloadKey)
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
}