plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("io.realm.kotlin")
}

android {
    namespace = "com.androiddevhispano.diaryapp.utils"
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
}

dependencies {

    implementation(libs.androidx.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    // COMPOSE NAVIGATION
    implementation(libs.androidx.navigation.compose)
    // DESUGAR JDK
    coreLibraryDesugaring(libs.android.tools.desugar)
    // FIREBASE
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.storage)
    // MONGODB REALM
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.realm.sync)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)

}