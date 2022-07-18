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

package org.eclipse.dataspaceconnector.identityhub.dtos.credentials;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *   Represents a VerifiableCredential that can be stored in the Identity Hub. The model follows the  <a href="https://www.w3.org/TR/vc-data-model/">W3C Verifiable Credential</a> specification.
 * </p>
 * <p>
 *   The MIME type "application/vc+ldp" should be used to identify the correct format when passing Verifiable Credentials as data to the Identity Hub.
 * </p>
 */
// TODO: implement Verifiable Credential model
@JsonDeserialize(builder = VerifiableCredential.Builder.class)
public class VerifiableCredential {

    private String id;
    private Map<String, String> claims;


    private VerifiableCredential() {
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getClaims() {
        return Map.copyOf(claims);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        VerifiableCredential verifiableCredential;

        private Builder() {
            verifiableCredential = new VerifiableCredential();
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder id(String id) {
            verifiableCredential.id = id;
            return this;
        }

        public Builder claims(Map<String, String> claims) {
            verifiableCredential.claims = Map.copyOf(claims);
            return this;
        }

        public VerifiableCredential build() {
            Objects.requireNonNull(verifiableCredential.id, "VerifiableCredential must contain id property.");
            Objects.requireNonNull(verifiableCredential.claims, "VerifiableCredential must contain claims property.");
            return verifiableCredential;
        }
    }
}
