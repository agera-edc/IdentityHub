package org.eclipse.dataspaceconnector.identityhub.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject;
import org.eclipse.dataspaceconnector.identityhub.dtos.MessageStatus;
import org.eclipse.dataspaceconnector.identityhub.dtos.VerifiableCredential;
import org.eclipse.dataspaceconnector.identityhub.store.HubObject;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubStore;

import java.util.Base64;

import static org.eclipse.dataspaceconnector.identityhub.dtos.MessageResponseObject.MESSAGE_ID_VALUE;

/**
 * Processor of "CollectionsWrite" messages, in order to write {@link HubObject}s into the {@link IdentityHubStore}.
 */
public class CollectionsWriteProcessor implements MessageProcessor {

    private final IdentityHubStore identityHubStore;
    private final ObjectMapper mapper;

    public CollectionsWriteProcessor(IdentityHubStore identityHubStore) {
        this.identityHubStore = identityHubStore;
        this.mapper = new ObjectMapper();
    }

    public MessageResponseObject process(byte[] data) {
        try {
            var decodedData = new String(Base64.getUrlDecoder().decode(data));
            var credential = mapper.readValue(decodedData, VerifiableCredential.class);
            identityHubStore.add(credential);
            return MessageResponseObject.Builder.newInstance().messageId(MESSAGE_ID_VALUE).status(MessageStatus.OK).build();
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return MessageResponseObject.Builder.newInstance().messageId(MESSAGE_ID_VALUE).status(MessageStatus.MALFORMED_MESSAGE).build();
        }
    }
}
