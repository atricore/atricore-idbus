package org.atricore.idbus.kernel.common.support.osgi;

import org.springframework.osgi.context.event.*;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IDBusBundleApplicationContextListener implements OsgiBundleApplicationContextListener {

    private static Log logger = LogFactory.getLog(IDBusBundleApplicationContextListener.class );

    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {

        if (event instanceof OsgiBundleContextRefreshedEvent) {
            OsgiBundleContextRefreshedEvent e = (OsgiBundleContextRefreshedEvent) event;
            logger.debug("Spring Application context in Bundle (" + e.getBundle().getBundleId() + ") " + e.getBundle().getSymbolicName() + " : REFRESHED/STARTED");

        } else if (event instanceof OsgiBundleContextFailedEvent) {

            OsgiBundleContextFailedEvent e = (OsgiBundleContextFailedEvent) event;
            logger.debug("Spring Application context in Bundle (" + e.getBundle().getBundleId() + ") " + e.getBundle().getSymbolicName() + " : FAILED");

        } else if (event instanceof OsgiBundleContextClosedEvent) {

            OsgiBundleContextClosedEvent e = (OsgiBundleContextClosedEvent) event;
            logger.debug("Spring Application context in Bundle (" + e.getBundle().getBundleId() + ") " + e.getBundle().getSymbolicName() + " : CLOSED");

        }

        //event.
        
    }
}
