package data.equippmentSlots

import itemClasses.Armor
import itemClasses.Item
import kotlinx.serialization.Serializable

@Serializable
class ArmorSlot(
    val initialArmor: Armor? = null
) : ItemSlot<Armor>(initialItem = initialArmor, savedName = "Rüstung") {
    override val itemClassName = "Rüstung"
    override fun accepts(item: Item): Boolean {
        return item is Armor
    }

    override fun getQuickViewStat1(): String {
        return when (quickViewStat1.value) {
            quickViewStats.DESC -> item.value?.description ?: ""
            quickViewStats.ARMOR_CLASS -> item.value?.armorClass?.name ?: ""
            quickViewStats.ARMOR_VALUE -> item.value?.armorValue.toString()
            else -> ""
        }
    }

    override fun getQuickViewStat2(): String {
        return when (quickViewStat2.value) {
            quickViewStats.DESC -> item.value?.description ?: ""
            quickViewStats.ARMOR_CLASS -> item.value?.armorClass?.name ?: ""
            quickViewStats.ARMOR_VALUE -> item.value?.armorValue.toString()
            else -> ""
        }
    }

}