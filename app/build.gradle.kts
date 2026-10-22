plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.managerdroid.help"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.managerdroid.help"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "2.3"
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
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Sem dependências externas: o helper usa apenas o SDK do Android.
}
