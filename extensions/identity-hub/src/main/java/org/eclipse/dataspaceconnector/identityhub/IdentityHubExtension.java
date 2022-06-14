package org.eclipse.dataspaceconnector.identityhub;

import org.eclipse.dataspaceconnector.identityhub.api.IdentityHubController;
import org.eclipse.dataspaceconnector.identityhub.processor.MessageProcessorFactory;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubInMemoryStore;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.identityhub.store.IdentityHubStore;

/**
 * EDC extension to boot the services used by the Identity Hub
 */
public class IdentityHubExtension implements ServiceExtension {
    @Inject
    private WebService webService;

    @Inject
    private IdentityHubStore identityHubStore;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var methodProcessorFactory = new MessageProcessorFactory(identityHubStore);
        var identityHubController = new IdentityHubController(methodProcessorFactory);
        webService.registerResource(identityHubController);
    }

    @Provider(isDefault = true)
    public IdentityHubStore identityHubStore() {
        return new IdentityHubInMemoryStore();
    }
}
