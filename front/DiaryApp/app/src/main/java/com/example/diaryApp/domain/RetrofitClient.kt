package com.example.diaryApp.domain

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.utils.Const
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
import java.util.concurrent.TimeUnit


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
            synchronized(this) {
                if (instance == null) {
                    initInstance()
                }
            }
        }
        return instance!!
    }

    private fun initInstance() {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // 연결 타임아웃
            .readTimeout(60, TimeUnit.SECONDS)    // 읽기 타임아웃
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val isLoginRequest = originalRequest.url.encodedPath.contains("/login")
                val isTokenRefreshRequest =
                    originalRequest.url.encodedPath.contains("/user/reissue")

                val requestBuilder = if (isLoginRequest || isTokenRefreshRequest) {
                    originalRequest.newBuilder()
                        .header("Content-Type", "application/json")
                } else {
                    originalRequest.newBuilder()
                        .header("Content-Type", "application/json")
                        .apply {
                            synchronized(this@RetrofitClient) {
                                accessToken?.let { header("Authorization", "Bearer $it") }
                                refreshToken?.let { header("Set-Cookie", it) }
                            }
                        }
                }

                val request = requestBuilder.build()
                var response = chain.proceed(request)

                if (response.code == 401) {
                    synchronized(this@RetrofitClient) {
                        val newTokens = refreshTokens()
                        if (newTokens != null) {
                            accessToken = newTokens.first
                            refreshToken = newTokens.second
                            runBlocking {
                                accessToken?.let {
                                    userStore.setValue(
                                        UserStore.KEY_ACCESS_TOKEN,
                                        it
                                    )
                                }
                                refreshToken?.let {
                                    userStore.setValue(
                                        UserStore.KEY_REFRESH_TOKEN,
                                        it
                                    )
                                }
                            }

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

    private fun refreshTokens(): Pair<String, String>? {
        val authService = getInstance().create(AuthService::class.java)
        return try {
            val cleanedRefreshToken = refreshToken?.substringBefore(";")
            val response = runBlocking { authService.reissueToken(cleanedRefreshToken ?: "") }
            if (response.isSuccessful) {
                val newAccessToken =
                    response.headers()["Authorization"]?.removePrefix("Bearer ")?.trim()
                val newRefreshToken = response.headers()["Set-Cookie"]

                if (newAccessToken != null && newRefreshToken != null) {
                    synchronized(this) {
                        accessToken = newAccessToken
                        refreshToken = newRefreshToken
                    }
                    Pair(newAccessToken, newRefreshToken)
                } else null
            } else null
        } catch (e: HttpException) {
            null
        }
    }

    interface AuthService {
        @POST("${Const.API_PATH}user/reissue")
        suspend fun reissueToken(@Header("Cookie") refreshToken: String): Response<Void>

        @POST("${Const.API_PATH}user/logout")
        suspend fun logout(@Header("Cookie") refreshToken: String): Response<Void>
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
            val authService = getInstance().create(AuthService::class.java)
            try {
                val cleanedRefreshToken = refreshToken?.substringBefore(";") ?: ""
                authService.logout(cleanedRefreshToken)
                // 로컬 토큰 삭제
                accessToken = null
                refreshToken = null

                userStore.clearValue(UserStore.KEY_USER_NAME)
                userStore.clearValue(UserStore.KEY_PASSWORD)
                userStore.clearValue(UserStore.KEY_ACCESS_TOKEN)
                userStore.clearValue(UserStore.KEY_REFRESH_TOKEN)
            } catch (e: HttpException) {
                // 로그아웃 실패 처리
            }


        }
    }


    private fun getGsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    val jsonString = json.asString
                    try {
                        LocalDateTime.parse(jsonString, DateTimeFormatter.ISO_DATE_TIME)
                    } catch (e1: Exception) {
                        try {
                            // 예외 시 소수 자릿수별 포맷 적용
                            when (jsonString.length) {
                                26 -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
                                25 -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"))
                                24 -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS"))
                                23 -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
                                22 -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"))
                                21 -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"))
                                else -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                            }
                        } catch (e2: Exception) {
                            null // 파싱 실패 시 null 반환
                        }
                    }
                })
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
                })
            .registerTypeAdapter(
                LocalTime::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    LocalTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_TIME)
                })
            .create()

        return GsonConverterFactory.create(gson)
    }

}