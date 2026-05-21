package com.example.madproject.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val monthlyBudgetKey = doublePreferencesKey("monthly_budget")

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[darkThemeKey] ?: false
    }

    val monthlyBudget: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[monthlyBudgetKey] ?: 0.0
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[darkThemeKey] = enabled
        }
    }

    suspend fun setMonthlyBudget(amount: Double) {
        context.dataStore.edit { prefs ->
            prefs[monthlyBudgetKey] = amount
        }
    }
}
