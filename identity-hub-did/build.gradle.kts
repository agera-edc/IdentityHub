plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project

dependencies {
    implementation(project(":extensions:identity-hub"))
    implementation(project(":identity-hub-client"))
    implementation(project(":spi:identity-hub-spi"))
    implementation("${edcGroup}:core:${edcVersion}")
    implementation("${edcGroup}:identity-did-spi:${edcVersion}")
    implementation("${edcGroup}:identity-did-crypto:${edcVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
    implementation("com.danubetech:verifiable-credentials-java:1.0.0")
}