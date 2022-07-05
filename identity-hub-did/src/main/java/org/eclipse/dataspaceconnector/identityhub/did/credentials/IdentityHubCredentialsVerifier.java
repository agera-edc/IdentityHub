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

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.crypto.credentials.VerifiableCredentialFactory;
import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PublicKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.spi.resolution.DidPublicKeyResolver;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.response.StatusResult;
import org.eclipse.dataspaceconnector.spi.result.AbstractResult;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements a sample credentials validator that checks for signed registration credentials.
 */
public class IdentityHubCredentialsVerifier implements CredentialsVerifier {

    private final IdentityHubClient identityHubClient;
    private final Monitor monitor;
    private final DidPublicKeyResolver didPublicKeyResolver;

    /**
     * Create a new credential verifier that uses an Identity Hub
     *
     * @param identityHubClient IdentityHubClient.
     */
    public IdentityHubCredentialsVerifier(IdentityHubClient identityHubClient, Monitor monitor, DidPublicKeyResolver didPublicKeyResolver) {
        this.identityHubClient = identityHubClient;
        this.monitor = monitor;
        this.didPublicKeyResolver = didPublicKeyResolver;
    }

    @Override
    public Result<Map<String, String>> verifyCredentials(String hubBaseUrl, PublicKeyWrapper othersPublicKey) {
        StatusResult<Collection<String>> statusResult;
        try {
            statusResult = identityHubClient.getVerifiableCredentials(hubBaseUrl);
        } catch (IOException e) {
            return Result.failure(e.getMessage());
        }

        if (statusResult.failed()) return Result.failure(statusResult.getFailureMessages());

        var serializedJwts = statusResult.getContent();

        // Parse JWTs
        var jwts = serializedJwts.stream()
                .map(this::getSignedJwt)
                .filter(AbstractResult::succeeded)
                .map(AbstractResult::getContent).collect(Collectors.toList());

        for (SignedJWT jwt : jwts) {
            // Extract issuer DID URL.
            var issuerResult = getIssuer(jwt);
            if (issuerResult.failed()) continue;
            var issuer = issuerResult.getContent();
            // Get issuer public key
            var issuerPublicKey = didPublicKeyResolver.resolvePublicKey(issuer);
            // Verify Signature
            var verificationResult = VerifiableCredentialFactory.verify(jwt, issuerPublicKey.getContent());
        }

        return Result.success(Map.of());
    }

    private Result<SignedJWT> getSignedJwt(String serializedJwt) {
        try {
            return Result.success(SignedJWT.parse(serializedJwt));
        } catch (ParseException e) {
            monitor.info("Error parsing JWT from IdentityHub", e);
            return Result.failure(e.getMessage());
        }
    }

    private Result<String> getIssuer(SignedJWT jwt) {
        try {
            return Result.success(jwt.getJWTClaimsSet().getIssuer());
        } catch (ParseException e) {
            monitor.info("Error parsing issuer from JWT", e);
            return Result.failure(e.getMessage());
        }
    }
}
