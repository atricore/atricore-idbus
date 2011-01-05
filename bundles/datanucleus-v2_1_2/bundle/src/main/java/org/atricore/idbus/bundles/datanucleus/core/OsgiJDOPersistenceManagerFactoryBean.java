package org.atricore.idbus.bundles.datanucleus.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundleClassLoader;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.osgi.framework.BundleContext;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;
import org.springframework.osgi.context.BundleContextAware;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
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

        ClassLoader osgiCl = new OsgiBundleClassLoader(bundleContext.getBundle());

        if (props.get("datanucleus.primaryClassLoader") == null) {
            if (logger.isDebugEnabled())
                logger.debug("Setting primary Classloader to Osgi Bundle classloader for " +
                        bundleContext.getBundle().getLocation());

            props.put("datanucleus.primaryClassLoader", osgiCl);
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Replacing primary Classloader to Osgi Bundle classloader for " +
                        bundleContext.getBundle().getLocation());
            props.put("datanucleus.primaryClassLoader", osgiCl);

        }

        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props, osgiCl);
        if (logger.isDebugEnabled())
            logger.debug("Using PMF ("+pmf.getName()+") " + pmf);

        return pmf;
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
            logger.debug("Creating new PersistenceManagerFactory based on name for " +
                        bundleContext.getBundle().getLocation());

        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(name, osgiCl);
        if (logger.isDebugEnabled())
            logger.debug("Using PMF ("+pmf.getName()+")" + pmf);
        
        return pmf;
    }

    public void setBundleContext(BundleContext bundleContext) {
        if (logger.isDebugEnabled())
            logger.debug("Recieved BundleContext " + bundleContext);

        this.bundleContext = bundleContext;
        setBeanClassLoader(Thread.currentThread().getContextClassLoader());
    }
}
