plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fr.steve.fresh"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.steve.fresh"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions {
            unitTests.isIncludeAndroidResources = true
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.runtime.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.robolectric.robolectric)
    testImplementation(libs.core)

    // For Android Instrumented Tests
    androidTestImplementation(libs.junit.v113)
    androidTestImplementation(libs.espresso.core.v340)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}