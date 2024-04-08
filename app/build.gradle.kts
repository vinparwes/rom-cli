plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("de.undercouch.download")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.rom_cli"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rom_cli"
        minSdk = 34
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.window:window:1.1.0-alpha03")

    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-view:1.3.2")

    implementation("com.google.gms:google-services:4.4.1")
    implementation("com.google.android.material:material:1.11.0")

    implementation("com.google.guava:guava:31.0.1-android")

    implementation("com.google.mediapipe:tasks-vision:0.10.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}