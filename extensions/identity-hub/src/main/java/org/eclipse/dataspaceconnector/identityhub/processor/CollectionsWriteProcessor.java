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

package org.eclipse.dataspaceconnector.identityhub.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageStatus;
import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;
import org.eclipse.dataspaceconnector.identityhub.store.HubObject;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubStore;

import java.io.IOException;

import static org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject.MESSAGE_ID_VALUE;

/**
 * Processor of "CollectionsWrite" messages, in order to write {@link HubObject}s into the {@link IdentityHubStore}.
 */
public class CollectionsWriteProcessor implements MessageProcessor {

    private final IdentityHubStore identityHubStore;
    private final ObjectMapper mapper;

    public CollectionsWriteProcessor(IdentityHubStore identityHubStore) {
        this.identityHubStore = identityHubStore;
        this.mapper = new ObjectMapper();
    }

    @Override
    public MessageResponseObject process(byte[] data) {
        try {
            var credential = mapper.readValue(data, VerifiableCredential.class);
            identityHubStore.add(credential);
            return MessageResponseObject.Builder.newInstance().messageId(MESSAGE_ID_VALUE).status(MessageStatus.OK).build();
        } catch (IllegalArgumentException | IOException e) {
            return MessageResponseObject.Builder.newInstance().messageId(MESSAGE_ID_VALUE).status(MessageStatus.MALFORMED_MESSAGE).build();
        }
    }
}
