package com.example.diarytablet.domain

import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.utils.Const
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


object RetrofitClient {
    private var instance: Retrofit? = null
    private var accessToken: String? = null
    private var refreshToken: String? = null

    private lateinit var userStore: UserStore // UserStore 추가

    fun init(userStore: UserStore) { // UserStore 초기화 메서드 추가
        this.userStore = userStore
    }

    fun getInstance(): Retrofit {
        if (instance == null) {
            initInstance()
        }
        return instance!!
    }

    private fun initInstance() {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(
                Interceptor {
                    val original: Request = it.request()
                    val request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${accessToken ?: ""}") // Authorization 헤더에 Bearer 추가
                        .header("Set-Cookie", refreshToken ?: "")
                        .build()
                    it.proceed(request)
                }
            )
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
        instance = Retrofit.Builder()
            .baseUrl(Const.WEB_API)
            .addConverterFactory(getGsonConverterFactory())

            .client(client)
            .build()
    }

    suspend fun login(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        saveTokensToStore(accessToken, refreshToken) // 토큰 저장 메서드 호출
        initInstance()
    }

    private suspend fun saveTokensToStore(accessToken: String, refreshToken: String) {
        // UserStore에 accessToken과 refreshToken 저장
        userStore.setValue(UserStore.KEY_ACCESS_TOKEN, accessToken)
        userStore.setValue(UserStore.KEY_REFRESH_TOKEN, refreshToken)
    }


    fun resetAccessToken(accessToken: String) {
        this.accessToken = accessToken
        initInstance()
    }

    fun logout() {
        initInstance()
    }

    private fun getGsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    LocalDateTime.parse(
                        json.asString,
                        when (json.asString.length) {
                            23 -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                            22 -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")
                            21 -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")
                            else -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        }
                    )
                })
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    LocalDate.parse(
                        json.asString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                })
            .registerTypeAdapter(
                LocalTime::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    LocalTime.parse(
                        json.asString,
                        DateTimeFormatter.ofPattern("HH:mm:ss")
                    )
                })
            .create()
        return GsonConverterFactory.create(gson)
    }
}