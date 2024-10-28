package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawingScreen() {
    val paths = remember { mutableStateListOf<Pair<List<Offset>, Pair<Color, Float>>>() } // 경로에 색상과 굵기 포함
    var currentPath by remember { mutableStateOf(mutableListOf<Offset>()) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentThickness by remember { mutableStateOf(6f) }

    Column {
        // 색상 및 굵기 설정 UI
        Row(modifier = Modifier.padding(8.dp)) {
            // 색상 선택 버튼
            Button(onClick = { currentColor = Color.Red }) { Text("Red") }
            Button(onClick = { currentColor = Color.Green }) { Text("Green") }
            Button(onClick = { currentColor = Color.Blue }) { Text("Blue") }

            Spacer(modifier = Modifier.width(16.dp))

            // 굵기 선택 슬라이더
            Slider(
                value = currentThickness,
                onValueChange = { currentThickness = it },
                valueRange = 1f..20f,
                modifier = Modifier.width(200.dp)
            )
        }

        // 드로잉 캔버스
        Canvas(
            modifier = Modifier
                .size(916.dp, 583.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = mutableListOf(offset)
                        },
                        onDrag = { change, _ ->
                            currentPath = (currentPath + change.position).toMutableList()
                        },
                        onDragEnd = {
                            // 현재 경로와 설정된 색상 및 굵기를 저장
                            paths.add(Pair(currentPath, Pair(currentColor, currentThickness)))
                            currentPath = mutableListOf() // 현재 경로 초기화
                        }
                    )
                }
        ) {
            // 저장된 모든 경로 그리기
            paths.forEach { (path, settings) ->
                val (color, thickness) = settings
                path.windowed(2) { (start, end) ->
                    drawLine(
                        color = color,
                        start = start,
                        end = end,
                        strokeWidth = thickness
                    )
                }
            }

            // 현재 드래그 중인 경로 그리기
            currentPath.windowed(2) { (start, end) ->
                drawLine(
                    color = currentColor,
                    start = start,
                    end = end,
                    strokeWidth = currentThickness
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()
}
