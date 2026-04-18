package ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.CharacterManager.selectedInventory
import data.equippmentSlots.ItemSlot
import itemClasses.Item
import org.jetbrains.skiko.Cursor

object CharacterDisplay {
    @Composable
    fun displayCharInfo() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if(selectedInventory.value != null) {
                Text("Character Info")
            }
            else {
                Text("Kein Inventar ausgewählt")
            }
        }
    }

    @Composable
    fun displayCharEquipment() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if(selectedInventory.value != null) {
                val background = remember { lerp(Color.Gray, Color.Transparent, 0.5f) }
                val slotSize = remember { mutableStateOf(130.dp) }
                Box {
                    getRandomCharacterImage()
                    Row(Modifier.fillMaxSize()) {
                        equippedElementTab(background, slotSize)
                        Box(Modifier.weight(1f))
                    }
                }

            }
            else {
                Text("Kein Inventar ausgewählt")
            }
        }
    }

    @Composable
    fun getRandomCharacterImage() {
        val imagePath = remember { "standardCharacters/" + (1..2).random() + ".png" }
        return Image(
            painterResource(imagePath),
            "Standard Character Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun equippedElementTab(
        background: Color,
        slotSize: MutableState<Dp>
    ) {
        val maxWith = remember { 300.dp }

        var isExtended by remember { mutableStateOf(false) }

        val animatedExtended by animateDpAsState(
            targetValue = if(isExtended) maxWith else 0.dp,
            animationSpec = tween(durationMillis = 150)
        )

        Row(
            Modifier
                .background(background)
                .fillMaxHeight()
        ) {
            Box(
                Modifier
                    .onClick { isExtended = !isExtended }
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowForward, "Toggle", Modifier.padding(10.dp))
            }
            if (animatedExtended != 0.dp) {
                Column(Modifier.width(animatedExtended)) {
                    if(animatedExtended == maxWith) {
                        for(slot in selectedInventory.value!!.equipmentSlotsList) {
                            equippedItemSlot(slot, slotSize)
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
        val backGroundColor = remember {
            mutableStateOf(
                if (slot.item.value != null) Color.Gray else Color.LightGray
            )
        }

        val boxShape = remember(slot.item.value?.equipped) { mutableStateOf(if (slot.item.value != null && !slot.item.value!!.equipped) RoundedCornerShape(10.dp) else CutCornerShape(10.dp)) }

        var isHovered by remember { mutableStateOf(false) }

        val borderColor = remember(slot.item.value?.equipped, isHovered) {
            mutableStateOf(
                if (slot.item.value == null) {
                    Color.Black.copy(alpha = 0.1f)
                } else if (!slot.item.value!!.equipped) {
                    Color.Black.copy(
                        alpha = 0.3f
                    )
                } else Color.Yellow.copy(alpha = 0.7f)
            )
        }

        val scale by animateFloatAsState(
            targetValue = if (isHovered) 1.08f else 1f,
            animationSpec = tween(durationMillis = 150)
        )

        val elevation by animateDpAsState(
            targetValue = if (isHovered) 6.dp else 0.dp,
            animationSpec = tween(durationMillis = 150)
        )

        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        ) {
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
                    .background(backGroundColor.value, shape = boxShape.value)
                    .border(width = 2.dp, color = borderColor.value, shape = boxShape.value)
                    .onClick {
                        println("Clicked ${slot.name.value}")
                    }
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
            ) {
                if(slot.item.value != null) {
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
                Text(slot.name.value, Modifier.weight(1f))
                Column(Modifier.weight(1f)) {
                    Text("Klasse:")
                    Text(slot.itemClassName)
                }
            }
        }
    }
}