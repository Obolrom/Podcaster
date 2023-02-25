plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "io.obolonsky.github"
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

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
}

dependencies {

    implementation(project(":core_ui"))
    implementation(project(":core"))
    implementation(project(":utils"))

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.44.2")
    kapt("com.google.dagger:dagger-compiler:2.44.2")

    // AppAuth
    implementation("net.openid:appauth:0.9.1")

    // Redux
    implementation("org.orbit-mvi:orbit-core:4.6.0")
    implementation("org.orbit-mvi:orbit-viewmodel:4.6.0")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.6.1")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // KTX
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.coil-kt:coil:2.0.0-rc03")
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.3")
}