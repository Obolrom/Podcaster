dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        jcenter() // Warning: this repository is going to shut down soon
    }
}
rootProject.name = "Podcaster"

include(":app")
project(":app").projectDir = File(rootDir, "app")

include(":core_ui")
project(":core_ui").projectDir = File(rootDir, "core_ui")

include(":core")
project(":core").projectDir = File(rootDir, "core")

include(":player_feature")
project(":player_feature").projectDir = File(rootDir, "player_feature")