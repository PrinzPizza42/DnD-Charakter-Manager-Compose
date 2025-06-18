package main

import Data.Write
import Main.Inventory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import main.InventoryDisplay.displayInv

object Sidebar {
    var openBoolean = true
    lateinit var inventoryMutableList: SnapshotStateList<Inventory>

    @Composable
    fun sidebar(selectedInventory: MutableState<Inventory?>) {
        val inventories = remember { inventoryMutableList }

        var open by remember { mutableStateOf(true) }
        openBoolean = open
        Column(
            if(open) {
                Modifier
                    .width(300.dp)
                    .background(LightGray)
                    .fillMaxHeight()
            } else {
                Modifier.width(50.dp)
                    .background(LightGray)
                    .fillMaxHeight()
            }
        ) {
            sidebarToggle(open = open, onToggle = { open = !open })
            if(open) {
                sidebarCreate(inventories)

                Column (
                    Modifier
                        .weight(1f)
                ){
                    addSidebarItems(inventories, selectedInventory)
                }
            }
        }
    }

    @Composable
    fun sidebarToggle(open: Boolean, onToggle: () -> Unit) {
        Button(
            onClick = {
                onToggle()
            },
            content = {
                if (open) {
                    Text("<")
                } else {
                    Text(">")
                }
            },
            modifier = Modifier
                .width(50.dp),
        )
    }

    @Composable
    fun sidebarCreate(inventories: SnapshotStateList<Inventory>) {
        val focusManager = LocalFocusManager.current

        Column (modifier = Modifier
            .width(300.dp)
            .background(Transparent)
        ) {
            val input = remember { mutableStateOf(TextFieldValue()) }
            TextField(
                value = input.value,
                onValueChange = {
                    input.value = it
                },
                modifier = Modifier
                    .fillMaxWidth(),
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
    fun addSidebarItems(inventories: SnapshotStateList<Inventory>, selectedInventory: MutableState<Inventory?>) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            items(inventories) { inv ->
                Divider(Modifier
                    .fillMaxWidth()
                    .height(8.dp))

                sidebarItem(inv, inventories, selectedInventory)
            }
        }
    }

    @Composable
    fun sidebarItem(inv: Inventory, inventories: SnapshotStateList<Inventory>, selectedInventory: MutableState<Inventory?>) {
        var showDelete by remember { mutableStateOf(false) }

        Box(Modifier
            .fillMaxWidth()
            .width(100.dp)
        ) {
            Row(Modifier
                .height(100.dp)
                .fillMaxWidth()
                .zIndex(0f)
            ) {
                Button(
                    onClick = {
                        println("opening inv " + inv.getName()) //TODO implement
                        selectedInventory.value = inv
                    },
                    content = {
                        Text(inv.getName())
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
                Column() {
                    Button(
                        onClick = {
                            println("saving inv " + inv.getName())
                            Write.safe(inv)
                        },
                        content = {
                            Text("save")
                        },
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                    )
                    Button(
                        onClick = {
                            showDelete = true
                        },
                        content = {
                            Text("del")
                        },
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                    )
                }
            }
            if(showDelete) {
                Box(Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f))
                    .clickable(
                        onClick = {showDelete = false},
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                ) {
                    Column (Modifier
                        .fillMaxSize()
                    ) {
                        Box(Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                        ) {
                            Text("Willst du " + inv.getName() + " wirklich löschen?", Modifier.align(Alignment.Center))
                        }

                        Row (Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                        ) {
                            Button(
                                onClick = {
                                    println("deleting inv " + inv.getName())
                                    inventories.remove(inv)
                                    inventoryMutableList = inventories
                                    Write.removeInv(inv)
                                    showDelete = false
                                },
                                content = {
                                    Text("Löschen")
                                },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    showDelete = false
                                },
                                content = {
                                    Text("Abbrechen")
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}