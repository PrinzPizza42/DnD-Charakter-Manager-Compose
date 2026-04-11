package main.ItemClasses.Weapons

import kotlinx.serialization.Serializable

@Serializable
class LongRangeWeapon() : Weapon() {
    init {
        standardIconName = "Longbow.png"
    }

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int, damage: String) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
        this.damage = damage
    }
}
