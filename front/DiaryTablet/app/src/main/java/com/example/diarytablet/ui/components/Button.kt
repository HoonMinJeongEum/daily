package com.example.diarytablet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
        NORMAL -> PastelNavy
        SEASHELL -> PastelSkyBlue
    }

    fun getTextColor(): Color = White
}

enum class BasicButtonShape {
    ROUNDED, FLAT;

    fun getShape(): RoundedCornerShape = when (this) {
        ROUNDED -> RoundedCornerShape(16.dp)
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
    imageResId: Int? = null // 이미지를 선택적으로 받음
) {
    val buttonShape = BasicButtonShape.ROUNDED
    val buttonColor = if (imageResId != null) BasicButtonColor.NORMAL else BasicButtonColor.SEASHELL
    val backgroundColor = buttonColor.getBackgroundColor()
    val contentColor = buttonColor.getTextColor()
    val image = if (buttonColor == BasicButtonColor.SEASHELL) R.drawable.jogae else imageResId

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
            ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        },
        border = if (isOutlined) BorderStroke(1.dp, backgroundColor) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
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
                fontSize = 16.sp,
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
