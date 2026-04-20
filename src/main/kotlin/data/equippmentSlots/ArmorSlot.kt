package data.equippmentSlots

import itemClasses.Armor
import itemClasses.Item
import kotlinx.serialization.Serializable

@Serializable
class ArmorSlot(
    val initialArmor: Armor? = null
) : ItemSlot<Armor>(initialItem = initialArmor, savedName = "Rüstung") {
    override val itemClassName = "Rüstung"
    override fun accepts(item: Item): Boolean {
        return item is Armor
    }
}