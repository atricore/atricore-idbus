package org.atricore.idbus.capabilities.management.main.spi.request;

import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class UpdateIdentityApplianceRequest extends AbstractManagementRequest {

    private IdentityAppliance appliance;

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

}
