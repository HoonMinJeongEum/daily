import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BlockButton
import com.example.diarytablet.ui.components.ButtonType

@Composable
fun MainModal(
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    onWordLearningClick: () -> Unit,
    onDrawingDiaryClick: () -> Unit,
    onDrawingQuizClick: () -> Unit
) {
    if (isModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3000000)) // 검정색 배경, opacity 70%
        ) {

            Image(
                    modifier = Modifier.align(Alignment.TopStart)
                        .clickable(onClick = onDismiss)
                        .padding(start = 50.dp , top = 50.dp)
                        .size(75.dp),
                        painter = painterResource(id = R.drawable.back_button), // 취소 버튼 아이콘
                        contentDescription = "Close",
                )


            // 모달 컨텐츠
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(50.dp), // 버튼 간 간격 조정
                verticalAlignment = Alignment.CenterVertically // 수직 정렬
            ) {

                BlockButton(onClick = onWordLearningClick, buttonType = ButtonType.WORD_LEARNING)
                BlockButton(onClick = onDrawingDiaryClick, buttonType = ButtonType.DRAWING_DIARY)
                BlockButton(onClick = onDrawingQuizClick, buttonType = ButtonType.DRAWING_QUIZ)
            }
        }
    }
}

