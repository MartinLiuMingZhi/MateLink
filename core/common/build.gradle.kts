plugins {
    id("matelink.android.library")
}

android {
    namespace = "com.xichen.matelink.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
