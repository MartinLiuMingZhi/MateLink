plugins {
    id("matelink.android.library")
}

android {
    namespace = "com.xichen.matelink.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
