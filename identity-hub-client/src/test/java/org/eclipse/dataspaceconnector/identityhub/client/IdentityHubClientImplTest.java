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

package org.eclipse.dataspaceconnector.identityhub.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageStatus;
import org.eclipse.dataspaceconnector.identityhub.dtos.RequestStatus;
import org.eclipse.dataspaceconnector.identityhub.dtos.ResponseObject;
import org.eclipse.dataspaceconnector.identityhub.model.credentials.VerifiableCredential;
import org.eclipse.dataspaceconnector.spi.monitor.ConsoleMonitor;
import org.eclipse.dataspaceconnector.spi.response.ResponseStatus;
import org.eclipse.dataspaceconnector.spi.response.StatusResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject.MESSAGE_ID_VALUE;
import static org.eclipse.dataspaceconnector.identityhub.junit.testfixtures.VerifiableCredentialTestUtil.buildSignedJwt;

public class IdentityHubClientImplTest {
    private static final Faker FAKER = new Faker();
    private static final String HUB_URL = String.format("https://%s", FAKER.internet().url());
    private static final String VERIFIABLE_CREDENTIAL_ID = FAKER.internet().uuid();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void getVerifiableCredentials() throws Exception {
        var credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();
        var jws = buildSignedJwt(credential, FAKER.internet().url(), FAKER.internet().url()).serialize().getBytes(StandardCharsets.UTF_8);

        Interceptor interceptor = chain -> {
            var request = chain.request();
            var replies = MessageResponseObject.Builder.newInstance().messageId(MESSAGE_ID_VALUE)
                    .status(MessageStatus.OK).entries(List.of(jws)).build();
            var responseObject = ResponseObject.Builder.newInstance()
                    .requestId(FAKER.internet().uuid())
                    .status(RequestStatus.OK)
                    .replies(List.of(replies))
                    .build();
            var body = ResponseBody.create(OBJECT_MAPPER.writeValueAsString(responseObject), MediaType.get("application/json"));

            return new Response.Builder()
                    .body(body)
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(200)
                    .message("")
                    .build();
        };

        var client = createClient(interceptor);
        var statusResult = client.getVerifiableCredentials(HUB_URL);
        assertThat(statusResult.succeeded());
        var jwts = statusResult.getContent().stream().map(jwt -> jwt.serialize().getBytes(StandardCharsets.UTF_8));
        assertThat(jwts).containsExactly(jws);
    }

    @Test
    void getVerifiableCredentialsServerError() {

        var errorMessage = FAKER.lorem().sentence();
        var body = "{}";
        var code = 500;

        Interceptor interceptor = chain -> {
            var request = chain.request();
            return new Response.Builder()
                    .body(ResponseBody.create(body, MediaType.get("application/json")))
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(code)
                    .message(errorMessage)
                    .build();
        };

        var client = createClient(interceptor);
        var statusResult = client.getVerifiableCredentials(HUB_URL);

        var expectedResult = StatusResult.failure(ResponseStatus.FATAL_ERROR, String.format("IdentityHub error response code: %s, response headers: , response body: %s", code, body));
        assertThat(statusResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void getVerifiableCredentialsDeserializationError() {
        Interceptor interceptor = chain -> {
            var request = chain.request();
            var body = ResponseBody.create("{}", MediaType.get("application/json"));

            return new Response.Builder()
                    .body(body)
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(200)
                    .message("{}")
                    .build();
        };

        var client = createClient(interceptor);
        var statusResult = client.getVerifiableCredentials(HUB_URL);
        assertThat(statusResult.fatalError());
    }

    @Test
    void addVerifiableCredentialsServerError() throws Exception {
        var credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();
        var jws = buildSignedJwt(credential, FAKER.internet().url(), FAKER.internet().url());
        var errorMessage = FAKER.lorem().sentence();
        var body = "{}";
        int code = 500;

        Interceptor interceptor = chain -> {
            var request = chain.request();
            return new Response.Builder()
                    .body(ResponseBody.create("{}", MediaType.get("application/json")))
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(code)
                    .message(errorMessage)
                    .build();
        };

        var client = createClient(interceptor);
        var statusResult = client.addVerifiableCredential(HUB_URL, jws);

        var expectedResult = StatusResult.failure(ResponseStatus.FATAL_ERROR, String.format("IdentityHub error response code: %s, response headers: , response body: %s", code, body));
        assertThat(statusResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void addVerifiableCredentialsIoException() throws Exception {
        var credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();
        var jws = buildSignedJwt(credential, FAKER.internet().url(), FAKER.internet().url());
        var exceptionMessage = "Can't resolve address";
        Interceptor interceptor = chain -> {
            var request = chain.request();
            throw new IOException(exceptionMessage);
        };

        var client = createClient(interceptor);
        var statusResult = client.addVerifiableCredential(HUB_URL, jws);

        var expectedResult = StatusResult.failure(ResponseStatus.FATAL_ERROR, exceptionMessage);
        assertThat(statusResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private IdentityHubClientImpl createClient(Interceptor interceptor) {
        var okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        return new IdentityHubClientImpl(okHttpClient, OBJECT_MAPPER, new ConsoleMonitor());
    }
}
