package data.statsTab

import kotlinx.serialization.Serializable

@Serializable
sealed class StatsTabModulData {
    abstract var title: String
    abstract var fillMaxWidth: Boolean
    abstract var heightValue: Float
    abstract var widthValue: Float

    @Serializable
    data class TextModul(
        override var title: String = "Text",
        override var fillMaxWidth: Boolean = false,
        override var heightValue: Float = 100f,
        override var widthValue: Float = 300f,
        var textContent: String = ""
    ) : StatsTabModulData()

    @Serializable
    data class CounterModul(
        override var title: String = "Counter",
        override var fillMaxWidth: Boolean = false,
        override var heightValue: Float = 100f,
        override var widthValue: Float = 150f,
        var counter: Int = 0,
        var intRangeMin: Int = -1000,
        var intRangeMax: Int = 1000,
        var bigStepSize: Int = 5
    ) : StatsTabModulData()
}