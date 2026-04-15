package ui

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import disk.ImageLoader
import data.CharacterManager.selectedInventory
import data.TabManager
import itemClasses.EmptySlot
import itemClasses.Item
import org.jetbrains.skiko.Cursor
import java.io.File
import java.util.UUID

object InventoryDisplay {
    val showSortedInv = mutableStateOf(false)

    @Composable
    fun displayInv(
        modifier: Modifier,
    ) {
        val slotSize = remember { mutableStateOf(100.dp) }

        Box(
            modifier = modifier.wrapContentSize(Alignment.Center)
        ) {
            if(selectedInventory.value != null) {
                Column {
                    sceneryAndBackPackTop(slotSize)
                    backPack(slotSize)
                }
            }
            else {
                Text("Kein Inventar ausgewählt")
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

    fun setImage(directory: String, preFile: String, item: Item?, reloadKey: MutableState<Int>) {
        if (directory != null && preFile != null) {
            val file = File(directory, preFile)
            val uuid = UUID.randomUUID().toString()
            val finalFileName = "${file.nameWithoutExtension}_${uuid}.${file.extension}"
            ImageLoader.copyImageToUserImagesFolder(file, finalFileName)
            item!!.userIconName = finalFileName
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
    private fun sceneryAndBackPackTop(
        slotSize: MutableState<Dp>
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
                            if(TabManager.showInventoryTab.value) {
                                Slider(
                                    value = slotSize.value.value,
                                    valueRange = range,
                                    onValueChange = {
                                        slotSize.value = it.dp
                                    },
                                    modifier = Modifier.width(150.dp),
                                )
                            }

                            val backGroundColor = remember { lerp(Color.Transparent, Color.Black, 0.2f) }

                            Column(
                                Modifier
                                    .size(150.dp, 100.dp)
                                    .background(
                                        backGroundColor,
                                        RoundedCornerShape(5.dp)
                                    )
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

                            val items = selectedInventory.value!!.items
                            val backPackWeight = remember(items) {
                                mutableStateOf(items.toMutableList().sumOf { it.weight * it.amount }.toFloat())
                            }
                            var backPackWeightUIValue by remember { mutableStateOf(selectedInventory.value!!.maxCarryingCapacity) }

                            var showPopup by remember { mutableStateOf(false) }
                            if(showPopup) {
                                Popup(
                                    onDismissRequest = { showPopup = false },
                                    properties = PopupProperties(focusable = true)
                                ) {
                                    Column(
                                        Modifier
                                            .shadow(10.dp)
                                            .background(Color.White, RoundedCornerShape(10.dp))
                                            .padding(5.dp)
                                    ) {
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
                                                    RoundedCornerShape(5.dp)
                                                )
                                                .clip(RoundedCornerShape(5.dp)),
                                            selectedInventory.value!!.maxCarryingCapacity,
                                            "Maximalgewicht",
                                            onConfirm = { value ->
                                                selectedInventory.value!!.maxCarryingCapacity = value
                                                backPackWeightUIValue = value
                                                showPopup = false
                                            },
                                            onDismiss = {
                                                showPopup = false
                                            }
                                        )
                                    }
                                }
                            }
                            Box(
                                Modifier
                                    .onClick { showPopup = true }
                            ) {
                                backPackTopValue(modifier, backPackWeight, backPackWeightUIValue, "Gewicht")
                            }

                            //BackPack value
                            val backPackValue = remember(items) {
                                mutableStateOf(
                                    items.toMutableList().sumOf { it.valueInGold * it.amount }.toFloat()
                                )
                            }
                            backPackTopValue(modifier, backPackValue, null, "Wert in Gold")

                            Button(
                                onClick = {
                                    ItemDisplay.showItemDisplayStructure()
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
    private fun backPackTopValue(modifier: Modifier, value: MutableState<Float>, maxValue: Float?, title: String) {
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
                        .clip(RoundedCornerShape(5.dp))
                        .shadow(0.dp, ambientColor = Color.White, spotColor = Color.Black)
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    private fun backPack(
        slotSize: MutableState<Dp>
    ) {
        val dragMode = remember { mutableStateOf(false) }
        val draggedItem = remember { mutableStateOf<Item?>(null) }
        val draggedItemIndexBuffer = remember { mutableStateOf<Int?>(null) }
        val draggedItemOffset = remember { mutableStateOf(Offset.Zero) }

        Box(Modifier.fillMaxSize()) {
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
                val inv = selectedInventory.value!!
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
                                draggedItemIndexBuffer
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
                        Box(Modifier.fillMaxWidth()) {
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
                                        if(ItemDisplay.item == draggedItem.value) ItemDisplay.close()
                                        draggedItemIndexBuffer.value = null
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
                        if (!draggedItem.value!!.equipped) RoundedCornerShape(10.dp) else CutCornerShape(
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
    private fun invSlot(
        item: Item?,
        draggedItem: MutableState<Item?>,
        slotSize: MutableState<Dp>,
        dragMode: MutableState<Boolean>,
        draggedItemIndexBuffer: MutableState<Int?>
    ) {
        val inv = selectedInventory

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
                    if (!item.equipped) RoundedCornerShape(10.dp) else CutCornerShape(
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
                    .shadow(elevation, shape = RoundedCornerShape(8.dp), clip = false)
                    .background(backGroundColor.value, shape = boxShape.value)
                    .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (dragMode.value && !inv.value!!.items.contains(draggedItem.value)) {
                                    inv.value!!.swapItemIndex(draggedItem.value!!, item, draggedItemIndexBuffer)
                                    draggedItemIndexBuffer.value = null
                                    draggedItem.value = null
                                    dragMode.value = false
                                }
                                else if (item !is EmptySlot) ItemDisplay.showItemDisplayStructure(item)
                            },
                            onLongPress = {
                                if (item !is EmptySlot && draggedItem.value == null) {
                                    draggedItem.value = item
                                    draggedItemIndexBuffer.value = inv.value!!.items.indexOf(item)
                                    inv.value!!.items[draggedItemIndexBuffer.value!!] = EmptySlot()
                                    dragMode.value = true
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
        }
 else {
            Box(
                Modifier
                    .size(100.dp)
                    .background(
                        backGroundColor.value.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }

    @Composable
    private fun getRandomSceneryImage() {
        val imagePath = remember { "sceneryImages/" + (1..7).random() + ".jpeg" }
        return Image(
            painterResource(imagePath),
            "Scenery Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}