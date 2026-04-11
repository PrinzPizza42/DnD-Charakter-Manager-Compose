package main.ItemClasses

import kotlinx.serialization.Serializable

@Serializable
class Potion() : Consumable() {
    init {
        standardIconName = "Potion - Rainbow.png"
    }

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
    }
}
