
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.confluent.confluent"

plugins {
  kotlin("jvm") version "1.7.10"
  application
  java
}



dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  implementation("org.apache.kafka:kafka-clients:3.3.1")
  implementation("org.apache.kafka:kafka-streams:3.3.1")
  implementation("org.apache.kafka:connect-runtime:3.3.1")
  implementation("io.confluent:kafka-json-serializer:5.0.1")
  implementation("org.slf4j:slf4j-api:2.0.3")
  implementation("org.slf4j:slf4j-log4j12:2.0.3")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
  //compile ("com.fasterxml.jackson.module:jackson-module-kotlin:[2.8.11.1,)")
  implementation("com.google.code.gson:gson:2.9.0")
  implementation("io.confluent:kafka-json-schema-serializer:5.5.1")
  implementation("com.github.everit-org.json-schema:org.everit.json.schema:1.12.1")
}

repositories {
  maven(url = "https://packages.confluent.io/maven/")
  maven(
    url = "https://jitpack.io"
  )
  mavenCentral()
  jcenter()
}

val configPath: String by project
val topic: String by project
val projectMainClassName: String by project

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

application {
  this.mainClass.set(projectMainClassName)

}
tasks.register<JavaExec>("runApp") {
  args = listOf( configPath, topic)
  mainClass.set(projectMainClassName)
  classpath =  sourceSets["main"].runtimeClasspath
}