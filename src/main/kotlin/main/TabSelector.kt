package main

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

object TabSelector {
    val width = 50.dp

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun displayTabSelector(
        showInventory: MutableState<Boolean>,
        showScrollPanel: MutableState<Boolean>,
        selectedInventory: MutableState<Inventory?>,
        showInvAnimationEndInv: MutableState<Boolean>,
        showInvAnimationEndSpells: MutableState<Boolean>
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
                                println("Auto saved inv " + selectedInventory.value!!.name + " on close")
                                selectedInventory.value = null
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
                    Image(
                        painter = painterResource("home.png"),
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
                var selectedInv by remember(showInventory.value) { mutableStateOf(showInventory.value) }
                var hoveredOverInv by remember { mutableStateOf(false) }

                val backgroundColorInv by animateColorAsState(
                    if(selectedInv) lerp(Color.LightGray, Color.DarkGray, 0.2f) else lerp(Color.LightGray, Color.DarkGray, 0.6f),
                    animationSpec = tween (300)
                )

                val shadowInv by animateFloatAsState(
                    if(hoveredOverInv) 10f else 1f,
                    animationSpec = tween (300)
                )

                val scaleInv by animateFloatAsState(
                    if(hoveredOverInv) 1.1f else 1f,
                    animationSpec = tween (300)
                )

                Box(
                    Modifier
                        .graphicsLayer(scaleX = scaleInv, scaleY = scaleInv)
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 0.dp)
                        .clickable(enabled = true, onClick = {
                            showInventory.value = !showInventory.value
                            if(showInventory.value) showInvAnimationEndInv.value = showInventory.value
                            println("showInv: " + showInventory.value)
                        })
                        .shadow(shadowInv.dp)
                        .background(backgroundColorInv, RoundedCornerShape(5.dp))
                        .pointerMoveFilter(
                            onEnter = {
                                hoveredOverInv = true
                                false
                            },
                            onExit = {
                                hoveredOverInv = false
                                false
                            }
                        )
                ) {
                    Image(
                        painter = painterResource("backPackIcon.png"),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "inv",
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.Center)
                    )
                }

                //Show scrollPanel button
                var selectedSpells by remember(showScrollPanel.value) { mutableStateOf(showScrollPanel.value) }
                var hoveredOverSpells by remember { mutableStateOf(false) }

                val backgroundColorSpells by animateColorAsState(
                    if(selectedSpells) lerp(Color.LightGray, Color.DarkGray, 0.2f) else lerp(Color.LightGray, Color.DarkGray, 0.6f),
                    animationSpec = tween (300)
                )

                val shadowSpells by animateFloatAsState(
                    if(hoveredOverSpells) 10f else 1f,
                    animationSpec = tween (300)
                )

                val scaleSpells by animateFloatAsState(
                    if(hoveredOverSpells) 1.1f else 1f,
                    animationSpec = tween (300)
                )

                Box(
                    Modifier
                        .graphicsLayer(scaleX = scaleSpells, scaleY = scaleSpells)
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 10.dp)
                        .clickable(enabled = true, onClick = {
                            showScrollPanel.value = !showScrollPanel.value
                            if(showScrollPanel.value) showInvAnimationEndSpells.value = showScrollPanel.value
                        })
                        .shadow(shadowSpells.dp)
                        .background(backgroundColorSpells, RoundedCornerShape(5.dp))
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
                        painter = painterResource("scrollIcon.png"),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "inv",
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.Center)
                    )
                }
            }

        }
    }
}