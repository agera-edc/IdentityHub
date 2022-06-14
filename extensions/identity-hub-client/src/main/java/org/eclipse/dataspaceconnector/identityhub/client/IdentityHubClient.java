package org.eclipse.dataspaceconnector.identityhub.client;

import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;

import java.util.Collection;

public interface IdentityHubClient {

    Collection<VerifiableCredential> getVerifiableCredentials();

    void pushVerifiableCredential(VerifiableCredential verifiableCredential);

}
