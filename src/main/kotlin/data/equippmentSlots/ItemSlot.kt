package data.equippmentSlots

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import data.Inventory
import itemClasses.Item
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Polymorphic
@Serializable
open class ItemSlot<T : Item>(
    @Transient
    val initialItem: T? = null,
    var savedItemUuID: String = "",
    var savedName: String = "Item",
    var savedQuickViewStat1: quickViewStats = quickViewStats.DESC,
    var savedQuickViewStat2: quickViewStats = quickViewStats.DAMAGE
) {
    @Transient
    var item: MutableState<T?> = mutableStateOf(initialItem)

    @Transient
    var name: MutableState<String> = mutableStateOf(savedName)

    @Transient
    var quickViewStat1: MutableState<quickViewStats> = mutableStateOf(savedQuickViewStat1)

    @Transient
    var quickViewStat2: MutableState<quickViewStats> = mutableStateOf(savedQuickViewStat2)

    open fun getQuickViewStat1(): String {
        return when (quickViewStat1.value) {
            quickViewStats.DESC -> item.value?.description ?: ""
            else -> ""
        }
    }

    open fun getQuickViewStat2(): String {
        return when (quickViewStat2.value) {
            quickViewStats.DESC -> item.value?.description ?: ""
            else -> ""
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    val uuid: String = Uuid.random().toString()

    @Transient
    open val itemClassName: String = "Item"

    open fun accepts(item: Item): Boolean {
        return true
    }

    fun prepareForSave() {
        if(this.item.value != null) this.savedItemUuID = this.item.value!!.uuid
        else this.savedItemUuID = ""
        this.savedName = this.name.value
        this.savedQuickViewStat1 = this.quickViewStat1.value
        this.savedQuickViewStat2 = this.quickViewStat2.value
    }

    fun load(inv: Inventory) {
        this.item.value = inv.getItemFromUuID(this.savedItemUuID) as T?
        this.name.value = this.savedName
        this.quickViewStat1.value = this.savedQuickViewStat1
        this.quickViewStat2.value = this.savedQuickViewStat2
    }

    fun equipNewItem(newItem: Item) {
        unequipItem()

        if (this.accepts(newItem)) {
            @Suppress("UNCHECKED_CAST")
            this.item.value = newItem as T

            newItem.equipped = true
            newItem.mutate()
        }
    }

    fun unequipItem() {
        this.item.value?.equipped = false
        this.item.value?.mutate()
        this.item.value = null
    }

    enum class quickViewStats() {
        DESC,
        ARMOR_VALUE,
        ARMOR_CLASS,
        DAMAGE
    }
}