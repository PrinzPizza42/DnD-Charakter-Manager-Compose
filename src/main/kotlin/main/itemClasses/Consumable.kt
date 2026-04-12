package main.itemClasses

import kotlinx.serialization.Serializable

@Serializable
open class Consumable() : Item() {
    init {
        standardIconName = "Loaf of Bread.png"
    }

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
    }
}
