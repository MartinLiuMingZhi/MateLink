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
    namespace = "com.xichen.matelink.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    // Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)

    // Hilt dependencies
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
