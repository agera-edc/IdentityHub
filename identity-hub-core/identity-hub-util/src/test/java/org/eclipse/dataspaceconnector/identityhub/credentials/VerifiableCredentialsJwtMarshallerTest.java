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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.nimbusds.jose.jwk.ECKey;
import org.eclipse.dataspaceconnector.iam.did.crypto.key.EcPrivateKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.crypto.key.EcPublicKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtUnmarshaller.VERIFIABLE_CREDENTIALS_KEY;
import static org.eclipse.dataspaceconnector.identityhub.junit.testfixtures.VerifiableCredentialTestUtil.generateEcKey;
import static org.eclipse.dataspaceconnector.identityhub.junit.testfixtures.VerifiableCredentialTestUtil.generateVerifiableCredential;

class VerifiableCredentialsJwtMarshallerTest {

    static final Faker FAKER = new Faker();
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final VerifiableCredential VERIFIABLE_CREDENTIAL = generateVerifiableCredential();

    Instant now = Instant.now();
    Clock clock = Clock.fixed(now, UTC);
    ECKey key = generateEcKey();
    EcPrivateKeyWrapper privateKey = new EcPrivateKeyWrapper(key);
    EcPublicKeyWrapper publicKey = new EcPublicKeyWrapper(key);
    VerifiableCredentialsJwtMarshaller service = new VerifiableCredentialsJwtMarshallerImpl(clock);

    @Test
    void buildSignedJwt_success() throws Exception {
        // Arrange
        var issuer = FAKER.lorem().word();
        var subject = FAKER.lorem().word();

        // Act
        var signedJwtResult = service.buildSignedJwt(VERIFIABLE_CREDENTIAL, issuer, subject, privateKey);

        // Assert
        assertThat(signedJwtResult.succeeded()).isTrue();
        var signedJwt = signedJwtResult.getContent();
        boolean result = signedJwt.verify(publicKey.verifier());
        assertThat(result).isTrue();

        assertThat(signedJwt.getJWTClaimsSet().toJSONObject())
                .containsEntry("iss", issuer)
                .containsEntry("sub", subject)
                .extractingByKey(VERIFIABLE_CREDENTIALS_KEY)
                .satisfies(c -> assertThat(OBJECT_MAPPER.convertValue(c, VerifiableCredential.class))
                        .usingRecursiveComparison()
                        .isEqualTo(VERIFIABLE_CREDENTIAL));

        assertThat(signedJwt.getJWTClaimsSet().getIssueTime()).isEqualTo(now.truncatedTo(SECONDS));
    }
}
