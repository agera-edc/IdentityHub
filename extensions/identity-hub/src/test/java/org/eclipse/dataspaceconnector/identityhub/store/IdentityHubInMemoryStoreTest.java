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

import com.github.javafaker.Faker;
import org.eclipse.dataspaceconnector.identityhub.api.VerifiableCredential;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityHubInMemoryStoreTest {
    private static final Faker FAKER = new Faker();

    @Test
    void addAndReadVerifiableCredential() {
        var store = new IdentityHubInMemoryStore<VerifiableCredential>();
        var credential1 = VerifiableCredential.Builder.newInstance().id(FAKER.internet().uuid()).build();
        var credential2 = VerifiableCredential.Builder.newInstance().id(FAKER.internet().uuid()).build();
        store.add(credential1);
        store.add(credential2);
        assertThat(store.getAll()).usingRecursiveFieldByFieldElementComparator().containsAll(List.of(credential1, credential2));
    }
}
