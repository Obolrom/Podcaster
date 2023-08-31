// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath(kotlin("gradle-plugin", version = "1.9.0"))

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2")

        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    tasks.withType(JavaCompile::class).all {
        options.compilerArgs = listOf(
            "-Adagger.formatGeneratedSource=disabled",
            "-Adagger.gradle.incremental=enabled",
        )
    }

    afterEvaluate {
        extensions.findByType(org.jetbrains.kotlin.gradle.plugin.KaptExtension::class.java)?.arguments {
            arg("gadder.formatGeneratedSource", "disabled")
            arg("gadder.gradle.incremental", "enabled")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}