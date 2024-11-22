// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.4.1")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.51.1") // Hilt plugin for the project
    }
}