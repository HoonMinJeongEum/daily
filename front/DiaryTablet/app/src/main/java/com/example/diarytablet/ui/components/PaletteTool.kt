import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diarytablet.ui.screens.ToolType

@Composable
fun PaletteTool(
    selectedTool: ToolType,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onToolSelect: (ToolType) -> Unit
) {
    val colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow)
    var selectedColor by remember { mutableStateOf(colors.first()) }
    var thickness by remember { mutableStateOf(6f) }

    Column(modifier = Modifier.padding(8.dp)) {
        // 색상 팔레트
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color, shape = CircleShape)
                        .clickable {
                            selectedColor = color
                            onColorChange(color)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 도구 선택
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolType.values().forEach { tool ->
                Text(
                    text = tool.name,
                    color = if (tool == selectedTool) Color.Blue else Color.Black,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onToolSelect(tool)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 두께 조절 슬라이더
        Text("Thickness")
        Slider(
            value = thickness,
            onValueChange = {
                thickness = it
                onThicknessChange(it)
            },
            valueRange = 1f..20f
        )
    }
}
