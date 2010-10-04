package org.atricore.idbus.kernel.common.support.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.ExternalResourcesClassLoader;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

import java.net.URL;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDBCDriverManager implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(JDBCDriverManager.class);

    private BundleContext bundleContext;

    private ExternalResourcesClassLoader driverLoader = null;

    private Map<String, Driver> cachedDriver = new HashMap<String, Driver>();

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

	public Connection getConnection( String driverClass, String url,
			Properties connectionProperties, Collection<String> driverClassPath ) throws JDBCManagerException {
        try {

            if (driverClass == null || "".equals(driverClass))
                throw new JDBCManagerException("Driver class canont be null or empty");

            if (url == null || "".equals(url))
                throw new JDBCManagerException("Connection URL canont be null or empty");

            if ( logger.isTraceEnabled())
                logger.trace( "Request JDBC Connection: driverClass="
                        + ( driverClass == null ? "" : driverClass ) + "; url="
                        + url );

            return doConnect( driverClass, url, connectionProperties, driverClassPath );
        } catch (SQLException e) {
            throw new JDBCManagerException(e);
        }
    }

    /**
     * Implementation of getConnection() methods. Gets connection from either java.sql.DriverManager,
     * or from IConnectionFactory defined in the extension
     */
    protected synchronized Connection doConnect(String driverClass,
                                              String url,
                                              Properties connectionProperties,
                                              Collection<String> driverClassPath)
            throws SQLException, JDBCManagerException {

        // no driverinfo extension for driverClass connectionFactory       
        // no JNDI Data Source URL defined, or
        // not able to get a JNDI data source connection,
        // use the JDBC DriverManager instead to get a JDBC connection
        if (!isRegistered(driverClass))
            registerDriver(driverClass, driverClassPath, false);

        if (logger.isTraceEnabled())
            logger.trace("Calling DriverManager.getConnection. url=" + url);
        try {
            return DriverManager.getConnection(url, connectionProperties);
        } catch (Exception e) {
            throw new JDBCManagerException(e);
        }
    }

    protected boolean isRegistered(String driverClass) {
        return cachedDriver.containsKey(driverClass);
    }


    /**
     * If driver is found in the drivers directory, its class is not accessible
     * in this class's ClassLoader. DriverManager will not allow this class to create
     * connections using such driver. To solve the problem, we create a wrapper Driver in
     * our class loader, and register it with DriverManager
     *
     * @param className
     * @param driverClassPath
     * @param refreshClassLoader
     * @throws JDBCManagerException
     */
    protected void registerDriver(String className, Collection<String> driverClassPath, boolean refreshClassLoader)
            throws JDBCManagerException {

        try {
            Driver driver = findDriver(className, driverClassPath, refreshClassLoader);
            if (driver != null) {
                try {
                    if (logger.isDebugEnabled())
                        logger.debug("Registering with DriverManager: wrapped driver for " + className);

                    DriverManager.registerDriver(new WrappedDriver(driver, className));
                } catch (SQLException e) {
                    // This shouldn't happen
                    logger.error("Failed to register wrapped driver instance: " + e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            throw new JDBCManagerException(e);
        }
    }

    protected Driver findDriver(String className, Collection<String> driverClassPath, boolean refreshClassLoader) throws Exception {
        Class driverClass = null;

        try {

            // 1. Try our standard classloader !
            driverClass = Class.forName(className);
            // Driver class in class path
            if (logger.isDebugEnabled())
                logger.debug("Loaded JDBC driver class in class path: " + className);
        } catch (ClassNotFoundException e) {

            // 2. Try dirver classpath
            if (logger.isDebugEnabled()) {
                logger.debug("Driver class not in class path: "
                        + className
                        + ". Trying to locate driver in drivers directory");
            }

            // Driver not in plugin class path; find it in drivers directory
            driverClass = loadDriver(className, true, refreshClassLoader, driverClassPath);

            // If driver class still cannot be found, try context classloader
            if (driverClass == null) {

                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader != null) {
                    try {
                        driverClass = Class.forName(className, true, loader);
                    } catch (ClassNotFoundException e1) {
                        driverClass = null;
                    }
                }
            }
        }

        if (driverClass == null) {
            logger.error("Failed to load JDBC driver class: " + className);
            throw new JDBCManagerException("Cannot load driver class: " + className);
        }

        Driver driver = null;
        try {
            driver = getDriverInstance(driverClass);
        } catch (Exception e) {
            logger.error("Failed to create new instance of JDBC driver:" + className + ". " + e.getMessage());
            throw new JDBCManagerException("Failed to create new instance of JDBC driver:" + className + ". " + e.getMessage(), e);
        }
        return driver;

    }

    protected Class loadDriver(String className, boolean refreshUrlsWhenFail, boolean refreshClassLoader, Collection<String> driverClassPath) throws Exception {

        assert className != null;

        if (driverLoader == null || refreshClassLoader) {
            driverLoader = bundleContext != null ?
                    new ExternalResourcesClassLoader(bundleContext, driverClassPath) :
                    new ExternalResourcesClassLoader(getClass().getClassLoader(), driverClassPath, null) ;
            driverLoader.refreshClasspath();
        }

        try {
            return driverLoader.loadClass(className);
        } catch (ClassNotFoundException e) {

            //re-scan resources.
            if (refreshUrlsWhenFail && driverLoader.refreshClasspath()) {

                if (logger.isDebugEnabled())
                    logger.debug("Cannot find dirver class, try again after refresh!");
                // New driver not found; try loading again
                return loadDriver(className, false, true, driverClassPath);
            }

            logger.error("ExternalResourcesClassLoader failed to load class: " + className, e);
            logger.error("refreshUrlsWhenFail: " + refreshUrlsWhenFail);
            logger.error("driverClassPath: " + driverClassPath);

            StringBuffer sb = new StringBuffer();
            for (URL url : driverLoader.getURLs()) {
                sb.append("[").append(url).append("]");
            }
            logger.error("Registered URLs: " + sb.toString());


            // no new driver found; give up
            logger.warn("Driver class not found in drivers directory: " + className);
            return null;
        }

    }

    protected Driver getDriverInstance(Class driver) throws Exception {
        String driverName = driver.getName();

        if (!this.cachedDriver.containsKey(driverName)) {

            Driver instance = null;
            try {
                instance = (Driver) driver.newInstance();
                } catch (Exception e) {
                throw new JDBCManagerException(e);
            }
            this.cachedDriver.put(driverName, instance);
        }

        return cachedDriver.get(driverName);
    }


//	The classloader of a driver (jtds driver, etc.) is
//	 "java.net.FactoryURLClassLoader", whose parent is
//	 "sun.misc.Launcher$AppClassLoader".
//	The classloader of class Connection (the caller of
//	 DriverManager.getConnection(url, props)) is
//	 "sun.misc.Launcher$AppClassLoader". As the classes loaded by a child
//	 classloader are always not visible to its parent classloader,
//	 DriverManager.getConnection(url, props), called by class Connection, actually
//	 has no access to driver classes, which are loaded by
//	 "java.net.FactoryURLClassLoader". The invoking of this method would return a
//	 "no suitable driver" exception.
//	On the other hand, if we use class WrappedDriver to wrap drivers. The DriverExt
//	 class is loaded by "sun.misc.Launcher$AppClassLoader", which is same as the
//	 classloader of Connection class. So DriverExt class is visible to
//	 DriverManager.getConnection(url, props). And the invoking of the very method
//	 would success.

    private static class WrappedDriver implements Driver {
        private Driver driver;
        private String driverClass;

        WrappedDriver(Driver d, String driverClass) {
            logger.debug(WrappedDriver.class.getName() + ":WrappedDriver=" + driverClass);
            this.driver = d;
            this.driverClass = driverClass;
        }

        /*
           * @see java.sql.Driver#acceptsURL(java.lang.String)
           */

        public boolean acceptsURL(String u) throws SQLException {
            boolean res = this.driver.acceptsURL(u);
            if (logger.isDebugEnabled())
                logger.debug("WrappedDriver(" + driverClass +
                        ").acceptsURL(" + u + ")returns: " + res);
            return res;
        }

        /*
           * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
           */

        public java.sql.Connection connect(String u, Properties p) throws SQLException {
            if (logger.isDebugEnabled())
                logger.debug(WrappedDriver.class.getName() + ":" + driverClass + ", connect=" + u);

            try {
                return this.driver.connect(u, p);
            } catch (RuntimeException e) {
                throw new SQLException(e.getMessage());
            }
        }

        /*
           * @see java.sql.Driver#getMajorVersion()
           */

        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        /*
           * @see java.sql.Driver#getMinorVersion()
           */

        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        /*
           * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
           */

        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p)
                throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        /*
           * @see java.sql.Driver#jdbcCompliant()
           */

        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        /*
           * @see java.lang.Object#toString()
           */

        public String toString() {
            return driverClass;
		}
	}

}
