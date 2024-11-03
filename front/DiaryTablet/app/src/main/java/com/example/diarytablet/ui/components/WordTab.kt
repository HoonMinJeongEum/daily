package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography

@Composable
fun WordTap(
    modifier: Modifier = Modifier,
    imageUrl: String? = null, // 이미지 URL을 인자로 받아옵니다.
    text: String = "기본 텍스트", // 기본 텍스트
    onButtonClick: () -> Unit // 버튼 클릭 시 호출될 함수
) {
    Box(
        modifier = modifier
            .width(780.dp)
            .height(510.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.word_container),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            // AsyncImage에서 data를 imageUrl로 설정
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(imageUrl) // imageUrl 인자를 사용
//                    .build(),
//                contentDescription = null, // 필요에 따라 이미지 설명 추가
//                modifier = Modifier
//                    .fillMaxSize()
//                    .
//                ,
//
////                contentScale = ContentScale.Crop
//            )
            Image(
                painter = painterResource(id = R.drawable.big_jogae),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = text, // 인자로 받은 텍스트
                    modifier = Modifier.wrapContentWidth(),
                    style = MyTypography.bodyLarge // 원하는 텍스트 스타일로 변경 가능
                )
                BasicButton(
                    onClick = onButtonClick, // 버튼 클릭 시 동작
                    modifier = Modifier.padding(top = 16.dp),
                    text = "제출"
                )
            }
        }
    }
}

@Preview
@Composable
fun previewWordTap() {
    WordTap (
        onButtonClick = {}
    )
}
