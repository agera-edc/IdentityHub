plugins {
    `java-library`
}

val jetBrainsAnnotationsVersion: String by project
val jacksonVersion: String by project
val verifiableCredentialsVersion: String by project

dependencies {
    api("org.jetbrains:annotations:${jetBrainsAnnotationsVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.danubetech:verifiable-credentials-java:${verifiableCredentialsVersion}")
}