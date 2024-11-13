package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.ui.theme.MyTypography

@Composable
fun RecommendWordModal(
    roundWords: List<String>,
    onWordSelected: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.3f)
        ) {
            roundWords.forEachIndexed { index, word ->
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed = interactionSource.collectIsPressedAsState().value
                Button(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(17.dp)
                        ),
                    onClick = {
                        onWordSelected(word)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPressed) Color(0xFF5A72A0) else Color(0xFF83B4FF)
                    ),
                    shape = RoundedCornerShape(17.dp),
                    interactionSource = interactionSource
                ){
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val buttonHeight = with(LocalDensity.current) { maxHeight.toPx() }
                        Text(
                            text = word,
                            fontSize = (buttonHeight * 0.25f).sp,
                            style = MyTypography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                if (index < roundWords.size - 1) {
                    Spacer(modifier = Modifier.weight(0.3f))
                }
            }
        }
    }
}

