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

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.did.crypto.key.EcPrivateKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtMarshallerTest.FAKER;
import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtMarshallerTest.OBJECT_MAPPER;
import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtMarshallerTest.VERIFIABLE_CREDENTIAL;
import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtUnmarshaller.VERIFIABLE_CREDENTIALS_KEY;
import static org.eclipse.dataspaceconnector.identityhub.junit.testfixtures.VerifiableCredentialTestUtil.generateEcKey;

class VerifiableCredentialsJwtUnmarshallerTest {

    static final JWSHeader JWS_HEADER = new JWSHeader.Builder(JWSAlgorithm.ES256).build();

    ECKey key = generateEcKey();
    EcPrivateKeyWrapper privateKey = new EcPrivateKeyWrapper(key);
    VerifiableCredentialsJwtMarshaller marshaller = new VerifiableCredentialsJwtMarshallerImpl(Clock.systemUTC());
    VerifiableCredentialsJwtUnmarshaller service = new VerifiableCredentialsJwtUnmarshallerImpl();

    @Test
    void extractCredential_OnJwtWithValidCredential() throws Exception {
        // Arrange
        var issuer = FAKER.lorem().word();
        var subject = FAKER.lorem().word();
        var jwt = marshaller.buildSignedJwt(VERIFIABLE_CREDENTIAL, issuer, subject, privateKey);

        // Act
        var result = service.extractCredential(jwt);

        // Assert
        assertThat(result.succeeded()).isTrue();
        assertThat(result.getContent().getKey()).isEqualTo(VERIFIABLE_CREDENTIAL.getId());
        assertThat(result.getContent().getValue())
                .asInstanceOf(map(String.class, Object.class))
                .containsEntry("iss", issuer)
                .containsEntry("sub", subject)
                .extractingByKey(VERIFIABLE_CREDENTIALS_KEY)
                .satisfies(c -> assertThat(OBJECT_MAPPER.convertValue(c, VerifiableCredential.class))
                        .usingRecursiveComparison()
                        .isEqualTo(VERIFIABLE_CREDENTIAL));
    }

    @Test
    void extractCredential_OnJwtWithMissingVcField() {
        // Arrange
        var claims = new JWTClaimsSet.Builder().claim(FAKER.lorem().word(), FAKER.lorem().word()).build();
        var jws = new SignedJWT(JWS_HEADER, claims);

        // Act
        var result = service.extractCredential(jws);

        // Assert
        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).containsExactly(String.format("No %s field found", VERIFIABLE_CREDENTIALS_KEY));
    }

    @Test
    void extractCredential_OnJwtWithWrongFormat() {
        // Arrange
        var claims = new JWTClaimsSet.Builder().claim(VERIFIABLE_CREDENTIALS_KEY, FAKER.lorem().word()).build();
        var jws = new SignedJWT(JWS_HEADER, claims);

        // Act
        var result = service.extractCredential(jws);

        // Assert
        assertThat(result.failed()).isTrue();
    }

}