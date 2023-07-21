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

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
}

dependencies {

    implementation(project(":core"))
    implementation(project(":utils"))
    implementation(project(":core_ui"))

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")

    implementation("androidx.media3:media3-exoplayer:${Media3.version}")
    implementation("androidx.media3:media3-ui:${Media3.version}")

    // Redux
    implementation("org.orbit-mvi:orbit-core:4.6.0")
    implementation("org.orbit-mvi:orbit-viewmodel:4.6.0")
    implementation("org.orbit-mvi:orbit-compose:4.6.0")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material:material:1.4.0")
    implementation("androidx.compose.material:material-icons-core:1.4.0")
    implementation("androidx.compose.material:material-icons-extended:1.4.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:1.4.0")

    // KTX
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.4.1")
    implementation("androidx.fragment:fragment-ktx:1.4.1")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.47")
    kapt("com.google.dagger:dagger-compiler:2.47")

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.coil-kt:coil:2.0.0-rc03")
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}