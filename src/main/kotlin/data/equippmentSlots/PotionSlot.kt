package data.equippmentSlots

import itemClasses.Potion
import kotlinx.serialization.Serializable

@Serializable
class PotionSlot(
    var potion: Potion? = null
) : ItemSlot<Potion>(savedItem = potion, savedName = "Trank") {
    override val itemClassName = "Trank"
}