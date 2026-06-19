package data.statsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.StepShifterIntBig

class StatsTabModulCounter : StatsTabModul() {
    var counter: Int = 0
    var intRange: IntRange = IntRange(-1000, 1000)
    var bigStepSize: Int = 5

    @Composable
    override fun paint() {
        Column(modifier) {
            Text(title, Modifier
                .background(Color.Gray, RoundedCornerShape(10.dp))
                .padding(5.dp)
            )
            StepShifterIntBig("", intRange, mutableStateOf(counter), { counter -= 1 }, { counter += 1 }, bigStepSize)
        }
    }

    @Composable
    override fun paintModuleSettingsPopup() {

    }

}