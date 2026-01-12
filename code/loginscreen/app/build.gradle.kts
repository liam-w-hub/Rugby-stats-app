// defining testing variables
val jUnitVersion = "4.13.2"
val androidXTestVersion = "1.5.0"
val mockitoVersion = "4.8.0"

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.loginscreen"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.loginscreen"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.firebase.auth.v2101)
    implementation(libs.firebase.database.v2003)
    implementation(libs.firebase.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)


    // from android studio testing documentation

    // Required -- JUnit 4 framework
    testImplementation("junit:junit:$jUnitVersion")
    // Optional -- Robolectric environment
    testImplementation("androidx.test:core:$androidXTestVersion")
    // Optional -- Mockito framework
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    // Optional -- mockito-kotlin
    //testImplementation "org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion"
    // Optional -- Mockk framework
    //testImplementation "io.mockk:mockk:$mockkVersion"
}
