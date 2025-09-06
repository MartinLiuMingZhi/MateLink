pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MateLink"
include(":app")
include(":core")
include(":feature")
include(":shared")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:ui")
include(":core:data")
include(":core:di")
include(":feature:auth")
include(":feature:space")
include(":feature:chat")
include(":feature:moment")
include(":feature:profile")
