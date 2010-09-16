package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;

import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2ServiceListener {

    private static final Log logger = LogFactory.getLog(SpmlR2ServiceListener.class);

    private List<SpmlAjaxClient> services;

    public List<SpmlAjaxClient> getServices() {
        return services;
    }

    public void setServices(List<SpmlAjaxClient> services) {
        this.services = services;
    }

    public void register(SpmlR2Client spml,  Map<String, ?> properties) throws Exception {
        for ( SpmlAjaxClient svc : services) {
            if (spml.hasTarget(svc.getPspTargetId())) {

                logger.info("Binding SPML Ajax Client w/PSP-Target [" + svc.getPspTargetId() + "] to SPML Client "
                        + spml.getPSProviderName());

                svc.setSpmlService(spml);
            }
        }
    }

    public void unregister(SpmlR2Client spml,  Map<String, ?> properties) throws Exception {
        for (SpmlAjaxClient svc : services) {
            if (spml.hasTarget(svc.getPspTargetId())) {


                logger.info("Unbinding SPML Ajax Client w/PSP-Target [" + svc.getPspTargetId() + "] from SPML Client "
                        + spml.getPSProviderName());
                svc.setSpmlService(null);
            }
        }
    }
}
