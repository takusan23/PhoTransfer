val kotlinVersion: String by rootProject.extra
val composeVersion: String by rootProject.extra


plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "io.github.takusan23.photransfer"
        minSdk = 24
        targetSdk = 31
        versionCode = 3
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
        kotlinCompilerVersion = kotlinVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Ktorのせいでエラー出るので
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {

    // 写真アップロードクライアント
    implementation(project(":client"))
    // 写真アップロードサーバー
    implementation(project(":server"))

    // NSDManager調子悪いので有志の実装を
    implementation("com.github.andriydruk:dnssd:0.9.15")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.4.0-beta02")
    // Compose + Material You
    implementation("androidx.compose.material3:material3:1.0.0-alpha01")
    // Composeで画像読み込む
    implementation("io.coil-kt:coil-compose:1.4.0")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.activity:activity-compose:1.4.0")

    // WorkManager Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // MaterialDesign
    implementation("com.google.android.material:material:1.4.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}