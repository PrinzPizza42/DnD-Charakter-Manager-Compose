package data.equippmentSlots

import itemClasses.Item
import itemClasses.Miscellaneous
import itemClasses.Potion
import kotlinx.serialization.Serializable

@Serializable
class MiscellaneousSlot(
    val initialMiscellaneous: Miscellaneous? = null
) : ItemSlot<Miscellaneous>(savedItem = initialMiscellaneous, savedName = "Verschiedenes") {
    override val itemClassName = "Verschiedenes"
    override fun accepts(item: Item): Boolean {
        return item is Miscellaneous
    }
}