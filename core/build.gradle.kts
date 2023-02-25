import Dependencies.Media3

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.44.2")

    // AppAuth
    implementation("net.openid:appauth:0.9.1")

    // Player
    implementation("androidx.media3:media3-ui:${Media3.version}")
    implementation("androidx.media3:media3-exoplayer:${Media3.version}")
    implementation("androidx.media3:media3-session:${Media3.version}")

    implementation("org.chromium.net:cronet-api:76.3809.111")
    // For loading data using the Cronet network stack
    implementation("androidx.media3:media3-datasource-cronet:${Media3.version}")
    // For loading data using the OkHttp network stack
    implementation("androidx.media3:media3-datasource-okhttp:${Media3.version}")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
}