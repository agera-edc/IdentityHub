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

package org.eclipse.dataspaceconnector.identityhub.did;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.crypto.credentials.VerifiableCredentialFactory;
import org.eclipse.dataspaceconnector.iam.did.spi.resolution.DidPublicKeyResolver;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.text.ParseException;

/**
 * Class responsible to resolve the public key of an issuer and verifies that JWT is signed by the issuer.
 *
 */
class SignatureVerifier {

    DidPublicKeyResolver didPublicKeyResolver;
    Monitor monitor;

    SignatureVerifier(DidPublicKeyResolver didPublicKeyResolver, Monitor monitor) {
        this.didPublicKeyResolver = didPublicKeyResolver;
        this.monitor = monitor;
    }

    protected boolean isSignedByIssuer(SignedJWT jwt) {
        var issuer = getIssuer(jwt);
        if (issuer.failed()) {
            return false;
        }
        var issuerPublicKey = didPublicKeyResolver.resolvePublicKey(issuer.getContent());
        if (issuerPublicKey.failed()) {
            return false;
        }
        var verificationResult = VerifiableCredentialFactory.verify(jwt, issuerPublicKey.getContent(), "identity-hub");
        return verificationResult.succeeded();
    }

    private Result<String> getIssuer(SignedJWT jwt) {
        try {
            var issuer = jwt.getJWTClaimsSet().getIssuer();
            return issuer == null ? Result.failure("Issuer missing from JWT") : Result.success(issuer);
        } catch (ParseException e) {
            monitor.info("Error parsing issuer from JWT", e);
            return Result.failure(e.getMessage());
        }
    }
}
