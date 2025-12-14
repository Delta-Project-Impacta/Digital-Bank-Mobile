package com.domleondev.deltabank.repository.response

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
//import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class DeltaBankApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
    }
}
