package itemClasses

import kotlinx.serialization.Serializable

@Serializable
class Miscellaneous() : Item() {
    init {
        standardIconName = "Log of Wood.png"
    }

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
    }
}
