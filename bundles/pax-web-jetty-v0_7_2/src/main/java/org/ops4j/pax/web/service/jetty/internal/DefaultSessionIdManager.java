package org.ops4j.pax.web.service.jetty.internal;

import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.mortbay.jetty.servlet.HashSessionIdManager;

import javax.servlet.http.HttpServletRequest;

public class DefaultSessionIdManager extends HashSessionIdManager {

    private final static String NEW_SESSION_ID_ATTR = "org.mortbay.jetty.newSessionId";

    private UUIDGenerator sessionUuidGenerator = new UUIDGenerator(true);

    public String newSessionId(HttpServletRequest request, long created) {
        synchronized (this) {
            // A requested session ID can only be used if it is in use already.
            String requestedId = request.getRequestedSessionId();
            if (requestedId != null) {
                String clusterId = getClusterId(requestedId);
                if (idInUse(clusterId))
                    return clusterId;
            }

            // Else reuse any new session ID already defined for this request.
            String newId = (String)request.getAttribute(NEW_SESSION_ID_ATTR);
            if (newId!=null && idInUse(newId))
                return newId;

            // pick a new unique ID!
            String id = sessionUuidGenerator.generateId();
            request.setAttribute(NEW_SESSION_ID_ATTR, id);
            return id;
        }
    }
}
