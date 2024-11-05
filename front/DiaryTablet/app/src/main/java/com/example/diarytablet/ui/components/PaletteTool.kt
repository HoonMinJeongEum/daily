package com.example.diarytablet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diarytablet.model.ToolType

@Composable
fun PaletteTool(
    selectedTool: ToolType,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onToolSelect: (ToolType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // 도구 선택 UI
        Row(modifier = Modifier.fillMaxWidth()) {
            ToolType.values().forEach { tool ->
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(if (tool == selectedTool) Color.Gray else Color.LightGray)
                        .padding(4.dp)
                        .clickable { onToolSelect(tool) }
                ) {
                    Text(text = tool.name, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 색상 선택 UI (단순히 예시로 여러 색상 제공)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Black).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color)
                        .clickable { onColorChange(color) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 두께 조절 슬라이더
        Text(text = "Brush Thickness")
        var thickness by remember { mutableStateOf(5f) } // 두께 상태 기억
        Slider(
            value = thickness,
            onValueChange = { newThickness ->
                thickness = newThickness
                onThicknessChange(newThickness)
            },
            valueRange = 1f..50f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
