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

package org.eclipse.dataspaceconnector.identityhub.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dataspaceconnector.identityhub.cli.TestUtils.createVerifiableCredential;
import static org.eclipse.dataspaceconnector.identityhub.cli.TestUtils.getSignedVerifiableCredential;
import static org.eclipse.dataspaceconnector.spi.response.StatusResult.success;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VerifiableCredentialsCommandTest {

    static final Faker FAKER = new Faker();
    static final ObjectMapper MAPPER = new ObjectMapper();

    static final VerifiableCredential VC1 = createVerifiableCredential();
    static final SignedJWT SIGNED_VC1 = getSignedVerifiableCredential(VC1);

    static final VerifiableCredential VC2 = createVerifiableCredential();
    static final SignedJWT SIGNED_VC2 = getSignedVerifiableCredential(VC2);

    String hubUrl = FAKER.internet().url();

    IdentityHubCli app = new IdentityHubCli();
    CommandLine cmd = new CommandLine(app);
    StringWriter sw = new StringWriter();

    @BeforeEach
    void setUp() {
        app.identityHubClient = mock(IdentityHubClient.class);
        app.hubUrl = hubUrl;
        cmd.setOut(new PrintWriter(sw));
    }

    @Test
    void list() throws Exception {
        // arrange
        when(app.identityHubClient.getVerifiableCredentials(app.hubUrl)).thenReturn(success(List.of(SIGNED_VC1, SIGNED_VC2)));

        // act
        var exitCode = executeList();

        // assert
        assertThat(exitCode).isEqualTo(0);
        assertThat(hubUrl).isEqualTo(app.hubUrl);

        String content = sw.toString();
        var claims = MAPPER.readValue(content, new TypeReference<List<Map<String, Object>>>() {});
        var vcs = claims.stream()
                .map(c -> MAPPER.convertValue(c.get("vc"), VerifiableCredential.class))
                .collect(Collectors.toList());

        assertThat(vcs)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(List.of(VC1, VC2));
    }

    @Test
    void add() throws Exception {
        // arrange
        var vcArgCaptor = ArgumentCaptor.forClass(SignedJWT.class);
        doReturn(success()).when(app.identityHubClient).addVerifiableCredential(eq(app.hubUrl), vcArgCaptor.capture());

        // act
        var exitCode = executeAdd(MAPPER.writeValueAsString(VC1));

        // assert
        assertThat(exitCode).isEqualTo(0);
        assertThat(hubUrl).isEqualTo(app.hubUrl);
        verify(app.identityHubClient).addVerifiableCredential(eq(app.hubUrl), isA(SignedJWT.class));

        var signedJWT = vcArgCaptor.getValue();
        // verify verifiable credential claim
        var vcClaim = signedJWT.getJWTClaimsSet().getJSONObjectClaim("vc").toJSONString();
        var verifiableCredential = MAPPER.readValue(vcClaim, VerifiableCredential.class);
        assertThat(verifiableCredential).usingRecursiveComparison().isEqualTo(VC1);
    }

    @Test
    void add_invalidJson_fails() {
        // arrange
        var json = "Invalid json";

        // act
        var exitCode = executeAdd(json);

        // assert
        assertThat(exitCode).isNotEqualTo(0);
        assertThat(hubUrl).isEqualTo(app.hubUrl);
    }

    private int executeList() {
        return cmd.execute("-s", hubUrl, "vc", "list");
    }

    private int executeAdd(String json) {
        return cmd.execute("-s", hubUrl, "vc", "add", "-c", json, "-i", "identity-hub-tests", "-k", "src/test/resources/test-key.pem");
    }
}