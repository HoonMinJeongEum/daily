package com.example.diarytablet.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.repository.QuizRepository
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
import com.example.diarytablet.domain.dto.request.quest.UpdateQuestRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SessionRequestDto
import com.example.diarytablet.domain.dto.response.quiz.SessionResponseDto
import com.example.diarytablet.domain.repository.QuestRepository
import com.example.diarytablet.utils.Const
import com.example.diarytablet.utils.openvidu.Session
import org.json.JSONArray
import org.webrtc.MediaStream

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val questRepository: QuestRepository,
    private val userStore: UserStore
) : ViewModel() {

    lateinit var socket: Socket
    val recommendWords = mutableStateOf<List<String>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    private val _path = mutableStateOf(Path())
    val path: State<Path> = _path

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token
    private val _sessionId = MutableLiveData<SessionResponseDto?>()
    val sessionId: LiveData<SessionResponseDto?> get() = _sessionId
    lateinit var session: Session
    private val _remoteMediaStream = MutableLiveData<MediaStream?>()
    val remoteMediaStream: LiveData<MediaStream?> get() = _remoteMediaStream
    private val _leaveSessionTriggered = MutableLiveData<Boolean>()
    val leaveSessionTriggered: LiveData<Boolean> get() = _leaveSessionTriggered

    private val _isCorrectAnswer = MutableLiveData<Boolean?>()
    val isCorrectAnswer: LiveData<Boolean?> get() = _isCorrectAnswer

    private val _canvasWidth = MutableLiveData<Int>()
    val canvasWidth: LiveData<Int> get() = _canvasWidth

    private val _canvasHeight = MutableLiveData<Int>()
    val canvasHeight: LiveData<Int> get() = _canvasHeight

    private val _userDisconnectedEvent = MutableLiveData<Boolean?>()
    val userDisconnectedEvent: LiveData<Boolean?> get() = _userDisconnectedEvent
    private val _parentJoinedEvent = MutableLiveData<Boolean>()
    val parentJoinedEvent: LiveData<Boolean> get() = _parentJoinedEvent

    fun setCanvasSize(width: Int, height: Int) {
        _canvasWidth.value = width
        _canvasHeight.value = height
    }

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        recommendWord()
        initializeSession()
    }

    // 세션 초기화 함수
    private fun initializeSession() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                var sessionId : String = ""
                userStore.getValue(UserStore.KEY_USER_NAME).collect { name ->
                    sessionId = name
                }
                Log.d("ViewModel", "Session ID: $sessionId")
                val sessionRequestDto = SessionRequestDto(customSessionId = sessionId)
                val response = quizRepository.initializeSession(sessionRequestDto)
                _sessionId.value = response.body()
                _sessionId.value?.let { sessionId ->
                    createSocket(sessionId.customSessionId)
                    createConnection(sessionId.customSessionId)
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 커넥션 생성 함수
    private fun createConnection(sessionId: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                Log.e("QuizViewModel", "Session ID: ${sessionId}")
                val response = quizRepository.createConnection(sessionId)
                _token.value = response.body()?.token
                Log.d("QuizViewModel", "Token 얻음: ${_token.value}")
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 소켓 연결
    private fun createSocket(sessionId: String) {
        socket = IO.socket(Const.WS_API + Const.WS_PORT)
        socket.connect()
        socket.emit("join", sessionId)
        Log.e("QuizViewModel", "roomId : ${sessionId}")

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e("QuizViewModel", "소켓 연결 오류 발생: ${args[0]}")
        }

        viewModelScope.launch {
            userStore.getValue(UserStore.KEY_ACCESS_TOKEN).collect { jwtToken ->
                Log.d("QuizViewModel", "JWT 토큰 전송: $jwtToken")
                socket.emit("authenticate", jwtToken) // 서버에 JWT 토큰 전송
            }
        }

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
        }

        socket.on("clear") {
            _path.value = Path()
        }

        socket.on("userDisconnected") {
            _userDisconnectedEvent.postValue(true)
            Log.d("QuizViewModel", "disconnect")
        }

        socket.on("joinParents") {
            _parentJoinedEvent.postValue(true)
            Log.d("QuizViewModel", "joinParents")
        }
    }

    // 추천 단어 가져오기 함수
    private fun recommendWord() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = quizRepository.recommendWord()
                recommendWords.value = response.body()?.map { it.word } ?: emptyList()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 퀘스트 완료
    fun updateQuest() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                questRepository.updateQuest(UpdateQuestRequestDto("QUIZ"))

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
    
    // 드로잉 시작, 진행, 종료에 따라 소켓 메시지 전송
    fun sendDrawAction(action: String, x: Float, y: Float) {
        val message = """{"draw":"$action,${x/ (canvasWidth.value?.toFloat() ?: 1f)},${y/(canvasHeight.value?.toFloat() ?: 1f)}"}"""
        socket.emit("draw", message)

        updatePath(action, x, y)
    }

    // 단어 설정
    fun sendSetWordAction(word: String) {
        val message = """{"setWord":"$word"}"""
        socket.emit("setWord", message)
        Log.d("QuizViewModel", "SetWord 전송: $word")
    }

    // 퀴즈 시작
    fun sendQuizStart() {
        socket.emit("quizStart")
        Log.d("QuizViewModel", "퀴즈 시작을 알림")
    }

    // 상태 초기화
    fun resetIsCorrectAnswer() {
        _isCorrectAnswer.value = null
    }

    // 퀴즈 종료
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