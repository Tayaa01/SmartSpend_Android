plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "tn.esprit.smartspend"
    compileSdk = 35 // Mise à jour vers 35

    defaultConfig {
        applicationId = "tn.esprit.smartspend"
        minSdk = 23
        targetSdk = 35 // Mise à jour vers 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM for consistent versions
    implementation(platform(libs.androidx.compose.bom))

    // Compose dependencies
    implementation(libs.androidx.ui) // UI toolkit
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Material3
    implementation ("io.coil-kt:coil-compose:2.1.0")
    implementation ("com.guolindev.permissionx:permissionx:1.8.0")

    // Additional Compose dependencies with explicit versions if needed

    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging tools for Compose
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    // Compose Material 3
    implementation ("androidx.compose.material3:material3:1.2.0-alpha02")

    // Material Icons
    implementation ("androidx.compose.material:material-icons-extended:1.5.1")
    implementation ("androidx.compose.material3:material3:1.2.0-alpha02")
    implementation ("androidx.compose.ui:ui:1.5.1")

    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.1")

}
