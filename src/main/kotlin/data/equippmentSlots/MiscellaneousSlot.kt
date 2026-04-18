package data.equippmentSlots

import itemClasses.Armor
import itemClasses.Miscellaneous
import kotlinx.serialization.Serializable

@Serializable
class MiscellaneousSlot(
    var miscellaneous: Miscellaneous? = null
) : ItemSlot<Miscellaneous>(savedItem = miscellaneous, slotName = "Verschiedenes") {
    override val itemClassName = "Verschiedenes"
}