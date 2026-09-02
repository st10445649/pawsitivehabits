plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.4"
    kotlin("plugin.serialization") version "2.3.21"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.zahraag.pawsitivehabits"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.zahraag.pawsitivehabits"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.ui.graphics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //roomdb
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    //colour picker
    implementation("com.github.skydoves:colorpicker-compose:1.2.0")

    //calendar package
    implementation("com.kizitonwose.calendar:compose:2.10.1")

    //vico graphs
    val vico = "2.0.0"
    implementation("com.patrykandpatrick.vico:compose-m2:${vico}")
    implementation("com.patrykandpatrick.vico:compose-m3:$vico" )
    implementation("com.patrykandpatrick.vico:compose:$vico")
    implementation("com.patrykandpatrick.vico:views:${vico}")


    //retrofit api
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    //firebase authentication
    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))

    implementation("com.google.firebase:firebase-auth")

    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("androidx.room:room-runtime:2.7.0-alpha11")
    implementation("androidx.room:room-ktx:2.7.0-alpha11")
    ksp("androidx.room:room-compiler:2.7.0-alpha11")
    implementation("org.mongodb:bson:4.11.1")

}