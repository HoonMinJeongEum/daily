package com.example.diarytablet.ui.components

import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import kotlinx.coroutines.launch

@Composable
fun WordTap(
    modifier: Modifier = Modifier,
    wordList: List<WordResponseDto>,
    onValidate: (WordRequestDto) -> Unit,
    onFinish: (List<WordRequestDto>) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    Column(modifier = modifier.fillMaxSize()) {

    ProgressBar(
        currentIndex = currentIndex,
        total = wordList.size
    )

    LazyRow (
        state = listState,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(130.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(start = 250.dp, end = 250.dp)

    ){
        itemsIndexed(wordList) { index, word ->
            Log.d("wordtab","${index}")
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
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(word.imageUrl)
//                    .build(),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
            if (listState.firstVisibleItemIndex == index-1) {
                Image(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clickable{
                            coroutineScope.launch {
                                listState.animateScrollToItem(++currentIndex)
                            }
                        }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.big_jogae),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.5f) // 박스의 너비의 절반을 차지
                    .aspectRatio(1f)

            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = word.word, // 인자로 받은 텍스트
                    modifier = Modifier.wrapContentWidth(),
                )
                BasicButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index + 1) // 다음 아이템으로 이동
                        }
//                        onValidate()
                    },

                    modifier = Modifier.padding(top = 16.dp),
                    text = "제출",
                    imageResId = 11
                )
            }
            if (listState.firstVisibleItemIndex == index+1) {
                Image(
                    painter = painterResource(id = R.drawable.left_arrow),
                    contentDescription = "다음",
                    modifier = Modifier
                        .size(70.dp)
                        .clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(--currentIndex)


                            }
                        }
                )
            }
        }}}
    }
}}

@Composable
fun ProgressBar(currentIndex: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until total) {
            val color = if (index < currentIndex) Color.Blue else if (index == currentIndex) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .padding(horizontal = 4.dp)
                    .background(color, shape = CircleShape)
            )
        }
    }
}