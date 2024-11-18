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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
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
    private val _isMicMuted = MutableLiveData(true) // 마이크 상태
    val isMicMuted: LiveData<Boolean> get() = _isMicMuted
    private val _isRemoteAudioMuted = MutableLiveData(true) // 스피커 상태
    val isRemoteAudioMuted: LiveData<Boolean> get() = _isRemoteAudioMuted

    // 단어
    lateinit var socket: Socket
    private val _isCorrectAnswer = MutableLiveData<Boolean?>()
    val isCorrectAnswer: LiveData<Boolean?> get() = _isCorrectAnswer
    private val _userDisconnectedEvent = MutableLiveData<Boolean?>()
    val userDisconnectedEvent: LiveData<Boolean?> get() = _userDisconnectedEvent
    private val _isWordSelected = MutableLiveData(false)
    val isWordSelected: LiveData<Boolean> get() = _isWordSelected

    // 그림
    private val _canvasWidth = MutableLiveData<Int>()
    val canvasWidth: LiveData<Int> get() = _canvasWidth
    private val _canvasHeight = MutableLiveData<Int>()
    val canvasHeight: LiveData<Int> get() = _canvasHeight
    private val _path = mutableStateOf(Path())
    val path: State<Path> = _path
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
    private val _aspectRatio = MutableLiveData(1.1f) // 기본 비율로 초기화
    val aspectRatio: LiveData<Float> = _aspectRatio
    private val _canvasWidthRation = MutableLiveData<Int>()  // 부모 앱의 canvasWidth 저장
    val canvasWidthRation: LiveData<Int> get() = _canvasWidthRation
    private val _parentWord = MutableLiveData<String>()
    val parentWord: LiveData<String> get() = _parentWord

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

            socket.on("aspectRatio") { args ->
                if (args.isNotEmpty()) {
                    val aspectRatio = (args[0] as Double).toFloat()
                    _aspectRatio.postValue(aspectRatio)// 부모 앱의 화면 비율 업데이트
                    Log.d("ParentViewModel", "캔버스 비율 수신: $aspectRatio")
                }
            }

            socket.on("checkWord") { args ->
                val json = args[0] as JSONObject
                val isCorrect = json.getBoolean("isCorrect") // JSON 객체에서 Boolean 추출
                val word = json.getString("processedWord")      // JSON 객체에서 String 추출
                _isCorrectAnswer.postValue(isCorrect)
                _parentWord.postValue(word)

                if (isCorrect) {
                    _isWordSelected.postValue(false)
                }
            }

            socket.on("clear") {
                _path.value = Path()
                _paths.postValue(mutableListOf())  // paths 초기화
                removedPaths.clear()
            }

            socket.on("setWord") {
                _isWordSelected.postValue(true)
            }

            socket.on("userDisconnected") {
                _userDisconnectedEvent.postValue(true)
                Log.d("QuizViewModel", "disconnect")
            }

            socket.on("draw") { args ->
                val responseData = args[0] as String
                val jsonMessage = JSONObject(responseData)
                val draws = jsonMessage.getString("draw").split(",")
                val action = draws[0]
                val x = draws[1].toFloat() * (canvasWidth.value?.toFloat() ?: 1f) // 상대방의 캔버스 크기 비율 적용
                val y = draws[2].toFloat() * (canvasHeight.value?.toFloat() ?: 1f)

                updatePath(action, x, y)
            }

            socket.on("color") { args ->
                val colorData = args[0] as String
                val json = JSONObject(colorData)
                val colorArgb = json.getInt("color")
                val style = _pathStyle.value
                style.color = Color(colorArgb)

                _pathStyle.postValue(style)
            }

            socket.on("width") { args ->
                val widthData = args[0] as String
                val json = JSONObject(widthData)
                val width = json.getString("width").toFloat()
                val style = _pathStyle.value
                style.width = width * canvasWidth.value!!

                _pathStyle.postValue(style)
            }

            socket.on("alpha") { args ->
                val alphaData = args[0] as String
                val json = JSONObject(alphaData)
                val alpha = json.getString("alpha").toFloat()
                val style = _pathStyle.value
                style.alpha = alpha

                _pathStyle.postValue(style)
            }

            socket.on("addPath") {
                val pathValue = path.value
                val pathStyleValue = pathStyle.value?.copy()
                if (pathStyleValue != null) {
                    addPath(Pair(pathValue, pathStyleValue))
                }
                _path.value = Path()
            }

            socket.on("undoPath") {
                undoPath()
            }

            socket.on("redoPath") {
                redoPath()
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
        if (stream.audioTracks.isNotEmpty()) {
            stream.audioTracks[0].setEnabled(true) // 오디오 트랙 활성화
        }
        session.setSpeakerMode(true)
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

    private fun addPath(pair: Pair<Path, PathStyle>) {
        val list = _paths.value
        list.add(pair)
        removedPaths.clear()
        _paths.postValue(list)
    }

    private fun undoPath() {
        val pathList = _paths.value
        if (pathList.isEmpty())
            return
        val last = pathList.last()
        val size = pathList.size

        removedPaths.add(last)
        _paths.postValue(pathList.subList(0, size-1))
    }

    private fun redoPath() {
        if (removedPaths.isEmpty())
            return
        _paths.postValue((_paths.value + removedPaths.removeLast()) as MutableList<Pair<Path, PathStyle>>)
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

