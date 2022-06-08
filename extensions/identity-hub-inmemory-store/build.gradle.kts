plugins {
    id("java")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    implementation("${edcGroup}:core:${edcVersion}")
    implementation(project(":extensions:identity-hub"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
