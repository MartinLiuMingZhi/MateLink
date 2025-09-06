plugins {
    id("matelink.android.application")
    id("matelink.android.compose")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.xichen.matelink"
    
    defaultConfig {
        applicationId = "com.xichen.matelink"
        versionCode = 1
        versionName = "1.0.0"
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:di"))

    // Feature modules
    implementation(project(":feature:auth"))
    implementation(project(":feature:space"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:moment"))
    implementation(project(":feature:profile"))
    
    // Shared module
    implementation(project(":shared"))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}