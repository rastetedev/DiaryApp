plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.androiddevhispano.diaryapp.write"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ProjectConfig.kotlinCompilerComposeVersion
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // COMPOSE NAVIGATION
    implementation(libs.androidx.navigation.compose)
    // CALENDAR DIALOG
    implementation(libs.sheets.compose.dialogs.calendar)
    // CLOCK DIALOG
    implementation(libs.sheets.compose.dialogs.clock)
    //PAGER
    implementation(libs.google.accompanist.pager)
    // DAGGER HILT
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    //REALM SYNC
    implementation(libs.realm.sync)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)

    // COIL
    implementation(libs.coil.compose)
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":data"))
}