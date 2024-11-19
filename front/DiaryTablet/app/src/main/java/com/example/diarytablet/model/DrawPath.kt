package com.example.diarytablet.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path

data class DrawPath(
        val path: Path,
        val brush: Brush,
        val thickness: Float,
        val toolType: ToolType
)
