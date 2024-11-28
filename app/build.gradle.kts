plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
//    alias(libs.plugins.hilt)
//    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")


}

android {
    namespace = "com.hmj3908.simplechat"
    compileSdk = 35
//    packaging {
//        resources {
//            excludes += [
//                'META-INF/gradle/incremental.annotation.processors'
//            ]
//        }
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }


    defaultConfig {
        applicationId = "com.hmj3908.simplechat"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        javaCompileOptions {
//            annotationProcessorOptions {
//                arguments += ["room.schemaLocation": "$projectDir/schemas".toString()]
//            }
//        }

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
        dataBinding = true
        buildConfig = true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Lifecycle - ViewModel, LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1") // Room 런타임
    ksp("androidx.room:room-compiler:2.6.1") // Room 컴파일러
    implementation("androidx.room:room-ktx:2.6.1") // Room Coroutines 및 Flow 지원

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")

    // Coroutines & Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Coroutine Core

    // Hilt - 의존성 주입
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
//    implementation(libs.bundles.hilt)

    // Socket 통신
    implementation("org.java-websocket:Java-WebSocket:1.5.2")

//    implementation("com.squareup:javapoet:1.13.0")


}