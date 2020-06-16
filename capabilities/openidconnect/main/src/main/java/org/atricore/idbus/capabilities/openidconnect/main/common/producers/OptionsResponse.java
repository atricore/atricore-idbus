package org.atricore.idbus.capabilities.openidconnect.main.common.producers;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OptionsResponse implements Serializable {

    private String origin ;

    private ErrorObject error;

    private Set<String> allowedMethods = new HashSet<String>();

    private Set<String> getAllowedHeaders = new HashSet<String>();

    public OptionsResponse(String origin, Set<String> allowedMethods, Set<String> getAllowedHeaders) {
        this.origin = origin;
        this.allowedMethods = allowedMethods;
        this.getAllowedHeaders = getAllowedHeaders;
    }

    public OptionsResponse(ErrorObject error) {
        this.error = error;
    }

    public ErrorObject getError() {
        return error;
    }

    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    public Set<String> getGetAllowedHeaders() {
        return getAllowedHeaders;
    }

    public String getOrigin() {
        return origin;
    }
}
