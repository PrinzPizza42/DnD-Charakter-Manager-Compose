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
    var slotName: String = "Item"
) {
    @Transient
    var item: MutableState<T?> = mutableStateOf(savedItem)

    @Transient
    var name: MutableState<String> = mutableStateOf(slotName)

    @OptIn(ExperimentalUuidApi::class)
    val uuid: String = Uuid.random().toString()

    @Transient
    open val itemClassName: String = "Item"

    fun prepareForSave() {
        this.savedItem = this.item.value
        this.slotName = this.name.value
    }
}