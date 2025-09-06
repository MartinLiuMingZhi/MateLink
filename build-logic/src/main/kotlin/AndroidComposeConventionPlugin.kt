import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val androidExtension = extensions.getByName("android")

            // Try to configure as ApplicationExtension first
            if (androidExtension is ApplicationExtension) {
                androidExtension.apply {
                    buildFeatures {
                        compose = true
                    }

                    composeOptions {
                        kotlinCompilerExtensionVersion = "1.5.1"
                    }
                }
            } else if (androidExtension is LibraryExtension) {
                // Configure as LibraryExtension
                androidExtension.apply {
                    buildFeatures {
                        compose = true
                    }

                    composeOptions {
                        kotlinCompilerExtensionVersion = "1.5.1"
                    }
                }
            }

            dependencies {
                val bom = platform("androidx.compose:compose-bom:2023.03.00")
                add("implementation", bom)
                add("implementation", "androidx.compose.ui:ui")
                add("implementation", "androidx.compose.ui:ui-graphics")
                add("implementation", "androidx.compose.ui:ui-tooling-preview")
                add("implementation", "androidx.compose.material3:material3")
                add("implementation", "androidx.activity:activity-compose:1.7.2")

                add("debugImplementation", "androidx.compose.ui:ui-tooling")
                add("debugImplementation", "androidx.compose.ui:ui-test-manifest")
            }
        }
    }
}
