/*
 *  Copyright (c) 2021 Microsoft Corporation
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

package org.eclipse.dataspaceconnector.identityhub.did.credentials;

import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PublicKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.client.VerifiableCredential;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.util.Collection;
import java.util.Map;

/**
 * Implements a sample credentials validator that checks for signed registration credentials.
 */
public class IdentityHubCredentialsVerifier implements CredentialsVerifier {

    private final IdentityHubClient identityHubClient;

    /**
     * Create a new credentials verifier that uses an Identity Hub
     *
     * @param identityHubClient
     */
    public IdentityHubCredentialsVerifier(IdentityHubClient identityHubClient) {
        this.identityHubClient = identityHubClient;
    }

    @Override
    public Result<Map<String, String>> verifyCredentials(String hubBaseUrl, PublicKeyWrapper othersPublicKey) {
        Collection<VerifiableCredential> verifiableCredentials = identityHubClient.getVerifiableCredentials(hubBaseUrl);

        // TODO: implement logic

        return Result.success(Map.of());
    }
}
