package main

import Main.Inventory
import Main.Spell
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

object ScrollDisplay {
    @Composable
    fun scrollDisplay(modifier: Modifier, selectedInventory: MutableState<Inventory>, refreshTrigger: MutableState<Int>) {
        Box(modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
            .background(Color.Red)
        ) {
            Row() {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                    ) {
                        //Functions bar
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .background(Color.Red),
                            contentAlignment = Alignment.Center
                        ) {
                            functionsBar()
                        }

                        // SpellDisplay
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Green)
                        ) {
                            spellDisplay(refreshTrigger, selectedInventory)
                        }
                     }
                }

                // ManaSideBar
                Box(Modifier
                    .fillMaxHeight()
                    .width(50.dp)
                    .background(Color.Blue)
                ) {
                    Text("Mana: 100/100")
                    manaSideBar()
                }
            }
        }
    }

    @Composable
    fun functionsBar() {
        Button(
            onClick = {
                println("add Spell")
            },
            content = {
                Text("+")
            },
            modifier = Modifier
                .height(50.dp)
                .width(100.dp)
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun spellDisplay(refreshTrigger: MutableState<Int>, selectedInventory: MutableState<Inventory>) {
        val spells = remember(refreshTrigger.value, selectedInventory.value) {
            mutableStateListOf<Spell>().apply {
                addAll(selectedInventory.value.spells)
            } }

        val selectedSpell = remember { mutableStateOf<Spell?>(null) }

        LazyColumn(

        ) {
            items(spells.size) { index ->
                val spell = spells[index]
                val scale = remember(selectedSpell.value) {
                    if(selectedSpell.value != null && selectedSpell.value == spell) {
                        mutableStateOf(1f)
                    } else {
                        mutableStateOf(0f)
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(70.dp)
                        .pointerMoveFilter(
                            onEnter = {
                                println("entered " + spell.name)
                                selectedSpell.value = spell
                                false
                            },
                            onExit = {
                                println("exited " + spell.name)
                                selectedSpell.value = null
                                false
                            }
                        )
                ) {
                    //Background
                    Box(
                        Modifier
                            .fillMaxSize()
                            .zIndex(0f)
                            .background(Color.White)
                    ) {
                    }

                    //Foreground
                    Box(
                        Modifier
                            .fillMaxSize()
                            .zIndex(1f)
                    ) {
                        Column {
                            Row(
                                Modifier
                                    .fillMaxSize()
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                ) {
                                    Text(spell.name)
                                }
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                ) {
                                    Text("" + spell.cost)
                                }
                            }
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(Color.Blue)
                                    .scale(
                                        scaleX = 1f,
                                        scaleY = scale.value
                                    )
                            ) {
                                Text(spell.description)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun manaSideBar() {

    }
}