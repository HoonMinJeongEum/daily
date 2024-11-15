package com.example.diarytablet.viewmodel

import androidx.lifecycle.ViewModel
import com.samsung.android.sdk.penremote.SpenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emitAll
import javax.inject.Inject

@HiltViewModel
class SpenEventViewModel @Inject constructor() : ViewModel() {


    private val _spenEventFlow = MutableSharedFlow<SpenEvent>()
    val spenEventFlow: SharedFlow<SpenEvent> = _spenEventFlow.asSharedFlow()

    // S Pen 이벤트를 발생시킴
    suspend fun emitSpenEvent(event: SpenEvent) {
        _spenEventFlow.emit(event)
    }
}
