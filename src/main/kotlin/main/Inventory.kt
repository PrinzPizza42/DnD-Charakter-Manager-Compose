package main

import main.ItemClasses.Item
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*
import kotlin.collections.ArrayList

@Serializable
class Inventory(
    var name: String = "Inventory",
    var items: ArrayList<Item> = ArrayList(),
    val spells: ArrayList<Spell> = ArrayList(),
    var spellSlotsUsed: ArrayList<Int> = ArrayList(),
    var spellSlotsMax: ArrayList<Int> = ArrayList(),
    var maxCarryingCapacity: Float = 100f
) {
    val uuid: String = UUID.randomUUID().toString()

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
