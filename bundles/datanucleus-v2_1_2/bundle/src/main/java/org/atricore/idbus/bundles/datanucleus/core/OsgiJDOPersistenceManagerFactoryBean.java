package org.atricore.idbus.bundles.datanucleus.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundleClassLoader;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;
import org.springframework.osgi.context.BundleContextAware;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiJDOPersistenceManagerFactoryBean
        extends LocalPersistenceManagerFactoryBean
        implements BundleContextAware {

    private static final String JNDI = "jndi:";
    private static final String OSGI = "osgi:";

    private static final Log logger = LogFactory.getLog(OsgiJDOPersistenceManagerFactoryBean.class);

    private BundleContext bundleContext;

    private String dataSourceUrl;

    private DataSource dataSource;

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
        try {
            if (dataSource == null) {
                if (dataSourceUrl == null)
                    dataSourceUrl = (String) props.get("jdbc.dataSourceUrl");

                dataSource = (DataSource) createDatasource(dataSourceUrl);

            }

            props.put("datanucleus.ConnectionFactory", dataSource);

        } catch (Exception e) {
            logger.error("Cannot locate DS for " + dataSourceUrl, e);
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

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Looks up a datasource from the url. The datasource can be passed either as jndi name or osgi ldap filter.
     * @param url
     * @return
     * @throws Exception
     */
    public Object createDatasource(String url) throws Exception {
        if (url == null) {
            throw new Exception("Illegal datasource url format. Datasource URL cannot be null.");
        } else if (url.trim().length() == 0) {
            throw new Exception("Illegal datasource url format. Datasource URL cannot be empty.");
        } else if (url.startsWith(JNDI)) {
            String jndiName = url.substring(JNDI.length());
            InitialContext ic = new InitialContext();
            Object ds =  ic.lookup(jndiName);
            return ds;
        } else if (url.startsWith(OSGI)) {
            String osgiFilter = url.substring(OSGI.length());
            String clazz = null;
            String filter = null;
            String[] tokens = osgiFilter.split("/", 2);
            if (tokens != null) {
                if (tokens.length > 0) {
                    clazz = tokens[0];
                }
                if (tokens.length > 1) {
                    filter = tokens[1];
                }
            }
            ServiceReference[] references = bundleContext.getServiceReferences(clazz, filter);
            if (references != null) {
                ServiceReference ref = references[0];
                Object ds = bundleContext.getService(ref);
                bundleContext.ungetService(ref);
                return ds;
            } else {
                throw new Exception("Unable to find service reference for datasource: " + clazz + "/" + filter);
            }
        } else {
            throw new Exception("Illegal datasource url format");
        }
    }
}
