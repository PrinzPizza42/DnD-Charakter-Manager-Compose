package main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import main.ItemClasses.Item
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import main.CharacterManager.selectedInventory
import main.ItemClasses.Armor
import main.ItemClasses.Consumable
import main.ItemClasses.EmptySlot
import main.ItemClasses.Miscellaneous
import main.ItemClasses.Potion
import main.ItemClasses.Weapons.LongRangeWeapon
import main.ItemClasses.Weapons.ShortRangeWeapon
import java.util.*
import kotlin.collections.ArrayList

@Serializable
class Inventory(
    var name: String = "Inventory",
    private var items: MutableList<Item> = mutableListOf(),
    val spells: ArrayList<Spell> = ArrayList(),
    var spellSlotsUsed: ArrayList<Int> = ArrayList(),
    var spellSlotsMax: ArrayList<Int> = ArrayList(),
    var maxCarryingCapacity: Float = 100f
) {
    val uuid: String = UUID.randomUUID().toString()
    val totalSlots = 50

    @Transient
    private var loadedLevels = false
    
    @Transient
    private val _spellLevels = ArrayList<Pair<Int, Int>>()

    init {
        // If this is a truly new inventory (not being deserialized)
        if (spellSlotsUsed.isEmpty() && spellSlotsMax.isEmpty() && items.isEmpty() && spells.isEmpty()) {
            addLastSpellLevel(3 to 3)
        }
    }

    constructor(other: Inventory) : this(
        name = other.name,
        items = ArrayList(other.items),
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

    val typePriority = mapOf(
        ShortRangeWeapon::class to 0,
        LongRangeWeapon::class to 1,
        Armor::class to 2,
        Potion::class to 3,
        Consumable::class to 4,
        Miscellaneous::class to 5
    )

    fun getItems(): MutableList<Item> {
        return (items.take(totalSlots).toMutableList() + List(totalSlots - items.size) { EmptySlot() }.toMutableList()) as MutableList<Item>
    }

    fun getItemsSortedByClass(): MutableList<Item> {
        val processedItems = items.sortedWith(compareBy { item -> typePriority[item::class] ?: Int.MAX_VALUE })
        return (processedItems.take(totalSlots).toMutableList() + List(totalSlots - processedItems.size) { EmptySlot() }.toMutableList()) as MutableList<Item>
    }

    fun addItem(item: Item) {
        items.add(item)
    }

    fun removeItem(item: Item) {
        items.remove(item)
    }

    //if index is -1 add last
    fun addItemAtIndex(item: Item, hoveredItem: Item) {
        if(hoveredItem is EmptySlot) {
            items.addLast(item)
        }
        else {
            val dropIndex = items.indexOf(hoveredItem)
            if (dropIndex != -1) {
                items.add(dropIndex, item)
            } else {
                items.addLast(item)
            }
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
