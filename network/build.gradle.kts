import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.apollographql.apollo3") version "3.5.0"
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

        buildConfigField(
            type = "String",
            name = Constants.SHAZAM_DETECT_API_KEY_NAME,
            value = gradleLocalProperties(rootDir)
                .getProperty(Constants.SHAZAM_DETECT_API_PROPERTY_NAME)
        )

        buildConfigField(
            type = "String",
            name = Constants.NASA_API_KEY,
            value = gradleLocalProperties(rootDir)
                .getProperty(Constants.NASA_API_PROPERTY_NAME)
        )

        buildConfigField(
            type = "String",
            name = Constants.MONO_BANK_TOKEN,
            value = gradleLocalProperties(rootDir)
                .getProperty(Constants.MONO_BANK_PROPERTY_NAME)
        )

        buildConfigField(
            type = "io.obolonsky.network.utils.ProductionTypes",
            name = "PRODUCTION_TYPE",
            value = "io.obolonsky.network.utils.ProductionTypes.PROD"
        )

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

    implementation(project(":core"))

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.github.haroldadmin:NetworkResponseAdapter:5.0.0")

    // RxJava 3
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.6.1")

    // Apollo
    implementation("com.apollographql.apollo3:apollo-runtime:3.5.0")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.40.5")
    kapt("com.google.dagger:dagger-compiler:2.40.5")

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testImplementation("com.apollographql.apollo3:apollo-mockserver:3.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

apollo {
    schemaFile.set(file("src/main/graphql/schema.graphqls"))
    srcDir(file("src/main/graphql/"))

    packageName.set("io.obolonsky.network")
}