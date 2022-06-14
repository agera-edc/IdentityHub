package org.eclipse.dataspaceconnector.identityhub.client;


import org.eclipse.dataspaceconnector.identityhub.client.api.IdentityHubApi;
import org.eclipse.dataspaceconnector.identityhub.client.models.RequestObject;
import org.eclipse.dataspaceconnector.identityhub.client.models.ResponseObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;

import java.util.Collection;

public class IdentityHubClientImpl implements IdentityHubClient {

    private final IdentityHubApi identityHubApi;

    public IdentityHubClientImpl(IdentityHubApi identityHubApi) {
        this.identityHubApi = identityHubApi;
    }

    @Override
    public Collection<VerifiableCredential> getVerifiableCredentials() {
        ResponseObject response = identityHubApi.handleRequest(new RequestObject());
        return null;
    }

    @Override
    public void pushVerifiableCredential(VerifiableCredential verifiableCredential) {

    }
}
