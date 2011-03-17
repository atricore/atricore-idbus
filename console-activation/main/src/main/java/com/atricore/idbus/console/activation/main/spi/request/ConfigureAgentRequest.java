package com.atricore.idbus.console.activation.main.spi.request;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ConfigureAgentRequest extends AbstractActivationRequest {

    private boolean replaceConfig = false;
    
    private String jossoAgentConfigUri;

    private List<ConfigureAgentResource> reosurces = new ArrayList<ConfigureAgentResource>();

    public String getJossoAgentConfigUri() {
        return jossoAgentConfigUri;
    }

    public void setJossoAgentConfigUri(String jossoAgentConfigUri) {
        this.jossoAgentConfigUri = jossoAgentConfigUri;
    }

    public boolean isReplaceConfig() {
        return replaceConfig;
    }

    public void setReplaceConfig(boolean replaceConfig) {
        this.replaceConfig = replaceConfig;
    }

    public List<ConfigureAgentResource> getReosurces() {
        return reosurces;
    }

}
