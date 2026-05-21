package com.example.madproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madproject.di.AppContainer
import com.example.madproject.ui.navigation.AppNavHost
import com.example.madproject.ui.theme.SmartExpenseTheme
import com.example.madproject.ui.viewmodel.AppViewModelFactory
import com.example.madproject.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as SmartExpenseApp).container
        setContent {
            SmartExpenseRoot(container)
        }
    }
}

@Composable
private fun SmartExpenseRoot(container: AppContainer) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = AppViewModelFactory(container)
    )
    val uiState by settingsViewModel.uiState.collectAsState()

    SmartExpenseTheme(darkTheme = uiState.isDarkTheme, dynamicColor = false) {
        AppNavHost(container = container)
    }
}