package com.example.diarytablet.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordLearningViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _wordList = mutableStateOf<List<WordResponseDto>>(emptyList())
    val wordList: State<List<WordResponseDto>> get() = _wordList

    private val _learnedWordList = mutableStateOf<List<WordRequestDto>>(emptyList())
    val learnedWordList: State<List<WordRequestDto>> get() = _learnedWordList

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        getWordList() // 초기 화면 로딩 시 프로필 리스트 가져오기
    }

    // 단어 목록을 가져오는 함수
    fun getWordList() {
        isLoading.value = true
        viewModelScope.launch {
            try {

                val mockWords = listOf(
                    WordResponseDto(id = 1, word = "하이", imageUrl = "https://example.com/hello.png"),
                    WordResponseDto(id = 2, word = "세상", imageUrl = "https://example.com/world.png"),
                    WordResponseDto(id = 3, word = "쌉틀린", imageUrl = "https://example.com/kotlin.png"),
                    WordResponseDto(id = 4, word = "아니", imageUrl = "https://example.com/kotlin.png"),
                    WordResponseDto(id = 5, word = "왜이러는데", imageUrl = "https://example.com/kotlin.png"),
                    WordResponseDto(id = 6, word = "씨발", imageUrl = "https://example.com/kotlin.png")
                )
                // Mock 데이터를 _wordList에 설정
                _wordList.value = mockWords
//                val words = wordRepository.getWordList()
//                _wordList.value = words
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }



    // 단어 검증 함수
    fun checkWordValidate(wordRequest: WordRequestDto) {
        isLoading.value = true
        viewModelScope.launch {
            try {

                val validationResponse = wordRepository.checkWordValidate(wordRequest)
                if (validationResponse.isSuccessful) { // 검증이 성공했을 때
                    _learnedWordList.value = _learnedWordList.value + wordRequest // 단어 추가
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun finishWordLearning(words: List<WordRequestDto>) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val finishResponse = wordRepository.finishWordLearning(words)
                // 학습 완료 결과 처리 (예: UI 업데이트)
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
