package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;

import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2ServiceListener {

    private static final Log logger = LogFactory.getLog(SpmlR2ServiceListener.class);

    private List<UserProvisioningAjaxService> services;

    public List<UserProvisioningAjaxService> getServices() {
        return services;
    }

    public void setServices(List<UserProvisioningAjaxService> services) {
        this.services = services;
    }

    public void register(SpmlR2Client spml,  Map<String, ?> properties) throws Exception {
        for (UserProvisioningAjaxService svc : services) {
            if (spml.hasTarget(svc.getPspTargetId())) {

                logger.info("Binding Ajax service w/PSP-Target [" + svc.getPspTargetId() + "] to SPML Client "
                        + spml.getPSProviderName());

                ((UserProvisioningAjaxServiceImpl)svc).setSpmlService(spml);
            }

        }

    }

    public void unregister(SpmlR2Client spml,  Map<String, ?> properties) throws Exception {
        for (UserProvisioningAjaxService svc : services) {
            if (spml.hasTarget(svc.getPspTargetId())) {

                logger.info("Unbinding Ajax service w/PSP-Target [" + svc.getPspTargetId() + "] from SPML Client "
                        + spml.getPSProviderName());

                ((UserProvisioningAjaxServiceImpl)svc).setSpmlService(null);
            }

        }

    }
}
