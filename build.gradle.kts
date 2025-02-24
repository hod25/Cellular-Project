// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()  // מאגר של Google (דרוש עבור Firebase ו-Google Services)
        mavenCentral()
    }
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services) // תלות ב-Google Services Plugin
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    //id("org.jetbrains.kotlin.android") version "1.9.0"
    alias(libs.plugins.kotlin.android) version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false  // תוסף עבור Firebase
}
