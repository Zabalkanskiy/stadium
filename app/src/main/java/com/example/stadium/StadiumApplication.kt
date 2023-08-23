package com.example.stadium

import android.app.Application
import android.content.Context

class StadiumApplication : Application(){
    init {
        app = this
    }

    companion object {
        private lateinit var app : StadiumApplication

        fun getAppContext(): Context = app.applicationContext
    }
}