package data.equippmentSlots

import itemClasses.Item
import itemClasses.Miscellaneous
import kotlinx.serialization.Serializable

@Serializable
class MiscellaneousSlot(
    val initialMiscellaneous: Miscellaneous? = null
) : ItemSlot<Miscellaneous>(initialItem = initialMiscellaneous, savedName = "Verschiedenes") {
    override val itemClassName = "Verschiedenes"
    override fun accepts(item: Item): Boolean {
        return item is Miscellaneous
    }
}