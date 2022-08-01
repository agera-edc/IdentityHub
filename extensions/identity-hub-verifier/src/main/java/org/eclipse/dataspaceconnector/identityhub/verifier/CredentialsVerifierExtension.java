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

package org.eclipse.dataspaceconnector.identityhub.verifier;

import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.resolution.DidPublicKeyResolver;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClientImpl;
import org.eclipse.dataspaceconnector.identityhub.credentials.VerifiableCredentialsJwtServiceImpl;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

/**
 * Extension to provide verification of IdentityHub Verifiable Credentials.
 */
@Requires({ DidPublicKeyResolver.class })
public class CredentialsVerifierExtension implements ServiceExtension {

    @Inject
    private OkHttpClient httpClient;

    @Inject
    private TypeManager typeManager;

    @Inject
    private Monitor monitor;

    @Inject
    private DidPublicKeyResolver didPublicKeyResolver;

    @Inject
    private JwtCredentialsVerifier jwtCredentialsVerifier;

    @Override
    public void initialize(ServiceExtensionContext context) {
        monitor.info("Initialized Identity Hub DID extension");
    }

    @Provider(isDefault = true)
    public JwtCredentialsVerifier createJwtVerifier(ServiceExtensionContext context) {
        return new DidJwtCredentialsVerifier(didPublicKeyResolver, monitor);
    }

    @Provider
    public CredentialsVerifier createCredentialsVerifier(ServiceExtensionContext context) {
        var client = new IdentityHubClientImpl(httpClient, typeManager.getMapper(), monitor);
        var verifiableCredentialsJwtService = new VerifiableCredentialsJwtServiceImpl(typeManager.getMapper());
        return new IdentityHubCredentialsVerifier(client, monitor, jwtCredentialsVerifier, verifiableCredentialsJwtService);
    }
}
