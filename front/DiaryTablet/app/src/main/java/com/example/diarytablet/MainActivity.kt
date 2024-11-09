package com.example.diarytablet

import DiaryScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userStore: UserStore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()

        val startDestination = runBlocking {
            val isAutoLoginEnabled = userStore.getAutoLoginState().firstOrNull() ?: false
            val accessToken = userStore.getValue(UserStore.KEY_ACCESS_TOKEN).firstOrNull()
            val refreshToken = userStore.getValue(UserStore.KEY_REFRESH_TOKEN).firstOrNull()

            if (isAutoLoginEnabled && !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                RetrofitClient.login(accessToken, refreshToken)
                "profileList"
            } else {
                "login"
            }
        }

        setContent {
            DiaryTabletApp(startDestination = startDestination)
        }
    }
    private fun askForPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }
    }
}

