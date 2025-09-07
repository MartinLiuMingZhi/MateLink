plugins {
    id("matelink.android.library")
    id("matelink.android.compose")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
}
hilt{
    enableAggregatingTask = true
}
android {
    namespace = "com.xichen.matelink.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))

    // Compose
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose Material 3
    implementation(libs.androidx.material3)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)
    implementation(libs.coil.video)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
