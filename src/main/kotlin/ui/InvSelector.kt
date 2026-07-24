package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import disk.ImageLoader
import disk.Write
import data.CharacterManager
import data.Inventory
import data.WindowManager
import itemClasses.EmptySlot
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.PopupProperties

object InvSelector {
    var hoveredInventory by mutableStateOf<Inventory?>(null)

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun inventorySelector() {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AnimatedContent(
                targetState = hoveredInventory,
                transitionSpec = {
                    if (targetState != null && initialState != null) {
                        val targetIndex = CharacterManager.inventories.indexOf(targetState)
                        val initialIndex = CharacterManager.inventories.indexOf(initialState)
                        if (targetIndex > initialIndex) {
                            (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
                        }
                    } else {
                        fadeIn().togetherWith(fadeOut())
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { inv ->
                if (inv?.userIconName != null) {
                    Image(
                        bitmap = inv.icon.toComposeImageBitmap(),
                        contentDescription = "Background",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.fillMaxSize().alpha(0.5f)
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(Color.Black))
                }
            }

            Column(
                Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
            ) {
                createInv()

                Column(
                    Modifier
                        .weight(1f)
                ) {
                    inventoryElementsColumn()
                }
            }
        }
    }

    @Composable
    fun createInv() {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .width(500.dp)
                .background(Color.White.copy(alpha = 0.5f))
        ) {
            val input = remember { mutableStateOf(TextFieldValue()) }
            TextField(
                value = input.value,
                onValueChange = {
                    input.value = it
                },
                modifier = Modifier
                    .width(500.dp)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.Escape -> {
                                    focusManager.clearFocus()
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    },
                label = {
                    Text("Charakter erstellen")
                },
                placeholder = {
                    Text("Name")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    CharacterManager.inventories.add(Inventory(input.value.text))
                    focusManager.clearFocus()
                    input.value = TextFieldValue("")
                }),
                colors = TextFieldDefaults.textFieldColors(focusedLabelColor = Color.Black, focusedIndicatorColor = Color.Black, cursorColor = Color.Black)
            )
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun inventoryElementsColumn() {
        LazyColumn(
            Modifier
                .width(300.dp)
                .fillMaxHeight()
                .onPointerEvent(PointerEventType.Exit) { hoveredInventory = null }
        ) {
            items(CharacterManager.inventories) { inv ->
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                invElement(inv)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun invElement(inv: Inventory) {
        var showDelete by remember { mutableStateOf(false) }

        Box(
            Modifier
                .fillMaxWidth()
                .onPointerEvent(PointerEventType.Enter) { hoveredInventory = inv }
        ) {
            if (showDelete) {
                Popup(
                    onDismissRequest = { showDelete = false },
                    alignment = Alignment.Center,
                    properties = PopupProperties(focusable = true)
                ) {
                    Box(
                        Modifier
                            .width(250.dp)
                            .shadow(10.dp, RoundedCornerShape(10.dp))
                            .background(
                                Color.White,
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Column {
                            Box(
                                Modifier
                                    .padding(5.dp)
                                    .height(50.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    "Willst du " + inv.name + " wirklich löschen?",
                                    Modifier.align(Alignment.Center)
                                )
                            }

                            Row(
                                Modifier
                                    .padding(5.dp)
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        CharacterManager.inventories.remove(inv)
                                        Write.removeInv(inv)
                                        showDelete = false
                                    },
                                    content = {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Approve")
                                    }
                                )
                                IconButton(
                                    onClick = {
                                        showDelete = false
                                    },
                                    content = {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Inv display
            Row(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .zIndex(0f)
            ) {
                Button(
                    onClick = {
                        CharacterManager.selectedInventory.value = Inventory(inv)
                        inv.items.forEach { item -> if(item !is EmptySlot) println("${inv.items.indexOf(item)} : ${item.name} : ${item.uuid}") }
                        WindowManager.mainWindowTitle.value = inv.name
                    },
                    content = {
                        Text(inv.name)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                )
            }

            // Delete Button
            Row(
                Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            )
            {
                if(inv.userIconName != null) {
                    Image(
                        bitmap = inv.icon.toComposeImageBitmap(),
                        contentScale = ContentScale.FillHeight,
                        contentDescription = "Character Image",
                        modifier = Modifier
                            .padding(5.dp)
                            .background(Color.Transparent, RoundedCornerShape(5.dp))
                            .size(90.dp)
                    )
                }
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
                Box(
                    Modifier
                        .padding(30.dp)
                        .height(30.dp)
                        .width(30.dp)
                ) {
                    IconButton(
                        onClick = {
                            showDelete = true
                        },
                        content = {Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")},
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(0f)
                    )
                }
            }
        }
    }
}