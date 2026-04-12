package main

import data.ImageLoader
import data.Read
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import main.CharacterManager.selectedInventory
import main.ui.InventoryDisplay.displayInv
import main.ui.ScrollDisplay.scrollDisplay
import main.ui.InvSelector.inventorySelector
import main.ui.Overlay.activeOverlay
import main.ui.Overlay.closeOverlay
import main.ui.TabSelector.displayTabSelector
import main.ui.CharacterDisplay

@Composable
@Preview
fun App(window: ComposeWindow) {
    val showInventoryTab = remember { mutableStateOf(true) }
    val showScrollTab = remember { mutableStateOf(true) }
    val showCharDetailsTab = remember { mutableStateOf(true) }
    val showEquippedItemsTab = remember { mutableStateOf(true) }

    val showInvSelector = remember(selectedInventory.value) { mutableStateOf(selectedInventory.value == null) }

    if(showInvSelector.value) inventorySelector()
    else {
        val sectionSwitch = remember { mutableStateOf(true) } // true = inv & spells; false = char details
        val modifier = if(activeOverlay.value != null) Modifier.fillMaxSize().blur(3.dp) else Modifier.fillMaxSize()

        Box(
            Modifier.fillMaxSize()
        ) {
            Box(
                modifier
            ){
                if(sectionSwitch.value) {
                    // Inv & Spells
                    section(
                        showInventoryTab,
                        showScrollTab,
                        {
                            displayInv(
                                selectedInventory,
                                Modifier.fillMaxSize(),
                                window
                            )
                        },
                        {
                            scrollDisplay(
                                Modifier.fillMaxSize(),
                                selectedInventory.value!!,
                                showScrollTab
                            )
                        },
                        { displayTabSelector(
                            showInventoryTab,
                            showScrollTab,
                            showCharDetailsTab,
                            showEquippedItemsTab,
                            selectedInventory,
                            sectionSwitch
                        ) }
                    )
                }
                else {
                    section(
                        showCharDetailsTab,
                        showEquippedItemsTab,
                        { CharacterDisplay.displayCharInfo() },
                        { CharacterDisplay.displayCharEquipment() },
                        { displayTabSelector(
                            showInventoryTab,
                            showScrollTab,
                            showCharDetailsTab,
                            showEquippedItemsTab,
                            selectedInventory,
                            sectionSwitch
                        ) }
                    )
                }
            }

            activeOverlay.value?.let { overlayContent ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    closeOverlay()
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        overlayContent()
                    }
                }
            }
        }
    }
}

@Composable
fun section(
    showTab1: MutableState<Boolean>,
    showTab2: MutableState<Boolean>,
    contentTab1: @Composable () -> Unit,
    contentTab2: @Composable () -> Unit,
    contentTabSelector: @Composable () -> Unit
) {
    val animationSpec = tween<Float>(300, 0)
    val tab1Weight by animateFloatAsState(
        targetValue = if (showTab1.value) 1f else 0.0001f,
        animationSpec = animationSpec
    )
    val tab2Weight by animateFloatAsState(
        targetValue = if (showTab2.value) 1f else 0.0001f,
        animationSpec = animationSpec
    )
    val emptyWeight by animateFloatAsState(
        targetValue = if (!showTab1.value && !showTab2.value) 1f else 0.0001f,
        animationSpec = animationSpec
    )

    Row(Modifier
        .fillMaxSize()
    ) {
        contentTabSelector()

        Box(Modifier.weight(tab1Weight)) {
            contentTab1()
        }

        Box(
            Modifier
                .weight(emptyWeight)
        ) {
            if (emptyWeight > 0.01f) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {
                    Text(
                        text = "Keine Panels ausgewählt",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp
                    )
                }
            }
        }

        Box(Modifier.weight(tab2Weight)) {
            contentTab2()
        }
    }
}

fun main() = application {
    val icon = remember { ImageLoader.loadImageFromResources("icon.png").get().toPainter() }

    Read.readData()

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1920.dp, 1200.dp)
        ),
        title = "DnD-Charakter-Manager",
        icon = icon
    ) {
        App(window)
    }
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
                                    if(!isError) onConfirm(input.value.text.toFloat())
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
                    }
                ,
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
                    onClick = {onDismiss()},
                    content = {
                        Text("Abbrechen")
                    }
                )
                Button(
                    onClick = {
                        val number = input.value.text.toFloatOrNull()
                        if (number != null) onConfirm(number)
                    },
                    content = {
                        Text("Bestätigen")
                    }
                )
            }
        }
    }
}
