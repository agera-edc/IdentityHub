/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.identityhub;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.identityhub.api.IdentityHubController;
import org.eclipse.dataspaceconnector.identityhub.processor.CollectionsQueryProcessor;
import org.eclipse.dataspaceconnector.identityhub.processor.CollectionsWriteProcessor;
import org.eclipse.dataspaceconnector.identityhub.processor.FeatureDetectionReadProcessor;
import org.eclipse.dataspaceconnector.identityhub.processor.MessageProcessorRegistry;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubInMemoryStore;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubStore;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.eclipse.dataspaceconnector.identityhub.model.WebNodeInterfaceMethod.COLLECTIONS_QUERY;
import static org.eclipse.dataspaceconnector.identityhub.model.WebNodeInterfaceMethod.COLLECTIONS_WRITE;
import static org.eclipse.dataspaceconnector.identityhub.model.WebNodeInterfaceMethod.FEATURE_DETECTION_READ;

/**
 * EDC extension to boot the services used by the Identity Hub
 */
public class IdentityHubExtension implements ServiceExtension {

    @EdcSetting
    private static final String SELF_DESCRIPTION_DOCUMENT_PATH_SETTING = "edc.self.description.document.path";
    private static final String DEFAULT_SELF_DESCRIPTION_FILE_NAME = "default-self-description.json";

    @Inject
    private WebService webService;

    @Inject
    private IdentityHubStore identityHubStore;

    @Override
    public void initialize(ServiceExtensionContext context) {

        var methodProcessorFactory = new MessageProcessorRegistry();
        methodProcessorFactory.register(COLLECTIONS_QUERY, new CollectionsQueryProcessor(identityHubStore));
        methodProcessorFactory.register(COLLECTIONS_WRITE, new CollectionsWriteProcessor(identityHubStore));
        methodProcessorFactory.register(FEATURE_DETECTION_READ, new FeatureDetectionReadProcessor());

        var path = context.getSetting(SELF_DESCRIPTION_DOCUMENT_PATH_SETTING, null);
        JsonNode selfDescription;
        try (var is = loadSelfDescriptionDocument(path)) {
            selfDescription = context.getTypeManager().getMapper().readTree(is);
        } catch (IOException e) {
            throw new EdcException(e);
        }

        var identityHubController = new IdentityHubController(methodProcessorFactory, selfDescription);
        webService.registerResource(identityHubController);
    }

    @Provider(isDefault = true)
    public IdentityHubStore identityHubStore() {
        return new IdentityHubInMemoryStore();
    }

    /**
     * Load static Self-Description document from the provided path. If the input {@link Path} is empty, then
     * the Self-Description is loaded from a default document of the classpath.
     *
     * @param path Path to the static Self-Description document. If null, then the default Self-Description will
     *             be loaded from the classpath.
     * @return Input stream for the Self-Description document.
     * @throws IOException In case of the document cannot be read.
     */
    private InputStream loadSelfDescriptionDocument(@Nullable String path) throws IOException {
        if (path != null) {
            return Files.newInputStream(Path.of(path));
        } else {
            // load from classpath
            return this.getClass().getClassLoader().getResourceAsStream(DEFAULT_SELF_DESCRIPTION_FILE_NAME);
        }
    }
}
