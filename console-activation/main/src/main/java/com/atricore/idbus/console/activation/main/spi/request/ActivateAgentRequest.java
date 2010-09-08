package com.atricore.idbus.console.activation.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivateAgentRequest extends AbstractActivationRequest {

    private String jossoAgentConfigUri;

    public String getJossoAgentConfigUri() {
        return jossoAgentConfigUri;
    }

    public void setJossoAgentConfigUri(String jossoAgentConfigUri) {
        this.jossoAgentConfigUri = jossoAgentConfigUri;
    }
}
