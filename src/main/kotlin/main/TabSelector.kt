package main

import Data.ImageLoader
import Data.Read
import Data.Write
import Main.Inventory
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
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

object TabSelector {
    val width = 50.dp

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun displayTabSelector(
        showInventory: MutableState<Boolean>,
        showScrollPanel: MutableState<Boolean>,
        selectedInventory: MutableState<Inventory?>
    ) {
        Column(Modifier
            .fillMaxHeight()
            .width(width)
            .background(Color.DarkGray)
        ) {
            // Return to invSelector Button
            var hoveredOver by remember { mutableStateOf(false) }

            val shadow by animateFloatAsState(
                if(hoveredOver) 10f else 1f
            )

            val scale by animateFloatAsState(
                if(hoveredOver) 1.1f else 1f
            )

            Box(
                Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .padding(5.dp, 5.dp, 5.dp, 15.dp)
                    .fillMaxWidth()
                    .shadow(shadow.dp)
                    .background(lerp(Color.LightGray, Color.DarkGray, 0.6f), RoundedCornerShape(5.dp))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .onClick(enabled = true, onClick = {
                            if(selectedInventory.value != null) {
                                Write.safe(selectedInventory.value!!)
                                selectedInventory.value = null
                                Read.readData()
                            }
                        })
                        .pointerMoveFilter(
                            onEnter = {
                                hoveredOver = true
                                false
                            },
                            onExit = {
                                hoveredOver = false
                                false
                            }
                        )
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

            Column(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(lerp(Color.LightGray, Color.DarkGray, 0.6f), RoundedCornerShape(5.dp))
            ) {
                //Show inv button
                tabElement(
                    {},
                    showInventory,
                    ImageLoader.loadImageFromResources("backPackIcon.png").get().toPainter()
                )

                //Show scrollPanel button
                tabElement(
                    {},
                    showScrollPanel,
                    ImageLoader.loadImageFromResources("scrollIcon.png").get().toPainter()
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
            if(showPanel.value) lerp(Color.LightGray, Color.DarkGray, 0.2f) else lerp(Color.LightGray, Color.DarkGray, 0.6f),
            animationSpec = tween (300)
        )

        val shadow by animateFloatAsState(
            if(hoveredOverSpells) 10f else 1f,
            animationSpec = tween (300)
        )

        val scale by animateFloatAsState(
            if(hoveredOverSpells) 1.1f else 1f,
            animationSpec = tween (300)
        )

        Box(
            Modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .fillMaxWidth()
                .padding(0.dp, 10.dp, 0.dp, 10.dp)
                .clickable(enabled = true, onClick = {
                    showPanel.value = !showPanel.value
                    onClick()
//                    if(showPanel.value) showInvAnimationEndSpells.value = showPanel.value
                })
                .shadow(shadow.dp)
                .background(backgroundColor, RoundedCornerShape(5.dp))
                .pointerMoveFilter(
                    onEnter = {
                        hoveredOverSpells = true
                        false
                    },
                    onExit = {
                        hoveredOverSpells = false
                        false
                    }
                )
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
    }
}