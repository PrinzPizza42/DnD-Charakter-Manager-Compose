package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.weapons.LongRangeWeapon
import kotlinx.serialization.Serializable

@Serializable
class LongRangeWeaponSlot(
    var longRangeWeapon: LongRangeWeapon? = null
) : ItemSlot<LongRangeWeapon>(savedItem = longRangeWeapon, slotName = "Fernkampf-Waffe") {
    override val itemClassName = "Fernkampf-Waffe"
}