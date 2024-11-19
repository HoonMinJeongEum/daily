package com.example.diaryApp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryApp.domain.dto.response.profile.Profile
import com.example.diaryApp.domain.repository.profile.ProfileListRepository
import com.example.diaryApp.utils.FileConverter.uriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    fun loadProfiles() {
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
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            val memberImgFile = memberImg.value?.let { uriToFile(getApplication(),it) }

            if (memberImgFile == null) {
                isLoading.value = false
                errorMessage.value = "이미지를 선택해주세요."
                return@launch
            }

            try {
                val requestBody = memberName.value.toRequestBody("text/plain".toMediaTypeOrNull())
                val imgRequestBody = memberImgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imgPart = MultipartBody.Part.createFormData("file", memberImgFile.name, imgRequestBody)
                val response = profileListRepository.createProfile(requestBody, imgPart)
                if (response.isSuccessful) {
                    loadProfiles()
                    onSuccess()

                } else {
                    onError()

                }

            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("ProfileViewModel", "Error adding profile: ${e.message}")
                onError()
            } finally {
                isLoading.value = false
            }
        }
    }

    suspend fun deleteProfile(memberId: Int
    ) {
        viewModelScope.launch {
            isLoading.value = true
        }
        try {
            profileListRepository.deleteProfile(memberId)
            Log.d("ProfileViewModel", "Succes Delete Profile")
            loadProfiles()
        } catch(e:Exception) {
            errorMessage.value = e.message
            Log.e("ProfileViewModel", "Error adding profile: ${e.message}")
        }

    }
}