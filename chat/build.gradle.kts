plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.obolonsky.chat"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {

    implementation(project(":core_ui"))
    implementation(project(":core"))
    implementation(project(":utils"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.49")
    ksp("com.google.dagger:dagger-compiler:2.49")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")

    // STOMP
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.coil-kt:coil:2.0.0-rc03")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.3")
}