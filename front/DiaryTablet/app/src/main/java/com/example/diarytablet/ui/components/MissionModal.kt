import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.MissionItem

@Composable
fun missionModal(
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    missionItems: List<MissionItem>
) {
    if (isModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3000000))
                .clickable { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .width(500.dp)
                    .padding(40.dp)
                    .background(Color(0xFFDEE5D4), shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)) // 모달 배경색 및 모서리
                    .padding(24.dp)
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .clickable { onDismiss() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mission_close),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)

                    )
                }
                Column(
                    modifier = Modifier.padding(vertical = 40.dp)
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {

                    // "오늘의 미션" 제목
                    Text(
                        text = "오늘의 미션",
                        color = Color(0xFF49566F),
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    )

//                    // 각 미션 항목을 반복하여 표시
                    missionItems.forEach { missionItem ->
                        MissionItemRow(missionItem = missionItem)
                    }
                }
            }
        }
    }
}

@Composable
fun MissionItemRow(missionItem: MissionItem) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(0.8f),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 미션 이름
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = missionItem.text,
                fontSize = 20.sp,
                color = Color(0xFF49566F)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,

            ) {


            val imageRes =
                if (missionItem.isSuccess) R.drawable.mission_clear_modal else R.drawable.mission_noclear_modal
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            if (missionItem.isSuccess) {
                Text(
                    text = "+10",
                    color = Color(0xFF339900),
                    fontSize = 24.sp
                )
            } else {
                Text(
                    text = "+10",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }
        }
    }



@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun previewModal(){
    missionModal(
        isModalVisible =  true,
        onDismiss = {},
        missionItems = listOf(
            MissionItem(text = "그림일기", isSuccess = true),
            MissionItem(text = "단어학습", isSuccess = false),
            MissionItem(text = "그림퀴즈", isSuccess = false)
        )
    )
}