/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial implementation
 *
 */

plugins {
    id("java")
}

val nimbusVersion: String by project
val jacksonVersion: String by project
val edcGroup: String by project
val edcVersion: String by project
val jupiterVersion: String by project
val assertj: String by project
val faker: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation(project(":spi:identity-hub-spi"))
    implementation("${edcGroup}:identity-did-spi:${edcVersion}")
    implementation("${edcGroup}:identity-did-crypto:${edcVersion}")

    testImplementation(testFixtures(project(":spi:identity-hub-spi")))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("com.github.javafaker:javafaker:${faker}")
}

publishing {
    publications {
        create<MavenPublication>("identity-hub-util") {
            artifactId = "identity-hub-util"
            from(components["java"])
        }
    }
}