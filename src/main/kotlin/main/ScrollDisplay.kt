package main

import Main.Inventory
import Main.Spell
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

object ScrollDisplay {
    @Composable
    fun scrollDisplay(modifier: Modifier, inv: Inventory, refreshTrigger: MutableState<Int>, showScrollPanel: MutableState<Boolean>) {
        val spells = remember(refreshTrigger.value) {
            mutableStateListOf<Spell>().apply {
                println("loading spells from source")
                clear()
                addAll(inv.spells)
                println(inv.spells.toString())
            } }

        val reloadScrollPanel = remember { mutableStateOf(false) }

        Row(modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
            .background(Color.Red)
        ) {
            // SpellDisplay
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color.Green)
            ) {
                if(!reloadScrollPanel.value) {
                    spellDisplay(refreshTrigger, inv, showScrollPanel, reloadScrollPanel, spells)
                }
                else {
                    reloadScrollPanel.value = false
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

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun spellDisplay(refreshTrigger: MutableState<Int>, inv: Inventory, showScrollPanel: MutableState<Boolean>, reloadScrollPanel: MutableState<Boolean>, spells: SnapshotStateList<Spell>) {
        val selectedSpell = remember(spells) { mutableStateOf<Spell?>(null) }

        val listState = rememberLazyListState()

        Column() {
            //Function bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val newSpell = Spell("Neuer Zauber (Vorlage)", "(Vorlage)", 1)
                        spells.add(0, newSpell)
                        inv.spells.add(0, newSpell)
                        reloadScrollPanel.value = true
                    },
                    content = {
                        Text("+")
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp)
                )
            }

            //Spell-List
            LazyColumn (
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                state = listState
            ) {
                items(spells, key = { it.uuid}) { spell ->

                    //Spell Item
                    val foregroundColor = if(spell.isTemplate) Color.Magenta else Color.Red
                    val isHovered = selectedSpell.value == spell
                    var inEditMode by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isHovered || inEditMode) 100f else 50f,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        ),
                    )

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .pointerMoveFilter(
                                onEnter = {
                                    selectedSpell.value = spell
                                    false
                                },
                                onExit = {
                                    selectedSpell.value = null
                                    false
                                }
                            )
                            .animateItem(
                                fadeInSpec = spring(Spring.StiffnessMediumLow, Spring.DampingRatioNoBouncy),
                                fadeOutSpec = tween(250, 10, FastOutSlowInEasing)
                            )
                    ) {
                        //Background
                        Box(
                            Modifier
                                .zIndex(0f)
                                .fillMaxWidth()
                                .height(scale.dp)
                                .background(Color.Blue)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 50.dp, 0.dp, 0.dp)
                            ) {
                                val descInput = remember { mutableStateOf(TextFieldValue(spell.description)) }
                                TextField(
                                    value = descInput.value,
                                    onValueChange = {
                                        descInput.value = it
                                        spell.description = it.text
                                    },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(20.dp, 0.dp, 0.dp, 0.dp)
                                        .weight(1f),
                                    singleLine = true,
                                    readOnly = !inEditMode
                                )

                                Button(
                                    onClick = {
                                        if(!inEditMode) {
                                            println("Cast spell " + spell.name)
                                        }
                                        else {
                                            spells.remove(spell)
                                            inv.spells.remove(spell)
                                            println("removed spell " + spell.name + " internal: " + spells.contains(spell) + " external: " + inv.spells.contains(spell))
                                        }
                                    },
                                    content = {
                                        if(!inEditMode) Text("Nutzen")
                                        else Text("Löschen") //TODO replace with fitting icon
                                    },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(20.dp, 10.dp, 20.dp, 10.dp)
                                        .width(100.dp)
                                )

                                Button(
                                    onClick = {
                                        inEditMode = !inEditMode
                                    },
                                    content = {
                                        if(!inEditMode) Text("Bearbeiten")
                                        else Text("Fertig")
                                    },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(20.dp, 10.dp, 20.dp, 10.dp)
                                        .width(130.dp)
                                )
                            }
                        }

                        //Foreground
                        Row(
                            Modifier
                                .zIndex(1f)
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(foregroundColor)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            ) {
                                val nameInput = remember { mutableStateOf(TextFieldValue(spell.name)) }
                                TextField(
                                    value = nameInput.value,
                                    onValueChange = {
                                        nameInput.value = it
                                        spell.name = it.text
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp, 0.dp, 0.dp, 0.dp),
                                    singleLine = true,
                                    readOnly = !inEditMode
                                )
                            }
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Text("" + spell.cost)
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