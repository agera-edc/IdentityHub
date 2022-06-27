package org.eclipse.dataspaceconnector.identityhub.models.credentials;

import java.time.LocalDateTime;

public class Proof {
    private String type;
    private LocalDateTime created;
    private String proofPurpose;
    private String verificationMethod;
    private String jws;


    // Temporary. TODO: Add builder
    public Proof() {
    }
}
