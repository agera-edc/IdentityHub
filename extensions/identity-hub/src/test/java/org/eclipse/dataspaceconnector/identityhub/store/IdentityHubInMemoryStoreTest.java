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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityHubInMemoryStoreTest {
    private static final Faker FAKER = new Faker();

    @Test
    void addAndReadVerifiableCredential() {
        var store = new IdentityHubInMemoryStore<SampleObject>();
        var sampleObject1 = new SampleObject(FAKER.internet().uuid());
        var sampleObject2 = new SampleObject(FAKER.internet().uuid());
        store.add(sampleObject1);
        store.add(sampleObject2);
        assertThat(store.getAll()).usingRecursiveFieldByFieldElementComparator().containsAll(List.of(sampleObject1, sampleObject2));
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
