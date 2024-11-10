package com.example.diaryApp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Socket
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.json.JSONObject
import androidx.compose.ui.graphics.Path
import io.socket.client.IO
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.dto.request.quiz.CheckSessionRequestDto
import com.example.diaryApp.domain.repository.quiz.QuizRepository
import com.example.diaryApp.utils.Const
import com.example.diaryApp.utils.openvidu.Session
import org.json.JSONArray
import org.webrtc.MediaStream

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userStore: UserStore
) : ViewModel() {
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // 오픈 비두
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token
    private val _sessionId = MutableLiveData<String?>()
    val sessionId: LiveData<String?> get() = _sessionId
    lateinit var session: Session
    private val _remoteMediaStream = MutableLiveData<MediaStream?>()
    val remoteMediaStream: LiveData<MediaStream?> get() = _remoteMediaStream
    private val _leaveSessionTriggered = MutableLiveData<Boolean>()
    val leaveSessionTriggered: LiveData<Boolean> get() = _leaveSessionTriggered

    // Node.js
    lateinit var socket: Socket
    private val _path = mutableStateOf(Path())
    val path: State<Path> = _path
    private val _canvasWidth = MutableLiveData<Int>()
    val canvasWidth: LiveData<Int> get() = _canvasWidth
    private val _canvasHeight = MutableLiveData<Int>()
    val canvasHeight: LiveData<Int> get() = _canvasHeight
    private val _isCorrectAnswer = MutableLiveData<Boolean?>()
    val isCorrectAnswer: LiveData<Boolean?> get() = _isCorrectAnswer
    private val _userDisconnectedEvent = MutableLiveData<Boolean?>()
    val userDisconnectedEvent: LiveData<Boolean?> get() = _userDisconnectedEvent
    private val _isWordSelected = MutableLiveData(false)
    val isWordSelected: LiveData<Boolean> get() = _isWordSelected

    fun setCanvasSize(width: Int, height: Int) {
        _canvasWidth.value = width
        _canvasHeight.value = height
    }

    fun loadQuiz(sessionId : String) {
        _sessionId.value = sessionId
        createConnection(sessionId)
    }

    private fun createConnection(sessionId: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                Log.e("QuizViewModel", "Session ID: $sessionId")
                val response = quizRepository.createConnection(sessionId)
                createSocket(sessionId)
                _token.value = response.body()?.token
                Log.d("QuizViewModel", "Token 얻음: ${_token.value}")
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun createSocket(sessionId: String) {
        viewModelScope.launch {
            socket = IO.socket(Const.WS_API + Const.WS_PORT)
            socket.connect()

            Log.e("QuizViewModel", "roomId : ${sessionId}")
            socket.emit("joinParents", sessionId)

            socket.on("initDrawing") { args ->
                val drawingData = args[0] as JSONArray
                for (i in 0 until drawingData.length()) {
                    val draw = drawingData.getString(i)
                    val jsonMessage = JSONObject(draw)
                    val draws = jsonMessage.getString("draw").split(",")
                    val action = draws[0]
                    val x = draws[1].toFloat()
                    val y = draws[2].toFloat()
                    updatePath(action, x, y)
                }
            }

            // 서버로부터 "draw" 이벤트 수신
            socket.on("draw") { args ->
                val responseData = args[0] as String
                val jsonMessage = JSONObject(responseData)
                val draws = jsonMessage.getString("draw").split(",")
                val action = draws[0]
                val x = draws[1].toFloat() * (canvasWidth.value?.toFloat() ?: 1f)
                val y = draws[2].toFloat() * (canvasHeight.value?.toFloat() ?: 1f)

                updatePath(action, x, y)
            }

            socket.on("checkWord") { args ->
                val isCorrect = args[0] as Boolean
                _isCorrectAnswer.postValue(isCorrect)

                if (isCorrect) {
                    _isWordSelected.postValue(false)
                }
            }

            socket.on("clear") {
                _path.value = Path()
            }

            socket.on("setWord") {
                _isWordSelected.postValue(true)
            }

            socket.on("userDisconnected") {
                _userDisconnectedEvent.postValue(true)
                Log.d("QuizViewModel", "disconnect")
            }

        }
    }

    fun checkSession(childName: String, onShowQuizAlert: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                Log.e("QuizViewModel", "childName: $childName")
                val response = quizRepository.checkSession(CheckSessionRequestDto(childName))
                val sessionId = response.body()?.sessionId ?: ""  // sessionId가 없으면 빈 문자열 할당

                onShowQuizAlert(sessionId)
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 그림 업데이트
    private fun updatePath(action: String, x: Float, y: Float) {
        _path.value = Path().apply {
            addPath(_path.value) // 기존 Path 유지
            when (action) {
                "DOWN" -> moveTo(x, y)
                "MOVE" -> lineTo(x, y)
            }
        }
    }

    // 그림 초기화
    fun resetPath() {
        socket.emit("clear" )
    }

    // 단어 확인
    fun sendCheckWordAction(word: String) {
        val message = """{"checkWord":"$word"}"""

        socket.emit("checkWord", message)
        Log.d("QuizViewModel", "CheckWord 전송: $word")
    }

    // 상태 초기화
    fun resetIsCorrectAnswer() {
        _isCorrectAnswer.value = null
    }

    fun leaveSession() {
        socket.disconnect()
        if (::session.isInitialized) {
            session.leaveSession()
        } else {
            Log.e("QuizViewModel", "Session이 초기화되지 않았습니다.")
        }
        _leaveSessionTriggered.value = true
    }

    fun resetLeaveSessionTrigger() {
        _leaveSessionTriggered.value = false
    }

    fun setRemoteMediaStream(stream: MediaStream) {
        _remoteMediaStream.postValue(stream)
    }
}