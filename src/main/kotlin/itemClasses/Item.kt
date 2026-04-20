package itemClasses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import disk.ImageLoader
import disk.JsonUtil
import kotlinx.serialization.*
import java.awt.image.BufferedImage
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
    
    @OptIn(ExperimentalUuidApi::class)
    var uuid: String = Uuid.random().toString()

    @Transient
    var mutationCount by mutableStateOf(0)

    fun mutate() {
        mutationCount += 1
    }

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

    @OptIn(ExperimentalUuidApi::class)
    constructor(name: String, description: String, weight: Int, valueInGold: Int, amount: Int, uuid: String? = null) : this() {
        this.name = name
        this.description = description
        this.weight = weight
        this.valueInGold = valueInGold
        this.amount = amount
        if(uuid != null) this.uuid = uuid
    }
}
