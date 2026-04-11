package data

import main.Inventory
import androidx.compose.runtime.snapshots.SnapshotStateList
import main.CharacterManager
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

object Read {
    fun readData() {
        println("Reading JSON")
        try {
            val preInvs = SnapshotStateList<Inventory>()
            Files.newDirectoryStream(JsonUtil.dataPath, "*.json").use { stream ->
                for (path in stream) {
                    if (path.isRegularFile()) {
                        try {
                            val jsonString = Files.readString(path)
                            val inv = JsonUtil.json.decodeFromString<Inventory>(jsonString)
                            preInvs.add(inv)
                        } catch (e: Exception) {
                            System.err.println("Fehler beim Laden von Datei ${path.fileName}: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                }
            }
            CharacterManager.inventories = preInvs
        } catch (e: Exception) {
            System.err.println("Konnte Verzeichnis nicht lesen: ${e.message}")
            e.printStackTrace()
        }
    }
}
