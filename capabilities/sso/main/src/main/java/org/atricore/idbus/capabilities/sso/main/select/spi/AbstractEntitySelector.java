package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;

/**
 *
 */
public abstract class AbstractEntitySelector implements EntitySelector, EntitySelectorConstants {

    private String selectorAttributesEndpoint;

    public int getMode() {
        return 0;
    }

    public boolean canHandle(EntitySelectionContext ctx) {
        return true;
    }

    public String getSelectorAttributesEndpoint() {
        return selectorAttributesEndpoint;
    }

    public void setSelectorAttributesEndpoint(String selectorAttributesEndpoint) {
        this.selectorAttributesEndpoint = selectorAttributesEndpoint;
    }


}
