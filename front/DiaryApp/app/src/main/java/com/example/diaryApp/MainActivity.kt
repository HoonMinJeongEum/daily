package com.example.diaryApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val diaryViewModel: DiaryViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiaryMobileApp(navController = rememberNavController(), diaryViewModel, profileViewModel)
        }
    }
}

