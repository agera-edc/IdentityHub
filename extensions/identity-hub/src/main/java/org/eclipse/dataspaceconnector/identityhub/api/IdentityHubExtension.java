package org.eclipse.dataspaceconnector.identityhub.api;

import org.eclipse.dataspaceconnector.identityhub.api.featuredetection.FeatureDetectionController;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.store.IdentityHubStore;

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
        var featureDetectionController = new FeatureDetectionController();
        var identityHubController = new IdentityHubController(identityHubStore);
        webService.registerResource(featureDetectionController);
        webService.registerResource(identityHubController);
    }
}
