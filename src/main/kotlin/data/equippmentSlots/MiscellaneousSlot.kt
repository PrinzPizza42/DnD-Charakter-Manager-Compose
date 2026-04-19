package data.equippmentSlots

import itemClasses.Item
import itemClasses.Miscellaneous
import itemClasses.Potion
import kotlinx.serialization.Serializable

@Serializable
class MiscellaneousSlot(
    var miscellaneous: Miscellaneous? = null
) : ItemSlot<Miscellaneous>(savedItem = miscellaneous, savedName = "Verschiedenes") {
    override val itemClassName = "Verschiedenes"
    override fun accepts(item: Item): Boolean {
        return item is Miscellaneous
    }
}