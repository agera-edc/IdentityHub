plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project

dependencies {
    implementation(project(":extensions:identity-hub"))
    implementation(project(":identity-hub-client"))
    implementation("${edcGroup}:core:${edcVersion}")
    implementation("${edcGroup}:identity-did-spi:${edcVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
}