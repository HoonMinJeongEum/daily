import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PaletteTool(
    onThicknessChange: (Float) -> Unit,
    onColorChange: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 두께 조절 슬라이더
        Text(text = "Brush Thickness")
        Slider(
            value = 6f,
            onValueChange = onThicknessChange,
            valueRange = 1f..20f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 색상 선택 버튼
        Text(text = "Brush Color")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ColorButton(Color.Black, onColorChange)
            ColorButton(Color.Red, onColorChange)
            ColorButton(Color.Blue, onColorChange)
            ColorButton(Color.Green, onColorChange)
        }
    }
}

@Composable
fun ColorButton(color: Color, onColorSelected: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color)
            .clickable { onColorSelected(color) }
    )
}
