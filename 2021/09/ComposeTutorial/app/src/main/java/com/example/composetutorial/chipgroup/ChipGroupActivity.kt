package com.example.composetutorial.chipgroup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.composetutorial.ui.theme.ComposeTutorialTheme

class ChipGroupActivity : ComponentActivity() {

    private val selectedCar: MutableState<Car?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ChipGroup(cars = getAllCars(), selectedCar = selectedCar.value,
                        onSelectedChanged = {
                            selectedCar.value = getCar(it)
                        })
                }
            }
        }
    }
}

