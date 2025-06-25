package main

import Main.Inventory
import Main.ItemClasses.EmptySlot
import Main.Spell
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.jetbrains.skiko.Cursor
import kotlin.math.roundToInt

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

        val spellLevels = remember { mutableStateListOf<Pair<Int, Int>>() }
        val spellLevelsCount = remember(spellLevels.size) { mutableStateOf(spellLevels.size) }

        val reloadScrollPanel = remember { mutableStateOf(false) }

        //null when no spell was tried to cast but could not be cast
        val couldNotCast = remember { mutableStateOf<Int?>(null) }

        Row(modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
        ) {
            // SpellDisplay
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color.Green)
            ) {
                if(!reloadScrollPanel.value) {
                    spellDisplay(refreshTrigger, inv, showScrollPanel, reloadScrollPanel, spells, spellLevels, spellLevelsCount, couldNotCast)
                }
                else {
                    reloadScrollPanel.value = false
                }
            }

            // ManaSideBar
            Box(Modifier
                .fillMaxHeight()
                .width(50.dp)
            ) {
                manaSideBar(inv, spellLevels, spellLevelsCount, couldNotCast)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun spellDisplay(refreshTrigger: MutableState<Int>,
                     inv: Inventory,
                     showScrollPanel: MutableState<Boolean>,
                     reloadScrollPanel: MutableState<Boolean>,
                     spells: SnapshotStateList<Spell>,
                     spellLevels: MutableList<Pair<Int, Int>>,
                     spellLevelsCount: MutableState<Int>,
                     couldNotCast: MutableState<Int?>
    ) {
        val selectedSpell = remember(spells) { mutableStateOf<Spell?>(null) }

        val listState = rememberLazyListState()

        Column() {
            //Function bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(Color.White), //TODO change
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
                    val foregroundColor = if(spell.isTemplate) Color.Red else Color.White
                    val isHovered = selectedSpell.value == spell
                    var inEditMode by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isHovered || inEditMode) 100f else 50f,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        ),
                    )

                    var sliderValue by remember { mutableStateOf(1f) }

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
                                .background(foregroundColor)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 50.dp, 0.dp, 0.dp)
                            ) {
                                val descInput = remember { mutableStateOf(TextFieldValue(spell.description)) }
                                BasicTextField(
                                    value = descInput.value,
                                    onValueChange = {
                                        descInput.value = it
                                        spell.description = it.text
                                    },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(20.dp, 15.dp, 0.dp, 0.dp)
                                        .weight(1f),
                                    singleLine = true,
                                    readOnly = !inEditMode
                                )

                                Button(
                                    onClick = {
                                        if(!inEditMode) {
                                            //TODO add effect or visual representation
                                            val sliderValueRounded = sliderValue.roundToInt()
                                            val oldPair = spellLevels[sliderValueRounded - 1]
                                            val newPair = Pair(oldPair.first - 1, oldPair.second)
                                            if(oldPair.first <= 0) {
                                                println("Could not cast spell because level " + sliderValueRounded + " does not contain enough unused spell slots: " + oldPair)
                                                couldNotCast.value = sliderValueRounded
                                            }
                                            else {
                                                spellLevels[sliderValueRounded - 1] = newPair
                                                inv.spellLevels[sliderValueRounded - 1] = newPair
                                                println("Cast spell " + spell.name)
                                            }
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
                                BasicTextField(
                                    value = nameInput.value,
                                    onValueChange = {
                                        nameInput.value = it
                                        spell.name = it.text
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp, 15.dp, 0.dp, 0.dp),
                                    singleLine = true,
                                    readOnly = !inEditMode
                                )
                            }

                            Text("Level: " + sliderValue.roundToInt(),
                                Modifier
                                    .padding(20.dp, 0.dp, 0.dp, 0.dp)
                            )

                            val steps: Int = if(spellLevelsCount.value - 2 > 0) spellLevelsCount.value - 2 else 0

                            Slider(
                                value = sliderValue,
                                onValueChange = {
                                    sliderValue = if( steps == 0) it.roundToInt().toFloat()
                                    else it
                                },
                                valueRange = 1f..if(spellLevelsCount.value > 0f) spellLevelsCount.value.toFloat() else 1f,
                                steps = steps,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun manaSideBar(inv: Inventory, spellLevels: MutableList<Pair<Int, Int>>, levels: MutableState<Int>, couldNotCast: MutableState<Int?>) {
        val scrollState = rememberScrollState()

        LaunchedEffect(Unit) {
            spellLevels.clear()
            spellLevels.addAll(inv.getSpellLevels())
            println("loaded levels")
        }

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                spellLevels.forEachIndexed { level, (used, max) ->
                    levelElement(level = level + 1, used = used, max = max, spellLevels, inv, couldNotCast)
                }
            }
            Column(
                Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text ="+",
                    modifier = Modifier
                        .clickable {
                            val newLevel = Pair(5, 5)
                            spellLevels.addLast(newLevel)
                            inv.addLastSpellSlot(newLevel)
                            println("added level")
                        }
                        .weight(1f)
                        .fillMaxWidth()
                        .background(lerp(Color.DarkGray, Color.White, 0.3f))
                        .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)))
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )

                Text(levels.value.toString(), Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(lerp(Color.DarkGray, Color.White, 0.2f))
                    .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )

                Text(
                    text ="-",
                    modifier = Modifier
                        .clickable {
                            if(spellLevels.size > 1) {
                                spellLevels.removeLast()
                                inv.removeSpellSlot(spellLevels.size)
                                println("removed level")
                            }
                        }
                        .weight(1f)
                        .fillMaxWidth()
                        .background(lerp(Color.DarkGray, Color.White, 0.3f))
                        .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)))
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )

                Text(
                    "Reset",
                     Modifier
                         .clickable {
                             resetUsedSpellSlots(spellLevels)
                             inv.resetUsedSpellSlots()
                             println("reset used slots")
                         }
                         .weight(1f)
                         .fillMaxWidth()
                         .background(lerp(Color.DarkGray, Color.White, 0.3f))
                         .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)))
                         .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun levelElement(level: Int, used: Int, max: Int, spellLevels: MutableList<Pair<Int, Int>>, inv: Inventory, couldNotCast: MutableState<Int?>) {
        val levelColor = lerp(getLevelColorFromGradient(level.toFloat() / spellLevels.size.toFloat()), Color.White, 0.15f)

        Box(Modifier
            .padding(4.dp, 8.dp)
            .shadow(elevation = 20.dp)
            .background(levelColor, RoundedCornerShape(5.dp))
        ) {
            Column(modifier =
                Modifier
                    .fillMaxSize()
            ) {
                Text(
                    level.toString(),
                    Modifier
                        .fillMaxWidth(),
                    color = Color.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clickable {
                            println("removed one slot from Level $level")
                            if (max > 0) {
                                val newUsed = if (used == max) used - 1 else used

                                spellLevels[level - 1] = Pair(newUsed, max - 1)
                                inv.spellLevels[level - 1] = Pair(newUsed, max - 1)
                            }
                        }
                        .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)))
                ) {
                    Text("-", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 15.sp)
                }
                Column {
                    repeat(max) { index ->
                        var isHovered by remember { mutableStateOf(false) }
                        val filled = index < used

                        val scale by animateFloatAsState(
                            targetValue = if (isHovered) 1.15f else 1f,
                            animationSpec = tween(durationMillis = 50)
                        )

                        val slotColor = getSlotColorFromGradient(
                            index.toFloat() / max.toFloat())

                        val backGroundColor =
                            if (couldNotCast.value == level) Color.Red else if (isHovered && filled) lerp(slotColor, Color.White, 0.3f) else if (filled) slotColor else lerp(levelColor, Color.Black, 0.3f)

                        val couldNotCountResetCount = remember(backGroundColor) { mutableStateOf(0) }

                        val backgroundColorAnimation by animateColorAsState(
                            targetValue = backGroundColor,
                            animationSpec = tween(durationMillis = 500)
                        )

                        Box(
                            Modifier
                                .height(20.dp)
                                .fillMaxWidth()
                                .graphicsLayer {
                                    this.scaleX = scale
                                    this.scaleY = scale
                                }
                                .padding(0.dp, 1.dp)
                                .background(
                                    if (backGroundColor == Color.Red) {
                                        if (couldNotCountResetCount.value > 20) {
                                            couldNotCast.value = null
                                        } else {
                                            couldNotCountResetCount.value++
                                        }
                                        backgroundColorAnimation
                                    } else backgroundColorAnimation,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .pointerMoveFilter(
                                    onEnter = {
                                        isHovered = true
                                        false
                                    },
                                    onExit = {
                                        isHovered = false
                                        false
                                    }
                                )
                                .clickable {
                                    if (level - 1 >= 0) {
                                        spellLevels[level - 1] = Pair(index + 1, max)
                                        inv.spellLevels[level - 1] = Pair(index + 1, max)
                                    }
                                }
                                .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)))
                        )
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clickable {
                            println("Added one slot to Level $level")
                            spellLevels[level - 1] = Pair(used, max + 1)
                            inv.spellLevels[level - 1] = Pair(used, max + 1)
                        }
                        .pointerHoverIcon(PointerIcon(_root_ide_package_.org.jetbrains.skiko.Cursor(Cursor.HAND_CURSOR)))
                ) {
                    Text("+", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 15.sp)
                }
            }
        }
    }

    private fun getLevelColorFromGradient(index: Float): Color {
        val startColor = Color(66, 48, 255)
        val endColor = Color(252, 3, 144)
        return lerp(startColor, endColor, index).copy(alpha = 0.5f)
    }

    private fun getSlotColorFromGradient(index: Float): Color {
        val startColor = Color.Blue
        val endColor = Color.Cyan
        return lerp(startColor, endColor, index)
    }

    private fun resetUsedSpellSlots(spellSlots: MutableList<Pair<Int, Int>>) {
        for(slot in spellSlots) {
            spellSlots[spellSlots.indexOf(slot)] = Pair(slot.second, slot.second)
        }
    }
}