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
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val mockitoVersion: String by project

dependencies {
    implementation("${edcGroup}:core:${edcVersion}")
    implementation("${edcGroup}:identity-did-spi:${edcVersion}")
    implementation("org.mockito:mockito-core:${mockitoVersion}")
}
