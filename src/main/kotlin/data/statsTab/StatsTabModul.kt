package data.statsTab

import kotlinx.serialization.Serializable

@Serializable
sealed class StatsTabModulData {
    abstract val title: String
    abstract val fillMaxWidth: Boolean
    abstract val heightValue: Float
    abstract val widthValue: Float

    @Serializable
    data class TextModul(
        override val title: String = "",
        override val fillMaxWidth: Boolean = false,
        override val heightValue: Float = 50f,
        override val widthValue: Float = 150f,
        var textContent: String = ""
    ) : StatsTabModulData()

    @Serializable
    data class CounterModul(
        override val title: String = "",
        override val fillMaxWidth: Boolean = false,
        override val heightValue: Float = 50f,
        override val widthValue: Float = 150f,
        var counter: Int = 0,
        var intRange1: Int = -1000,
        var intRange2: Int = 1000,
        var bigStepSize: Int = 5
    ) : StatsTabModulData()
}