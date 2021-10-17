package com.example.composetutorial.chipgroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ChipGroupMulti(
    cars: List<Car> = getAllCars(),
    selectedCars: List<Car?> = listOf(),
    onSelectedChanged: (String) -> Unit = {},
) {
    Column(Modifier.padding(8.dp)) {
        LazyRow {
            items(cars) { car ->
                Chip(
                    car.value,
                    isSelected = selectedCars.contains(car),
                    onSelectionChanged = {
                        onSelectedChanged(it)
                    }
                )
            }
        }
    }
}