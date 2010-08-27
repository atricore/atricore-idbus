package org.atricore.idbus.bundles.datanucleus.core;

import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;
import org.springframework.osgi.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundleClassLoader;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.JDOHelper;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiJDOPersistenceManagerFactoryBean
        extends LocalPersistenceManagerFactoryBean
        implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(OsgiJDOPersistenceManagerFactoryBean.class);

    private BundleContext bundleContext;

    @Override
    protected PersistenceManagerFactory newPersistenceManagerFactory(Map props) {
        if (logger.isDebugEnabled())
            logger.debug("Creating new PersistenceManagerFactory based on properties");

        /* TODO : DO NOT USE THE ENTIRE BUNDLESPACE, try local bundle classloader instead  */
        ClassLoader osgiCl = new OsgiBundlespaceClassLoader(bundleContext,
                new OsgiBundleClassLoader(bundleContext.getBundle()),
                bundleContext.getBundle());

        // TODO : This does not work well ... check it
        // ClassLoader osgiCl = new OsgiBundleClassLoader(bundleContext.getBundle());
        if (props.get("datanucleus.primaryClassLoader") == null) {

            if (logger.isDebugEnabled())
                logger.debug("Setting primary Classloader!");
            props.put("datanucleus.primaryClassLoader", osgiCl);
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Replacing primary Classloader!");
            props.put("datanucleus.primaryClassLoader", osgiCl);

        }

        return JDOHelper.getPersistenceManagerFactory(props, osgiCl);
    }

    @Override
    protected PersistenceManagerFactory newPersistenceManagerFactory(String name) {

        /* DO NOT USE THE ENTIRE BUNDLESPACE, instead embed datanucleus in your own bundle.
        ClassLoader osgiCl = new OsgiBundlespaceClassLoader(bundleContext,
                new OsgiBundleClassLoader(bundleContext.getBundle()),
                bundleContext.getBundle());
        */
        ClassLoader osgiCl = new OsgiBundleClassLoader(bundleContext.getBundle()); 
        if (logger.isDebugEnabled())
            logger.debug("Creating new PersistenceManagerFactory based on name");

        return JDOHelper.getPersistenceManagerFactory(name, osgiCl);
    }

    public void setBundleContext(BundleContext bundleContext) {
        if (logger.isDebugEnabled())
            logger.debug("Recieved BundleContext " + bundleContext);

        this.bundleContext = bundleContext;
        setBeanClassLoader(Thread.currentThread().getContextClassLoader());
    }
}
