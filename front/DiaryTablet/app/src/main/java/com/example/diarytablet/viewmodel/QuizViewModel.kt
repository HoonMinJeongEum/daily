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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.diarytablet.domain.dto.request.quiz.SessionRequestDto
import com.example.diarytablet.domain.dto.response.quiz.SessionResponseDto
import com.example.diarytablet.domain.repository.QuestRepository
import com.example.diarytablet.utils.Const
import com.example.diarytablet.utils.openvidu.Session
import org.webrtc.MediaStream

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val questRepository: QuestRepository,
    private val userStore: UserStore
) : ViewModel() {
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // 비디오
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token
    private val _sessionId = MutableLiveData<SessionResponseDto?>()
    val sessionId: LiveData<SessionResponseDto?> get() = _sessionId
    lateinit var session: Session
    private val _remoteMediaStream = MutableLiveData<MediaStream?>()
    val remoteMediaStream: LiveData<MediaStream?> get() = _remoteMediaStream
    private val _leaveSessionTriggered = MutableLiveData<Boolean>()
    val leaveSessionTriggered: LiveData<Boolean> get() = _leaveSessionTriggered
    private val _isMicMuted = MutableLiveData(true) // 마이크 상태
    val isMicMuted: LiveData<Boolean> get() = _isMicMuted
    private val _isRemoteAudioMuted = MutableLiveData(true) // 스피커 상태
    val isRemoteAudioMuted: LiveData<Boolean> get() = _isRemoteAudioMuted

    // 단어
    lateinit var socket: Socket
    val recommendWords = mutableStateOf<List<String>>(emptyList())
    private val _isCorrectAnswer = MutableLiveData<Boolean?>()
    val isCorrectAnswer: LiveData<Boolean?> get() = _isCorrectAnswer
    private val _userDisconnectedEvent = MutableLiveData<Boolean?>()
    val userDisconnectedEvent: LiveData<Boolean?> get() = _userDisconnectedEvent
    private val _parentJoinedEvent = MutableLiveData<Boolean>()
    val parentJoinedEvent: LiveData<Boolean> get() = _parentJoinedEvent
    private val _parentWord = MutableLiveData<String>()
    val parentWord: LiveData<String> get() = _parentWord

    // 그림
    private val _canvasWidth = MutableLiveData<Int>()
    val canvasWidth: LiveData<Int> get() = _canvasWidth
    private val _canvasHeight = MutableLiveData<Int>()
    val canvasHeight: LiveData<Int> get() = _canvasHeight
    private val _paths = NonNullLiveData<MutableList<Pair<Path, PathStyle>>>(
        mutableListOf()
    )
    private val _pathStyle = NonNullLiveData(
        PathStyle()
    )
    private val removedPaths = mutableListOf<Pair<Path, PathStyle>>()
    val paths: LiveData<MutableList<Pair<Path, PathStyle>>>
        get() = _paths
    val pathStyle: LiveData<PathStyle>
        get() = _pathStyle
    
    fun setCanvasSize(width: Int, height: Int) {
        _canvasWidth.value = width
        _canvasHeight.value = height
    }

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        isLoading.value = true
        recommendWord()
        initializeSession()
    }

    // 세션 초기화 함수
    private fun initializeSession() {
        viewModelScope.launch {
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
                Log.d("QuizViewModel", "loading end")
                isLoading.value = false
            }
        }
    }

    // 커넥션 생성 함수
    private fun createConnection(sessionId: String) {
        viewModelScope.launch {
            errorMessage.value = null
            try {
                Log.e("QuizViewModel", "Session ID: ${sessionId}")
                val response = quizRepository.createConnection(sessionId)
                _token.value = response.body()?.token
                Log.d("QuizViewModel", "Token 얻음: ${_token.value}")
            } catch (e: Exception) {
                errorMessage.value = e.message
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
                userStore.getValue(UserStore.KEY_REFRESH_TOKEN).collect { refreshToken ->
                    Log.d("QuizViewModel", "JWT 토큰 및 리프레시 토큰 전송: JWT=$jwtToken, RefreshToken=$refreshToken")

                    val authData = JSONObject().apply {
                        put("jwtToken", jwtToken)
                        put("refreshToken", refreshToken)
                    }

                    socket.emit("authenticate", authData.toString()) // 서버에 JWT와 리프레시 토큰 전송
                }
            }
        }

        socket.on("checkWord") { args ->
            val json = args[0] as JSONObject
            val isCorrect = json.getBoolean("isCorrect") // JSON 객체에서 Boolean 추출
            val word = json.getString("processedWord")
            _isCorrectAnswer.postValue(isCorrect)
            _parentWord.postValue(word)
        }

        socket.on("clear") {
            _paths.postValue(mutableListOf())
            removedPaths.clear()
            _isUndoAvailable.postValue(false)
            _isRedoAvailable.postValue(false)
        }

        socket.on("userDisconnected") {
            _userDisconnectedEvent.postValue(true)
            Log.d("QuizViewModel", "disconnect")
        }

        socket.on("joinParents") {
            _parentJoinedEvent.postValue(true)
            Log.d("QuizViewModel", "joinParents")
            resetPath()
            updateWidth(_pathStyle.value.width)
            updateColor(_pathStyle.value.color)
            updateAlpha(_pathStyle.value.alpha)
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
            }
        }
    }

    fun sendAspectRatio(aspectRatio: Float) {
        socket.emit("aspectRatio", aspectRatio)
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
        if (stream.audioTracks.isNotEmpty()) {
            stream.audioTracks[0].setEnabled(true) // 오디오 트랙 활성화
        }
        _remoteMediaStream.postValue(stream)
    }

    // 마이크 제어
    fun toggleMicMute() {
        _isMicMuted.value = _isMicMuted.value?.not() ?: false
        session.getLocalParticipant().audioTrack?.setEnabled(_isMicMuted.value!!)
    }

    // 스피커 제어
    fun toggleRemoteAudioMute() {
        _isRemoteAudioMuted.value = _isRemoteAudioMuted.value?.not() ?: false
        session.muteAllRemoteParticipants(_isRemoteAudioMuted.value!!)
    }

    // 그림 초기화
    fun resetPath() {
        socket.emit("clear" )
    }

    // 드로잉 시작, 진행, 종료에 따라 소켓 메시지 전송
    fun sendDrawAction(action: String, x: Float, y: Float) {
        val message = """{"draw":"$action,${x/ (canvasWidth.value?.toFloat() ?: 1f)},${y/(canvasHeight.value?.toFloat() ?: 1f)}"}"""
        socket.emit("draw", message)
    }

    fun updateWidth(width: Float) {
        val style = _pathStyle.value
        style.width = width

        _pathStyle.postValue(style)

        val widthData = JSONObject().apply {
            put("width", width / canvasWidth.value!!)
        }
        socket.emit("width", widthData.toString())
    }

    fun updateColor(color: Color) {
        val style = _pathStyle.value
        style.color = color

        _pathStyle.postValue(style)

        val colorData = JSONObject().apply {
            put("color", color.toArgb())
        }
        socket.emit("color", colorData.toString())
    }

    fun updateAlpha(alpha: Float) {
        val style = _pathStyle.value
        style.alpha = alpha

        _pathStyle.postValue(style)

        val alphaData = JSONObject().apply {
            put("alpha", alpha)
        }
        socket.emit("alpha", alphaData.toString())
    }
    private val _isUndoAvailable = MutableLiveData(false)
    val isUndoAvailable: LiveData<Boolean> get() = _isUndoAvailable

    private val _isRedoAvailable = MutableLiveData(false)
    val isRedoAvailable: LiveData<Boolean> get() = _isRedoAvailable

    fun addPath(pair: Pair<Path, PathStyle>) {
        val list = _paths.value
        list.add(pair)
        removedPaths.clear()
        _paths.postValue(list)
        _isUndoAvailable.postValue(list.isNotEmpty())
        _isRedoAvailable.postValue(removedPaths.isNotEmpty())
        socket.emit("addPath")
    }

    fun undoPath() {
        val pathList = _paths.value
        if (pathList.isEmpty())
            return
        val last = pathList.last()
        val size = pathList.size
        removedPaths.add(last)
        _paths.postValue(pathList.subList(0, size-1))
        _isUndoAvailable.postValue((pathList.size - 1) > 0)
        _isRedoAvailable.postValue(removedPaths.isNotEmpty())
        socket.emit("undoPath")
    }

    fun redoPath() {
        if (removedPaths.isEmpty())
            return
        _paths.postValue((_paths.value + removedPaths.removeLast()) as MutableList<Pair<Path, PathStyle>>)
        _isUndoAvailable.postValue(true)
        _isRedoAvailable.postValue(removedPaths.isNotEmpty())
        socket.emit("redoPath")
    }

}

class NonNullLiveData<T: Any>(defaultValue: T) : MutableLiveData<T>(defaultValue) {

    init {
        value = defaultValue
    }

    override fun getValue() = super.getValue()!!
}

data class PathStyle(
    var color: Color = Color.Black,
    var alpha: Float = 1.0f,
    var width: Float = 10.0f
)