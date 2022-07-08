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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.crypto.credentials.VerifiableCredentialFactory;
import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PublicKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.spi.resolution.DidPublicKeyResolver;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.models.credentials.Claim;
import org.eclipse.dataspaceconnector.identityhub.models.credentials.VerifiableCredential;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.AbstractResult;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements a sample credentials validator that checks for signed registration credentials.
 */
public class IdentityHubCredentialsVerifier implements CredentialsVerifier {

    private final IdentityHubClient identityHubClient;
    private final Monitor monitor;
    private final DidPublicKeyResolver didPublicKeyResolver;
    private final ObjectMapper objectMapper;

    /**
     * Create a new credential verifier that uses an Identity Hub
     *
     * @param identityHubClient IdentityHubClient.
     */
    public IdentityHubCredentialsVerifier(IdentityHubClient identityHubClient, Monitor monitor, DidPublicKeyResolver didPublicKeyResolver, ObjectMapper objectMapper) {
        this.identityHubClient = identityHubClient;
        this.monitor = monitor;
        this.didPublicKeyResolver = didPublicKeyResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result<Map<String, String>> verifyCredentials(String hubBaseUrl, PublicKeyWrapper othersPublicKey) {
        var claims = getClaims(hubBaseUrl);
        if (claims.failed()) return Result.failure(claims.getFailureMessages());
        // This logic will be removed after changing the CredentialVerifier contract.
        var mappedClaims = claims.getContent().stream().collect(Collectors.toMap(c -> String.join(":", c.getIssuer(), c.getProperty()), Claim::getValue));
        return Result.success(mappedClaims);
    }

    // TODO: Change input to DID URL.
    public Result<Collection<Claim>> getClaims(String hubBaseUrl) {
        Collection<Claim> claims = new ArrayList<>();
        var serializedJwts = identityHubClient.getVerifiableCredentials(hubBaseUrl);

        if (serializedJwts.failed()) return Result.failure(serializedJwts.getFailureMessages());

        // Parse JWTs
        var jwts = serializedJwts.getContent()
                .stream()
                .map(this::getSignedJwt)
                .filter(AbstractResult::succeeded)
                .map(AbstractResult::getContent).collect(Collectors.toList());

        for (SignedJWT jwt : jwts) {

            var issuerResult = getIssuer(jwt);
            if (issuerResult.failed()) continue;
            var issuer = issuerResult.getContent();
            // Get issuer public key
            var issuerPublicKey = didPublicKeyResolver.resolvePublicKey(issuer);
            // Verify Signature
            var verificationResult = VerifiableCredentialFactory.verify(jwt, issuerPublicKey.getContent());
            if (!verificationResult) continue;

            var verifiableCredential = getVerifiableCredential(jwt);
            if (verifiableCredential.succeeded()) continue;

            claims.addAll(getClaims(verifiableCredential.getContent(), issuer));
        }

        return Result.success(claims);
    }

    private Result<VerifiableCredential> getVerifiableCredential(SignedJWT jwt) {
        var serializedVerifiableCredential = jwt.getPayload().toString();
        try {
            return Result.success(objectMapper.readValue(serializedVerifiableCredential, VerifiableCredential.class));
        } catch (JsonProcessingException e) {
            return Result.failure(e.getMessage());
        }
    }

    private List<Claim> getClaims(VerifiableCredential verifiableCredential, String issuer) {
        var subject = verifiableCredential.getCredentialSubject().get("id");
        var vcIssuer = verifiableCredential.getIssuer();
        if (!issuer.equals(vcIssuer)) return List.of();

        return verifiableCredential.getCredentialSubject().entrySet().stream()
                .map(entry -> new Claim(subject, entry.getKey(), entry.getValue(), issuer))
                .collect(Collectors.toList());
    }

    private Result<SignedJWT> getSignedJwt(String serializedJwt) {
        try {
            return Result.success(SignedJWT.parse(serializedJwt));
        } catch (ParseException e) {
            monitor.info("Error parsing JWT from IdentityHub", e);
            return Result.failure(String.join("Error parsing JWT from IdentityHub: ", e.getMessage()));
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
