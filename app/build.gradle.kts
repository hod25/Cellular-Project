plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "1.9.0"
    alias(libs.plugins.androidx.navigation.safeargs)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    //id("org.jetbrains.kotlin.android") version "1.9.0"
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CLOUD_NAME", "\"${project.properties["CLOUD_NAME"] ?: ""}\"")
        buildConfigField("String", "API_KEY", "\"${project.properties["API_KEY"] ?: ""}\"")
        buildConfigField("String", "API_SECRET", "\"${project.properties["API_SECRET"] ?: ""}\"")
        buildConfigField("String", "TMDB_BASE_URL", "\"${project.properties["TMDB_BASE_URL"] ?: ""}\"")
        buildConfigField("String", "TMDB_POSTER_BASE_URL", "\"${project.properties["TMDB_POSTER_BASE_URL"] ?: ""}\"")
        buildConfigField("String", "TMDB_ACCESS_TOKEN", "\"${project.properties["TMDB_ACCESS_TOKEN"] ?: ""}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation (libs.cloudinary.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    
    implementation(libs.androidx.navigation.fragment)
//    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.room.runtime)
    //kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
