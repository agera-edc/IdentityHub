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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PrivateKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.sql.Date;
import java.time.Clock;

import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtUnmarshaller.VERIFIABLE_CREDENTIALS_KEY;

public class VerifiableCredentialsJwtMarshallerImpl implements VerifiableCredentialsJwtMarshaller {
    private final Clock clock;

    public VerifiableCredentialsJwtMarshallerImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Result<SignedJWT> buildSignedJwt(VerifiableCredential credential, String issuer, String subject, PrivateKeyWrapper privateKey) {
        var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).build();
        var claims = new JWTClaimsSet.Builder()
                .claim(VERIFIABLE_CREDENTIALS_KEY, credential)
                .issuer(issuer)
                .subject(subject)
                .issueTime(Date.from(clock.instant()))
                .build();

        var jws = new SignedJWT(jwsHeader, claims);

        try {
            jws.sign(privateKey.signer());
        } catch (JOSEException e) {
            return Result.failure(e.getMessage());
        }

        return Result.success(jws);
    }
}
