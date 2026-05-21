package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.ExpenseRepository
import com.example.madproject.data.ExportManager
import com.example.madproject.data.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: SettingsDataStore,
    private val repository: ExpenseRepository,
    private val exportManager: ExportManager
) : ViewModel() {
    private val exportMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settings.isDarkTheme,
        settings.monthlyBudget,
        exportMessage
    ) { isDark, budget, message ->
        SettingsUiState(isDark, budget, message)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settings.setDarkTheme(enabled)
        }
    }

    fun setMonthlyBudget(amount: Double) {
        viewModelScope.launch {
            settings.setMonthlyBudget(amount)
        }
    }

    fun clearMessage() {
        exportMessage.value = null
    }

    fun exportCsv() {
        viewModelScope.launch {
            val expenses = repository.getAllOnce()
            val result = exportManager.exportCsv(expenses)
            exportMessage.value = result.message
        }
    }

    fun exportPdf() {
        viewModelScope.launch {
            val expenses = repository.getAllOnce()
            val result = exportManager.exportPdf(expenses)
            exportMessage.value = result.message
        }
    }
}

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val monthlyBudget: Double = 0.0,
    val exportMessage: String? = null
)
