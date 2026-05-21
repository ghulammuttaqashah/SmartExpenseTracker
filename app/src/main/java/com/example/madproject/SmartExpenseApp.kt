package com.example.madproject

import android.app.Application
import com.example.madproject.di.AppContainer
import com.example.madproject.di.DefaultAppContainer

class SmartExpenseApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
