package com.example.diarytablet.ui.components
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.R
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.model.ToolType


@Composable
fun PaletteDesign(
    selectedTool: ToolType,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onToolSelect: (ToolType) -> Unit,
    stickerList: List<StickerStock>,
    onStickerSelect: (StickerStock) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ColorPalette(selectedColor = selectedColor, onColorChange = onColorChange)

        Spacer(modifier = Modifier.height(16.dp))

        // 두께 조절 및 도구 선택
        ThicknessAndToolSelection(
            selectedTool = selectedTool,
            onToolSelect = onToolSelect,
            selectedColor = selectedColor,
            onColorChange = onColorChange,
            onThicknessChange = onThicknessChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 스티커 및 되돌리기 버튼
        ToolIcons(stickerList = stickerList, onStickerSelect = onStickerSelect)
    }
}

@Composable
fun ColorPalette(selectedColor: Color, onColorChange: (Color) -> Unit) {
    // 색상 리스트 정의 (3줄로 배치)
    val colors = listOf(
        Color.Red, Color(0xFFFFA500), Color.Yellow, Color(0xFFADFF2F),
        Color.Green, Color(0xFF87CEEB), Color.Blue, Color(0xFF000080),
        Color(0xFFFFC0CB), Color(0xFF800080), Color.Gray, Color.Black
    )

    // 3줄로 색상을 간격 없이 꽉 차게 배치
    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in colors.indices step 4) {
            Row(modifier = Modifier.fillMaxWidth()) {
                colors.slice(i until i + 4).forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // 각 박스가 동일한 너비를 가지도록 설정
                            .aspectRatio(1f) // 정사각형 모양 유지
                            .background(color)
                            .clickable { onColorChange(color) }
                            .border(
                                width = if (color == selectedColor) 2.dp else 0.dp,
                                color = Color.White
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ThicknessAndToolSelection(
    selectedTool: ToolType,
    onToolSelect: (ToolType) -> Unit,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 두께 조절 버튼
        Text(text = "두께 조절")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(4f, 6f, 8f, 10f).forEach { thickness ->
                Box(
                    modifier = Modifier
                        .size(30.dp + thickness.dp * 2)
                        .background(Color.Gray, CircleShape)
                        .clickable { onThicknessChange(thickness) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 도구 선택 (이미지 사용)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ToolImage(
                imageRes = R.drawable.palette_pen, // 연필 이미지
                selected = selectedTool == ToolType.PENCIL,
                onClick = { onToolSelect(ToolType.PENCIL) }
            )
            ToolImage(
                imageRes = R.drawable.palette_eraser, // 지우개 이미지
                selected = selectedTool == ToolType.ERASER,
                onClick = { onToolSelect(ToolType.ERASER) }
            )
            ToolImage(
                imageRes = R.drawable.palette_crayon, // 크레파스 이미지
                selected = selectedTool == ToolType.CRAYON,
                onClick = { onToolSelect(ToolType.CRAYON) }
            )
        }
    }
}

@Composable
fun ToolIcons(
    stickerList: List<StickerStock>,
    onStickerSelect: (StickerStock) -> Unit
) {
    // 스티커 목록과 되돌리기/다시하기 버튼
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "보유 스티커")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            stickerList.forEach { sticker ->
                Image(
                    painter = rememberAsyncImagePainter(
                        model = sticker.img,
                        placeholder = painterResource(R.drawable.loading),
                        error = painterResource(R.drawable.loading)
                    ),
                    contentDescription = "스티커 이미지",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onStickerSelect(sticker) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_undo), contentDescription = "Undo")
            Icon(painter = painterResource(id = R.drawable.ic_redo), contentDescription = "Redo")
        }
    }
}

@Composable
fun ToolImage(imageRes: Int, selected: Boolean, onClick: () -> Unit) {
    val borderSize by animateDpAsState(targetValue = if (selected) 4.dp else 0.dp, label = "")

    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(borderSize)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPaletteDesign() {
    PaletteDesign(
        selectedTool = ToolType.PENCIL,
        selectedColor = Color.Black,
        onColorChange = {},
        onThicknessChange = {},
        onToolSelect = {},
        stickerList = listOf(), // 테스트를 위해 빈 스티커 리스트 사용
        onStickerSelect = {}
    )
}

