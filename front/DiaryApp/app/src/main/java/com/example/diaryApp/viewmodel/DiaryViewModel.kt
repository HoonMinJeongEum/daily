package com.example.diaryApp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.diaryApp.domain.dto.request.diary.DiaryCommentRequestDto
import com.example.diaryApp.domain.dto.response.diary.Diary
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import com.example.diaryApp.domain.repository.diary.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    application: Application,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

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

    val memberName = mutableStateOf<String>("")
    val memberId = mutableIntStateOf(0)


    fun updateYearMonth(selectedYear: Int, selectedMonth: Int) {
        _year.value = selectedYear
        _month.value = selectedMonth
        fetchDiaryList()
    }

    fun fetchDiaryList() {
        viewModelScope.launch {
            val list = diaryRepository.getDiaryList(memberId.intValue, year.value, month.value + 1)
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

    fun fetchComment(comment: String) {
        val diaryId = _diaryDetail.value?.id

        if (diaryId != null) {
            // 요청에 사용할 DTO 생성
            val diaryCommentRequestDto = DiaryCommentRequestDto(
                diaryId = diaryId,
                comment = comment
            )

            viewModelScope.launch {
                try {
                    // Repository를 통해 API 호출
                    val response = diaryRepository.fetchComment(diaryCommentRequestDto)
                    if (response.isSuccessful) {
                        Log.d("DiaryViewModel", "Comment posted successfully: ${response.body()}")
                        // 댓글이 성공적으로 등록된 후 필요한 추가 동작이 있다면 여기에 작성
                    } else {
                        Log.e("DiaryViewModel", "Failed to post comment: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("DiaryViewModel", "Error posting comment", e)
                }
            }
        } else {
            Log.e("DiaryViewModel", "Cannot post comment: diaryId is null")
        }
    }

}

