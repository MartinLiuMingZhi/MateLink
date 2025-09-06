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

includeBuild("build-logic")

include(
    ":app",
    ":core:common",
    ":core:network", 
    ":core:database",
    ":core:ui",
    ":core:data",
    ":core:di",
    ":feature:auth",
    ":feature:space", 
    ":feature:chat",
    ":feature:moment",
    ":feature:profile",
    ":shared"
)
