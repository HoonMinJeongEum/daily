package com.example.diarytablet.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.repository.MainScreenRepository
import com.example.diarytablet.ui.components.MissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.datastore.UserStore.Companion.KEY_PROFILE_IMAGE
import com.example.diarytablet.domain.dto.request.CompleteMissionItemRequestDto
import kotlinx.coroutines.flow.Flow


@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mainScreenRepository: MainScreenRepository,
    private val userStore: UserStore
) : ViewModel() {

    private val _missions = mutableStateListOf<MissionItem>()
    val missions: List<MissionItem> get() = _missions
    val origin: String = savedStateHandle.get<String>("origin") ?: "Unknown"
//    val isFinished: Boolean = savedStateHandle.get<Boolean>("isFinished") ?: false

    private val _isFinished = mutableStateOf(savedStateHandle.get<Boolean>("isFinished") ?: false)
    val isFinished: State<Boolean> get() = _isFinished
    init {
        loadStatus()
    }

    fun loadStatus() {
        viewModelScope.launch {
            try {
                Log.d("main","${userStore.getValue(UserStore.KEY_USER_NAME)}")
                val response = mainScreenRepository.getMainScreenStatus()

                // 서버 응답을 기반으로 미션 상태 설정
                val loadedMissions = listOf(
                    MissionItem("단어 학습", response.wordStatus),
                    MissionItem("그림 일기", response.diaryStatus),
                    MissionItem("그림 퀴즈", response.quizStatus)
                )
                _missions.clear()
                _missions.addAll(loadedMissions)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setFinished(value: Boolean) {
        _isFinished.value = value
    }

    fun completeMissionItem(mission: MissionItem, navbarViewModel: NavBarViewModel) {
        viewModelScope.launch {
            try {
                val questType = when (mission.text) {
                    "그림 일기" -> "DIARY"
                    "그림 퀴즈" -> "QUIZ"
                    "단어 학습" -> "WORD"
                    else -> null
                }

                questType?.let {
                    val requestDto = CompleteMissionItemRequestDto(questType)
                    val response = mainScreenRepository.completeMissionItem(requestDto)

                    if (response.isSuccessful) {
                        loadStatus() // MainViewModel 상태 갱신
                        navbarViewModel.loadStatus() // NavbarViewModel 상태 갱신
                    } else {
                        Log.e("MainViewModel", "Error updating mission status: ${response.message()}")
                    }
                } ?: Log.e("MainViewModel", "Invalid mission type: ${mission.text}")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception updating mission status", e)
            }
        }
    }
}
