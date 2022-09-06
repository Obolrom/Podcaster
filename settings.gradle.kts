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

include(":player")
project(":player").projectDir = File(rootDir, "features/player")

include(":shazam")
project(":shazam").projectDir = File(rootDir, "features/shazam")

include(":downloads")
project(":downloads").projectDir = File(rootDir, "features/downloads")

include(":network")
project(":network").projectDir = File(rootDir, "network")

include(":repository")
project(":repository").projectDir = File(rootDir, "repository")

include(":spaceX")
project(":spaceX").projectDir = File(rootDir, "features/spaceX")

include(":utils")
project(":utils").projectDir = File(rootDir, "utils")