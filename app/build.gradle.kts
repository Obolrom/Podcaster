plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "io.obolonsky.podcaster"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(project(":core_ui"))
    implementation(project(":core"))
    implementation(project(":player_feature"))
    implementation(project(":shazam_feature"))

    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")

    // Base
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.4.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.1")

    // KTX
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")

    // Firebase
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.2.11")
    implementation("com.google.firebase:firebase-analytics-ktx:21.0.0")

    // Room
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    kapt(Dependencies.Room.kapt)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.github.haroldadmin:NetworkResponseAdapter:5.0.0")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.40.5")
    kapt("com.google.dagger:dagger-compiler:2.40.5")

    implementation("androidx.work:work-runtime-ktx:2.7.1")

    //Navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")

    // Player
    implementation(Dependencies.ExoPlayer.player)
    implementation(Dependencies.ExoPlayer.playerUi)
    implementation(Dependencies.ExoPlayer.mediaSession)

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.coil-kt:coil:2.0.0-rc03")
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.3")
}

tasks.register<tasks.GreetingTask>("greet")

tasks.register<tasks.GreetingTask>("heyHo") {
    this.greeting.set("fuck you")
}

tasks.register<tasks.IncrementalReverseTask>("incrementalReverse") {
    inputDir.set(file("inputs"))
    outputDir.set(file("$buildDir/outputs"))
    inputProperty.set(project.findProperty("taskInputProperty") as String? ?: "original")
}