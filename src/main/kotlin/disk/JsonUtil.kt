package disk

import itemClasses.Armor
import itemClasses.Consumable
import itemClasses.EmptySlot
import itemClasses.Item
import itemClasses.Miscellaneous
import itemClasses.Potion
import itemClasses.weapons.LongRangeWeapon
import itemClasses.weapons.ShortRangeWeapon
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object JsonUtil {
    val dataPath: Path
    val userImagesPath: Path

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            polymorphic(Item::class) {
                subclass(ShortRangeWeapon::class)
                subclass(LongRangeWeapon::class)
                subclass(Consumable::class)
                subclass(Potion::class)
                subclass(Miscellaneous::class)
                subclass(Armor::class)
                subclass(EmptySlot::class)
            }
        }
    }

    init {
        val home = System.getProperty("user.home")
        val dndPath = Paths.get(home, ".DnD-Character-Manager")
        if (!Files.exists(dndPath)) {
            Files.createDirectories(dndPath)
            println("Created data dir: ${dndPath.toAbsolutePath()}")
        } else {
            println(".DnD-Character-Manager exists already")
        }
        dataPath = dndPath

        val imagesPath = dataPath.resolve("user_images")
        if (!Files.exists(imagesPath)) {
            Files.createDirectories(imagesPath)
            println("Created user image dir: ${imagesPath.toAbsolutePath()}")
        }
        userImagesPath = imagesPath
    }
}
