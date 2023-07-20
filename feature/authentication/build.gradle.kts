plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("io.realm.kotlin")
}

android {
    namespace = "com.androiddevhispano.diaryapp.authentication"
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
    // COMPOSE NAVIGATION
    implementation(libs.androidx.navigation.compose)
    // MESSAGE BAR COMPOSE
    implementation(libs.com.github.stevdza.san.messagebarcompose)
    // ONE-TAP COMPOSE
    implementation(libs.com.github.stevdza.san.onetapcompose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // MONGODB REALM
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.realm.sync)
    // FIREBASE
    implementation(libs.google.firebase.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)

    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
}