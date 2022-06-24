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
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.identityhub.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import org.eclipse.dataspaceconnector.identityhub.models.credentials.VerifiableCredential;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityHubInMemoryStoreTest {
    private static final Faker FAKER = new Faker();

    @Test
    void addAndReadVerifiableCredential() {
        // Arrange
        var store = new IdentityHubInMemoryStore();
        var credentialsCount = FAKER.number().numberBetween(1, 10);
        var credentials = Stream.generate(() -> VerifiableCredential.Builder.newInstance().id(FAKER.internet().uuid()).build())
                .limit(credentialsCount).collect(Collectors.toList());

        // Act
        credentials.forEach(store::add);

        // Assert
        assertThat(store.getAll()).usingRecursiveFieldByFieldElementComparator().containsAll(credentials);
    }

    private static class SampleObject {
        private final String id;

        public SampleObject(@JsonProperty("id") String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
