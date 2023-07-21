object Dependencies {

    object Room {
        const val version = "2.4.3"

        const val runtime = "androidx.room:room-runtime:$version"
        const val ktx = "androidx.room:room-ktx:$version"
        const val kapt = "androidx.room:room-compiler:$version"
    }

    object ExoPlayer {
        const val version = "2.15.0"

        const val player = "com.google.android.exoplayer:exoplayer:$version"
        const val playerUi = "com.google.android.exoplayer:exoplayer-ui:$version"
        const val mediaSession = "com.google.android.exoplayer:extension-mediasession:$version"
    }

    object Media3 {
        const val version = "1.0.1"
    }
}