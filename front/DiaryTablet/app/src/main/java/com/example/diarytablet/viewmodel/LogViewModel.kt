package com.example.diarytablet.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.diary.Diary
import com.example.diarytablet.domain.dto.response.diary.DiaryForList
import com.example.diarytablet.domain.repository.DiaryRepository
import com.example.diarytablet.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    application: Application,
    private val diaryRepository: DiaryRepository,
    private val wordRepository : WordRepository
) : ViewModel(){

    private val _wordList = MutableLiveData<Response<List<WordLearnedResponseDto>>>()
    val wordList: LiveData<Response<List<WordLearnedResponseDto>>> get() = _wordList

    val dateSortedWordList = MediatorLiveData<List<WordLearnedResponseDto>>().apply {
        addSource(_wordList) { response ->
            value = response.body()?.sortedByDescending { it.createdAt } ?: emptyList()
        }
    }

    private val _year = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val year: StateFlow<Int> get() = _year

    private val _month = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val month: StateFlow<Int> get() = _month

    private val _diaryId = MutableLiveData<Int>()
    val diaryId: LiveData<Int> get() = _diaryId

    private val _diaryList = MutableLiveData<Response<List<DiaryForList>>>()
    val diaryList: LiveData<Response<List<DiaryForList>>> get() = _diaryList

    private val _diaryDetail = MutableLiveData<Diary?>()
    val diaryDetail: LiveData<Diary?> get() = _diaryDetail


    fun getWordList() {
        viewModelScope.launch {
            val list = wordRepository.getLearnedWordList()
            _wordList.postValue(list)
        }
    }



    fun updateYearMonth(selectedYear: Int, selectedMonth: Int) {
        _year.value = selectedYear
        _month.value = selectedMonth
        fetchDiaryList()
    }

    fun fetchDiaryList() {
        viewModelScope.launch {
            val list = diaryRepository.getDiaryList(year.value, month.value + 1)
            _diaryList.postValue(list)
        }
    }

    fun fetchDiaryById(diaryId: Int) {
        _diaryId.value = diaryId
        viewModelScope.launch {
            val response = diaryRepository.getDiaryById(diaryId)
            _diaryDetail.value = response
        }
    }

    fun clearDiaryDetail() {
        _diaryDetail.value = null
    }

}