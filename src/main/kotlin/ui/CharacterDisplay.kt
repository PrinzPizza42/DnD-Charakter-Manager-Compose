package ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.window.Popup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import data.CharacterManager.selectedInventory
import data.ItemDisplayManager
import data.WindowManager
import data.equippmentSlots.ArmorSlot
import data.equippmentSlots.ConsumableSlot
import data.equippmentSlots.ItemSlot
import data.equippmentSlots.MiscellaneousSlot
import data.equippmentSlots.PotionSlot
import data.equippmentSlots.weapons.LongRangeWeaponSlot
import data.equippmentSlots.weapons.ShortRangeWeaponSlot
import data.equippmentSlots.weapons.WeaponSlot
import data.statsTab.StatsTabModulData
import disk.ImageLoader
import itemClasses.Item
import org.jetbrains.skiko.Cursor
import java.awt.FileDialog
import java.io.File
import java.util.UUID

object CharacterDisplay {
    var equipmentEditMode by mutableStateOf(false)
    var statsEditMode by mutableStateOf(false)

    @Composable
    fun displayCharInfo() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (selectedInventory.value != null) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        Text("Character Info")
                        IconButton(
                            onClick = {
                                selectedInventory.value?.addModul(
                                    StatsTabModulData.CounterModul()
                                )
                            },
                            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "add counter") }
                        )
                        IconButton(
                            onClick = {
                                selectedInventory.value?.addModul(
                                    StatsTabModulData.TextModul()
                                )
                            },
                            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "add text") }
                        )
                        IconButton(
                            onClick = {
                                statsEditMode = !statsEditMode
                            },
                            content = { Icon(imageVector = if(statsEditMode) Icons.AutoMirrored.Default.ArrowBack else Icons.Default.Edit, contentDescription = "Edit mode") }
                        )
                    }
                    for((index, modul) in selectedInventory.value!!.statsTabModulList.withIndex()) {
                        StatsTabModulView(modul, index)
                    }
                }
            } else {
                Text("Kein Inventar ausgewählt")
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun StatsTabModulView(modulData: StatsTabModulData, index: Int) {
        val baseModifier = Modifier
            .padding(2.dp)
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .padding(5.dp)

        val actualWidth = if (modulData is StatsTabModulData.CounterModul && modulData.widthValue < 260f) 260f else modulData.widthValue
        val layoutModifier = if (modulData.fillMaxWidth) {
            baseModifier
                .fillMaxWidth()
                .height(modulData.heightValue.dp)
        } else {
            baseModifier
                .width(actualWidth.dp)
                .height(modulData.heightValue.dp)
        }

        val editModulDimensions = remember { mutableStateOf(false) }

        Box(layoutModifier) {
            if(editModulDimensions.value) {
                ModulSettingsPopUp(modulData, editModulDimensions, index)
            }
            if (statsEditMode) {
                Box(
                    Modifier
                        .zIndex(2f)
                        .padding(3.dp)
                        .height(modulData.heightValue.dp)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .onClick {},
                    contentAlignment = Alignment.Center
                )
                {
                    Row {
                        Row(
                            Modifier.background(Color.LightGray, RoundedCornerShape(10.dp))
                        ) {
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.moveModulDown(
                                        selectedInventory.value!!.statsTabModulList.indexOf(
                                            modulData
                                        )
                                    )
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "move back",
                                        modifier = Modifier.rotate(90f)
                                    )
                                }
                            )
                            Spacer(Modifier.width(5.dp))
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.moveModulUp(
                                        selectedInventory.value!!.statsTabModulList.indexOf(
                                            modulData
                                        )
                                    )
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                        contentDescription = "move forward",
                                        modifier = Modifier.rotate(90f)
                                    )
                                }
                            )
                            Spacer(Modifier.width(5.dp))
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.removeModul(
                                        selectedInventory.value!!.statsTabModulList.indexOf(
                                            modulData
                                        )
                                    )
                                },
                                content = { Icon(imageVector = Icons.Default.Delete, contentDescription = "delete") }
                            )
                        }
                        Spacer(Modifier.width(5.dp))
                        Column(Modifier.background(Color.LightGray, RoundedCornerShape(10.dp))) {
                            IconButton(
                                onClick = {
                                    editModulDimensions.value = true
                                },
                                content = { Icon(imageVector = Icons.Default.Edit, contentDescription = "edit modul dimensions") }
                            )
                        }
                    }
                }
            }
            when (modulData) {
                is StatsTabModulData.TextModul -> {
                    Column(Modifier.fillMaxSize()) {
                        Text(modulData.title, Modifier
                            .background(Color.Gray, RoundedCornerShape(10.dp))
                            .padding(2.dp)
                        )
                        TextField(
                            modifier = Modifier.fillMaxSize(),
                            value = modulData.textContent,
                            onValueChange = { newText ->
                                selectedInventory.value?.statsTabModulList?.set(index, modulData.copy(textContent = newText))
                            }
                        )
                    }
                }
                is StatsTabModulData.CounterModul -> {
                    Column(Modifier.fillMaxSize()) {
                        Text(modulData.title, Modifier
                            .background(Color.Gray, RoundedCornerShape(10.dp))
                            .padding(2.dp)
                        )
                        val counterState = remember(modulData.counter) { mutableStateOf(modulData.counter) }
                        StepShifterIntBig(
                            label = "",
                            range = IntRange(modulData.intRangeMin, modulData.intRangeMax),
                            value = counterState,
                            onDecrease = { step ->
                                selectedInventory.value?.statsTabModulList?.set(index, modulData.copy(counter = modulData.counter - step))
                            },
                            onIncrease = { step ->
                                selectedInventory.value?.statsTabModulList?.set(index, modulData.copy(counter = modulData.counter + step))
                            },
                            bigStep = modulData.bigStepSize
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ModulSettingsPopUp(modulData: StatsTabModulData, editModulDimensions: MutableState<Boolean>, index: Int) {
        Popup(
            onDismissRequest = { editModulDimensions.value = false },
            alignment = Alignment.Center,
            properties = PopupProperties(focusable = true)
        ) {
            Column(
                Modifier
                    .padding(7.dp)
                    .shadow(10.dp, RoundedCornerShape(10.dp))
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                    .padding(7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dimensions
                Text("Größe", fontSize = TextUnit(20f, TextUnitType.Sp))

                var checked by remember { mutableStateOf(modulData.fillMaxWidth) }
                Text("Gesamte Breite einnehmen")
                Checkbox(
                    checked,
                    onCheckedChange = {
                        modulData.fillMaxWidth = it
                        checked = it
                    }
                )

                Spacer(Modifier.height(10.dp))
                Text("Breite")
                FloatInputOverlay(
                    Modifier.width(250.dp),
                    modulData.widthValue,
                    onConfirm = { modulData.widthValue = it },
                    onDismiss = {},
                    text = "",
                    showCancelButton = false
                )

                Spacer(Modifier.height(10.dp))
                Text("Höhe")
                FloatInputOverlay(
                    Modifier.width(250.dp),
                    modulData.heightValue,
                    onConfirm = { modulData.heightValue = it },
                    onDismiss = {},
                    text = "",
                    showCancelButton = false
                )
                Spacer(Modifier.height(20.dp))

                // Modul settings
                Text("Moduleinstellungen", fontSize = TextUnit(20f, TextUnitType.Sp))

                var titel by remember { mutableStateOf(modulData.title) }
                Text("Titel")
                TextField(
                    modifier = Modifier.width(250.dp),
                    value = titel,
                    onValueChange = { newText ->
                        modulData.title = newText
                        titel = newText
                    },
                    singleLine = true
                )

                if(modulData is StatsTabModulData.CounterModul) {
                    Spacer(Modifier.height(10.dp))
                    Text("Schrittgröße")
                    FloatInputOverlay(
                        Modifier.width(250.dp),
                        modulData.bigStepSize.toFloat(),
                        onConfirm = { modulData.bigStepSize = it.toInt() },
                        onDismiss = {},
                        text = "",
                        showCancelButton = false
                    )

                    Spacer(Modifier.height(10.dp))
                    Text("Mindest Wert")
                    FloatInputOverlay(
                        Modifier.width(250.dp),
                        modulData.intRangeMin.toFloat(),
                        onConfirm = { modulData.intRangeMin = it.toInt() },
                        onDismiss = {},
                        text = "",
                        showCancelButton = false
                    )

                    Spacer(Modifier.height(10.dp))
                    Text("Maximal Wert")
                    FloatInputOverlay(
                        Modifier.width(250.dp),
                        modulData.intRangeMax.toFloat(),
                        onConfirm = { modulData.intRangeMax = it.toInt() },
                        onDismiss = {},
                        text = "",
                        showCancelButton = false
                    )
                }
            }

        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun displayCharEquipment() {
        val fullWindow = remember { mutableStateOf(false) }
        var showPopUp by remember { mutableStateOf(false) }
        val window = WindowManager.LocalWindow.current
        var path by remember { mutableStateOf("") }
        val selectedFile: MutableState<File?> = remember { mutableStateOf(null) }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (selectedInventory.value != null) {
                val background = remember { lerp(Color.Gray, Color.Transparent, 0.3f) }
                val slotSize = remember { mutableStateOf(130.dp) }
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
                                        setImage(path, selectedFile.value!!.name)
                                        showPopUp = false
                                    }
                                ) {
                                    Text("Übernehmen")
                                }
                            }
                        }
                    }
                }
                Box {
                    Image(
                        selectedInventory.value!!.icon.toComposeImageBitmap(),
                        "Character Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
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
                                            setImage(directory, preFile)
                                        } catch (e: NullPointerException) {
                                            println("Could not get image from filepicker")
                                            e.printStackTrace()
                                        }
                                    } else showPopUp = true
                                    showPopUp = true
                                    showPopUp = false
                                }
                            ),
                    )
                    Row(Modifier.fillMaxSize()) {
                        equippedElementTab(slotSize, background, fullWindow)
                        if (!fullWindow.value) Box(Modifier.weight(1f))
                    }
                }

            } else {
                Text("Kein Inventar ausgewählt")
            }
        }
    }

    @Composable
    fun equipmentTabTop(animatedExtended: Dp, maxWith: Dp, fullWindow: MutableState<Boolean>) {
        val showPopup = remember { mutableStateOf(false) }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(if (animatedExtended == maxWith) maxWith else 0.dp).height(50.dp)
        ) {
            Row(
                Modifier
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                if (animatedExtended == maxWith) {
                    IconButton(
                        onClick = { equipmentEditMode = !equipmentEditMode },
                        content = {
                            Icon(
                                imageVector = if (!equipmentEditMode) Icons.Default.Edit else Icons.Default.ArrowBack,
                                contentDescription = "Edit"
                            )
                        }
                    )
                    if (equipmentEditMode) {
                        IconButton(
                            onClick = { showPopup.value = true },

                            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
                        )
                    }
                    Text("Ausrüstung")
                    val animatedRotation by animateFloatAsState(
                        targetValue = if (fullWindow.value) -90f else 0f,
                        animationSpec = tween(durationMillis = 150)
                    )
                    IconButton(
                        onClick = { fullWindow.value = !fullWindow.value },
                        content = { Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle full window") },
                        modifier = Modifier.rotate(animatedRotation)
                    )
                }
            }

            if (showPopup.value) {
                listPopupStructure(
                    listElement = { slot ->
                        Row(
                            Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(5.dp, RoundedCornerShape(10.dp))
                                .background(Color.Gray, RoundedCornerShape(10.dp))
                        ) {
                            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                                Text(slot.itemClassName, Modifier.padding(5.dp))
                            }
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.addSlot(slot)
                                    showPopup.value = false
                                },
                                content = { Icon(Icons.Default.Check, "Select") },
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    },
                    listOf(
                        ArmorSlot(), WeaponSlot(), ShortRangeWeaponSlot(), LongRangeWeaponSlot(),
                        ConsumableSlot(), PotionSlot(), MiscellaneousSlot()
                    ),
                    showPopup,
                    title = mutableStateOf("Item Klasse für den Slot auswählen"),
                    searchStringSelector = { slot -> slot.itemClassName }
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun equippedElementTab(slotSize: MutableState<Dp>, background: Color, fullWindow: MutableState<Boolean>) {
        val maxWith = remember { 300.dp }

        var isExtended by remember { mutableStateOf(false) }

        val animatedExtended by animateDpAsState(
            targetValue = if (isExtended) maxWith else 0.dp,
            animationSpec = tween(durationMillis = 150)
        )

        Column(Modifier.fillMaxHeight().background(background, RoundedCornerShape(10.dp))) {
            equipmentTabTop(animatedExtended, maxWith, fullWindow)
            Row(Modifier.fillMaxHeight()) {
                Box(
                    Modifier
                        .onClick { isExtended = !isExtended }
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowForward, "Toggle", Modifier.padding(10.dp))
                }
                if (animatedExtended != 0.dp && selectedInventory.value != null) {
                    val mod = if (fullWindow.value) Modifier.fillMaxSize() else Modifier.width(animatedExtended)
                    LazyColumn(mod) {
                        if (animatedExtended == maxWith && selectedInventory.value != null) {
                            items(selectedInventory.value!!.equipmentSlotsList) { slot ->
                                equippedItemSlot(slot, slotSize)
                            }
                        }
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun equippedItemSlot(
        slot: ItemSlot<out Item>,
        slotSize: MutableState<Dp>
    ) {
        val backGroundColor = remember(slot.item.value) {
            if (slot.item.value != null) Color.Gray else Color.LightGray
        }

        val boxShape = remember(slot.item.value, slot.item.value?.equipped) {
            if (slot.item.value != null && !slot.item.value!!.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)
        }

        var isHovered by remember { mutableStateOf(false) }

        val borderColor = remember(slot.item.value, slot.item.value?.equipped, isHovered) {
            if (slot.item.value == null) {
                Color.Black.copy(alpha = 0.1f)
            } else if (!slot.item.value!!.equipped) {
                Color.Black.copy(
                    alpha = 0.3f
                )
            } else Color.Yellow.copy(alpha = 0.7f)
        }

        val scale by animateFloatAsState(
            targetValue = if (isHovered) 1.08f else 1f,
            animationSpec = tween(durationMillis = 150)
        )

        val elevation by animateDpAsState(
            targetValue = if (isHovered) 6.dp else 0.dp,
            animationSpec = tween(durationMillis = 150)
        )

        var showEditPopup by remember { mutableStateOf(false) }
        val showChooseItemPopup = remember { mutableStateOf(false) }

        val showEditQuickViewStatPopup = remember { mutableStateOf(false) }
        var editStat1 by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        )
        {
            if (showEditQuickViewStatPopup.value) {
                listPopupStructure(
                    listElement = { stat ->
                        Row(
                            Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(5.dp, RoundedCornerShape(10.dp))
                                .background(Color.Gray, RoundedCornerShape(10.dp))
                        ) {
                            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                                Text(stat.name, Modifier.padding(5.dp))
                            }
                            IconButton(
                                onClick = {
                                    if(editStat1) slot.quickViewStat1.value = stat else slot.quickViewStat2.value = stat
                                    showEditQuickViewStatPopup.value = false
                                },
                                content = { Icon(Icons.Default.Check, "Select") },
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    },
                    ItemSlot.quickViewStats.entries,
                    showEditQuickViewStatPopup,
                    title = mutableStateOf("Eigenschaft für die Übersicht auswählen"),
                    searchStringSelector = { stat -> stat.name }
                )
            }
            if (equipmentEditMode) {
                Box(
                    Modifier
                        .zIndex(2f)
                        .padding(3.dp)
                        .height(slotSize.value)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .onClick {},
                    contentAlignment = Alignment.Center
                )
                {
                    Row {
                        Row(
                            Modifier.background(Color.LightGray, RoundedCornerShape(10.dp))
                        ) {
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.moveSlotDown(
                                        selectedInventory.value!!.equipmentSlotsList.indexOf(
                                            slot
                                        )
                                    )
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "move back",
                                        modifier = Modifier.rotate(90f)
                                    )
                                }
                            )
                            Spacer(Modifier.width(5.dp))
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.moveSlotUp(
                                        selectedInventory.value!!.equipmentSlotsList.indexOf(
                                            slot
                                        )
                                    )
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                        contentDescription = "move forward",
                                        modifier = Modifier.rotate(90f)
                                    )
                                }
                            )
                            Spacer(Modifier.width(5.dp))
                            IconButton(
                                onClick = {
                                    selectedInventory.value?.removeSlot(
                                        selectedInventory.value!!.equipmentSlotsList.indexOf(
                                            slot
                                        )
                                    )
                                },
                                content = { Icon(imageVector = Icons.Default.Delete, contentDescription = "delete") }
                            )
                        }
                        Spacer(Modifier.width(5.dp))
                        Column(Modifier.background(Color.LightGray, RoundedCornerShape(10.dp))) {
                            IconButton(
                                onClick = {
                                    editStat1 = true
                                    showEditQuickViewStatPopup.value = true
                                },
                                content = { Icon(imageVector = Icons.Default.Edit, contentDescription = "edit quickViewStat1") }
                            )
                            Spacer(Modifier.width(5.dp))
                            IconButton(
                                onClick = {
                                    editStat1 = false
                                    showEditQuickViewStatPopup.value = true
                                },
                                content = { Icon(imageVector = Icons.Default.Edit, contentDescription = "edit quickViewStat2") }
                            )
                        }
                    }
                }
            }
            Row(Modifier.zIndex(1f)) {
                Box(
                    Modifier
                        .padding(3.dp)
                        .size(slotSize.value)
                        .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                        .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                        }
                        .shadow(elevation, shape = RoundedCornerShape(8.dp), clip = false)
                        .background(backGroundColor, shape = boxShape)
                        .border(width = 2.dp, color = borderColor, shape = boxShape)
                        .onClick { if (slot.item.value != null) showEditPopup = true else showChooseItemPopup.value = true }
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                ) {
                    if (showEditPopup) {
                        Popup(
                            onDismissRequest = { showEditPopup = false },
                            alignment = Alignment.Center,
                            properties = PopupProperties(focusable = true, dismissOnBackPress = true)
                        ) {
                            Row(
                                Modifier.background(Color.LightGray, RoundedCornerShape(10.dp))
                            ) {
                                Button(
                                    onClick = {
                                        showChooseItemPopup.value = true
                                        showEditPopup = false
                                    },
                                    content = { Text("Item ersetzen") }
                                )
                                Button(
                                    onClick = {
                                        if(slot.item.value != null) ItemDisplayManager.openNewItemDisplay(slot.item.value!!)
                                        showEditPopup = false
                                    },
                                    content = { Text("Item anzeigen") }
                                )
                                Button(
                                    onClick = {
                                        slot.unequipItem()

                                        showEditPopup = false
                                    },
                                    content = { Text("Item entfernen") }
                                )
                            }
                        }
                    }
                    if (showChooseItemPopup.value) {
                        ItemSlotItemPickerPopup(showChooseItemPopup, slot)
                    }

                    if (slot.item.value != null) {
                        //BackgroundIcon
                        val icon = remember(slot.item.value!!.icon) { slot.item.value!!.icon.toPainter() }
                        Image(
                            icon,
                            slot.item.value!!.iconName,
                            Modifier.fillMaxSize()
                        )
                        //Name
                        Text(
                            slot.item.value!!.name,
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
                                Modifier.weight(4f)
                            )
                            //Amount
                            Text(
                                slot.item.value!!.amount.toString(),
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
                Column(
                    Modifier
                        .height(slotSize.value)
                        .weight(1f)
                        .padding(5.dp)
                ) {
                    Text(slot.name.value, Modifier.weight(0.25f))
                    Column(Modifier.weight(1f)) {
                        if (slot.item.value != null) {
                            Text(slot.getQuickViewStat1())
                            Text(slot.getQuickViewStat2())
                        }
                    }
                }
            }
        }
    }

    fun setImage(directory: String, preFile: String) {
        if (directory != null && preFile != null) {
            val file = File(directory, preFile)
            val uuid = UUID.randomUUID().toString()
            val finalFileName = "${file.nameWithoutExtension}_${uuid}.${file.extension}"
            ImageLoader.copyImageToUserImagesFolder(file, finalFileName)
            selectedInventory.value!!.userIconName = finalFileName
        }
    }
}