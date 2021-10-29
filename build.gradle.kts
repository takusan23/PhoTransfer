// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    /** Kotlinのバージョン */
    val kotlinVersion by extra("1.5.31")

    /** Composeのバージョン */
    val composeVersion by extra("1.1.0-beta01")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean") {
    doFirst {
        delete(rootProject.buildDir)
    }
}