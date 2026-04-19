package data.equippmentSlots

import itemClasses.Miscellaneous
import kotlinx.serialization.Serializable

@Serializable
class MiscellaneousSlot(
    var miscellaneous: Miscellaneous? = null
) : ItemSlot<Miscellaneous>(savedItem = miscellaneous, savedName = "Verschiedenes") {
    override val itemClassName = "Verschiedenes"
}