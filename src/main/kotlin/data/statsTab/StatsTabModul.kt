package data.statsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

abstract class StatsTabModul {
    var height: Dp = 50.dp
    var width: Dp = 150.dp
    var fillMaxWidth: Boolean = false
    var title: String = ""
    val modifier: Modifier = if(fillMaxWidth)
    {
        Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(height)
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .padding(5.dp)
    }
    else
    {
        Modifier
            .padding(10.dp)
            .width(width)
            .height(height)
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .padding(5.dp)
    }

    @Composable
    abstract fun paint()

    @Composable
    fun paintSizeSettingPopup() {

    }

    @Composable
    abstract fun paintModuleSettingsPopup()
}