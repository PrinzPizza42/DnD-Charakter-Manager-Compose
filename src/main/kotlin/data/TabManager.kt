package data

import androidx.compose.runtime.mutableStateOf
import data.CharacterManager.selectedInventory

object TabManager {
    val showInventoryTab = mutableStateOf(true)
    val showScrollTab = mutableStateOf(true)
    val showCharDetailsTab = mutableStateOf(true)
    val showEquippedItemsTab = mutableStateOf(true)

    val sectionSwitch = mutableStateOf(true)

    val inventoryWindow = mutableStateOf<CustomWindow?>(null)
    val spellsWindow = mutableStateOf<CustomWindow?>(null)
    val charInfoWindow = mutableStateOf<CustomWindow?>(null)
    val equippedItemsWindow = mutableStateOf<CustomWindow?>(null)
}