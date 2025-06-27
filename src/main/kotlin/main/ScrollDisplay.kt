package main

import Main.Inventory
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
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.ExperimentalResourceApi
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

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
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

        val selectedSpellSliderValue = remember { mutableStateOf(0f) }

        var addButtonHover by remember { mutableStateOf(false) }

        val addButtonScale by animateFloatAsState(
            targetValue = if(addButtonHover) 1.1f else 0.9f,
            animationSpec = tween(durationMillis = 500)
        )

        Column() {
            //Function bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(Color.DarkGray), //TODO use correlating level color of the selected spell
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource("scrollPanelBackgroundTop.png"),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0f)
                )

                //Add Button
                Box(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .zIndex(1f)
                ) {
                    Image(
                        painter = painterResource("scrollBackGround.png"),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .scale(
                                scaleX = 1f,
                                scaleY = addButtonScale
                            )
                            .zIndex(0f)
                            .align(Alignment.Center),
                    )
                    Text(
                        "Neuer Zauber",
                        Modifier
                            .zIndex(1f)
                            .clickable {
                                val newSpell = Spell("Neuer Zauber (Vorlage)", "(Vorlage)", 1)
                                spells.add(0, newSpell)
                                inv.spells.add(0, newSpell)
                                reloadScrollPanel.value = true
                            }
                            .pointerMoveFilter (
                                onEnter = {
                                    addButtonHover = true
                                    false
                                },
                                onExit = {
                                    addButtonHover = false
                                    false
                                }
                            )
                            .height(100.dp)
                            .width(200.dp)
                            .wrapContentSize(Alignment.Center)
                            .clipToBounds(),
                        fontSize = 20.sp,
                    )
                }
            }
            val backgroundScale by animateFloatAsState(
                targetValue = (selectedSpellSliderValue.value + 1) * 300f / spellLevelsCount.value,
                animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessVeryLow)
            )

            //Spell-List
            Box(
                Modifier
                    .fillMaxSize()
            ) {
                Box(
                    Modifier
                        .zIndex(0f)
                        .fillMaxSize()
                        .background(
                            lerp(getLevelColorFromGradient(selectedSpellSliderValue.value / spellLevelsCount.value.toFloat()).copy(alpha = 1f), Color.Black,  1f / spellLevelsCount.value.toFloat() / selectedSpellSliderValue.value),
                        )
                )
                Image(
                    painter = painterResource("scrollPanelBackgroundMiddle.png"),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .zIndex(1f)
                        .fillMaxSize()
                        .shadow(10.dp, shape = RectangleShape)
                )

                LazyColumn(
                    Modifier
                        .zIndex(2f)
                        .fillMaxSize(),
                    state = listState
                ) {
                    items(spells, key = { it.uuid }) { spell ->

                        //Spell Item
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

                        val textStyle =
                            if (inEditMode) TextStyle().copy(fontStyle = FontStyle.Italic) else if (spell.isTemplate) TextStyle().copy(
                                textDecoration = TextDecoration.Underline
                            ) else TextStyle()

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
                            val density = LocalDensity.current
                            val scrollEndsWith = with(density) { 43.toDp() }

                            //Background
                            Box(
                                Modifier
                                    .zIndex(1f)
                                    .fillMaxWidth()
                                    .height(scale.dp)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                ) {
                                    Image(
                                        painter = painterResource("scrollBackGroundLeft.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(scrollEndsWith),
                                        contentScale = ContentScale.FillBounds,
                                    )
                                    Image(
                                        painter = painterResource("scrollBackGroundMiddle.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1f),
                                        contentScale = ContentScale.FillBounds,
                                    )
                                    Image(
                                        painter = painterResource("scrollBackGroundRight.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(scrollEndsWith),
                                        contentScale = ContentScale.FillBounds,
                                    )
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 40.dp, 0.dp, 6.dp)
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
                                        readOnly = !inEditMode,
                                        textStyle = textStyle
                                    )

                                    val firstButtonText: String = if (inEditMode) "Löschen" else if(sliderValue.roundToInt() - 1 >= 0 && spellLevelsCount.value > 0 && spellLevels[sliderValue.roundToInt() - 1].first <= 0) "Nicht genug" else "Benutzen"
                                    val secondButtonText: String = if (inEditMode) "Fertig" else  "Bearbeiten"

                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .padding(20.dp, 10.dp, 20.dp, 10.dp)
                                            .width(130.dp)
                                    ) {
                                        Text(
                                            firstButtonText,
                                            Modifier
                                                .zIndex(1f)
                                                .clickable {
                                                    if (!inEditMode) {
                                                        val sliderValueRounded = sliderValue.roundToInt()
                                                        val oldPair = spellLevels[sliderValueRounded - 1]
                                                        val newPair = Pair(oldPair.first - 1, oldPair.second)
                                                        if (oldPair.first <= 0) {
                                                            println("Could not cast spell because level " + sliderValueRounded + " does not contain enough unused spell slots: " + oldPair)
                                                            couldNotCast.value = sliderValueRounded
                                                        } else {
                                                            spellLevels[sliderValueRounded - 1] = newPair
                                                            inv.spellLevels[sliderValueRounded - 1] = newPair
                                                            println("Cast spell " + spell.name)
                                                        }
                                                    } else {
                                                        spells.remove(spell)
                                                        inv.spells.remove(spell)
                                                        println(
                                                            "removed spell " + spell.name + " internal: " + spells.contains(
                                                                spell
                                                            ) + " external: " + inv.spells.contains(spell)
                                                        )
                                                    }
                                                }
                                                .fillMaxSize()
                                                .wrapContentSize(Alignment.Center)
                                                .clipToBounds(),
                                            fontSize = 23.sp,
                                        )
                                    }

                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .padding(20.dp, 10.dp, 20.dp, 10.dp)
                                            .width(130.dp)
                                    ) {
                                        Text(
                                            secondButtonText,
                                            Modifier
                                                .zIndex(1f)
                                                .clickable {
                                                    inEditMode = !inEditMode
                                                }
                                                .fillMaxSize()
                                                .wrapContentSize(Alignment.Center)
                                                .clipToBounds(),
                                            fontSize = 23.sp,
                                        )
                                    }
                                }
                            }

                            //Foreground
                            Box(
                                Modifier
                                    .zIndex(1f)
                                    .fillMaxWidth()
                                    .height(50.dp)
                            )
                            {
                                val density = LocalDensity.current
                                val scrollEndsWith = with(density) { 43.toDp() }
                                Row(Modifier.zIndex(1f)) {
                                    Image(
                                        painter = painterResource("scrollForegroundRight.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(scrollEndsWith),
                                        contentScale = ContentScale.FillHeight,
                                    )
                                    Image(
                                        painter = painterResource("scrollForegroundMiddle.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1f),
                                        contentScale = ContentScale.FillBounds,
                                    )
                                    Image(
                                        painter = painterResource("scrollForegroundLeft.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(scrollEndsWith),
                                        contentScale = ContentScale.FillHeight,
                                    )
                                }
                                Row(
                                    Modifier
                                        .zIndex(2f)
                                        .fillMaxWidth()
                                        .height(50.dp)
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
                                            readOnly = !inEditMode,
                                            textStyle = textStyle
                                        )
                                    }

                                    Text(
                                        "Level: " + sliderValue.roundToInt(),
                                        Modifier
                                            .padding(20.dp, 0.dp, 0.dp, 0.dp)
                                    )

                                    val steps: Int =
                                        if (spellLevelsCount.value - 2 > 0) spellLevelsCount.value - 2 else 0

                                    Slider(
                                        value = sliderValue,
                                        onValueChange = {
                                            sliderValue = if (steps == 0) it.roundToInt().toFloat()
                                            else it
                                            if (isHovered) selectedSpellSliderValue.value = sliderValue
                                        },
                                        valueRange = 1f..if (spellLevelsCount.value > 0f) spellLevelsCount.value.toFloat() else 1f,
                                        steps = steps,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(0.dp, 0.dp, 6.dp, 0.dp)
                                            .weight(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = getLevelColorFromGradient(
                                                sliderValue.roundToInt().toFloat() / spellLevelsCount.value.toFloat()
                                            ).copy(alpha = 1f),
                                            activeTrackColor = getLevelColorFromGradient(
                                                sliderValue.roundToInt().toFloat() / spellLevelsCount.value.toFloat()
                                            ),
                                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                                        )
                                    )
                                }
                            }
                        }
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
        var isHoveredLevel by remember { mutableStateOf(false) }
        var hoveredSlot by remember { mutableStateOf(-1) } //-1 when not hovered

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
                            targetValue = if (isHoveredLevel && hoveredSlot != -1 && hoveredSlot != 0 && hoveredSlot >= index) 1f + (index.toFloat() / max.toFloat()) * 0.25f  else 1f,
                            animationSpec = tween(durationMillis = 200)
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
                                        isHoveredLevel = true
                                        hoveredSlot = index
                                        false
                                    },
                                    onExit = {
                                        isHovered = false
                                        isHoveredLevel = false
                                        hoveredSlot = -1
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