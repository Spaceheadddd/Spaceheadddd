plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.spacehead.handwarmer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.spacehead.handwarmer"
        minSdk = 29
        targetSdk = 35
        versionCode = 7
        versionName = "1.5.1"
    }

    // A fixed debug key, so every CI build installs over the previous one
    // instead of failing with a signature mismatch.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
