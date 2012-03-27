package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * Created by IntelliJ IDEA.
 * User: sgonzalez
 * Date: Mar 3, 2010
 * Time: 8:35:23 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ApplianceBuilder {

    IdentityAppliance build(IdentityAppliance appliance) ;

    public byte[] exportProject(IdentityAppliance appliance);

    public byte[] exportMetadata(IdentityAppliance appliance, String providerName, String channelName);

}
