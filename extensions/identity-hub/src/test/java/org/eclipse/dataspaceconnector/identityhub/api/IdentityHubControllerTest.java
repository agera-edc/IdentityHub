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

package org.eclipse.dataspaceconnector.identityhub.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.restassured.specification.RequestSpecification;
import org.eclipse.dataspaceconnector.identityhub.dtos.Descriptor;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageRequestObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.RequestObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;
import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces.COLLECTIONS_QUERY;
import static org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces.COLLECTIONS_WRITE;
import static org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces.FEATURE_DETECTION_READ;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ExtendWith(EdcExtension.class)
public class IdentityHubControllerTest {

    private static final String API_URL = "http://localhost:8181/api";
    private static final Faker FAKER = new Faker();
    private static final String VERIFIABLE_CREDENTIAL_ID = FAKER.internet().uuid();
    private static final String NONCE = FAKER.lorem().characters(32);
    private static final String TARGET = FAKER.internet().url();
    private static final String REQUEST_ID = FAKER.internet().uuid();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void pushAndQueryVerifiableCredentials() throws IOException {
        VerifiableCredential credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();

        pushVerifiableCredential(credential);
        List<VerifiableCredential> verifiableCredentials = queryVerifiableCredentials();

        assertThat(verifiableCredentials).usingRecursiveFieldByFieldElementComparator().containsExactly(credential);
    }

    @Test
    void detectFeatures() {
        baseRequest()
                .body(createRequestObject(FEATURE_DETECTION_READ))
                .post()
            .then()
                .statusCode(200)
                .body("requestId", equalTo(REQUEST_ID))
                .body("replies", hasSize(1))
                .body("replies[0].entries", hasSize(1))
                .body("replies[0].entries[0].interfaces.collections['CollectionsQuery']", is(true))
                .body("replies[0].entries[0].interfaces.collections['CollectionsWrite']", is(true));
    }

    @Test
    void useUnsupportedMethod() {
        baseRequest()
                .body(createRequestObject("Not supported method"))
                .post()
            .then()
                .statusCode(200)
                .body("requestId", equalTo(REQUEST_ID))
                .body("replies", hasSize(1))
                .body("replies[0].status.code", equalTo(501))
                .body("replies[0].status.detail", equalTo("The interface method is not implemented"));
    }

    @Test
    void writeMalformedMessage() {
        byte[] data = "invalid base64".getBytes(StandardCharsets.UTF_8);
        baseRequest()
                .body(createRequestObject(COLLECTIONS_WRITE, data))
                .post()
            .then()
                .statusCode(200)
                .body("requestId", equalTo(REQUEST_ID))
                .body("replies", hasSize(1))
                .body("replies[0].status.code", equalTo(400))
                .body("replies[0].status.detail", equalTo("The message was malformed or improperly constructed"));
    }

    private RequestSpecification baseRequest() {
        return given()
                .baseUri(API_URL)
                .basePath("/identity-hub")
                .contentType(JSON)
                .when();
    }

    private RequestObject createRequestObject(String method) {
        return createRequestObject(method, null);
    }

    private RequestObject createRequestObject(String method, byte[] data) {
        return RequestObject.Builder.newInstance()
                .requestId(REQUEST_ID)
                .target(TARGET)
                .addMessageRequestObject(MessageRequestObject.Builder.newInstance()
                        .descriptor(Descriptor.Builder.newInstance()
                                .method(method)
                                .nonce(NONCE)
                                .build())
                        .data(data)
                        .build())
                .build();
    }

    private void pushVerifiableCredential(VerifiableCredential credential) throws IOException {
        byte[] data = OBJECT_MAPPER.writeValueAsString(credential).getBytes(StandardCharsets.UTF_8);
        baseRequest()
                .body(createRequestObject(COLLECTIONS_WRITE, data))
                .post()
                .then()
                .statusCode(200)
                .body("requestId", equalTo(REQUEST_ID))
                .body("replies", hasSize(1))
                .body("replies[0].status.code", equalTo(200))
                .body("replies[0].status.detail", equalTo("The message was successfully processed"));
    }

    private List<VerifiableCredential> queryVerifiableCredentials() {
        return baseRequest()
                .body(createRequestObject(COLLECTIONS_QUERY))
                .post()
                .then()
                .statusCode(200)
                .body("requestId", equalTo(REQUEST_ID))
                .body("replies", hasSize(1))
                .body("replies[0].status.code", equalTo(200))
                .body("replies[0].status.detail", equalTo("The message was successfully processed"))
                .extract().body().jsonPath().getList("replies[0].entries", VerifiableCredential.class);
    }
}
