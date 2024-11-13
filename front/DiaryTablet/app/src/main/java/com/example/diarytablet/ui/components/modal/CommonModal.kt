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
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.DailyButton
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.GrayText
import com.example.diarytablet.ui.theme.MyTypography
import kotlinx.coroutines.launch

@Composable
fun CommonModal(
    onDismissRequest: () -> Unit,
    titleText: String,
    cancelText: String,
    confirmText: String,
    modifier: Modifier = Modifier,
    confirmButtonColor: Color,
    onConfirm: () -> Unit
){
    val context = LocalContext.current
    val screenWidth = context.resources.displayMetrics.widthPixels

    // 화면 비율에 따른 fontSize 계산 (예: 화면 너비의 1.5%)
    val buttonFontSize = (screenWidth * 0.015).sp

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp, bottom = 24.dp, start = 18.dp, end = 18.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = titleText,
                        color = DeepPastelNavy,
                        style = MyTypography.bodySmall,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DailyButton(
                            text = cancelText,
                            fontSize = buttonFontSize,
                            textColor = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            backgroundColor = GrayText,
                            cornerRadius = 35,
                            width = 80.dp,
                            height = 50.dp,
                            onClick = {
                                onDismissRequest()
                            }
                        )

                        DailyButton(
                            text = confirmText,
                            fontSize = buttonFontSize,
                            textColor = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            backgroundColor = confirmButtonColor,
                            cornerRadius = 35,
                            width = 80.dp,
                            height = 50.dp,
                            onClick = {
                                onConfirm()
                                onDismissRequest()
                            }
                        )
                    }

                }
            }
        }
    }
}