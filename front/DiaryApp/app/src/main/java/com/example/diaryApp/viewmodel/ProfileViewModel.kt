package com.example.diaryApp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryApp.domain.dto.response.Profile
import com.example.diaryApp.domain.repository.ProfileListRepository
import com.example.diaryApp.utils.FileConverter.uriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val profileListRepository: ProfileListRepository
) : AndroidViewModel(application) {

    val _profileList = mutableStateOf<List<Profile>>(emptyList())
    val profileList: State<List<Profile>> get() = _profileList
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    val memberName = mutableStateOf<String>("")
    val memberImg = mutableStateOf<Uri?>(null)

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val profiles = profileListRepository.getProfileList()
                _profileList.value = profiles
                Log.d("ProfileViewModel", "Profile list updated: ${_profileList.value}")
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("ProfileViewModel", "Error loading profiles: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addProfile(
        onSuccess: () -> Unit,
        onErrorPassword: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            val memberImgFile = memberImg.value?.let { uriToFile(getApplication(),it) }
            if (memberImgFile == null) {
                errorMessage.value = "이미지를 선택해주세요."
                isLoading.value = false
                return@launch
            }

            try {
                val requestBody = memberName.value.toRequestBody("text/plain".toMediaTypeOrNull())
                val imgRequestBody = memberImgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imgPart = MultipartBody.Part.createFormData("file", memberImgFile.name, imgRequestBody)
                profileListRepository.createProfile(requestBody, imgPart)
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("ProfileViewModel", "Error adding profile: ${e.message}")
                onError()
            } finally {
                isLoading.value = false
                loadProfiles()
            }
        }
    }
}