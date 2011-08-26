package org.atricore.idbus.capabilities.openid.main.messaging;

import java.util.HashMap;
import java.util.Map;

public class SubmitOpenIDV2AuthnRequest extends OpenIDMessage {

    private String opEndpoint;
    private Map parameterMap = new HashMap();

    public SubmitOpenIDV2AuthnRequest(String version, String opEndpoint, Map parameterMap) {
        super(version);

        this.opEndpoint = opEndpoint;
        this.parameterMap.putAll(parameterMap);
    }

    public String getOpEndpoint() {
        return opEndpoint;
    }

    public Map getParameterMap() {
        return parameterMap;
    }
}
