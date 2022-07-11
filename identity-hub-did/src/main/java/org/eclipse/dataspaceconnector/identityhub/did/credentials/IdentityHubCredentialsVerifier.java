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

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.FromJwtConverter;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.crypto.credentials.VerifiableCredentialFactory;
import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PublicKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.spi.resolution.DidPublicKeyResolver;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.models.credentials.Claim;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.AbstractResult;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        var mappedClaims = claims
                .getContent()
                .stream()
                .collect(Collectors.toMap(c -> String.join(":", c.getIssuer(), c.getProperty()), Claim::getValue));
        return Result.success(mappedClaims);
    }

    // TODO: Change input to DID URL.
    public Result<Collection<Claim>> getClaims(String hubBaseUrl) {
        var serializedJwts = identityHubClient.getVerifiableCredentials(hubBaseUrl);

        if (serializedJwts.failed()) return Result.failure(serializedJwts.getFailureMessages());

        var verifiableCredentials = serializedJwts.getContent()
                .stream()
                .map(this::getVerifiedCredential)
                .filter(AbstractResult::failed)
                .map(AbstractResult::getContent)
                .map(this::extractClaims)
                .filter(AbstractResult::succeeded)
                .flatMap(AbstractResult::getContent)
                .collect(Collectors.toList());

        return Result.success(verifiableCredentials);
    }

    private Result<Stream<Claim>> extractClaims(VerifiableCredential verifiableCredential) {
        var issuer = verifiableCredential.getIssuer().toString();
        var subject = verifiableCredential.getCredentialSubject().getClaims().get("subj").toString();
        var claims = verifiableCredential.getCredentialSubject()
                .getClaims()
                .entrySet()
                .stream()
                .map(entry -> toClaim(entry, issuer, subject))
                .filter(AbstractResult::succeeded)
                .map(AbstractResult::getContent);

        return Result.success(claims);
    }

    private Result<Claim> toClaim(Map.Entry<String, Object> entry, String issuer, String subject) {
        try {
            var value = objectMapper.writeValueAsString(entry.getValue());
            return Result.success(new Claim(subject, entry.getKey(), value, issuer));
        } catch (JsonProcessingException e) {
            return Result.failure("Error parsing claims");
        }
    }

    private Result<VerifiableCredential> getVerifiedCredential(String jwt) {
        try {
            var jwtVerifiableCredential = JwtVerifiableCredential.fromCompactSerialization(jwt);
            VerifiableCredential verifiableCredential = FromJwtConverter.fromJwtVerifiableCredential(jwtVerifiableCredential);
            var issuer = verifiableCredential.getIssuer();
            var issuerPublicKey = didPublicKeyResolver.resolvePublicKey(issuer.toString());
            // TODO: Use: jwtVerifiableCredential.verify_RSA_PS256, for that need to change the keyResolver contract.
            var isSignatureValid = jwtVerifiableCredential.getJwsObject().verify(issuerPublicKey.getContent().verifier());
            return isSignatureValid ? Result.success(verifiableCredential) : Result.failure("Signature is not valid");
        } catch (ParseException e) {
            return Result.failure("Failed parsing the jwt " + e.getMessage());
        } catch (JOSEException e) {
            return Result.failure("Could not verify signature " + e.getMessage());
        }
    }
}
