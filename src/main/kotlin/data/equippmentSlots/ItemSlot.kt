package data.equippmentSlots

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import data.CharacterManager.selectedInventory
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
    var savedQuickViewStat: String = "Test"
) {
    @Transient
    var item: MutableState<T?> = mutableStateOf(initialItem)

    @Transient
    var name: MutableState<String> = mutableStateOf(savedName)

    @Transient
    var quickViewStat: MutableState<String> = mutableStateOf(savedQuickViewStat)

    @OptIn(ExperimentalUuidApi::class)
    val uuid: String = Uuid.random().toString()

    @Transient
    open val itemClassName: String = "Item"

    open fun accepts(item: Item): Boolean {
        return true
    }

    fun prepareForSave() {
        if(this.item.value != null) this.savedItemUuID = this.item.value!!.uuid
        this.savedName = this.name.value
        this.savedQuickViewStat = this.quickViewStat.value
    }

    fun load(inv: Inventory) {
        this.item.value = inv.getItemFromUuID(this.savedItemUuID) as T?
        this.name.value = this.savedName
        this.quickViewStat.value = this.savedQuickViewStat
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
}