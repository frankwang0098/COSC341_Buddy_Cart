plugins {
    alias(libs.plugins.android.application)
}

android {
<<<<<<< HEAD
    namespace = "com.example.buddycart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.buddycart"
        minSdk = 24
=======
    namespace = "com.example.cosc341_buddy_cart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cosc341_buddy_cart"
        minSdk = 35
>>>>>>> 3b1bf1977c2bd21f18883e7670405a78853ae081
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}