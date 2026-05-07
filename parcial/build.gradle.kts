// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Application plugin from version catalog
    alias(libs.plugins.android.application) apply false
    
    // Jetpack Compose Compiler plugin from version catalog
    alias(libs.plugins.kotlin.compose) apply false
    
    // Hilt Dependency Injection plugin from version catalog
    alias(libs.plugins.hilt) apply false
    
    // Kotlin Symbol Processing (KSP) plugin for Hilt and Room
    alias(libs.plugins.ksp) apply false
    
    // Room plugin for database configuration
    alias(libs.plugins.room) apply false
}