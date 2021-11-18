package com.example.composetutorial.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

val Int.ScaleAwareSp: TextUnit
    @Composable
    get() = dpToScaleAwareSp(dp)

val Float.ScaleAwareSp
    @Composable
    get() = dpToScaleAwareSp(dp)

@Composable
private fun dpToScaleAwareSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }