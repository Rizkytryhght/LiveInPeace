plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.services) // ✅ Ganti dari id("...")
}


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
//        sourceCompatibility = JavaVersion.VERSION_21
//        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
//        jvmTarget = "21"
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
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
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:22.0.0")
    implementation ("com.google.firebase:firebase-firestore:24.4.0")
    implementation ("androidx.room:room-runtime:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("io.github.chaosleung:pinview:1.4.4")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation ("androidx.activity:activity-ktx:1.7.2")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    implementation(libs.mpandroidchart)

    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation ("androidx.compose.foundation:foundation:1.5.4")
    implementation ("androidx.compose.ui:ui-tooling:1.5.4")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("androidx.compose.ui:ui")
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")
}