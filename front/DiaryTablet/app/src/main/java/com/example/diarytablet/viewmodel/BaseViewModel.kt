package com.example.diarytablet.viewmodel
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.repository.BaseRepository
import com.example.diarytablet.utils.Response

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

open class BaseViewModel(
    application: Application,
    private val baseRepository: BaseRepository
) : AndroidViewModel(application) {

    val loading: MutableState<Boolean> = mutableStateOf(true)
    val error: MutableState<Exception?> = mutableStateOf(null)

    protected suspend fun <T> run(call: suspend () -> T): Flow<Response<T>> = flow {
        var retry = false
        loading.value = true
        error.value = null
        emit(Response.Loading)

        try {
            emit(Response.Success(call()))
            loading.value = false
        } catch (e: HttpException) {
            loading.value = false
            if (e.code() == 403 && !retry) {
                retry = true
                if (reissueAccessToken()) {
                    emit(Response.Loading) // 재시도 중임을 나타내기 위해 로딩 상태 다시 전송
                    emit(Response.Success(call()))
                } else {
                    error.value = Exception("세션이 만료되었습니다. 다시 로그인해주세요.")
                    emit(Response.Failure(error.value!!))
                }
            } else {
                error.value = e
                emit(Response.Failure(e))
            }
        } catch (e: Exception) {
            Log.e("SOLSOL", e.stackTraceToString())
            error.value = e
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun reissueAccessToken(): Boolean {
        return try {
            val newTokens = baseRepository.reissueToken()
            RetrofitClient.resetAccessToken(newTokens.accessToken)

            // 암호화 없이 일반적으로 토큰 저장
            UserStore(getApplication()).apply {
                setValue(UserStore.KEY_ACCESS_TOKEN, newTokens.accessToken)
//                setValue(UserStore.KEY_REFRESH_TOKEN, newTokens.refreshToken)
            }
            true
        } catch (e: Exception) {
            Log.e("SOLSOL", "토큰 재발급 실패: ${e.message}")
            false
        }
    }
}
