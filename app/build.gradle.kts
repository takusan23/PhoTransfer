plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "io.github.takusan23.photransfer"
    compileSdk = 33

    defaultConfig {
        applicationId = "io.github.takusan23.photransfer"
        minSdk = 24
        targetSdk = 33
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
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
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

    // JetpackCompose BOM指定
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // BOM にない
    implementation("androidx.navigation:navigation-compose:2.6.0")
    // Composeで画像読み込む
    implementation("io.coil-kt:coil-compose:1.4.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    // WorkManager Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // MaterialDesign
    implementation("com.google.android.material:material:1.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}