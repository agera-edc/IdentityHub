plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project
val nimbusVersion: String by project
val okHttpVersion: String by project
val mockitoVersion: String by project
val assertj: String by project

dependencies {
    implementation(project(":extensions:identity-hub"))
    implementation(project(":identity-hub-client"))
    implementation(project(":spi:identity-hub-spi"))
    implementation("${edcGroup}:core:${edcVersion}")
    implementation("${edcGroup}:identity-did-spi:${edcVersion}")
    implementation("${edcGroup}:identity-did-crypto:${edcVersion}")
    implementation("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")
    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation(project(":identity-hub-junit"))
    testImplementation("${edcGroup}:identity-did-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
}