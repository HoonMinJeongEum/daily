package com.example.diaryApp.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import com.example.diaryApp.domain.dto.response.word.Word
import com.example.diaryApp.domain.repository.word.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    application: Application,
    private val wordRepository: WordRepository
) : ViewModel() {

    val memberName = mutableStateOf<String>("")
    val memberId = mutableIntStateOf(0)

    private val _wordList = MutableLiveData<Response<List<Word>>?>()
    val wordList: LiveData<Response<List<Word>>?> get() = _wordList

    // "날짜순"으로 정렬된 리스트를 derivedStateOf로 제공 (최신순으로 정렬)
    val dateSortedWordList = MediatorLiveData<List<Word>>().apply {
        addSource(_wordList) { response ->
            value = response?.body()?.sortedByDescending { it.createdAt } ?: emptyList()
        }
    }

    fun clearAll() {
        memberName.value = ""
        memberId.value = 0
        _wordList.value = null
    }

    fun getWordList() {
        viewModelScope.launch {
            val list = wordRepository.getWordList(memberId.intValue)
            _wordList.postValue(list)
        }
    }
}