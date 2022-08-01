/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.identityhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.identityhub.api.IdentityHubController;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubStore;
import org.eclipse.dataspaceconnector.junit.extensions.DependencyInjectionExtension;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.injection.ObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DependencyInjectionExtension.class)
class IdentityHubExtensionTest {

    private static final String SELF_DOCUMENT_DESCRIPTION_PATH = "edc.self.description.document.path";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private IdentityHubExtension extension;
    private WebService webServiceMock;
    private ServiceExtensionContext serviceExtensionContextMock;

    @BeforeEach
    void setUp(ServiceExtensionContext context, ObjectFactory factory) {

        serviceExtensionContextMock = spy(context); //used to inject the config
        var identityHubStore = mock(IdentityHubStore.class);
        context.registerService(IdentityHubStore.class, identityHubStore);

        webServiceMock = mock(WebService.class);
        context.registerService(WebService.class, webServiceMock);

        extension = factory.constructInstance(IdentityHubExtension.class);
    }

    @Test
    void should_returnCustomSelfDescription_if_selfDescriptionDocumentPathProvided() throws IOException {
        var path = "src/test/resources/self-description.json";
        when(serviceExtensionContextMock.getSetting(SELF_DOCUMENT_DESCRIPTION_PATH, null)).thenReturn(path);
        var captor = ArgumentCaptor.forClass(Object.class);

        extension.initialize(serviceExtensionContextMock);

        verify(webServiceMock).registerResource(captor.capture());

        var controller = captor.getValue();
        assertThat(controller).isInstanceOf(IdentityHubController.class);

        var identityHubController = (IdentityHubController) controller;

        var expected = loadJsonFile(path);
        assertThat(identityHubController.getSelfDescription()).isEqualTo(expected);
    }

    @Test
    void should_returnDefaultSelfDescription_if_noSelfDescriptionDocumentPathProvided() throws IOException {
        var captor = ArgumentCaptor.forClass(Object.class);

        extension.initialize(serviceExtensionContextMock);

        verify(webServiceMock).registerResource(captor.capture());

        var controller = captor.getValue();
        assertThat(controller).isInstanceOf(IdentityHubController.class);

        var identityHubController = (IdentityHubController) controller;

        var expected = loadJsonFile("src/main/resources/default-self-description.json");
        assertThat(identityHubController.getSelfDescription()).isEqualTo(expected);
    }

    private static JsonNode loadJsonFile(String path) throws IOException {
        var content = Files.readString(Path.of(path));
        return OBJECT_MAPPER.readTree(content);
    }
}