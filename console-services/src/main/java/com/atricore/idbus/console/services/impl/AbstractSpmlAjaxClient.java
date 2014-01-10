package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
import com.atricore.idbus.console.services.spi.exceptions.SpmlAjaxClientException;
import com.atricore.idbus.console.services.spi.request.AbstractProvisioningRequest;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractSpmlAjaxClient implements SpmlAjaxClient {

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    private String defaultPspTargetId;

    private SpmlR2ServiceRegistry spmlServiceRegistry;


    public String getDefaultPspTargetId() {
        return defaultPspTargetId;
    }

    public void setDefaultPspTargetId(String pspTargetId) {
        this.defaultPspTargetId = pspTargetId;
    }

    public SpmlR2ServiceRegistry getSpmlServiceRegistry() {
        return spmlServiceRegistry;
    }

    public void setSpmlServiceRegistry(SpmlR2ServiceRegistry spmlServiceRegistry) {
        this.spmlServiceRegistry = spmlServiceRegistry;
    }

    protected String resolvePspTargetId(AbstractProvisioningRequest req) {
        String pspTargetId = req.getPspTargetId();
        if (pspTargetId == null)
            pspTargetId = defaultPspTargetId;
        return pspTargetId;
    }

    protected SpmlR2Client resolveSpmlService(String defaultPspTargetId) throws SpmlAjaxClientException {
        return spmlServiceRegistry.lookUpClient(defaultPspTargetId);
    }

}
