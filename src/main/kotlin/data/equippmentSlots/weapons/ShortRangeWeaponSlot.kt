package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.Item
import itemClasses.Potion
import itemClasses.weapons.ShortRangeWeapon
import kotlinx.serialization.Serializable

@Serializable
class ShortRangeWeaponSlot(
    var shortRangeWeapon: ShortRangeWeapon? = null
) : ItemSlot<ShortRangeWeapon>(savedItem = shortRangeWeapon, savedName = "Nahkampf-Waffe") {
    override val itemClassName = "Nahkampf-Waffe"
    override fun accepts(item: Item): Boolean {
        return item is ShortRangeWeapon
    }
}