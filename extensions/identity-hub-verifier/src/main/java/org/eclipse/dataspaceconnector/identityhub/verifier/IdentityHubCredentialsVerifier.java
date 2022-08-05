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

package org.eclipse.dataspaceconnector.identityhub.verifier;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.document.DidDocument;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.AbstractResult;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.partitioningBy;

/**
 * Obtains and verifies credentials associated with a DID.
 * The DID document contains an IdentityHub service, the IdentityHubCredentialsVerifier gets credentials from the
 * IdentityHub instance and verifies the credentials.
 */
public class IdentityHubCredentialsVerifier implements CredentialsVerifier {

    private static final String IDENTITY_HUB_SERVICE_TYPE = "IdentityHub";
    private final IdentityHubClient identityHubClient;
    private final Monitor monitor;
    private final JwtCredentialsVerifier jwtCredentialsVerifier;
    private final VerifiableCredentialsJwtService verifiableCredentialsJwtService;

    /**
     * Create a new credential verifier that uses an Identity Hub
     *
     * @param identityHubClient IdentityHubClient.
     */
    public IdentityHubCredentialsVerifier(IdentityHubClient identityHubClient, Monitor monitor, JwtCredentialsVerifier jwtCredentialsVerifier, VerifiableCredentialsJwtService verifiableCredentialsJwtService) {
        this.identityHubClient = identityHubClient;
        this.monitor = monitor;
        this.jwtCredentialsVerifier = jwtCredentialsVerifier;
        this.verifiableCredentialsJwtService = verifiableCredentialsJwtService;
    }

    /**
     * Get credentials from the IdentityHub of participant, verifies the credentials, and returns the successfully verified credentials.
     * To be successfully verified, credentials needs to be signed by the specified issuer in the JWT.
     *
     * @param didDocument of a participant. The Did Document should contain an IdentityHub service.
     * @return VerifiableCredentials.
     */
    @Override
    public Result<Map<String, Object>> getVerifiedCredentials(DidDocument didDocument) {
        monitor.info("Retrieving verified credentials for " + didDocument.getId());

        var hubBaseUrl = getIdentityHubBaseUrl(didDocument);
        if (hubBaseUrl.failed()) {
            monitor.severe("Could not retrieve Identity Hub URL from DID document");
            return Result.failure(hubBaseUrl.getFailureMessages());
        }

        monitor.info("Using Identity Hub URL: " + hubBaseUrl.getContent());

        var jwts = identityHubClient.getVerifiableCredentials(hubBaseUrl.getContent());
        if (jwts.failed()) {
            monitor.severe("Could not retrieve Verifiable Credentials from Identity Hub");
            return Result.failure(jwts.getFailureMessages());
        }

        monitor.info(String.format("Retrieved %s verifiable credentials", jwts.getContent().size()));

        List<SignedJWT> filter1 = jwts.getContent()
                .stream()
                .filter(jwt -> jwtCredentialsVerifier.verifyClaims(jwt, didDocument.getId()))
                .collect(Collectors.toList());

        monitor.info(String.format("Validated claims for %s verifiable credentials", filter1.size()));

        var filter2 = filter1.stream()
                .filter(jwtCredentialsVerifier::isSignedByIssuer)
                .collect(Collectors.toList());

        monitor.info(String.format("Validated signature for %s verifiable credentials", filter2.size()));

        var partitionedResult = filter2.stream().map(verifiableCredentialsJwtService::extractCredential).collect(partitioningBy(AbstractResult::succeeded));
        var successfulResults = partitionedResult.get(true);
        var failedResults = partitionedResult.get(false);

        monitor.info(String.format("Extracted %s verifiable credentials", successfulResults.size()));
        failedResults.forEach(result -> monitor.warning("Invalid credentials: " + String.join(",", result.getFailureMessages())));

        var claims = successfulResults.stream()
                .map(AbstractResult::getContent)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return Result.success(claims);
    }

    private Result<String> getIdentityHubBaseUrl(DidDocument didDocument) {
        var hubBaseUrl = didDocument
                .getService()
                .stream()
                .filter(s -> s.getType().equals(IDENTITY_HUB_SERVICE_TYPE))
                .findFirst();

        return hubBaseUrl.map(u -> Result.success(u.getServiceEndpoint()))
                .orElse(Result.failure("Failed getting Identity Hub URL"));
    }
}
