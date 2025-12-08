package com.domleondev.deltabank.repository.response

import android.app.Application
import com.google.firebase.FirebaseApp

class DeltaBankApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}