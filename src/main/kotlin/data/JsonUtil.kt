package data

import main.ItemClasses.Armor
import main.ItemClasses.Consumable
import main.ItemClasses.Item
import main.ItemClasses.Miscellaneous
import main.ItemClasses.Potion
import main.ItemClasses.Weapons.LongRangeWeapon
import main.ItemClasses.Weapons.ShortRangeWeapon
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
