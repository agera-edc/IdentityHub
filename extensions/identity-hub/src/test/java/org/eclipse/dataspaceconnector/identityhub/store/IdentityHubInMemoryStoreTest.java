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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityHubInMemoryStoreTest {
    private static final Faker FAKER = new Faker();
    private static final byte[] CREDENTIAL = FAKER.lorem().characters().getBytes();

    @Test
    void addAndReadVerifiableCredential() {
        IdentityHubStore store = new IdentityHubInMemoryStore();
        store.add(CREDENTIAL);
        assertThat(store.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(CREDENTIAL);
    }
}
