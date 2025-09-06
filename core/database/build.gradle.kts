plugins {
    id("matelink.android.library")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.xichen.matelink.core.database"
}

dependencies {
    implementation(project(":core:common"))
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
