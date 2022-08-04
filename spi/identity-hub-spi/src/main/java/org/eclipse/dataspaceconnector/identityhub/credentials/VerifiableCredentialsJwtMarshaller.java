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

package org.eclipse.dataspaceconnector.identityhub.credentials;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PrivateKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.util.Map;

/**
 * Service with operations for manipulation of VerifiableCredentials in JWT format.
 */
public interface VerifiableCredentialsJwtMarshaller {

    /**
     * Builds a verifiable credential as a signed JWT
     *
     * @param credential The verifiable credential to sign
     * @param issuer     The issuer of the verifiable credential
     * @param subject    The subject of the verifiable credential
     * @param privateKey The private key of the issuer, used for signing
     * @return The Verifiable Credential as a JWT
     * @throws Exception In case the credential can not be signed
     */
    SignedJWT buildSignedJwt(VerifiableCredential credential, String issuer, String subject, PrivateKeyWrapper privateKey) throws Exception;
}
