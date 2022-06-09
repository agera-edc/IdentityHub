package org.eclipse.dataspaceconnector.identityhub.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;

/**
 * See <a href="https://identity.foundation/decentralized-web-node/spec/#messages">Message documentation</a>
 */
@JsonDeserialize(builder = MessageRequestObject.Builder.class)
public class MessageRequestObject {
    private Descriptor descriptor;
    private byte[] data;

    private MessageRequestObject() {
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public byte[] getData() {
        return data;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final MessageRequestObject messageRequestObject;

        private Builder() {
            this(new MessageRequestObject());
        }

        private Builder(MessageRequestObject messageRequestObject) {
            this.messageRequestObject = messageRequestObject;
        }

        @JsonCreator()
        public static MessageRequestObject.Builder newInstance() {
            return new MessageRequestObject.Builder();
        }

        public MessageRequestObject.Builder descriptor(Descriptor descriptor) {
            messageRequestObject.descriptor = descriptor;
            return this;
        }

        public MessageRequestObject.Builder data(byte[] data) {
            messageRequestObject.data = data;
            return this;
        }


        public MessageRequestObject build() {
            Objects.requireNonNull(messageRequestObject.getDescriptor(), "MessageRequestObject must contain a descriptor property.");
            return messageRequestObject;
        }
    }
}
