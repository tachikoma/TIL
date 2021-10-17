package com.example.composetutorial.chipgroup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.composetutorial.ui.theme.ComposeTutorialTheme

class ChipGroupMultiActivity : ComponentActivity() {

    private val selectedCar: MutableState<List<Car?>> = mutableStateOf(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ChipGroupMulti(cars = getAllCars(), selectedCars = selectedCar.value,
                        onSelectedChanged = {
                            val oldList: MutableList<Car?> = selectedCar.value.toMutableList()
                            var carFromString = getCar(it)

                            if (oldList.contains(carFromString)) {
                                oldList.remove(carFromString)
                            } else {
                                oldList.add(carFromString)
                            }

                            selectedCar.value = oldList
                        })
                }
            }
        }
    }
}

