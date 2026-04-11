package main

import kotlinx.serialization.Serializable

@Serializable
class DnDCharacter(
    var inventory: Inventory? = null,
    var name: String = "",
    var race: String = "",
    var level: Int = 0,
    var health: Int = 0
)
