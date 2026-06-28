package data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import data.equippmentSlots.ArmorSlot
import data.equippmentSlots.ConsumableSlot
import data.equippmentSlots.ItemSlot
import data.equippmentSlots.PotionSlot
import data.equippmentSlots.weapons.LongRangeWeaponSlot
import data.equippmentSlots.weapons.ShortRangeWeaponSlot
import data.statsTab.StatsTabModulData
import disk.ImageLoader
import disk.JsonUtil
import itemClasses.Item
import kotlinx.serialization.Polymorphic
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
import java.awt.image.BufferedImage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
class Inventory(
    var name: String = "Inventory",
    @SerialName("items")
    private var _serializedItems: MutableList<Item> = mutableListOf(),
    val spells: MutableList<Spell> = ArrayList(),
    var spellSlotsUsed: MutableList<Int> = ArrayList(),
    var spellSlotsMax: MutableList<Int> = ArrayList(),
    var maxCarryingCapacity: Float = 100f,
    @SerialName("equippedItems")
    private var _serializedEquipmentSlotsList: MutableList<@Polymorphic ItemSlot<out Item>> = mutableListOf(),
    @SerialName("slotsTabModulList")
    private var _serializedStatsTabModulList: MutableList<StatsTabModulData> = mutableListOf()
) {
    val uuid: String = UUID.randomUUID().toString()
    val totalSlots = 50
    var userIconName: String? = null

    val icon: BufferedImage
        get() {
            userIconName?.let { uName ->
                try {
                    return ImageLoader.loadImageFromFile(
                        JsonUtil.userImagesPath.resolve(uName).toAbsolutePath().toString()
                    ).get()
                } catch (e: Exception) {
                    println("Could not find user icon $uName for $name")
                    userIconName = null
                    println("Reset user icon name for $name")
                }
            }
            return ImageLoader.loadImageFromResources("standardCharacters/" + (1..2).random() + ".png").get()
        }

    @Transient
    val items = mutableStateListOf<Item>()

    @Transient
    var equipmentSlotsList: SnapshotStateList<ItemSlot<out Item>> = mutableStateListOf(
        ArmorSlot(),
        ShortRangeWeaponSlot(),
        LongRangeWeaponSlot(),
        PotionSlot(),
        ConsumableSlot()
    )

    @Transient
    var statsTabModulList: SnapshotStateList<StatsTabModulData> = mutableStateListOf(
        StatsTabModulData.CounterModul(),
        StatsTabModulData.TextModul(),
        StatsTabModulData.TextModul()
    )

    fun removeItemFromSlots(item: Item) {
        equipmentSlotsList.filter { it.item.value?.uuid == item.uuid }.forEach {
            it.unequipItem()
        }
    }

    fun removeSlot(index: Int) {
        equipmentSlotsList[index].item.value?.equipped = false
        equipmentSlotsList.removeAt(index)
    }

    fun moveSlotUp(index: Int) {
        switchSlot(index, index + 1)
    }

    fun moveSlotDown(index: Int) {
        switchSlot(index, index - 1)
    }

    private fun switchSlot(index1: Int, index2: Int) {
        if(index1 < 0 || index2 < 0 || index1 >= equipmentSlotsList.size || index2 >= equipmentSlotsList.size) return

        val bufferSlot = equipmentSlotsList[index1]
        equipmentSlotsList[index1] = equipmentSlotsList[index2]
        equipmentSlotsList[index2] = bufferSlot
    }

    fun addSlot(slot: ItemSlot<out Item>) {
        equipmentSlotsList.addLast(slot)
    }

    @Transient
    private var loadedLevels = false
    
    @Transient
    private val _spellLevels = ArrayList<Pair<Int, Int>>()

    init {
        if (spellSlotsUsed.isEmpty() && spellSlotsMax.isEmpty() && _serializedItems.isEmpty() && spells.isEmpty() && _serializedEquipmentSlotsList.isEmpty()) {
            addLastSpellLevel(3 to 3)
        }

        items.addAll(_serializedItems)

        if (_serializedEquipmentSlotsList.isNotEmpty()) {
            equipmentSlotsList.clear()
            equipmentSlotsList.addAll(_serializedEquipmentSlotsList)
        }

        // Sync the @Transient states for all loaded slots
        for (slot in equipmentSlotsList) {
            slot.load(this)
        }

        if(_serializedStatsTabModulList.isNotEmpty()) {
            statsTabModulList.clear()
            statsTabModulList.addAll(_serializedStatsTabModulList)
        }

        while (items.size < totalSlots) {
            items.add(EmptySlot())
        }
    }

    fun prepareForSaving() {
        _serializedItems = items.toMutableList()

        _serializedEquipmentSlotsList = equipmentSlotsList

        for(slot in _serializedEquipmentSlotsList) {
            slot.prepareForSave()
        }

        _serializedStatsTabModulList = statsTabModulList
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getItemFromUuID(string: String): Item? {
        return this.items.firstOrNull { it.uuid == string }
    }

    constructor(other: Inventory) : this(
        name = other.name,
        _serializedItems = ArrayList(other.items),
        _serializedEquipmentSlotsList = ArrayList(other.equipmentSlotsList),
        spells = ArrayList(other.spells),
        spellSlotsUsed = ArrayList(other.spellSlotsUsed),
        spellSlotsMax = ArrayList(other.spellSlotsMax),
        maxCarryingCapacity = other.maxCarryingCapacity
    ) {
        this.userIconName = other.userIconName
    }

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
        item.mutate()
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
}
