package data.equippmentSlots

import itemClasses.Consumable
import kotlinx.serialization.Serializable

@Serializable
class ConsumableSlot(
    var consumable: Consumable? = null
) : ItemSlot<Consumable>(savedItem = consumable, slotName = "Verbrauchbares") {
    override val itemClassName = "Verbrauchbares"
}