plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.services) }

android {
    namespace = "com.example.liveinpeace"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.liveinpeace"
        minSdk = 23
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)

// Room Database
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation("androidx.room:room-ktx:2.6.1")

// Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

// Coil untuk gambar di Compose
    implementation(libs.coil.compose)

// UI Components
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.material)
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.recyclerview)
    implementation("de.hdodenhof:circleimageview:3.1.0")

// DataStore dan Lifecycle
    implementation(libs.datastore.preferences)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("androidx.activity:activity-ktx:1.7.2")

// Jetpack Compose Navigation
    implementation(libs.androidx.navigation.compose)

// MPAndroidChart untuk grafik
    implementation(libs.mpandroidchart)
    implementation("androidx.compose.material:material-icons-extended")
}