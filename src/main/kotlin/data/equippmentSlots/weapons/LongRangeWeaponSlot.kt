package data.equippmentSlots.weapons

import data.equippmentSlots.ItemSlot
import itemClasses.Item
import itemClasses.weapons.LongRangeWeapon
import kotlinx.serialization.Serializable

@Serializable
class LongRangeWeaponSlot(
    val initialLongRangeWeapon: LongRangeWeapon? = null
) : ItemSlot<LongRangeWeapon>(initialItem = initialLongRangeWeapon, savedName = "Fernkampf-Waffe") {
    override val itemClassName = "Fernkampf-Waffe"
    override fun accepts(item: Item): Boolean {
        return item is LongRangeWeapon
    }

    override fun getQuickViewStat1(): String {
        return when (quickViewStat1.value) {
            quickViewStats.DESC -> item.value?.description ?: ""
            quickViewStats.DAMAGE -> item.value?.damage ?: ""
            else -> ""
        }
    }

    override fun getQuickViewStat2(): String {
        return when (quickViewStat2.value) {
            quickViewStats.DESC -> item.value?.description ?: ""
            quickViewStats.DAMAGE -> item.value?.damage ?: ""
            else -> ""
        }
    }
}