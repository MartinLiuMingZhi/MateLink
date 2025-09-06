plugins {
    id("matelink.android.library")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.xichen.matelink.core.network"
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    
    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
