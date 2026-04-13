package data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Spell(
    var name: String = "",
    var description: String = "",
    var cost: Int = 0
) {
    var isTemplate: Boolean = name.equals("Neuer Zauber (Vorlage)", ignoreCase = true)
        set(value) {
            field = value
        }
        get() {
            return name.equals("Neuer Zauber (Vorlage)", ignoreCase = true) || field
        }

    val uuid: String = UUID.randomUUID().toString()

    fun updateTemplateStatus() {
        isTemplate = name.equals("Neuer Zauber (Vorlage)", ignoreCase = true)
    }
}
