dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = java.net.URI.create("https://jitpack.io")
        }
        jcenter() // Warning: this repository is going to shut down soon
    }
}
rootProject.name = "Podcaster"
include(":app")
