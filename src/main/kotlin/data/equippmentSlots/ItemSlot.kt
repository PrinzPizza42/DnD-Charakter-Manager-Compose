package data.equippmentSlots

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import itemClasses.Item
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
open class ItemSlot<T : Item>(
    var savedItem: T? = null,
    var savedName: String = "Item",
    var savedQuickViewStat: String = savedItem?.valueInGold.toString()
) {
    @Transient
    var item: MutableState<T?> = mutableStateOf(savedItem)

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
        this.savedItem = this.item.value
        this.savedName = this.name.value
        this.savedQuickViewStat = this.quickViewStat.value
    }
}