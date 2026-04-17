package data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import itemClasses.Item
import ui.ItemDisplay

object ItemDisplayManager {
    var itemDisplays = mutableStateListOf<ItemDisplay>()

    fun openNewItemDisplay(item: Item? = null) {
        val newItemDisplay = ItemDisplay(mutableStateOf(item))
        newItemDisplay.draw()
        itemDisplays.add(newItemDisplay)
    }

    fun removeItemDisplay(itemDisplay: ItemDisplay) {
        itemDisplay.close()
        itemDisplays.remove(itemDisplay)
    }

    fun checkForDeletedItem(item: Item) {
        println("Checking for deleted item ${item.name}")
        var found = false
        for(display in itemDisplays) {
            if(display.item.value == item) {
                removeItemDisplay(display)
                found = true
            }
        }
        println("Finished check, found: $found")
    }

    fun closeAllDisplays() {
        for (display in itemDisplays) {
            removeItemDisplay(display)
        }
    }
}