package data.equippmentSlots

import itemClasses.Item
import itemClasses.Potion
import kotlinx.serialization.Serializable

@Serializable
class PotionSlot(
    val initialPotion: Potion? = null
) : ItemSlot<Potion>(savedItem = initialPotion, savedName = "Trank") {
    override val itemClassName = "Trank"
    override fun accepts(item: Item): Boolean {
        return item is Potion
    }
}