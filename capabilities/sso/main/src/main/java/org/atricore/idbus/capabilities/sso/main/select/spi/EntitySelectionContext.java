package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EntitySelectionContext {

    private Map<String, String> attributes;

    private Map<String, String> reqAttributes;

    private SelectEntityRequestType request;

    private CircleOfTrustManager cotManager;

    public EntitySelectionContext(CircleOfTrustManager cotManager, Map<String, String> attributes, SelectEntityRequestType request) {
        this.attributes = attributes;
        this.request = request;
        this.cotManager = cotManager;
    }

    public String getAttribute(String name) {
        if (attributes == null)
            return null;
        return attributes.get(name);
    }

    public Collection<String> getAttributeNames() {
        if (attributes == null)
            return Collections.emptyList();

        return attributes.keySet();
    }

    public SelectEntityRequestType getRequest() {
        return request;
    }

    public String getRequestAttribute(String name) {
        if (reqAttributes == null) {

            reqAttributes = new HashMap<String, String>();
            for (RequestAttributeType attr : request.getRequestAttribute()) {
                reqAttributes.put(attr.getName(), attr.getValue());
            }
        }
        return reqAttributes.get(name);
    }

    public CircleOfTrustManager getCotManager() {
        return cotManager;
    }
}
