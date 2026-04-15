package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropdownString(
    label: String?,
    options: List<String>,
    selectedOption: MutableState<String>,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.padding(5.dp)
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
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    content = { Text(text = option) },
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
        Text(label, modifier = Modifier.width(150.dp))
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

fun loadPainterFromFile(path: String): Painter? {
    return ImageLoader.loadImageFromFile(path).map { it.toPainter() }.orElse(null)
}

@Composable
fun openAsWindowIconButton(onClick: () -> Unit) {
    IconButton(
        onClick = {
            onClick()
        },
        content = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open as window"
            )
        }
    )
}

@Composable
fun getFloatInputOverlay(
    modifier: Modifier,
    startValue: Float,
    text: String,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    Text(text)
                },
                singleLine = true,
                isError = isError
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
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