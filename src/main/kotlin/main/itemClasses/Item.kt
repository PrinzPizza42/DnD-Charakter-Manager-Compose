package main.itemClasses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.ImageLoader
import data.JsonUtil
import kotlinx.serialization.*
import java.awt.image.BufferedImage
import java.util.*

@Serializable
@Polymorphic
abstract class Item() {
    var name: String = ""
    var description: String = ""
    var weight: Int = 0
    var valueInGold: Int = 0
    var amount: Int = 0

    @Transient
    var standardIconName: String = "Log of Wood.png"
    
    val iconName: String
        get() = standardIconName

    var userIconName: String? = null
    var equipped: Boolean = false
    
    val uuid: String = UUID.randomUUID().toString()

    @Transient
    var mutationCount by mutableStateOf(0)

    val icon: BufferedImage
        get() {
            userIconName?.let { uName ->
                try {
                    return ImageLoader.loadImageFromFile(
                        JsonUtil.userImagesPath.resolve(uName).toAbsolutePath().toString()
                    ).get()
                } catch (e: Exception) {
                    println("Could not find user icon $uName for $name")
                    userIconName = null
                    println("Reset user icon name for $name")
                }
            }
            return ImageLoader.loadImageFromResources(standardIconName).get()
        }

    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
    }
}
