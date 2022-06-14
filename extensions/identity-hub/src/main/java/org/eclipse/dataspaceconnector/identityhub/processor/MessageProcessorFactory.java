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

import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubStore;

import static org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces.COLLECTIONS_QUERY;
import static org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces.COLLECTIONS_WRITE;
import static org.eclipse.dataspaceconnector.identityhub.dtos.WebNodeInterfaces.FEATURE_DETECTION_READ;

/**
 * Factory used to provide the right MessageProcessor according to the message method.
 */
public class MessageProcessorFactory {

    private final IdentityHubStore identityHubStore;

    public MessageProcessorFactory(IdentityHubStore identityHubStore) {
        this.identityHubStore = identityHubStore;
    }

    public MessageProcessor create(String method) {
        switch (method) {
            case COLLECTIONS_QUERY:
                return new CollectionsQueryProcessor(identityHubStore);
            case COLLECTIONS_WRITE:
                return new CollectionsWriteProcessor(identityHubStore);
            case FEATURE_DETECTION_READ:
                return new FeatureDetectionReadProcessor();
            default:
                return new InterfaceNotImplementedProcessor();
        }
    }
}
