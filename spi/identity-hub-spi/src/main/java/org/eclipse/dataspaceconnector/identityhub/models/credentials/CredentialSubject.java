package org.eclipse.dataspaceconnector.identityhub.models.credentials;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CredentialSubject {

    /**
     * Identifier of the subject of the credential
     */
    String id;

    Map<String, String> claims;

    public CredentialSubject(@JsonProperty("id") String id, Map<String, String> claims) {
        this.id = id;
        this.claims = claims;
    }
}
