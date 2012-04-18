package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.ui.BrandingResource;
import org.atricore.idbus.capabilities.sso.ui.BrandingResourceType;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingEvent;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingEventListener;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingServiceException;

import javax.servlet.jsp.tagext.TryCatchFinally;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBrandingServiceImpl implements WebBrandingService {

    private static final Log logger = LogFactory.getLog(WebBrandingServiceImpl.class);

    private Map<String, WebBranding> brandings = new ConcurrentHashMap<String, WebBranding>();
    
    private Set<WebBrandingEventListener> listeners = new HashSet<WebBrandingEventListener>(); 

    public WebBrandingServiceImpl() {

    }

    public void init() {
        // Leave this to make easy detecting service startup
        logger.info("Web Branding service ACTIVE");
        System.out.println("Web Branding service ACTIVE");
    }

    public WebBranding lookup(String id) {
        return brandings.get(id);
    }

    public void publish(String id, WebBranding branding) throws WebBrandingServiceException {

        brandings.put(id, branding);
        WebBrandingEvent event = new WebBrandingEvent (WebBrandingEvent.PUBLISH, branding.getId());
        for (WebBrandingEventListener listener : listeners) {
            try {
                listener.handleEvent(event);
            } catch (Exception e) {
                logger.error("Error notifying publish to listener " + listener + ", " + e.getMessage(), e);
            }
        }

    }

    public void remove(String id) throws WebBrandingServiceException {
        if (!brandings.containsKey(id))
            throw new WebBrandingServiceException("Branding not found for id ["+id+"]");

        WebBrandingEvent event = new WebBrandingEvent (WebBrandingEvent.REMOVE, id);
        for (WebBrandingEventListener listener : listeners) {
            try {
                listener.handleEvent(event);
            } catch (Exception e) {
                logger.error("Error notifying removal to listener " + listener + ", " + e.getMessage(), e);
            }
        }
        brandings.remove(id);
    }

    public Collection<WebBranding> list() {
        return brandings.values();
    }

    public void register(WebBrandingEventListener listener) {
        listeners.add(listener);
    }

    public void unregister(WebBrandingEventListener listener) {
        listeners.remove(listener);
    }
}
