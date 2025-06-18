package main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

object ScrollDisplay {
    @Composable
    fun scrollDisplay(modifier: Modifier) {
        Box(modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
            .background(Color.Red)
        ) {
            Text("ScrollDisplay", Modifier.fillMaxSize())
        }
    }
}