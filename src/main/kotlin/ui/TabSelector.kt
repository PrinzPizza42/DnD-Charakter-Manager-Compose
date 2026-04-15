package ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import data.CharacterManager
import data.CharacterManager.selectedInventory
import data.CustomWindow
import data.Inventory
import data.TabManager
import data.TabManager.sectionSwitch
import data.TabManager.showCharDetailsTab
import data.TabManager.showEquippedItemsTab
import data.TabManager.showInventoryTab
import data.TabManager.showScrollTab
import data.WindowManager
import disk.ImageLoader
import disk.Read
import disk.Write
import kotlin.uuid.ExperimentalUuidApi

object TabSelector {
    val width = 50.dp

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun displayTabSelector() {
        Column(
            Modifier
                .fillMaxHeight()
                .width(width)
                .background(Color.DarkGray)
        ) {
            returnToHomeButton(selectedInventory)

            // Inventory and Spells Section
            Column(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(
                        lerp(Color.LightGray, Color.DarkGray, 0.6f),
                        RoundedCornerShape(5.dp)
                    )
            ) {
                RadioButton(
                    selected = sectionSwitch.value,
                    onClick = {
                        sectionSwitch.value = true
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.LightGray,
                        unselectedColor = Color.White
                    )
                )

                // Show inv button
                tabElement(
                    {},
                    showInventoryTab,
                    ImageLoader.loadImageFromResources("backPackIcon.png").get().toPainter(),
                    { InventoryDisplay.displayInv(Modifier.fillMaxSize()) },
                    windowState = TabManager.inventoryWindow
                )

                // Show scrollPanel button
                tabElement(
                    {},
                    showScrollTab,
                    ImageLoader.loadImageFromResources("scrollIcon.png").get().toPainter(),
                    { ScrollDisplay.scrollDisplay(Modifier.fillMaxSize()) },
                    windowState = TabManager.spellsWindow
                )
            }

            // Character details and equipped items
            Column(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(
                        lerp(Color.LightGray, Color.DarkGray, 0.6f),
                        RoundedCornerShape(5.dp)
                    )
            ) {
                RadioButton(
                    selected = !sectionSwitch.value,
                    onClick = {
                        sectionSwitch.value = false
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.LightGray,
                        unselectedColor = Color.White
                    )
                )

                tabElement(
                    {},
                    showCharDetailsTab,
                    ImageLoader.loadImageFromResources("icon.png").get().toPainter(),
                    { CharacterDisplay.displayCharInfo() },
                    windowState = TabManager.charInfoWindow
                )
                tabElement(
                    {},
                    showEquippedItemsTab,
                    ImageLoader.loadImageFromResources("icon.png").get().toPainter(),
                    { CharacterDisplay.displayCharEquipment() },
                    windowState = TabManager.equippedItemsWindow
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun returnToHomeButton(selectedInventory: MutableState<Inventory?>) {
        var hoveredOver by remember { mutableStateOf(false) }

        val shadow by animateFloatAsState(
            if (hoveredOver) 10f else 1f
        )

        val scale by animateFloatAsState(
            if (hoveredOver) 1.1f else 1f
        )

        Box(
            Modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .padding(5.dp, 5.dp, 5.dp, 15.dp)
                .fillMaxWidth()
                .shadow(shadow.dp)
                .background(
                    lerp(Color.LightGray, Color.DarkGray, 0.6f),
                    androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                )
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .onClick(enabled = true, onClick = {
                        if (selectedInventory.value != null) {
                            Write.safe(selectedInventory.value!!)
                            selectedInventory.value = null
                            Read.readData()
                        }
                    })
                    .onPointerEvent(PointerEventType.Enter) {
                        hoveredOver = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        hoveredOver = false
                    }
            ) {
                val home = remember { ImageLoader.loadImageFromResources("home.png").get().toPainter() }
                Image(
                    painter = home,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "home",
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalUuidApi::class)
    @Composable
    fun tabElement(
        onClick: () -> Unit,
        showPanel: MutableState<Boolean>,
        icon: Painter,
        windowContent: @Composable () -> Unit,
        windowState: MutableState<CustomWindow?>
    ) {
        var hoveredOver by remember { mutableStateOf(false) }

        val backgroundColor by animateColorAsState(
            if (showPanel.value) lerp(
                Color.LightGray,
                Color.DarkGray,
                0.2f
            ) else lerp(Color.LightGray, Color.DarkGray, 0.6f),
            animationSpec = tween(300)
        )

        val shadow by animateFloatAsState(
            if (hoveredOver) 10f else 1f,
            animationSpec = tween(300)
        )

        val scale by animateFloatAsState(
            if (hoveredOver) 1.1f else 1f,
            animationSpec = tween(300)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp, 0.dp, 10.dp)
        ) {
            Box(
                Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clickable(enabled = true, onClick = {
                        showPanel.value = !showPanel.value
                        onClick()
                    })
                    .shadow(shadow.dp)
                    .background(backgroundColor, RoundedCornerShape(5.dp))
                    .onPointerEvent(PointerEventType.Enter) {
                        hoveredOver = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        hoveredOver = false
                    }
            ) {
                Image(
                    painter = icon,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "icon",
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.Center)
                )
            }

            LaunchedEffect(showPanel.value) {
                if(showPanel.value) {
                    windowState.value?.close()
                }
            }

            openAsWindowIconButton(
                onClick = {
                    if(windowState.value == null) {
                        windowState.value = WindowManager.openNewWindow(
                            onCloseRequest = {},
                            content = windowContent,
                            openTabState = showPanel
                        )
                        showPanel.value = false
                    }
                    else {
                        windowState.value?.close()
                        windowState.value = null
                        showPanel.value = true
                    }
                }
            )
        }
    }
}