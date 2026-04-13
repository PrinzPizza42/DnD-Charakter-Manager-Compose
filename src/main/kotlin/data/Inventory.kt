package data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import itemClasses.Item
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import itemClasses.Armor
import itemClasses.Consumable
import itemClasses.EmptySlot
import itemClasses.Miscellaneous
import itemClasses.Potion
import itemClasses.weapons.LongRangeWeapon
import itemClasses.weapons.ShortRangeWeapon
import java.util.*
import kotlin.collections.ArrayList

@Serializable
class Inventory(
    var name: String = "Inventory",
    @SerialName("items")
    private var _serializedItems: MutableList<Item> = mutableListOf(),
    val spells: ArrayList<Spell> = ArrayList(),
    var spellSlotsUsed: ArrayList<Int> = ArrayList(),
    var spellSlotsMax: ArrayList<Int> = ArrayList(),
    var maxCarryingCapacity: Float = 100f
) {
    val uuid: String = UUID.randomUUID().toString()
    val totalSlots = 50

    @Transient
    val items = mutableStateListOf<Item>()

    @Transient
    private var loadedLevels = false
    
    @Transient
    private val _spellLevels = ArrayList<Pair<Int, Int>>()

    init {
        // If this is a truly new inventory (not being deserialized)
        if (spellSlotsUsed.isEmpty() && spellSlotsMax.isEmpty() && _serializedItems.isEmpty() && spells.isEmpty()) {
            addLastSpellLevel(3 to 3)
        }

        // Initialize SnapshotStateList from serialized items
        items.addAll(_serializedItems)
        
        // Ensure the list always has 50 slots
        while (items.size < totalSlots) {
            items.add(EmptySlot())
        }
    }

    // Call this before serializing to JSON
    fun prepareForSaving() {
        // Only save non-empty slots to keep JSON clean, or save all if you want to preserve positions
        // Given it's slot-based, we should save all to preserve positions, including EmptySlots
        _serializedItems = items.toMutableList()
    }

    constructor(other: Inventory) : this(
        name = other.name,
        _serializedItems = ArrayList(other.items),
        spells = ArrayList(other.spells),
        spellSlotsUsed = ArrayList(other.spellSlotsUsed),
        spellSlotsMax = ArrayList(other.spellSlotsMax),
        maxCarryingCapacity = other.maxCarryingCapacity
    )

    fun addLastSpellLevel(level: Pair<Int, Int>) {
        spellSlotsUsed.add(level.first)
        spellSlotsMax.add(level.second)
        if (loadedLevels) {
            _spellLevels.add(level)
        }
    }

    fun removeSpellLevel(index: Int) {
        if (index in spellSlotsUsed.indices) {
            spellSlotsUsed.removeAt(index)
            spellSlotsMax.removeAt(index)
            if (loadedLevels && index in _spellLevels.indices) {
                _spellLevels.removeAt(index)
            }
        }
    }

    fun getSpellLevels(): ArrayList<Pair<Int, Int>> {
        if (!loadedLevels) {
            loadedLevels = true
            _spellLevels.clear()
            for (i in spellSlotsUsed.indices) {
                if (i < spellSlotsMax.size) {
                    _spellLevels.add(Pair(spellSlotsUsed[i], spellSlotsMax[i]))
                }
            }
        }
        return _spellLevels
    }

    fun resetUsedSpellSlots() {
        for (i in spellSlotsMax.indices) {
            spellSlotsUsed[i] = spellSlotsMax[i]
        }
        if (loadedLevels) {
            for (i in _spellLevels.indices) {
                val current = _spellLevels[i]
                _spellLevels[i] = Pair(current.second, current.second)
            }
        }
    }

    @Transient
    val typePriority = mapOf(
        ShortRangeWeapon::class to 0,
        LongRangeWeapon::class to 1,
        Armor::class to 2,
        Potion::class to 3,
        Consumable::class to 4,
        Miscellaneous::class to 5
    )

    fun getItemsSortedByClass(): List<Item> {
        return items.sortedWith(compareBy { item -> typePriority[item::class] ?: Int.MAX_VALUE })
    }

    /**
     * Triggers a list update for the given item to notify Compose that its properties might have changed.
     * Increments the mutationCount inside the item which is observed by Compose.
     */
    fun notifyItemChanged(item: Item) {
        item.mutationCount++
        val index = items.indexOf(item)
        if (index != -1) {
            items[index] = item
        }
    }

    /**
     * Adds an item to the first available empty slot, or at the end if no empty slot is found.
     */
    fun addItem(item: Item) {
        val firstEmpty = items.indexOfFirst { it is EmptySlot }
        if (firstEmpty != -1) {
            items[firstEmpty] = item
        } else if (items.size < totalSlots) {
            items.add(item)
        }
    }

    fun removeItem(item: Item) {
        val index = items.indexOf(item)
        if (index != -1) {
            items[index] = EmptySlot()
        }
    }

    /**
     * Places an item at a specific slot (the slot where hoveredItem is).
     */
    fun swapItemIndex(item: Item, hoveredItem: Item, draggedItemIndexBuffer: MutableState<Int?>) {
        val dropIndex = items.indexOf(hoveredItem)
        if (dropIndex != -1 && draggedItemIndexBuffer.value != null) {
            if (hoveredItem is EmptySlot) {
                items[dropIndex] = item
            } else {
                items[dropIndex] = item
                items[draggedItemIndexBuffer.value!!] = hoveredItem
                if (items.size > totalSlots) {
                    val lastEmpty = items.findLast { it is EmptySlot }
                    if (lastEmpty != null) {
                        items.remove(lastEmpty)
                    } else {
                        items.removeAt(items.size - 1)
                    }
                }
            }
        } else {
            addItem(item)
        }
    }

    fun updateSpellSlotsFromLevels() {
        if (loadedLevels) {
            spellSlotsUsed.clear()
            spellSlotsMax.clear()
            for (level in _spellLevels) {
                spellSlotsUsed.add(level.first)
                spellSlotsMax.add(level.second)
            }
        }
    }
}
