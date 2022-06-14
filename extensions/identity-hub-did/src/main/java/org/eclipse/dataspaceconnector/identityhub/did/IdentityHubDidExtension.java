/*
 *  Copyright (c) 2021 Microsoft Corporation
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
package org.eclipse.dataspaceconnector.identityhub.did;

import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.hub.IdentityHubClient;
import org.eclipse.dataspaceconnector.identityhub.client.ApiClientFactory;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClientImpl;
import org.eclipse.dataspaceconnector.identityhub.client.api.IdentityHubApi;
import org.eclipse.dataspaceconnector.identityhub.did.credentials.IdentityHubCredentialsVerifier;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import static java.lang.String.format;
import static org.eclipse.dataspaceconnector.iam.did.spi.document.DidConstants.DID_URL_SETTING;


@Provides(CredentialsVerifier.class)
public class IdentityHubDidExtension implements ServiceExtension {

    @EdcSetting
    private final static String HUB_URL_SETTING = "edc.identity.hub.url";

    @Override
    public void initialize(ServiceExtensionContext context) {
        var hubUrl = context.getSetting(HUB_URL_SETTING, null);
        if (hubUrl == null) {
            throw new EdcException(format("Mandatory setting '(%s)' missing", DID_URL_SETTING));
        }

        var credentialsVerifier = new IdentityHubCredentialsVerifier(context.getMonitor());
        context.registerService(CredentialsVerifier.class, credentialsVerifier);

        context.getMonitor().info("Initialized Identity Hub DID extension");
    }
}
