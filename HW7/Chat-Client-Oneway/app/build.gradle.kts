plugins {
    id("com.android.application")
}

android {
    namespace = "edu.stevens.cs522.chatclient"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.stevens.cs522.chatclient"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Hack to get around overlapping dependencies added by Kotlin libraries
configurations.implementation {
    exclude ("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}

dependencies {

    // Dependencies for fragments (need FragmentActivity for LifeCycleOwner)
    implementation(libs.androidx.fragment)

    // Material design (floating action button)
    implementation(libs.material)

    implementation(files("libs/cs522-library.aar"))
    implementation(libs.guava)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}