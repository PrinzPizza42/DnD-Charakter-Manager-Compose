package main

import Main.Inventory
import Main.ItemClasses.Item
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

object InventoryDisplay {

    @Composable
    fun displayEmptyDisplay(modifier: Modifier) {
        Box (modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
        ) {
            Text("Wähle ein Inventar aus")
        }
        //TODO set closed backpack image here and no content (maybe with "Wähle einen Charakter aus" text with blurred background?)
    }

    @Composable
    fun displayInv(inv: Inventory, modifier: Modifier) {
        //TODO set opened backpack image as background here (maybe splitt in three pieces so it does not stretch when the width changes?)
        //TODO set randomly selected scenery behind backpack
        Column (modifier
            .fillMaxHeight()
        ) {
            sceneryAndBackPackTop()
            backPack(inv)
        }
    }

    @Composable
    fun sceneryAndBackPackTop() {
        Box(Modifier
            .fillMaxWidth()
            .height(150.dp)
        ) {
            //Background
            Box(Modifier
                .zIndex(0f)
                .background(Color.Green)
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
            ) {
            }

            //Foreground
            Column (Modifier
                .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .zIndex(1f)
                        .weight(1f)
                        .fillMaxWidth()
                )
                {
                }
                Box(
                    Modifier
                        .zIndex(1f)
                        .background(Color.LightGray)
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
                {
                }
            }

            //Sorting
            Column (Modifier
                .fillMaxWidth()

            ) {
                Box(
                    Modifier
                        .zIndex(2f)
                        .weight(1f)
                        .fillMaxWidth()
                )
                {
                }
                Box(
                    Modifier
                        .zIndex(2f)
                        .background(Color.Transparent)
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
                {
                    val options = listOf("Eigene Sortierung", "Nach Klasse")
                    var selectedOption by remember { mutableStateOf(options[0]) }
                    Row {
                        options.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f)
                            ) {
                                RadioButton(
                                    selected = (option == selectedOption),
                                    onClick = { selectedOption = option }
                                )
                                Text(option)
                            }
                        }

                        Button(
                            onClick = {
                                println("adding item")
                            },
                            content = {
                                Text("+")
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .height(75.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun backPack(inv: Inventory) {
        //Background
        Box(Modifier
            .fillMaxSize()
        ) {
            Box(Modifier
                .zIndex(1f)
                .fillMaxSize()
            ) {

            }

            //Foreground
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color.Red)
                    .zIndex(2f),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                println("Displaying inv " + inv.name)
                items(inv.items.size) { index ->
                    item(inv.items[index])
                }
            }
        }
    }

    @Composable
    fun item(item: Item) {
        Box(Modifier
            .fillMaxSize()
            .background(Color.Green)
            .aspectRatio(1f)
        )
        {
            Column(Modifier
                .fillMaxSize()

            ) {
                //Name
                Text(
                    item.name,
                    Modifier
                        .fillMaxWidth()
                        .weight(5f)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    //Filler
                    Box(
                        Modifier.weight(4f)
                    ) {}
                    //Amount
                    Text(
                        item.amount.toString(),
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
