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

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityHubInMemoryStoreTest {
    private static final Faker FAKER = new Faker();
    private static final String VERIFIABLE_CREDENTIAL_ID = FAKER.internet().uuid();

    @Test
    void addAndReadVerifiableCredential() {
        var store = new IdentityHubInMemoryStore<VerifiableCredential>();
        var credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();
        store.add(credential);
        assertThat(store.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(credential);
    }
}
