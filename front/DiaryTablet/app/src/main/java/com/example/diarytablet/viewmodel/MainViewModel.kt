package com.example.diarytablet.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // 필요한 종속성을 주입합니다. 예를 들어, Repository나 UseCase.
) : ViewModel() {

    // UI 상태를 정의합니다. 예: 미션 목록, 사용자 데이터 등.
    private val _missions = mutableStateListOf<MissionItem>()
    val missions: List<MissionItem> get() = _missions

    // 초기화 시 데이터 로드
    init {
        loadMissions()
    }

    // 미션을 로드하는 함수
    private fun loadMissions() {
        viewModelScope.launch {
            // 데이터 로드 로직 (예: Repository에서 호출)
            val loadedMissions = listOf(
                MissionItem("미션 1", true),
                MissionItem("미션 2", false),
                MissionItem("미션 3", true)
            )
            _missions.addAll(loadedMissions)
        }
    }

    // 미션 성공 상태를 업데이트하는 함수
    fun updateMissionStatus(mission: MissionItem, isSuccess: Boolean) {
        val index = _missions.indexOf(mission)
        if (index != -1) {
            _missions[index] = mission.copy(isSuccess = isSuccess)
        }
    }
}
