package org.atricore.idbus.capabilities.sso.ui.internal;

import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpringWebBrandingRegistry implements ApplicationContextAware {
    
    private ApplicationContext appCtx;
    
    private WebBrandingRegistry registry;

    public SpringWebBrandingRegistry(WebBrandingRegistry registry) {
        this.registry = registry;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
        
        Map<String, WebBranding> brandings = appCtx.getBeansOfType(WebBranding.class);
        for (WebBranding b : brandings.values()) {
            registry.register(b);
        }
    }
}
