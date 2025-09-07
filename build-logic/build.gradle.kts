plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.6.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "matelink.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "matelink.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "matelink.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
    }
}
