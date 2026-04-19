package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.weapons.ShortRangeWeapon
import kotlinx.serialization.Serializable

@Serializable
class ShortRangeWeaponSlot(
    var shortRangeWeapon: ShortRangeWeapon? = null
) : ItemSlot<ShortRangeWeapon>(savedItem = shortRangeWeapon, savedName = "Nahkampf-Waffe") {
    override val itemClassName = "Nahkampf-Waffe"
}