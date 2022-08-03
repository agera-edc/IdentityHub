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

import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtMapper;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

public class IdentityHubClientExtension implements ServiceExtension {

    @Inject
    private OkHttpClient httpClient;

    @Inject
    private TypeManager typeManager;

    @Inject
    private Monitor monitor;

    @Provider(isDefault = true)
    public IdentityHubClient identityHubClient(ServiceExtensionContext context) {
        return new IdentityHubClientImpl(httpClient, typeManager.getMapper(), monitor);
    }

    @Provider(isDefault = true)
    public VerifiableCredentialsJwtMapper verifiableCredentialsJwtMapper(ServiceExtensionContext context) {
        return new VerifiableCredentialsJwtMapperImpl(typeManager.getMapper(), context.getClock());
    }
}
