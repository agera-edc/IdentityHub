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
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.ECKey;
import org.eclipse.dataspaceconnector.iam.did.crypto.key.EcPrivateKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.crypto.key.EcPublicKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PrivateKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;

import static com.nimbusds.jose.JWSAlgorithm.ES256;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtUnmarshaller.VERIFIABLE_CREDENTIALS_KEY;
import static org.eclipse.dataspaceconnector.identityhub.junit.testfixtures.VerifiableCredentialTestUtil.generateEcKey;
import static org.eclipse.dataspaceconnector.identityhub.junit.testfixtures.VerifiableCredentialTestUtil.generateVerifiableCredential;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VerifiableCredentialsJwtMarshallerTest {

    static final Faker FAKER = new Faker();
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final VerifiableCredential VERIFIABLE_CREDENTIAL = generateVerifiableCredential();

    String issuer = FAKER.lorem().word();
    String subject = FAKER.lorem().word();
    Instant now = Instant.now();
    Clock clock = Clock.fixed(now, UTC);
    ECKey key = generateEcKey();
    EcPrivateKeyWrapper privateKey = new EcPrivateKeyWrapper(key);
    EcPublicKeyWrapper publicKey = new EcPublicKeyWrapper(key);
    VerifiableCredentialsJwtMarshaller service = new VerifiableCredentialsJwtMarshallerImpl(clock);

    @Test
    void buildSignedJwt_success() throws Exception {
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

    @Test
    void buildSignedJwt_failure() throws Exception {
        // Act
        var failure = FAKER.lorem().sentence();
        var pk = mock(PrivateKeyWrapper.class);
        var s = mock(JWSSigner.class);
        when(pk.signer()).thenReturn(s);
        when(s.supportedJWSAlgorithms()).thenReturn(Set.of(ES256));
        when(s.sign(any(), any())).thenThrow(new JOSEException(failure));
        var signedJwtResult = service.buildSignedJwt(VERIFIABLE_CREDENTIAL, issuer, subject, pk);

        // Assert
        assertThat(signedJwtResult.failed()).isTrue();
        assertThat(signedJwtResult.getFailureMessages()).containsExactly(failure);
    }
}
