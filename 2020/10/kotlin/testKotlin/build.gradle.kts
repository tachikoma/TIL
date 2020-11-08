import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.iam00th"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("io.reactivex.rxjava2:rxjava:2.1.0")
    implementation("io.reactivex.rxjava2:rxkotlin:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}