package com.atricore.idbus.console.lifecycle.main.boot;

import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.event.*;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServicesLifecycleManager implements OsgiBundleApplicationContextListener, InitializingBean {

    private static Log logger = LogFactory.getLog(ServicesLifecycleManager.class );

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
            Map<String, IdentityApplianceManagementService> svcs =
                    ctx.getBeansOfType(IdentityApplianceManagementService.class);

            for (String beanId : svcs.keySet()) {

                IdentityApplianceManagementService svc = svcs.get(beanId);
                try {
                    logger.info("Booting service " + beanId +
                            " [" + e.getBundle().getBundleId() + ":"+e.getBundle().getLocation()+"]");
                    
                    svc.boot();
                } catch (IdentityServerException e1) {
                    logger.error("Cannot boot service : " + beanId +
                            " [" + e.getBundle().getBundleId() + ":"+e.getBundle().getLocation()+"] : "
                            + e1.getMessage(), e1);
                }
            }
                
        } else if (event instanceof OsgiBundleContextFailedEvent) {

            OsgiBundleContextFailedEvent e = (OsgiBundleContextFailedEvent) event;
            if (logger.isDebugEnabled())
                logger.debug("Spring Application context in Bundle (" +
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
