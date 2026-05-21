package com.example.madproject.di

import android.content.Context
import com.example.madproject.data.ExpenseDatabase
import com.example.madproject.data.ExpenseRepository
import com.example.madproject.data.ExportManager
import com.example.madproject.data.SettingsDataStore

interface AppContainer {
    val expenseRepository: ExpenseRepository
    val settingsDataStore: SettingsDataStore
    val exportManager: ExportManager
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext

    private val database: ExpenseDatabase by lazy {
        ExpenseDatabase.create(appContext)
    }

    override val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(database.expenseDao())
    }

    override val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(appContext)
    }

    override val exportManager: ExportManager by lazy {
        ExportManager(appContext)
    }
}
