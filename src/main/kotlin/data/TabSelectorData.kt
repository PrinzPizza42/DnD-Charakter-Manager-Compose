package data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TabSelectorData {
    val inventoryWindow = mutableStateOf<CustomWindow?>(null)
    val spellsWindow = mutableStateOf<CustomWindow?>(null)
    val charInfoWindow = mutableStateOf<CustomWindow?>(null)
    val equippedItemsWindow = mutableStateOf<CustomWindow?>(null)
}