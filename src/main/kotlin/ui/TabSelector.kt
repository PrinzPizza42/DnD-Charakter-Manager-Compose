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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.runtime.Composable
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
import data.Inventory
import disk.ImageLoader
import disk.Read
import disk.Write

object TabSelector {
    val width = 50.dp

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun displayTabSelector(
        showInventory: MutableState<Boolean>,
        showScrollPanel: MutableState<Boolean>,
        showCharDetailsTab: MutableState<Boolean>,
        showEquippedItemsTab: MutableState<Boolean>,
        selectedInventory: MutableState<Inventory?>,
        sectionSwitch: MutableState<Boolean>
    ) {
        Column(
            Modifier.Companion
                .fillMaxHeight()
                .width(width)
                .background(Color.Companion.DarkGray)
        ) {
            returnToHomeButton(selectedInventory)

            // Inventory and Spells Section
            Column(
                Modifier.Companion
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(
                        lerp(Color.Companion.LightGray, Color.Companion.DarkGray, 0.6f),
                        RoundedCornerShape(5.dp)
                    )
            ) {
                RadioButton(
                    selected = sectionSwitch.value,
                    onClick = {
                        sectionSwitch.value = true
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Companion.LightGray,
                        unselectedColor = Color.Companion.White
                    )
                )

                // Show inv button
                tabElement(
                    {},
                    showInventory,
                    ImageLoader.loadImageFromResources("backPackIcon.png").get().toPainter()
                )

                // Show scrollPanel button
                tabElement(
                    {},
                    showScrollPanel,
                    ImageLoader.loadImageFromResources("scrollIcon.png").get().toPainter()
                )
            }

            // Character details and equipped items
            Column(
                Modifier.Companion
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(
                        lerp(Color.Companion.LightGray, Color.Companion.DarkGray, 0.6f),
                        androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                    )
            ) {
                RadioButton(
                    selected = !sectionSwitch.value,
                    onClick = {
                        sectionSwitch.value = false
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Companion.LightGray,
                        unselectedColor = Color.Companion.White
                    )
                )

                tabElement(
                    {},
                    showCharDetailsTab,
                    ImageLoader.loadImageFromResources("icon.png").get().toPainter()
                )
                tabElement(
                    {},
                    showEquippedItemsTab,
                    ImageLoader.loadImageFromResources("icon.png").get().toPainter()
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
            Modifier.Companion
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .padding(5.dp, 5.dp, 5.dp, 15.dp)
                .fillMaxWidth()
                .shadow(shadow.dp)
                .background(
                    lerp(Color.Companion.LightGray, Color.Companion.DarkGray, 0.6f),
                    androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                )
        ) {
            Box(
                Modifier.Companion
                    .fillMaxWidth()
                    .onClick(enabled = true, onClick = {
                        if (selectedInventory.value != null) {
                            Write.safe(selectedInventory.value!!)
                            selectedInventory.value = null
                            Read.readData()
                        }
                    })
                    .onPointerEvent(PointerEventType.Companion.Enter) {
                        hoveredOver = true
                    }
                    .onPointerEvent(PointerEventType.Companion.Exit) {
                        hoveredOver = false
                    }
            ) {
                val home = remember { ImageLoader.loadImageFromResources("home.png").get().toPainter() }
                Image(
                    painter = home,
                    contentScale = ContentScale.Companion.FillWidth,
                    contentDescription = "home",
                    modifier = Modifier.Companion
                        .padding(2.dp)
                        .align(Alignment.Companion.Center)
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun tabElement(
        onClick: () -> Unit,
        showPanel: MutableState<Boolean>,
        icon: Painter
    ) {
        var hoveredOverSpells by remember { mutableStateOf(false) }

        val backgroundColor by animateColorAsState(
            if (showPanel.value) lerp(
                Color.Companion.LightGray,
                Color.Companion.DarkGray,
                0.2f
            ) else lerp(Color.Companion.LightGray, Color.Companion.DarkGray, 0.6f),
            animationSpec = tween(300)
        )

        val shadow by animateFloatAsState(
            if (hoveredOverSpells) 10f else 1f,
            animationSpec = tween(300)
        )

        val scale by animateFloatAsState(
            if (hoveredOverSpells) 1.1f else 1f,
            animationSpec = tween(300)
        )

        Box(
            Modifier.Companion
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .fillMaxWidth()
                .padding(0.dp, 10.dp, 0.dp, 10.dp)
                .clickable(enabled = true, onClick = {
                    showPanel.value = !showPanel.value
                    onClick()
                })
                .shadow(shadow.dp)
                .background(backgroundColor, androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
                .onPointerEvent(PointerEventType.Companion.Enter) {
                    hoveredOverSpells = true
                }
                .onPointerEvent(PointerEventType.Companion.Exit) {
                    hoveredOverSpells = false
                }
        ) {
            Image(
                painter = icon,
                contentScale = ContentScale.Companion.FillWidth,
                contentDescription = "icon",
                modifier = Modifier.Companion
                    .padding(2.dp)
                    .align(Alignment.Companion.Center)
            )
        }
    }
}