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

import java.util.Collection;

/**
 * IdentityHubStore store used to store HubObjects.
 * A custom IdentityHubStore store can be injected via service loader as an EDC Service extension.
 */
public interface IdentityHubStore {

    Collection<HubObject> getAll();

    void add(HubObject hubObject);
}
