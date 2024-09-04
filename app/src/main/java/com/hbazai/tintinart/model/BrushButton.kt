package com.hbazai.tintinart.model

import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp

data class BrushButton(
    val name:String,
    val icon:Painter,
    val strokValue:Float,
    val size:Dp,
)
