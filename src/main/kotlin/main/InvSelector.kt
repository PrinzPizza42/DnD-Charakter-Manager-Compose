package main

import Data.ImageLoader
import Data.Write
import Main.Inventory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex

object InvSelector {
    lateinit var inventoryMutableList: SnapshotStateList<Inventory>

    @Composable
    fun inventorySelector(selectedInventory: MutableState<Inventory?>) {
        val inventories = remember { inventoryMutableList }

        Box(Modifier
            .fillMaxSize()
            .background(Color.Black)
        ) {
            Column(
                Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
            ) {
                createInv(inventories)

                Column(
                    Modifier
                        .weight(1f)
                ) {
                    inventoryElementsColumn(inventories, selectedInventory)
                }
            }
        }
    }

    @Composable
    fun createInv(inventories: SnapshotStateList<Inventory>) {
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
                    .width(500.dp),
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
                    println("entered:" + input.value.text)
                    inventories.add(Inventory(input.value.text))
                    inventoryMutableList = inventories
                    focusManager.clearFocus()
                    input.value = TextFieldValue("")
                })
            )
        }
    }

    @Composable
    fun inventoryElementsColumn(inventories: SnapshotStateList<Inventory>, selectedInventory: MutableState<Inventory?>) {
        LazyColumn(
            Modifier
                .width(300.dp)
                .fillMaxHeight()
        ) {
            items(inventories) { inv ->
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                invElement(inv, inventories, selectedInventory)
            }
        }
    }

    @Composable
    fun invElement(
        inv: Inventory,
        inventories: SnapshotStateList<Inventory>,
        selectedInventory: MutableState<Inventory?>
    ) {
        var showDelete by remember { mutableStateOf(false) }

        Box(
            Modifier
                .fillMaxWidth()
        ) {
            if(showDelete) {
                Popup(
                    onDismissRequest = { showDelete = false },
                    alignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .width(250.dp)
                            .shadow(10.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp))
                    ) {
                        Column {
                            Box(
                                Modifier
                                    .padding(5.dp)
                                    .height(50.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Willst du " + inv.name + " wirklich löschen?", Modifier.align(Alignment.Center))
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
                                        inventories.remove(inv)
                                        inventoryMutableList = inventories
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
            //Inv display
            Row(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .zIndex(0f)
            ) {
                Button(
                    onClick = {
                        println("opening inv " + inv.name)
                        selectedInventory.value = Inventory(inv)
                        inv.items.forEach { item -> println(item.name + " : " + item.uuid) }
                    },
                    content = {
                        Text(inv.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            // Delete Button
            Row(Modifier
                .fillMaxSize()
                .zIndex(1f)
            ) {
                Box(Modifier
                    .fillMaxHeight()
                    .weight(1f)
                )
                Box(
                    Modifier
                        .padding(30.dp)
                        .height(30.dp)
                        .width(30.dp)
                ) {
                    Button(
                        onClick = {
                            showDelete = true
                        },
                        content = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(0f)
                    )
                    val delete = remember { ImageLoader.loadImageFromResources("deleteIconRed.png").get().toPainter() }
                    Image(
                        painter = delete,
                        contentDescription = "delete",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f)
                    )
                }
            }
        }
    }
}