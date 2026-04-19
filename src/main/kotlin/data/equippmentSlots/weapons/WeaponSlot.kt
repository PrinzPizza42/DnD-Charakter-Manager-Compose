package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.weapons.Weapon
import kotlinx.serialization.Serializable

@Serializable
class WeaponSlot(
    var weapon: Weapon? = null
) : ItemSlot<Weapon>(savedItem = weapon, savedName = "Waffe") {
    override val itemClassName = "Waffe"
}