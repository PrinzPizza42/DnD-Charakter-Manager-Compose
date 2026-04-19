package data.equippmentSlots

import itemClasses.Armor
import itemClasses.Item
import itemClasses.Potion
import kotlinx.serialization.Serializable

@Serializable
class ArmorSlot(
    var armor: Armor? = null
) : ItemSlot<Armor>(savedItem = armor, savedName = "Rüstung") {
    override val itemClassName = "Rüstung"
    override fun accepts(item: Item): Boolean {
        return item is Armor
    }
}