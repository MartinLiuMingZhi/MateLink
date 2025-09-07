plugins {
    id("matelink.android.library")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.xichen.matelink.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // DiskLruCache
    implementation("com.jakewharton:disklrucache:2.0.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
