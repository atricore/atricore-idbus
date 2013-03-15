package org.atricore.idbus.kernel.common.support.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.event.*;

import java.util.Map;

/**
 * This provides a post Spring application context initialization bootstrapping for services.
 * Any component implementing IdentityServiceLifecycle will be initialized after the application context
 * declaring it is started.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityServiceBootstrap implements OsgiBundleApplicationContextListener, InitializingBean {

    private static Log logger = LogFactory.getLog(IdentityServiceBootstrap.class );

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() throws Exception {

    }

    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {

        if (event instanceof OsgiBundleContextRefreshedEvent) {

            OsgiBundleContextRefreshedEvent e = (OsgiBundleContextRefreshedEvent) event;

            if (logger.isDebugEnabled())
                logger.debug("Spring Application context in Bundle (" +
                    e.getBundle().getBundleId() + ") " + e.getBundle().getSymbolicName() + " : REFRESHED/STARTED");

            ApplicationContext ctx = e.getApplicationContext();
            Map<String, IdentityServiceLifecycle> svcs =
                    ctx.getBeansOfType(IdentityServiceLifecycle.class);

            for (String beanId : svcs.keySet()) {

                IdentityServiceLifecycle svc = svcs.get(beanId);
                try {
                    logger.info("Bootstrap service " + beanId +
                            " [" + e.getBundle().getBundleId() + ":"+e.getBundle().getSymbolicName()+"]");
                    
                    svc.boot();
                } catch (Exception e1) {
                    logger.error("Bootstrap service failure " + beanId +
                            " [" + e.getBundle().getBundleId() + ":"+e.getBundle().getSymbolicName()+"]"
                            + e1.getMessage(), e1);
                }
            }
                
        } else if (event instanceof OsgiBundleContextFailedEvent) {

            OsgiBundleContextFailedEvent e = (OsgiBundleContextFailedEvent) event;
            logger.error("Spring Application context in Bundle (" +
                e.getBundle().getBundleId() + ") " + e.getBundle().getSymbolicName() + " : FAILED");

        } else if (event instanceof OsgiBundleContextClosedEvent) {

            OsgiBundleContextClosedEvent e = (OsgiBundleContextClosedEvent) event;
            if (logger.isDebugEnabled())
                logger.debug("Spring Application context in Bundle (" +
                    e.getBundle().getBundleId() + ") " + e.getBundle().getSymbolicName() + " : CLOSED");

        }  else {

            if (logger.isDebugEnabled())
                logger.debug("Spring Application context in Bundle (" +
                    event.getBundle().getBundleId() + ") " + event.getBundle().getSymbolicName() + " : <UNKNOWN>" +
                    event.getClass().getSimpleName());

        }

        //event.

    }
}
