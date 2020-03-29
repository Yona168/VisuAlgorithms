plugins {
    kotlin("jvm") version "1.3.71"
    id("org.openjfx.javafxplugin").version("0.0.8")
}

group = "com.github.yona168"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    javafx{
        modules=listOf("javafx.swing", "javafx.media", "javafx.controls", "javafx.base", "javafx.graphics")
    }
}