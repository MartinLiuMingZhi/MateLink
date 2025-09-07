plugins {
    id("matelink.android.library")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
}

hilt{
    enableAggregatingTask = true
}

android {
    namespace = "com.xichen.matelink.core.di"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
    
    // Network
    implementation(libs.okhttp)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
