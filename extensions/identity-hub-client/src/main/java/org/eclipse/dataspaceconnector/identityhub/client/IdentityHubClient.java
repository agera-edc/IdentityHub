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

package org.eclipse.dataspaceconnector.identityhub.client;

import org.eclipse.dataspaceconnector.identityhub.models.credentials.VerifiableCredential;

import java.util.Collection;

public interface IdentityHubClient {

    Collection<VerifiableCredential> getVerifiableCredentials(String hubBaseUrl);

    void addVerifiableCredential(String hubBaseUrl, VerifiableCredential verifiableCredential);

}
