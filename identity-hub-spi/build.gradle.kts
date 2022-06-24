plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

val jetBrainsAnnotationsVersion: String by project

dependencies {
    api("org.jetbrains:annotations:${jetBrainsAnnotationsVersion}")
}