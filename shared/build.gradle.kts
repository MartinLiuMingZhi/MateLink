plugins {
    id("matelink.android.library")
}

android {
    namespace = "com.xichen.matelink.shared"
}

dependencies {
    implementation(project(":core:common"))
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}