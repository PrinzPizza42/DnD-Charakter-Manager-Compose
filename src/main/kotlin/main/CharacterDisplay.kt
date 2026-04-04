package main

import Main.ItemClasses.EmptySlot
import Main.ItemClasses.Item
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import main.InventoryDisplay.showItemDisplayStructure
import org.jetbrains.skiko.Cursor

object CharacterDisplay {
    @Composable
    fun displayCharInfo() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Character Info")
        }
    }

//    @Composable
//    fun displayCharEquipment() {
//        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            val background = Color.Gray
//            val slotSize = 150.dp
//            Row(Modifier.fillMaxSize()) {
//                Column(
//                    Modifier
//                        .background(background)
//                        .width(300.dp)
//                ) {
//                    for (i in 1..5) {
//                        invSlot(
//
//                        )
//                    }
//                }
//                getRandomCharacterImage()
//                Column(
//                    Modifier
//                        .background(background)
//                        .width(150.dp)
//                ) {
//
//                }
//            }
//            Text("Character Equipment")
//        }
//    }

    @Composable
    fun getRandomCharacterImage() {
        val imagePath = remember { "standardCharacters/" + (1..2).random() + ".png" }
        return Image(
            painterResource(imagePath),
            "Standard Character Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }

//    @OptIn(ExperimentalComposeUiApi::class)
//    @Composable
//    fun invSlot(
//        item: Item?,
//        slotSize: MutableState<Dp>,
//        dragMode: MutableState<Boolean>,
//        showOverlay: (@Composable (() -> Unit)) -> Unit,
//        window: ComposeWindow,
//        updateInventory: (Item) -> Unit
//    ) {
//        val backGroundColor = remember { mutableStateOf(if(item !is EmptySlot) lerp(Color.Transparent, Color.Black, 0.1f) else Color.LightGray.copy(alpha = 0.2f)) }
//
//        if (item != null) {
//            val boxShape = remember(item.equipped) {
//                mutableStateOf(
//                    if (!item.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)
//                )
//            }
//            var isHovered by remember { mutableStateOf(false) }
//            val borderColor = remember(item.equipped, dragMode.value, isHovered) {
//                mutableStateOf(
//                    if(dragMode.value && isHovered) {
//                        Color.Red
//                    } else if (item is EmptySlot) {
//                        Color.Black.copy(alpha = 0.1f)
//                    } else if (!item.equipped) {
//                        Color.Black.copy(
//                            alpha = 0.3f
//                        )
//                    } else Color.Yellow.copy(alpha = 0.7f)
//                )}
//
//            val scale by animateFloatAsState(
//                targetValue = if (isHovered && item !is EmptySlot && !dragMode.value) 1.08f else 1f,
//                animationSpec = tween(durationMillis = 150)
//            )
//
//            val elevation by animateDpAsState(
//                targetValue = if (isHovered && item !is EmptySlot && !dragMode.value) 6.dp else if (item !is EmptySlot) 2.dp else 0.dp,
//                animationSpec = tween(durationMillis = 150)
//            )
//
//            Box(
//                modifier = Modifier
//                    .padding(4.dp)
//                    .size(slotSize.value)
//                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
//                    .onPointerEvent(PointerEventType.Exit) { isHovered = false }
//                    .graphicsLayer {
//                        this.scaleX = scale
//                        this.scaleY = scale
//                    }
//                    .shadow(elevation, shape = RoundedCornerShape(8.dp), clip = false)
//                    .background(backGroundColor.value, shape = boxShape.value)
//                    .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onTap = {
//                                println("clicked item " + item.name)
//                                if (dragMode.value) {
//                                    println("drop item before " + item.name)
//                                    addItemAtIndex(draggedItem.value!!, item)
//                                    draggedItem.value = null
//                                    dragMode.value = false
//                                } else if (item !is EmptySlot) {
//                                    showOverlay({
//                                        showItemDisplayStructure(mutableStateOf(item), updateInventory, window)
//                                    })
//                                }
//                            },
//                            onLongPress = {
//                                if (item !is EmptySlot && draggedItem.value == null) {
//                                    draggedItem.value = item
//                                    dragMode.value = true
//                                    removeItem(item)
//                                }
//                            }
//                        )
//                    }
//                    .pointerHoverIcon(
//                        if (item !is EmptySlot) PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)) else PointerIcon(
//                            Cursor(Cursor.DEFAULT_CURSOR)
//                        )
//                    )
//            ) {
//                if (item !is EmptySlot) {
//                    Box(Modifier.padding(3.dp))
//                    {
//                        //BackgroundIcon
//                        val icon = remember(item.icon) { item.icon.toPainter() }
//                        Image(
//                            icon,
//                            item.iconName,
//                            Modifier
//                                .fillMaxSize()
//                        )
//                        //Name
//                        Text(
//                            item.name,
//                            Modifier
//                                .padding(5.dp, 0.dp)
//                                .background(
//                                    color = lerp(Color.Transparent, Color.White, 0.8f),
//                                    shape = RoundedCornerShape(15.dp)
//                                )
//                                .padding(10.dp, 0.dp)
//                        )
//                        Row(
//                            Modifier
//                                .align(Alignment.BottomEnd)
//                                .fillMaxWidth()
//                        ) {
//                            //Filler
//                            Box(
//                                Modifier
//                                    .weight(4f)
//                            )
//                            //Amount
//                            Text(
//                                item.amount.toString(),
//                                Modifier
//                                    .padding(5.dp, 0.dp)
//                                    .background(
//                                        color = lerp(Color.Transparent, Color.White, 0.8f),
//                                        shape = CircleShape
//                                    )
//                                    .padding(10.dp, 0.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        } else {
//            Box(
//                Modifier
//                    .size(100.dp)
//                    .background(backGroundColor.value.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
//            )
//        }
//    }
}