package org.eclipse.dataspaceconnector.identityhub.store;

import com.github.javafaker.Faker;
import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityHubInMemoryStoreTest {
    private static final Faker FAKER = new Faker();
    private static final String VERIFIABLE_CREDENTIAL_ID = FAKER.internet().uuid();

    @Test
    void addAndReadVerifiableCredential() {
        IdentityHubStore store = new IdentityHubInMemoryStore();
        VerifiableCredential credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();
        store.add(credential);
        assertThat(store.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(credential);
    }
}
