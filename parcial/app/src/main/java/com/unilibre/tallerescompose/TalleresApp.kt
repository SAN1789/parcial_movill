package com.unilibre.tallerescompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for the project.
 * The @HiltAndroidApp annotation triggers Hilt's code generation,
 * including a base class for your application that serves as the 
 * application-level dependency container.
 */
@HiltAndroidApp
class TalleresApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application-level initialization can go here if needed
    }
}
