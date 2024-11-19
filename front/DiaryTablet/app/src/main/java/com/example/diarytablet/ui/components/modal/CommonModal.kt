package com.example.diarytablet.ui.components.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.DailyButton
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.GrayText
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.myFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommonModal(
    onDismissRequest: () -> Unit,
    titleText: String,
    cancelText: String = "아니오",
    confirmText: String = "네",
    modifier: Modifier = Modifier,
    confirmButtonColor: Color = PastelNavy,
    confirmIconResId: Int? = null,
    onConfirm: () -> Unit
){
    val context = LocalContext.current
    val screenWidth = context.resources.displayMetrics.widthPixels
    val screenHeight = context.resources.displayMetrics.heightPixels

    val buttonWidth = (screenWidth * 0.065).dp
    val buttonHeight = (screenHeight * 0.045).dp
    val buttonFontSize = (buttonWidth.value * 0.2).sp
    val textFontSize = (screenWidth * 0.014).sp
    val lineHeightRatio = 1.5f // 원하는 배율로 설정
    val lineHeight = textFontSize * lineHeightRatio

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp, bottom = 40.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = titleText,
                        fontSize = textFontSize,
                        color = DeepPastelNavy,
                        fontFamily = myFontFamily,
                        textAlign = TextAlign.Center,
                        lineHeight = lineHeight,
                        modifier = Modifier.padding(bottom = 40.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        DailyButton(
                            text = cancelText,
                            fontSize = buttonFontSize,
                            textColor = Color.White,
                            fontWeight = FontWeight.Normal,
                            backgroundColor = GrayText,
                            cornerRadius = 35,
                            width = buttonWidth,
                                    height = buttonHeight,
                                            onClick = {
                                onDismissRequest()
                            }
                        )

                        DailyButton(
                            text = confirmText,
                            fontSize = buttonFontSize,
                            textColor = Color.White,
                            fontWeight = FontWeight.Normal,
                            backgroundColor = confirmButtonColor,
                            iconResId = confirmIconResId,
                            cornerRadius = 35,
                            width = buttonWidth,
                                    height = buttonHeight,
                            onClick = {
                                onConfirm()
                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun CommonPopup(
    onDismissRequest: () -> Unit,
    titleText: String,
){
    val context = LocalContext.current
    val screenWidth = context.resources.displayMetrics.widthPixels
    val screenHeight = context.resources.displayMetrics.heightPixels

    val textFontSize = (screenWidth * 0.014).sp
    val lineHeightRatio = 1.5f // 원하는 배율로 설정
    val lineHeight = textFontSize * lineHeightRatio

    LaunchedEffect(Unit) {
        delay(2000)
        onDismissRequest()
    }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 40.dp, start = 30.dp, end = 30.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = titleText,
                        fontSize = textFontSize,
                        color = DeepPastelNavy,
                        fontFamily = myFontFamily,
                        textAlign = TextAlign.Center,
                        lineHeight = lineHeight,
                    )
                }
            }
        }
    }
}