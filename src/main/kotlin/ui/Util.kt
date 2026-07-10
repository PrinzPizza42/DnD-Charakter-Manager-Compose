package ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import disk.ImageLoader
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import data.CharacterManager.selectedInventory
import data.ItemDisplayManager
import data.equippmentSlots.ItemSlot
import itemClasses.Item

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropdownString(
    label: String?,
    options: List<String>,
    selectedOption: MutableState<String>,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    menuModifier: Modifier = Modifier,
    itemTextColor: Color = Color.Unspecified
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.padding(5.dp)
    ) {
        OutlinedTextField(
            value = selectedOption.value,
            onValueChange = {},
            readOnly = true,
            label =  { if(label != null) Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown öffnen",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .width(200.dp)
                .onPointerEvent(PointerEventType.Release) {
                    expanded = true
                },
            colors = textFieldColors
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = menuModifier
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    content = { Text(text = option, color = itemTextColor) },
                    onClick = {
                        selectedOption.value = option
                        onChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun StepShifterIntSmall(
    label: String,
    range: IntRange,
    value: MutableState<Int>,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(4.dp)
    ) {
        Text(label, modifier = Modifier.width(150.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            StepShifterInt(
                range,
                value,
                onIncrease,
                onDecrease
            )
        }
    }
}

@Composable
fun StepShifterIntBig(
    label: String,
    range: IntRange,
    value: MutableState<Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    bigStep: Int = 5
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(4.dp)
    ) {
        if (label.isNotEmpty()) {
            Text(label, modifier = Modifier.width(90.dp))
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (value.value >= range.first + bigStep) {
                            onDecrease(bigStep)
                            value.value -= bigStep
                        }
                    },
                    enabled = value.value >= range.first + bigStep
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$bigStep", textAlign = TextAlign.Center)
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "MinusBigStep")
                    }
                }
                StepShifterInt(
                    range,
                    value,
                    { onIncrease(1) },
                    { onDecrease(1) }
                )
                IconButton(
                    onClick = {
                        if (value.value <= range.last - bigStep) {
                            onIncrease(bigStep)
                            value.value += bigStep
                        }
                    },
                    enabled = value.value <= range.last - bigStep
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "PlusBigStep")
                        Text("$bigStep", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepShifterInt(
    range: IntRange,
    value: MutableState<Int>,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { if (value.value > range.first) {
                onDecrease()
                value.value -= 1
            } },
            enabled = value.value > range.first
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Minus")
        }

        Text(
            text = "${value.value}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { if (value.value < range.last) {
                onIncrease()
                value.value += 1
            }},
            enabled = value.value < range.last
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Plus")
        }
    }
}

@Composable
fun StepShifterFloatBig(
    label: String,
    range: ClosedFloatingPointRange<Float>,
    value: MutableState<Float>,
    onIncrease: (Float) -> Unit,
    onDecrease: (Float) -> Unit,
    bigStep: Float = 5f
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(4.dp)
    ) {
        if (label.isNotEmpty()) {
            Text(label, modifier = Modifier.width(90.dp))
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (value.value >= range.start + bigStep) {
                            onDecrease(bigStep)
                            value.value -= bigStep
                        }
                    },
                    enabled = value.value >= range.start + bigStep
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$bigStep", textAlign = TextAlign.Center)
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "MinusBigStep")
                    }
                }
                StepShifterFloat(
                    range,
                    value,
                    onIncrease,
                    onDecrease
                )
                IconButton(
                    onClick = {
                        if (value.value <= range.endInclusive - bigStep) {
                            onIncrease(bigStep)
                            value.value += bigStep
                        }
                    },
                    enabled = value.value <= range.endInclusive - bigStep
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "PlusBigStep")
                        Text("$bigStep", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepShifterFloat(
    range: ClosedFloatingPointRange<Float>,
    value: MutableState<Float>,
    onIncrease: (Float) -> Unit,
    onDecrease: (Float) -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { if (value.value > range.start) {
                onDecrease(1f)
                value.value -= 1f
            } },
            enabled = value.value > range.start
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Minus")
        }

        Box {
            Text(
                text = "${value.value}",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { showPopup = true }.padding(horizontal = 8.dp)
            )

            if (showPopup) {
                Popup(
                    onDismissRequest = { showPopup = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Box(
                        Modifier
                            .shadow(10.dp)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(5.dp)
                    ) {
                        FloatInputOverlay(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(5.dp))
                                .clip(RoundedCornerShape(5.dp)),
                            startValue = value.value,
                            text = "Neuer Wert",
                            onConfirm = { newValue ->
                                val clampedValue = newValue.coerceIn(range)
                                val diff = clampedValue - value.value
                                if (diff > 0) onIncrease(diff)
                                else if (diff < 0) onDecrease(-diff)
                                value.value = clampedValue
                                showPopup = false
                            },
                            onDismiss = { showPopup = false }
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = { if (value.value < range.endInclusive) {
                onIncrease(1f)
                value.value += 1f
            }},
            enabled = value.value < range.endInclusive
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Plus")
        }
    }
}

fun loadPainterFromFile(path: String): Painter? {
    return ImageLoader.loadImageFromFile(path).map { it.toPainter() }.orElse(null)
}

@Composable
fun openAsWindowIconButton(onClick: () -> Unit, showPanel: MutableState<Boolean>) {
    val animatedRotation by animateFloatAsState(
        targetValue = if(!showPanel.value) -90f else 0f,
        animationSpec = tween(durationMillis = 150)
    )

    IconButton(
        onClick = {
            onClick()
        },
        content = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open as window"
            )
        },
        modifier = Modifier.rotate(animatedRotation).width(50.dp)
    )
}

@Composable
fun FloatInputOverlay(
    modifier: Modifier,
    startValue: Float,
    text: String,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit,
    showCancelButton: Boolean = true
) {
    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        val input = remember { mutableStateOf(TextFieldValue(startValue.toString())) }
        var isError by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TextField(
                value = input.value,
                onValueChange = {
                    input.value = it
                    isError = it.text.toFloatOrNull() == null
                },
                modifier = Modifier
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.Enter -> {
                                    if (!isError) onConfirm(input.value.text.toFloat())
                                    true
                                }

                                Key.Escape -> {
                                    onDismiss()
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    },
                label = {
                    if(text.isNotBlank() && text != null) Text(text)
                },
                singleLine = true,
                isError = isError
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                Button(
                    onClick = {
                        val number = input.value.text.toFloatOrNull()
                        if (number != null) onConfirm(number)
                    },
                    content = {
                        Text("Bestätigen")
                    }
                )
                if(showCancelButton) {
                    Button(
                        onClick = { onDismiss() },
                        content = {
                            Text("Abbrechen")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun <T : Item> ItemSlotItemPickerPopup(showPopup: MutableState<Boolean> = mutableStateOf(true), slot: ItemSlot<T>) {
    val filteredList = remember(selectedInventory.value) {
        selectedInventory.value!!.items
            .filter { item -> slot.accepts(item) }
            .map { @Suppress("UNCHECKED_CAST") (it as T) }
            .toMutableStateList()
    }

    listPopupStructure(
        { item: T ->
            Row(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(5.dp, RoundedCornerShape(10.dp))
                    .background(Color.Gray, RoundedCornerShape(10.dp))
            ) {
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.CenterStart){
                    Text(item.name, Modifier.padding(5.dp))
                }
                IconButton(
                    onClick = {
                        slot.equipNewItem(item)

                        showPopup.value = false
                    },
                    content = { Icon(Icons.Default.Check, "Select") },
                    modifier = Modifier.padding(5.dp)
                )
                IconButton(
                    onClick = {
                        ItemDisplayManager.openNewItemDisplay(item)
                    },
                    content = { Icon(Icons.Default.Menu, "Open in ItemDisplay") },
                    modifier = Modifier.padding(5.dp)
                )
            }
        },
        filteredList,
        showPopup,
        title = mutableStateOf(slot.itemClassName),
        searchStringSelector = { item -> item.name }
    )
}

@Composable
fun ItemEquipPopup(showPopup: MutableState<Boolean> = mutableStateOf(true), item: Item) {
    val filteredList = remember(selectedInventory.value) {
        selectedInventory.value!!.equipmentSlotsList
            .filter { slot -> slot.accepts(item) }
            .toMutableStateList()
    }

    listPopupStructure(
        { slot ->
            Row(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(5.dp, RoundedCornerShape(10.dp))
                    .background(Color.Gray, RoundedCornerShape(10.dp))
            ) {
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.CenterStart){
                    Text(slot.name.value, Modifier.padding(5.dp))
                }
                IconButton(
                    onClick = {
                        slot.equipNewItem(item)

                        showPopup.value = false
                    },
                    content = { Icon(Icons.Default.Check, "Select") },
                    modifier = Modifier.padding(5.dp)
                )
            }
        },
        filteredList,
        showPopup,
        title = mutableStateOf("${item.name} equipped"),
        searchStringSelector = { slot -> slot.name.value }
    )
}

@Composable
fun <T> listPopupStructure(
    listElement: @Composable (item: T) -> Unit,
    list: List<T>,
    showPopup: MutableState<Boolean>,
    popupSize: Pair<Dp, Dp> = Pair(300.dp, 500.dp),
    title: MutableState<String?> = mutableStateOf(null),
    searchStringSelector: ((T) -> String)? = null
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(list, searchQuery) {
        if (searchQuery.isBlank() || searchStringSelector == null) {
            list
        } else {
            list.filter { item ->
                searchStringSelector(item).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Popup(
        onDismissRequest = { showPopup.value = false },
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true, dismissOnBackPress = true)
    ) {
        Column(
            Modifier
                .size(popupSize.first, popupSize.second)
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .background(Color.LightGray, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(
                Modifier.height(45.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title.value != null) {
                    Box(Modifier.weight(1f))
                    Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Text(title.value!!)
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    IconButton(
                        onClick = { showPopup.value = false },
                        content = { Icon(Icons.Default.Close, "Close") }
                    )
                }
            }
            if (searchStringSelector != null) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    placeholder = { Text("Suchen...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Suche") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White
                    )
                )
            }

            if (filteredList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (list.isEmpty()) "Keine Elemente gefunden" else "Keine Treffer für die Suche")
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(filteredList) { currentItem ->
                        listElement(currentItem)
                    }
                }
            }
        }
    }
}