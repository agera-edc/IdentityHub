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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageRequestObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.RequestObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.RequestStatus;
import org.eclipse.dataspaceconnector.identityhub.dtos.ResponseObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces;
import org.eclipse.dataspaceconnector.identityhub.processor.MessageProcessor;
import org.eclipse.dataspaceconnector.identityhub.processor.MessageProcessorFactory;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "IdentityHub")
@Produces({"application/json"})
@Consumes({"application/json"})
@Path("/identity-hub")
/**
 * Identity Hub controller, exposing a <a href="https://identity.foundation/decentralized-web-node/spec">Decentralized Web Node</a> compatible endpoint.
 *
 * See {@link WebNodeInterfaces} for a list of currently supported DWN interfaces.
 */
public class IdentityHubController {

    private final MessageProcessorFactory messageProcessorFactory;

    public IdentityHubController(MessageProcessorFactory messageProcessorFactory) {
        this.messageProcessorFactory = messageProcessorFactory;
    }

    @POST
    public ResponseObject handleRequest(RequestObject requestObject) {
        List<MessageResponseObject> replies = requestObject.getMessages()
                .stream()
                .map(this::processMessage)
                .collect(Collectors.toList());

        return ResponseObject.Builder.newInstance()
                .requestId(requestObject.getRequestId())
                .status(RequestStatus.OK)
                .replies(replies)
                .build();
    }

    private MessageResponseObject processMessage(MessageRequestObject messageRequestObject) {
        String method = messageRequestObject.getDescriptor().getMethod();
        MessageProcessor processor = messageProcessorFactory.create(method);
        byte[] bytes = messageRequestObject.getData();
        return processor.process(bytes);
    }

}

