plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.applicationproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.applicationproject"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Dots indicator
    implementation(libs.dotsindicator)
    // Material Components
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation(libs.recyclerview)
    implementation(libs.foundation.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

// Material Design
    implementation ("com.google.android.material:material:1.9.0")

// RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.0")

// ConstraintLayout
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
}