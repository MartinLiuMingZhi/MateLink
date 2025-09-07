plugins {
    id("matelink.android.library")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
}
hilt{
    enableAggregatingTask = true
}

android {
    namespace = "com.xichen.matelink.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://api.matelink.com/\"")
        buildConfigField("String", "API_VERSION", "\"v1\"")
        buildConfigField("boolean", "DEBUG", "true")
    }
}

dependencies {
    implementation(project(":core:common"))

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
