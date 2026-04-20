package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.Item
import itemClasses.weapons.Weapon
import kotlinx.serialization.Serializable

@Serializable
class WeaponSlot(
    val initialWeapon: Weapon? = null
) : ItemSlot<Weapon>(initialItem = initialWeapon, savedName = "Waffe") {
    override val itemClassName = "Waffe"
    override fun accepts(item: Item): Boolean {
        return item is Weapon
    }
}