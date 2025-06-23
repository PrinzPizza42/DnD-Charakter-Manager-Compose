package main

import Data.Write
import Main.Inventory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object TabSelector {
    val width = 50.dp

    @Composable
    fun displayTabSelector(showInventory: MutableState<Boolean>, showScrollPanel: MutableState<Boolean>, selectedInventory: MutableState<Inventory?>) {
        Column(Modifier
            .fillMaxHeight()
            .width(width)
        ) {
            // Return to invSelector Button
            Button(
                onClick = {
                    println("Home")
                    if(selectedInventory.value != null) {
                        Write.safe(selectedInventory.value!!)
                        println("Auto saved inv " + selectedInventory.value!!.name + " on close")
                        selectedInventory.value = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(10.dp, 30.dp)
                ,
                content = {
                    Text("X") //TODO use home icon instead
                }
            )

            //Show inv button
            Button(
                onClick = {
                    showInventory.value = !showInventory.value
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(20.dp, 20.dp)
                ,
                content = {
                    Text("Inv") //TODO replace with scroll
                }
            )

            //Show scrollPanel button
            Button(
                onClick = {
                    showScrollPanel.value = !showScrollPanel.value
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(20.dp, 20.dp)
                ,
                content = {
                    Text("Scroll") //TODO replace with scroll icon
                }
            )
        }
    }
}