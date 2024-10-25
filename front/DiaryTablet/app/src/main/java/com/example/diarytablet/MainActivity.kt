package com.example.diarytablet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.ui.screens.LoginScreen
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: LoginViewModel = hiltViewModel()

            NavHost(navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(viewModel) {
                        navController.navigate("main")
                    }
                }
                composable("main") {
                    MainScreen()
                }
            }
        }
    }
}

