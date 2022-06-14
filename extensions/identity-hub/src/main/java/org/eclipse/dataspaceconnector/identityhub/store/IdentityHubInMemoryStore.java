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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * In memory store of Hub Objects.
 */
// TODO: Add tests when the logic to store objects is defined
// TODO: Handle concurrency
public class IdentityHubInMemoryStore implements IdentityHubStore {

    private final Collection<HubObject> hubObjects = new ArrayList<>();

    @Override
    public Collection<HubObject> getAll() {
        return List.copyOf(hubObjects);
    }

    @Override
    public void add(HubObject hubObject) {
        hubObjects.add(hubObject);
    }
}
