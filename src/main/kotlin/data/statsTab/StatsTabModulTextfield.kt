package data.statsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class StatsTabModulTextfield : StatsTabModul() {
    var text: String = ""

    @Composable
    override fun paint() {
        Column(modifier) {
            Text(title, Modifier
                .background(Color.Gray, RoundedCornerShape(10.dp))
                .padding(5.dp)
            )
            TextField(text, { text = it })
        }
    }

    @Composable
    override fun paintModuleSettingsPopup() {

    }
}