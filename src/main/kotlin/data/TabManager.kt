package data

import androidx.compose.runtime.mutableStateOf
import data.CharacterManager.selectedInventory

object TabManager {
    val showInventoryTab = mutableStateOf(true)
    val showScrollTab = mutableStateOf(true)
    val showCharDetailsTab = mutableStateOf(true)
    val showEquippedItemsTab = mutableStateOf(true)

    val sectionSwitch = mutableStateOf(true)
}