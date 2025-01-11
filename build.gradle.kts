plugins {
    kotlin("jvm") version "2.0.0"
}

group = "ru.webrelab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.24.2")
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}