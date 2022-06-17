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
import io.swagger.v3.oas.annotations.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * MessageResponseObject are objects in the replies of a <a href="https://identity.foundation/decentralized-web-node/spec/#response-objects">Response Object </a>.
 * See <a href="https://identity.foundation/decentralized-web-node/spec/#messages">message documentation</a>.
 */
@JsonDeserialize(builder = MessageResponseObject.Builder.class)
public class MessageResponseObject {

    // TODO: implement messageId as a stringified Version 1 CID of the associated message (as per spec)
    // Temporary message id value.
    public static final String MESSAGE_ID_VALUE = "messageId";

    private String messageId;
    private MessageStatus status;
    private Collection<?> entries = new ArrayList<>();

    private MessageResponseObject() {
    }

    public String getMessageId() {
        return messageId;
    }

    public MessageStatus getStatus() {
        return status;
    }

    /**
     * @return Resulting message entries returned from the invocation of the corresponding message as free form objects
     */
    public Collection<?> getEntries() {
        return entries;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private MessageResponseObject messageResponseObject;

        private Builder() {
            messageResponseObject = new MessageResponseObject();
        }

        @JsonCreator()
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder messageId(String messageId) {
            messageResponseObject.messageId = messageId;
            return this;
        }

        public Builder status(MessageStatus status) {
            messageResponseObject.status = status;
            return this;
        }

        public Builder entries(Collection<?> entries) {
            messageResponseObject.entries = Collections.unmodifiableCollection(entries);
            return this;
        }

        public MessageResponseObject build() {
            Objects.requireNonNull(messageResponseObject.messageId, "MessageResponseObject must contain messageId property.");
            Objects.requireNonNull(messageResponseObject.status, "MessageResponseObject must contain status property.");
            return messageResponseObject;
        }
    }
}
