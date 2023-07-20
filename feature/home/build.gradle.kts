plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.androiddevhispano.diaryapp.home"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "MONGO_APP_ID", project.property("MONGO_APP_ID").toString())
        buildConfigField("String", "CLIENT_ID_WEB", project.property("CLIENT_ID_WEB").toString())

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
    // DAGGER HILT
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    // CALENDAR DIALOG
    implementation(libs.sheets.compose.dialogs.calendar)
    // FIREBASE
    implementation(libs.google.firebase.auth)
    //REALM SYNC
    implementation(libs.realm.sync)
    // DESUGAR JDK
    coreLibraryDesugaring(libs.android.tools.desugar)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)

    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":data"))
}