package com.example.diarytablet.viewmodel

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
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.datastore.UserStore.Companion.KEY_PROFILE_IMAGE


@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainScreenRepository: MainScreenRepository,
    private val userStore: UserStore
) : ViewModel() {

    private val _missions = mutableStateListOf<MissionItem>()
    val missions: List<MissionItem> get() = _missions
    private val _shellCount = mutableIntStateOf(0)
    val shellCount: State<Int> get() = _shellCount
    private val _profileImageUrl = mutableStateOf("")
    val profileImageUrl: State<String> get() = _profileImageUrl

    init {
        loadStatus()
    }

    // 미션을 로드하는 함수
    private fun loadStatus() {
        viewModelScope.launch {
            try {
                val response = mainScreenRepository.getMainScreenStatus()

                // shellCount 업데이트
                _shellCount.value = response.shellCount

                // 서버 응답을 기반으로 미션 상태 설정
                val loadedMissions = listOf(
                    MissionItem("그림일기", response.diaryStatus),
                    MissionItem("그림퀴즈", response.quizStatus),
                    MissionItem("단어학습", response.wordStatus)
                )
                _missions.clear()
                _missions.addAll(loadedMissions)

                _profileImageUrl.value = response.image

                userStore.setValue(KEY_PROFILE_IMAGE, response.image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMissionStatus(mission: MissionItem, isSuccess: Boolean) {
        val index = _missions.indexOf(mission)
        if (index != -1) {
            _missions[index] = mission.copy(isSuccess = isSuccess)
        }
    }
}
