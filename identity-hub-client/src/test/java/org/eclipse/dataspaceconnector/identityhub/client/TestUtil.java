package org.eclipse.dataspaceconnector.identityhub.client;

import com.github.javafaker.Faker;
import org.eclipse.dataspaceconnector.identityhub.dtos.credentials.VerifiableCredential;

import java.util.Map;

public class TestUtil {

    private static final Faker FAKER = new Faker();

    public static VerifiableCredential createVerifiableCredential() {
        return VerifiableCredential.Builder.newInstance()
                .id(FAKER.internet().uuid())
                .claims(Map.of(
                        FAKER.lorem().word(), FAKER.lorem().word(),
                        FAKER.lorem().word(), FAKER.lorem().word()))
                .build();
    }
}
