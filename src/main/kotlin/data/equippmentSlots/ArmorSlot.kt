package data.equippmentSlots

import itemClasses.Armor
import kotlinx.serialization.Serializable

@Serializable
class ArmorSlot(
    var armor: Armor? = null
) : ItemSlot<Armor>(savedItem = armor, savedName = "Rüstung") {
    override val itemClassName = "Rüstung"
}