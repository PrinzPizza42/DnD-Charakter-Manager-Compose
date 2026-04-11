package main.ItemClasses

import kotlinx.serialization.Serializable

@Serializable
class Armor() : Item() {
    init {
        standardIconName = "armorIcon.png"
    }
    
    var armorValue: Int = 10
    var armorClass: ArmorClasses = ArmorClasses.MEDIUM

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int, armorValue: Int, armorClass: ArmorClasses) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
        this.armorValue = armorValue
        this.armorClass = armorClass
    }
}
