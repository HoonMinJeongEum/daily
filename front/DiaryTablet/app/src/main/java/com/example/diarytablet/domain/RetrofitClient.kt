package com.example.diarytablet.domain

import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.utils.Const
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


object RetrofitClient {
    private var instance: Retrofit? = null
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var userStore: UserStore

    fun init(userStore: UserStore) {
        this.userStore = userStore
        runBlocking {
            accessToken = userStore.getValue(UserStore.KEY_ACCESS_TOKEN).firstOrNull()
            refreshToken = userStore.getValue(UserStore.KEY_REFRESH_TOKEN).firstOrNull()
        }
    }

    fun getInstance(): Retrofit {
        if (instance == null) {
            initInstance()
        }
        return instance!!
    }

    private fun initInstance() {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var requestBuilder = chain.request().newBuilder()
                    .header("Content-Type", "application/json")

                // Access token이 null이 아닌 경우 Authorization 헤더 추가
                accessToken?.let {
                    requestBuilder = refreshToken?.let { it1 ->
                        requestBuilder
                            .header("Authorization", "Bearer $it")
                            .header("Cookie", it1)
                    }!!
                }

                val request = requestBuilder.build()
                var response = chain.proceed(request)

                // 401 Unauthorized 응답 시 refresh token으로 재발급 시도
                if (response.code == 401) {
                    response.close()
                    synchronized(this) {
                        val newTokens = refreshTokens()
                        if (newTokens != null) {
                            accessToken = newTokens.first
                            refreshToken = newTokens.second

                            // 저장소에 갱신된 토큰을 저장
                            runBlocking {
                                accessToken?.let { token ->
                                    userStore.setValue(UserStore.KEY_ACCESS_TOKEN, token)
                                }
                                refreshToken?.let { token ->
                                    userStore.setValue(UserStore.KEY_REFRESH_TOKEN, token)
                                }
                            }

                            // 갱신된 accessToken으로 요청 재시도
                            val newRequest = requestBuilder
                                .header("Authorization", "Bearer ${accessToken ?: ""}")
                                .build()
                            response = chain.proceed(newRequest)
                        }
                    }
                }
                response
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        instance = Retrofit.Builder()
            .baseUrl(Const.WEB_API)
            .addConverterFactory(getGsonConverterFactory())
            .client(client)
            .build()
    }

    // 토큰을 갱신하는 메서드
    private fun refreshTokens(): Pair<String, String>? {
        val retrofit = Retrofit.Builder()
            .baseUrl(Const.WEB_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authService = retrofit.create(AuthService::class.java)

        return try {
            val response = runBlocking {
                authService.reissueToken("Bearer ${refreshToken ?: ""}")
            }
            if (response.isSuccessful) {
                // 새로운 토큰을 응답 헤더에서 추출
                response.headers()["Authorization"]?.removePrefix("Bearer ")?.trim()?.let { newAccessToken ->
                    response.headers()["Set-Cookie"]?.let { newRefreshToken ->
                        Pair(newAccessToken, newRefreshToken)
                    }
                }
            } else {
                null
            }
        } catch (e: HttpException) {
            null
        }
    }

    interface AuthService {
        @POST("user/reissue")
        suspend fun reissueToken(@Header("Cookie") refreshToken: String): Response<Void>

        @POST("user/logout")
        suspend fun logout(): Response<Void>
    }

    fun login(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        initInstance()
    }

    fun resetAccessToken(accessToken: String) {
        this.accessToken = accessToken
        initInstance()
    }

    fun logout() {
        runBlocking {
            val retrofit = Retrofit.Builder()
                .baseUrl(Const.WEB_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().build()) // 이미 기본 클라이언트 설정 사용
                .build()
            val authService = retrofit.create(AuthService::class.java)

            try {
                authService.logout() // 토큰은 인터셉터에 의해 자동으로 헤더에 포함됨
            } catch (e: HttpException) {
                // 로그아웃 API 호출 실패 시 예외 처리
            }

            // 로컬 토큰 삭제
            accessToken = null
            refreshToken = null
            userStore.setValue(UserStore.KEY_ACCESS_TOKEN, "")
            userStore.setValue(UserStore.KEY_REFRESH_TOKEN, "")
        }
        initInstance()
    }


    private fun getGsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(LocalDateTime::class.java,
                JsonDeserializer { json, _, _ -> LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")) }
            )
            .registerTypeAdapter(LocalDate::class.java,
                JsonDeserializer { json, _, _ -> LocalDate.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
            )
            .registerTypeAdapter(LocalTime::class.java,
                JsonDeserializer { json, _, _ -> LocalTime.parse(json.asString, DateTimeFormatter.ofPattern("HH:mm:ss")) }
            )
            .create()
        return GsonConverterFactory.create(gson)
    }
}