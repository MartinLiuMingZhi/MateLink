plugins {
    id("matelink.android.library")
    id("matelink.android.compose")
}

android {
    namespace = "com.xichen.matelink.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    
    // Compose
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)
    implementation(libs.coil.video)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
