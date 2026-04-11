package main.ItemClasses.Weapons

import main.ItemClasses.Item
import kotlinx.serialization.Serializable

@Serializable
open class Weapon() : Item() {
    var damage: String = ""

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int, damage: String) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
        this.damage = damage
    }
}
