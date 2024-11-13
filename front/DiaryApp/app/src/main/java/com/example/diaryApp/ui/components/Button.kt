package com.example.diaryApp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelBlue
import com.example.diaryApp.ui.theme.GrayText
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.myFontFamily

@Composable
fun DailyButton(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Int = 16,
    textColor: Color = Color.White,
    fontWeight: androidx.compose.ui.text.font.FontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
    backgroundColor: Color = Color.Blue,
    shadowColor: Color = Color.LightGray,
    shadowElevation: Dp = 0.dp,
    cornerRadius: Int = 8,
    @DrawableRes iconResId: Int? = null,
    width: Int = 200,
    height: Int = 50,
    onClick: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }

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
                .size(width = width.dp, height = height.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius.dp))
                .alpha(alpha)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed.value = true
                            tryAwaitRelease() // 사용자가 터치에서 손을 뗄 때까지 대기
                            isPressed.value = false
                        },
                        onTap = { onClick() }
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
                        fontSize = fontSize.sp,
                        color = textColor,
                        fontWeight = fontWeight,
                        fontFamily = myFontFamily
                    )
                )
            }
        }
    }
}

@Composable
fun DailyRegisterButton(
    text: String,
    textStyle: TextStyle = MyTypography.bodyMedium,
    backgroundColor: Color = Color.Blue,
    shadowColor: Color = Color.LightGray,
    shadowElevation: Dp = 0.dp,
    cornerRadius: Int = 8,
    @DrawableRes iconResId: Int? = null,
    width: Int = 200,
    height: Int = 50,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isPressed.value) 0.8f else 1.0f,
        label = "Button Press Alpha Animation" // label 추가
    )

    val selectedFontColor = if (isSelected) DeepPastelBlue else GrayText
    val borderColor = if (isSelected) DeepPastelBlue else GrayText
    val borderWidth = if (isSelected) 12f else 3f

    Box(
        modifier = Modifier
            .shadow(
                elevation = shadowElevation, // 그림자 크기
                shape = RoundedCornerShape(cornerRadius.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .drawBehind {
                drawLine(
                    borderColor,
                    Offset(0f, size.height),
                    Offset(size.width, size.height),
                    borderWidth
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
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
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                iconResId?.let {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
                Text(
                    text = text,
                    style = textStyle.copy(
                        color = selectedFontColor
                    )
                )
            }
        }
    }
}

@Composable
fun AddProfileButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.25f)
            .aspectRatio(1f)
            .padding(8.dp)
            .clickable(
                interactionSource = remember{ MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.add_profile),
            contentDescription = "Add Profile",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AddCouponButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp, 80.dp)
            .padding(8.dp)
            .clickable(
                interactionSource = remember{ MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.add_profile),
            contentDescription = "Add Profile",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}