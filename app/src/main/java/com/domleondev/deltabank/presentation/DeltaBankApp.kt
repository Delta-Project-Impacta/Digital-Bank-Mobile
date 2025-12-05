package com.domleondev.deltabank.presentation

import android.app.Application
import com.google.firebase.FirebaseApp

class DeltaBankApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
