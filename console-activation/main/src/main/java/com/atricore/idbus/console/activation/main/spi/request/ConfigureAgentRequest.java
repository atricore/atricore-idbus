package com.atricore.idbus.console.activation.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ConfigureAgentRequest extends AbstractActivationRequest {

    private boolean replaceConfig = false;
    
    private String jossoAgentConfigUri;

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
}
