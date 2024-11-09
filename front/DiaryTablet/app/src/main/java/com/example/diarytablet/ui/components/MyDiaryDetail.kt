package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.response.diary.CommentDto
import com.example.diarytablet.domain.dto.response.diary.Diary
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.viewmodel.LogViewModel

@Composable
fun MyDiaryDetail(
    diaryId: Int,
    onBackClick: () -> Unit, // 뒤로 가기 등의 동작을 위한 클릭 핸들러
    viewModel: LogViewModel
) {
    LaunchedEffect(diaryId) {
        viewModel.fetchDiaryById(diaryId)
    }

    val diaryDetail = viewModel.diaryDetail.observeAsState()
    var isDialogOpen by remember { mutableStateOf(false) }
    val diary = diaryDetail.value

    if (isDialogOpen && diary != null) {
        Dialog(onDismissRequest = { isDialogOpen = false }) {
            MyDiaryComment(
                comments = diary.comments,
                onDismissRequest = { isDialogOpen = false }
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // 두 버튼을 수직 가운데 정렬
        ) {
            DynamicColorButton(
                onClick = onBackClick,
                text = "< 날짜 선택",
                isSelected = true,
                textStyle = MyTypography.bodySmall
            )

            Spacer(modifier = Modifier.width(20.dp))

            Image(
                painter = painterResource(id = R.drawable.chat),
                contentDescription = "Open Dialog",
                modifier = Modifier
                    .clickable {
                        isDialogOpen = true
                    }
                    .size(60.dp)
            )
        }

        diary?.let {
            MyDiaryContent(diary = it)
        } ?: CircularProgressIndicator()

    }
}

@Composable
fun MyDiaryContent(
    diary: Diary
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 이미지 너비를 최대 너비로 설정
            ) {
                Image(
                    painter = rememberAsyncImagePainter(diary.drawImg),
                    contentDescription = "Draw Image",
                    modifier = Modifier.fillMaxWidth() // 이미지의 너비를 최대 너비로 설정
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 이미지 너비를 최대 너비로 설정
            ) {
                Image(
                    painter = rememberAsyncImagePainter(diary.writeImg),
                    contentDescription = "Write Image",
                    modifier = Modifier.fillMaxWidth() // 이미지의 너비를 최대 너비로 설정
                )
            }
        }
    }
}

@Composable
fun MyDiaryComment(
    comments: List<CommentDto>,
    onDismissRequest: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(42.dp) // 아이콘 크기 조정
                    )
                }
            }

            val titleText = if (comments.isEmpty()) {
                "부모님이 아직 댓글을 달지 않았어요."
            } else {
                "부모님이 댓글을 남겼어요!"
            }

            Text(
                text = titleText,
                style = MyTypography.bodyMedium,
                color = PastelNavy,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            if (comments.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(comments) { comment ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = comment.createdAt,
                                style = MyTypography.bodySmall,
                                color = Color.Gray,
                            )
                            Text(
                                text = comment.comment,
                                style = MyTypography.bodySmall,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
