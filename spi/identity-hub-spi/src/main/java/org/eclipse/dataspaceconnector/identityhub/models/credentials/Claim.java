package org.eclipse.dataspaceconnector.identityhub.models.credentials;

/**
 * Verifiable Credential claim
 */
public class Claim {
    String subject;
    String property;
    String value;
    String issuer;

    public String getSubject() {
        return subject;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public String getIssuer() {
        return issuer;
    }

    public Claim(String subject, String property, String value) {
        this.subject = subject;
        this.property = property;
        this.value = value;
    }

    public Claim(String subject, String property, String value, String issuer) {
        this.subject = subject;
        this.property = property;
        this.value = value;
        this.issuer = issuer;
    }
}
