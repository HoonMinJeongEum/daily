package com.example.diarytablet.ui.components.modal//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.diarytablet.ui.components.BasicButton
//import kotlinx.coroutines.delay
//
//@Composable
//fun PreviewModal(
//    isModalVisible: Boolean,
//    onDismiss: () -> Unit,
//    drawingSteps: List<DrawingStep>,
//    mainText: String,
//    buttonText: String,
//    successButtonColor: Color
//) {
//    if (isModalVisible) {
//        NormalModal(
//            isModalVisible = isModalVisible,
//            onDismiss = onDismiss,
//            onSuccessClick = onDismiss, // 미리보기 완료 후 닫기
//            mainText = mainText,
//            buttonText = buttonText,
//            successButtonColor = successButtonColor
//        ) {
//            // 미리보기 화면을 중앙에 표시
//            DrawingPlaybackView(drawingSteps = drawingSteps, page = 0)
//        }
//    }
//}
//
//@Composable
//fun NormalModal(
//    isModalVisible: Boolean,
//    onDismiss: () -> Unit,
//    onSuccessClick: () -> Unit,
//    mainText: String,
//    buttonText: String,
//    successButtonColor: Color,
//    content: @Composable () -> Unit // 추가된 content 슬롯
//) {
//    if (isModalVisible) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xB3000000))
//                .clickable { onDismiss() }
//        ) {
//            Box(
//                modifier = Modifier
//                    .width(550.dp)
//                    .wrapContentHeight()
//                    .padding(40.dp)
//                    .background(Color.White, shape = RoundedCornerShape(30.dp))
//                    .padding(24.dp)
//                    .align(Alignment.Center)
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    // 닫기 버튼
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    Text(
//                        fontSize = 28.sp,
//                        text = mainText
//                    )
//
//                    // 미리보기 콘텐츠 추가
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(300.dp) // 미리보기 영역 높이 조절
//                            .padding(vertical = 16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        content() // 미리보기 내용 표시
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        BasicButton(
//                            text = buttonText,
//                            imageResId = 11,
//                            onClick = onSuccessClick,
//                            ButtonColor = successButtonColor
//                        )
//                        Spacer(modifier = Modifier.width(16.dp))
//                        BasicButton(
//                            text = "취소",
//                            imageResId = 11,
//                            onClick = onDismiss,
//                            ButtonColor = Color.LightGray
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
////@Composable
////fun DrawingPlaybackView(drawingSteps: List<DrawingStep>, page: Int) {
////    val currentStepIndex = remember { mutableStateOf(0) }
////    val currentPath = remember { Path() }
////
////    LaunchedEffect(Unit) {
////        while (currentStepIndex.value < drawingSteps.size) {
////            delay(100) // 각 단계가 그려지는 간격 (100ms)
////            currentPath.addPath(drawingSteps[currentStepIndex.value].path)
////            currentStepIndex.value++
////        }
////    }
////
////    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
////        if (page == 0 && currentStepIndex.value < drawingSteps.size) {
////            val step = drawingSteps[currentStepIndex.value.coerceAtMost(drawingSteps.size - 1)]
////            drawPath(
////                path = currentPath,
////                color = step.color,
////                style = Stroke(
////                    width = step.thickness,
////                    cap = StrokeCap.Round
////                )
////            )
////        }
////    }
////}
