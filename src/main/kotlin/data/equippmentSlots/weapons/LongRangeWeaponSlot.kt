package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.Item
import itemClasses.Potion
import itemClasses.weapons.LongRangeWeapon
import kotlinx.serialization.Serializable

@Serializable
class LongRangeWeaponSlot(
    var longRangeWeapon: LongRangeWeapon? = null
) : ItemSlot<LongRangeWeapon>(savedItem = longRangeWeapon, savedName = "Fernkampf-Waffe") {
    override val itemClassName = "Fernkampf-Waffe"
    override fun accepts(item: Item): Boolean {
        return item is LongRangeWeapon
    }
}