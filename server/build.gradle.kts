plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("stdlib"))
    // Ktor HttpServer
    implementation("io.ktor:ktor-server-core:2.1.1")
    implementation("io.ktor:ktor-server-netty:2.1.1")
    implementation("ch.qos.logback:logback-classic:1.2.5")
    testImplementation("junit:junit:4.13.2")
}