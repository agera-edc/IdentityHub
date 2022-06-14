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
import org.eclipse.dataspaceconnector.identityhub.client.ApiClient;
import org.eclipse.dataspaceconnector.identityhub.client.ApiClientFactory;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClientImpl;
import org.eclipse.dataspaceconnector.identityhub.client.api.IdentityHubApi;
import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.util.Collection;
import java.util.Map;

import static java.net.URLDecoder.decode;
import static java.util.stream.Collectors.toMap;

/**
 * Implements a sample credentials validator that checks for signed registration credentials.
 */
public class IdentityHubCredentialsVerifier implements CredentialsVerifier {

    private final Monitor monitor;

    /**
     * Create a new credentials verifier that uses an Identity Hub
     *  @param monitor Monitor
     */
    public IdentityHubCredentialsVerifier(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public Result<Map<String, String>> verifyCredentials(String hubBaseUrl, PublicKeyWrapper othersPublicKey) {
        var identityHubApi = new IdentityHubApi(ApiClientFactory.createApiClient(hubBaseUrl));
        var client = new IdentityHubClientImpl(identityHubApi);

        Collection<VerifiableCredential> verifiableCredentials = client.getVerifiableCredentials();

        // TODO: implement logic

        return Result.success(Map.of());
    }
}
