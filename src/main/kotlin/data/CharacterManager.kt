package data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object CharacterManager {
    lateinit var inventories: SnapshotStateList<Inventory>
    val selectedInventory = mutableStateOf<Inventory?>(null)
}