package data.equippmentSlots

import itemClasses.Consumable
import itemClasses.Item
import itemClasses.Potion
import kotlinx.serialization.Serializable

@Serializable
class ConsumableSlot(
    val initialConsumable: Consumable? = null
) : ItemSlot<Consumable>(savedItem = initialConsumable, savedName = "Verbrauchbares") {
    override val itemClassName = "Verbrauchbares"
    override fun accepts(item: Item): Boolean {
        return item is Consumable
    }
}