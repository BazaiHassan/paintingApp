package com.hbazai.tintinart.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokWidth: Float = 5f
)
