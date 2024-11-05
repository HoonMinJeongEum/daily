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
import android.util.Base64
import org.json.JSONObject
import androidx.compose.ui.graphics.Path
import io.socket.client.IO
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.diarytablet.domain.dto.request.quiz.CheckWordRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SessionRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SetWordRequestDto
import com.example.diarytablet.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diarytablet.domain.dto.response.quiz.SessionResponseDto
import com.example.diarytablet.utils.openvidu.Session
import org.webrtc.MediaStream

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userStore: UserStore
) : ViewModel() {
//    val sessionId = mutableStateOf<String?>(null)
//    val token = mutableStateOf<String?>(null)
    lateinit var socket: Socket
    val recommendWords = mutableStateOf<List<RecommendWordResponseDto>>(emptyList())
    val setWordResponse = mutableStateOf<String?>(null)
    val checkWordResponse = mutableStateOf<Boolean?>(null)
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

    init {
        loadQuiz()
    }

    // 세션 초기화 함수
    private fun initializeSession() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
//                val jwtToken = userStore.getValue(UserStore.KEY_ACCESS_TOKEN).firstOrNull()

//                if (jwtToken != null) {
//                    val (familyId, familyName) = extractFamilyFromJwt(jwtToken)

//                    if (familyId != null && familyName != null) {
//                        val sessionId = "Session$familyId"
                        val sessionId = "Session1"
                        val sessionRequestDto = SessionRequestDto(customSessionId = "Session1")
                        val response = quizRepository.initializeSession(sessionRequestDto)
                        _sessionId.value = response.body()
                        _sessionId.value?.let { sessionId ->

                            createConnection(sessionId.customSessionId, null)
//                        }
//                    }
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 커넥션 생성 함수
    private fun createConnection(sessionId: String, params: Map<String, Any>?) {
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

    // 추천 단어 가져오기 함수
    fun recommendWord() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = quizRepository.recommendWord()
                recommendWords.value = response.body() ?: emptyList()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 단어 설정 함수
    fun setWord(request: SetWordRequestDto) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = quizRepository.setWord(request)
                setWordResponse.value = response.body()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // 단어 확인 함수
    fun checkWord(request: CheckWordRequestDto) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = quizRepository.checkWord(request)
                checkWordResponse.value = response.body()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
    private fun loadQuiz() {
        socket = IO.socket("ws://10.0.2.2:6080")
        socket.connect()
        socket.emit("join", "room1") // 방 ID를 지정해 연결

        // 서버로부터 "draw" 이벤트 수신
        socket.on("draw") { args ->
            val responseData = args[0] as String
            val jsonMessage = JSONObject(responseData)
            val draws = jsonMessage.getString("draw").split(",")
            val action = draws[0]
            val x = draws[1].toFloat()
            val y = draws[2].toFloat()

            // 수신된 좌표 업데이트
            updatePath(action, x, y)
        }
//        recommendWord()
        initializeSession()
//        val setWordRequestDto = SetWordRequestDto(word="사과")
//        setWord(setWordRequestDto)
//         val checkWordRequestDto = CheckWordRequestDto(word="안녕ㅅ")
//        checkWord(checkWordRequestDto)
//        setWord(setWordRequestDto)
//        createConnection("Session1", null)
    }

    // Path 업데이트 메서드
    private fun updatePath(action: String, x: Float, y: Float) {
        _path.value = Path().apply {
            addPath(_path.value) // 기존 Path 유지
            when (action) {
                "DOWN" -> moveTo(x, y)
                "MOVE" -> lineTo(x, y)
            }
        }
    }

    // 드로잉 시작, 진행, 종료에 따라 소켓 메시지 전송
    fun sendDrawAction(action: String, x: Float, y: Float) {
        val message = """{"draw":"$action,$x,$y"}"""
        socket.emit("draw", message)

        // 로컬에서도 Path 업데이트
        updatePath(action, x, y)
    }

    // Fmaily 가져오는 메서드
    private fun extractFamilyFromJwt(jwt: String): Pair<String?, String?> {
        return try {
            val payload = jwt.split(".")[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes)
            val jsonObject = JSONObject(decodedString)

            val familyObject = jsonObject.getJSONObject("family")
            val familyId = familyObject.getString("id")
            val familyName = familyObject.getString("name")

            Pair(familyId, familyName)
        } catch (e: Exception) {
            Log.e("QuizViewModel", "Error decoding JWT", e)
            Pair(null, null)
        }
    }

    fun leaveSession() {
        session?.leaveSession()
        _leaveSessionTriggered.value = true
    }

    fun resetLeaveSessionTrigger() {
        _leaveSessionTriggered.value = false
    }

    fun setRemoteMediaStream(stream: MediaStream) {
        _remoteMediaStream.postValue(stream)
    }
}