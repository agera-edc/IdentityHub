package org.eclipse.dataspaceconnector.identityhub.models.credentials;

/**
 * Verifiable Credential claim
 */
public class Claim {
    String subject;
    String property;
    String value;
    String issuer;

    public Claim(String subject, String property, String value, String issuer) {
        this.subject = subject;
        this.property = property;
        this.value = value;
        this.issuer = issuer;
    }
}
