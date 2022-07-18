plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
}

val jetBrainsAnnotationsVersion: String by project
val jacksonVersion: String by project
val faker: String by project

dependencies {
    api("org.jetbrains:annotations:${jetBrainsAnnotationsVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testFixturesImplementation("com.github.javafaker:javafaker:${faker}")
}

publishing {
    publications {
        create<MavenPublication>("identity-hub-spi") {
            artifactId = "identity-hub-spi"
            from(components["java"])
        }
    }
}