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

package org.eclipse.dataspaceconnector.identityhub.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;

/**
 * See <a href="https://identity.foundation/decentralized-web-node/spec/#message-descriptors">message descriptor documentation</a>.
 */
@JsonDeserialize(builder = Descriptor.Builder.class)
public class Descriptor {
    private String method;
    private String nonce;
    // TODO: if data is available, this field should contain stringified Version 1 CID of the DAG PB encoded data.
    private String dataCid;
    // TODO: if data is available, check that format is application/vc+ldp as it is the only one supported at the moment
    private String dataFormat;

    private Descriptor() {
    }

    public String getMethod() {
        return method;
    }

    public String getNonce() {
        return nonce;
    }

    public String getDataCid() {
        return dataCid;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private Descriptor descriptor;

        @JsonCreator()
        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
            descriptor = new Descriptor();
        }

        public Builder method(String method) {
            descriptor.method = method;
            return this;
        }

        public Builder nonce(String nonce) {
            descriptor.nonce = nonce;
            return this;
        }

        public Builder dataCid(String dataCid) {
            descriptor.dataCid = dataCid;
            return this;
        }

        public Builder dataFormat(String dataFormat) {
            descriptor.dataFormat = dataFormat;
            return this;
        }

        public Descriptor build() {
            Objects.requireNonNull(descriptor.method, "Descriptor must contain method property.");
            Objects.requireNonNull(descriptor.nonce, "Descriptor must contain nonce property.");
            return descriptor;
        }
    }
}
