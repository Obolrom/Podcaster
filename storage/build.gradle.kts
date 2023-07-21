import Dependencies.Media3

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":core"))

    // Dagger 2
    implementation("com.google.dagger:dagger:2.47")
    kapt("com.google.dagger:dagger-compiler:2.47")

    // Room
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    kapt(Dependencies.Room.kapt)

    // Player
    implementation("androidx.media3:media3-ui:${Media3.version}")
    implementation("androidx.media3:media3-exoplayer:${Media3.version}")
    implementation("androidx.media3:media3-session:${Media3.version}")

    implementation("org.chromium.net:cronet-api:76.3809.111")
    // For loading data using the Cronet network stack
    implementation("androidx.media3:media3-datasource-cronet:${Media3.version}")
    // For loading data using the OkHttp network stack
    implementation("androidx.media3:media3-datasource-okhttp:${Media3.version}")

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
}