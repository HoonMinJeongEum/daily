package com.example.diaryApp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaryApp.viewmodel.WordViewModel

@Composable
fun WordListItemByMember(wordViewModel: WordViewModel) {

    val wordList by wordViewModel.wordList.observeAsState()

    LaunchedEffect(Unit) {
        wordViewModel.getWordList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (wordList?.isSuccessful == true && wordList!!.body()?.isNotEmpty() == true) {
            items(wordList!!.body()!!) { word ->
                Log.d("wordScreen", "${word}")
            }
        } else {
                // 성공하지 않거나, body가 null인 경우
                Log.d("wordScreen", "No words or error in response")
            }
    }
}

@Composable
fun WordListItemByDate() {

}

@Composable
fun WordItem() {

}