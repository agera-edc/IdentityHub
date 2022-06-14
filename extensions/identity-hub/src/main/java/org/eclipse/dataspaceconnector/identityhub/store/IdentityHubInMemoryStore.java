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

import org.eclipse.dataspaceconnector.common.concurrency.LockManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * In memory store of Hub Objects.
 */
public class IdentityHubInMemoryStore implements IdentityHubStore {

    private final LockManager lockManager = new LockManager(new ReentrantReadWriteLock());

    private final Collection<HubObject> hubObjects = new ArrayList<>();

    @Override
    public Collection<HubObject> getAll() {
        return lockManager.readLock(() -> List.copyOf(hubObjects));
    }

    @Override
    public void add(HubObject hubObject) {
        lockManager.writeLock(() -> hubObjects.add(hubObject));
    }
}
