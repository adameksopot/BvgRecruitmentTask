plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint.gradle)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.bvgrecruitmenttask"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bvgrecruitmenttask"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    ktlint {
        android.set(true)
        ignoreFailures.set(false)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(
        libs
            .androidx
            .lifecycle
            .runtime
            .ktx,
    )
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(
        libs
            .androidx
            .ui
            .tooling
            .preview,
    )
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(
        libs
            .androidx
            .ui
            .test
            .junit4,
    )
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(
        libs
            .androidx
            .ui
            .test
            .manifest,
    )
    debugImplementation("androidx.arch.core:core-testing:2.2.0")

    // OkHttp + SSE
    implementation(libs.okhttp)
    implementation(libs.okhttp.eventsource)
    implementation(libs.okhttp.sse)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(
        libs
            .androidx
            .lifecycle
            .runtime
            .ktx
            .v241,
    )

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Moshi
    implementation(libs.moshi.kotlin)
    kapt(
        libs
            .squareup
            .moshi
            .kotlin
            .codegen,
    )
    kaptTest(
        libs
            .squareup
            .moshi
            .kotlin
            .codegen,
    )
    kaptAndroidTest(
        libs
            .squareup
            .moshi
            .kotlin
            .codegen,
    )

    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test.v161)
    androidTestImplementation(libs.kotlinx.coroutines.test.v142)
    testImplementation(libs.turbine)
    implementation ("org.mockito:mockito-inline:5.2.0"){
        exclude(group = "net.bytebudd", module = "byte-buddy")
    }
    implementation (libs.byte.buddy)
}
