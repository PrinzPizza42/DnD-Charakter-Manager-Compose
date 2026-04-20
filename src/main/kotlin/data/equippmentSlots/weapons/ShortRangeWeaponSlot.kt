package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.Item
import itemClasses.weapons.ShortRangeWeapon
import kotlinx.serialization.Serializable

@Serializable
class ShortRangeWeaponSlot(
    val initialShortRangeWeapon: ShortRangeWeapon? = null
) : ItemSlot<ShortRangeWeapon>(initialItem = initialShortRangeWeapon, savedName = "Nahkampf-Waffe") {
    override val itemClassName = "Nahkampf-Waffe"
    override fun accepts(item: Item): Boolean {
        return item is ShortRangeWeapon
    }
}