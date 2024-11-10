package com.example.diarytablet.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.PastelSkyBlue
import com.example.diarytablet.ui.theme.White

enum class BasicButtonColor {
    NORMAL, SEASHELL;

    fun getBackgroundColor(): Color = when (this) {
        NORMAL -> PastelSkyBlue
        SEASHELL -> PastelNavy
    }

    fun getTextColor(): Color = White
}

enum class BasicButtonShape {
    ROUNDED, FLAT;

    fun getShape(): RoundedCornerShape = when (this) {
        ROUNDED -> RoundedCornerShape(50.dp)
        FLAT -> RoundedCornerShape(16.dp)
    }
}

@Composable
fun BasicButton(
    modifier: Modifier = Modifier.wrapContentWidth(),
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    isOutlined: Boolean = false,
    imageResId: Int? = null, // 이미지를 선택적으로 받음
    fontSize: Float = 24f,
    ButtonColor: Color = Color.White
) {
    val buttonShape = BasicButtonShape.ROUNDED
    val buttonColor = if (imageResId != null && imageResId != 11) BasicButtonColor.NORMAL else BasicButtonColor.SEASHELL
    val backgroundColor = buttonColor.getBackgroundColor()
    val contentColor = buttonColor.getTextColor()
    val image = when {
        imageResId == 11 -> null
        buttonColor == BasicButtonColor.SEASHELL -> R.drawable.jogae
        else -> imageResId
    }


    Button(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp),
        enabled = enabled,
        shape = buttonShape.getShape(),
        colors = if (isOutlined) {
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = contentColor
            )
        } else {
            if (ButtonColor == Color.White) {
                ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                    contentColor = contentColor
                )
            }
        },
        border = if (isOutlined) BorderStroke(1.dp, backgroundColor) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            image?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.size(13.dp))
            }
            Text(
                text = text,
                fontSize = fontSize.sp,
                style = MyTypography.bodyLarge,
                color = contentColor,
                modifier = Modifier.align(Alignment.CenterVertically)

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButton() {
    BasicButton(
        onClick = {},
        text = "Sample Button",
        isOutlined = false,
        imageResId = R.drawable.shop // 이미지가 있을 경우
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewButtonWithoutImage() {
    BasicButton(
        onClick = {},
        text = "Sample Button",
        isOutlined = false
        // 이미지가 없을 경우 SEASHELL로 표시
    )
}
@Composable
fun DynamicColorButton(
    text: String,
    fontSize: Int = 28,
    fontWeight: FontWeight = FontWeight.Bold,
    textStyle: TextStyle = MyTypography.bodyMedium,
    shadowColor: Color = Color.LightGray,
    shadowElevation: Dp = 0.dp,
    cornerRadius: Int = 40,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isPressed.value) 0.8f else 1.0f,
        label = "Button Press Alpha Animation"
    )

    val backgroundColor = if (isSelected) Color(0xFF83B4FF) else Color(0xFFD1D1D1)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(cornerRadius.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius.dp))
                .alpha(alpha)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed.value = true
                            tryAwaitRelease()
                            isPressed.value = false
                        },
                        onTap = { onClick() }
                    )
                }
                .padding(horizontal = 24.dp, vertical = 16.dp), // 버튼 내용과 맞게 패딩 설정
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = textStyle.copy(
                    color = textColor
                )
            )
        }
    }
}

