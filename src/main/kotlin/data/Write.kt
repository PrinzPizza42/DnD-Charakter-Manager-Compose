package data

import main.Inventory
import main.CharacterManager
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object Write {
    fun safe(inv: Inventory) {
        saveInJSON(inv)
    }

    private fun saveInJSON(inv: Inventory) {
        try {
            val path = JsonUtil.dataPath.resolve("${inv.name}.json")
            val file = path.toFile()
            if (file.exists()) {
                println("File already exists at: ${file.absolutePath}")
                val deleted = file.delete()
                println("Deleted: $deleted")
            }
            
            val jsonString = JsonUtil.json.encodeToString(Inventory.serializer(), inv)
            Files.writeString(path, jsonString)
            
            println("Saved file")
        } catch (e: Exception) {
            e.printStackTrace()
            println("could not save inv ${inv.name} into file")
        }
    }

    fun removeInv(inv: Inventory) {
        try {
            val path = JsonUtil.dataPath.resolve("${inv.name}.json")
            if (Files.exists(path)) {
                Files.delete(path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("could not delete inv ${inv.name} from files")
        }
    }
}
