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

/**
 * Verifies verifiable credentials in JWT format.
 */
public interface JwtCredentialsVerifier {
    /**
     * Verifies if a JWT is really signed by the claimed issuer (iss field).
     *
     * @param jwt to be verified.
     * @return if the JWT is signed by the claimed issuer.
     */
    boolean isSignedByIssuer(SignedJWT jwt);

    /**
     * Verifies if a JWT targets the given subject.
     *
     * @param jwt             to be verified.
     * @param expectedSubject subject claim to verify.
     * @return if the JWT is for the given subject and signed by the claimed issuer.
     */
    boolean verifyClaims(SignedJWT jwt, String expectedSubject);
}
