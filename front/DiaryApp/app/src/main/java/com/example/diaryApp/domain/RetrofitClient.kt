package com.example.diaryApp.domain

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.diaryApp.utils.Const
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

    fun getInstance(): Retrofit {
        if (instance == null) {
            initInstance()
        }
        return instance!!
    }

    @SuppressLint("NewApi")
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

    fun login(
        accessToken: String,
        refreshToken: String
    ) {
        RetrofitClient.accessToken = accessToken
        RetrofitClient.refreshToken = refreshToken
        initInstance()
    }

    fun resetAccessToken(accessToken: String) {
        RetrofitClient.accessToken = accessToken
        initInstance()
    }

    fun logout() {
        initInstance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getGsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer<Any?> { json, _, _ ->
                    LocalDateTime.parse(
                        json.asString,
                        when (json.asString.length) {
                            26 -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                            25 -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS")
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