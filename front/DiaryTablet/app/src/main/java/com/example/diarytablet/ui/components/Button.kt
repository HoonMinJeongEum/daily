package com.example.diarytablet.ui.components

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.DeepPastelBlue
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.PastelSkyBlue
import com.example.diarytablet.ui.theme.SkyBlue
import com.example.diarytablet.ui.theme.White
import com.example.diarytablet.ui.theme.myFontFamily
import com.example.diarytablet.utils.playButtonSound

enum class BasicButtonColor {
    NORMAL, SEASHELL;

    fun getBackgroundColor(): Color = when (this) {
        NORMAL -> SkyBlue
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
    ButtonColor: Color = Color.White,
    useDisabledColor: Boolean = false,
    sound : Int = R.raw.all_button
) {
    val context = LocalContext.current
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
        onClick = {
            playButtonSound(context,sound )
            onClick()
        },
        modifier = modifier
            .padding(4.dp),
        enabled = enabled,
        shape = buttonShape.getShape(),
        colors = if (isOutlined) {
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = contentColor,
                disabledContainerColor = if (!enabled && useDisabledColor) Color.Transparent else Color.Gray,
                disabledContentColor = if (!enabled && useDisabledColor) contentColor else Color.LightGray
            )
        } else {
            if (ButtonColor == Color.White) {
                ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor,
                    disabledContainerColor = if (!enabled && useDisabledColor) backgroundColor else Color.Gray,
                    disabledContentColor = if (!enabled && useDisabledColor) contentColor else Color.LightGray
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                    contentColor = contentColor,
                    disabledContainerColor = if (!enabled && useDisabledColor) backgroundColor else Color.Gray,
                    disabledContentColor = if (!enabled && useDisabledColor) contentColor else Color.LightGray
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
    val context = LocalContext.current

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
                        onTap = {
                            onClick()
                            playButtonSound(context,R.raw.all_button )
                        }
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


@Composable
fun DailyButton(
    text: String,
    fontSize: TextUnit = 16.sp,
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Normal,
    backgroundColor: Color = PastelNavy,
    shadowColor: Color = Color.LightGray,
    shadowElevation: Dp = 0.dp,
    cornerRadius: Int = 8,
    @DrawableRes iconResId: Int? = null,
    width: Dp = 200.dp, // Dp 타입으로 변경하고 기본값 설정
    height: Dp = 50.dp,
    onClick: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val alpha by animateFloatAsState(
        targetValue = if (isPressed.value) 0.8f else 1.0f,
        label = "Button Press Alpha Animation" // label 추가
    )
    Box(
        modifier = Modifier
            .shadow(
                elevation = shadowElevation, // 그림자 크기
                shape = RoundedCornerShape(cornerRadius.dp), // 버튼 모서리에 맞춘 그림자 모양
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
    ) {
        Box(
            modifier = Modifier
                .size(width, height)
                .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius.dp))
                .alpha(alpha)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed.value = true
                            tryAwaitRelease() // 사용자가 터치에서 손을 뗄 때까지 대기
                            isPressed.value = false
                        },
                        onTap = {
                            onClick()
                            playButtonSound(context,R.raw.all_button)

                        }
                    )
                },
            contentAlignment = Alignment.Center // 중앙 정렬 설정
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp), // 아이콘과 텍스트 사이의 간격 설정
                modifier = Modifier.align(Alignment.Center)
            ) {
                iconResId?.let {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp), // 아이콘 크기 조정
                        tint = Color.Unspecified
                    )
                }
                Text(
                    text = text,
                    style = TextStyle(
                        fontSize = fontSize,
                        color = textColor,
                        fontWeight = fontWeight,
                        fontFamily = myFontFamily
                    )
                )
            }
        }
    }
}