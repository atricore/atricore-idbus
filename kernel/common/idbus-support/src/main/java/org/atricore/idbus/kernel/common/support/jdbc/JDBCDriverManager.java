package org.atricore.idbus.kernel.common.support.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.ExternalResourcesClassLoader;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDBCDriverManager implements BundleContextAware, InitializingBean {

    private static final Log logger = LogFactory.getLog(JDBCDriverManager.class);

    private BundleContext bundleContext;

    private ExternalResourcesClassLoader defaultDriverLoader = null;

    private List<DriverDescriptor> configuredDrivers = new ArrayList<DriverDescriptor>();

    private Map<String, Driver> cachedDrivers = new HashMap<String, Driver>();

    private List<String> defaultDriversUrls;

    private boolean loadDefaultDrivers;

    private static long requestedConnections;

    public List<String> getDefaultDriversUrls() {
        return defaultDriversUrls;
    }

    public void setDefaultDriversUrls(List<String> defaultDriversUrls) {
        this.defaultDriversUrls = defaultDriversUrls;
    }

    public boolean isLoadDefaultDrivers() {
        return loadDefaultDrivers;
    }

    public void setLoadDefaultDrivers(boolean loadDefaultDrivers) {
        this.loadDefaultDrivers = loadDefaultDrivers;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void afterPropertiesSet() throws Exception {

        if (defaultDriversUrls == null || defaultDriversUrls.size() == 0) {
            defaultDriversUrls = new ArrayList<String>();

            // Build default URL according to platform ...

            String karafBase = System.getProperty("karaf.base");

            // Add starting slash if not present, required in Windows.
            if (!karafBase.startsWith("/"))
                karafBase = "/" + karafBase;

            // Replace Windows file separator, if any
            karafBase = karafBase.replaceAll("\\\\", "/");

            // Add default drivers URL
            String defaultUrl = "file://" + karafBase + "/lib/jdbc";

            defaultDriversUrls.add(defaultUrl);
        }

        if (loadDefaultDrivers) {

            StringBuffer sb = new StringBuffer();
            for (String url : defaultDriversUrls) {
                sb.append(":");
                sb.append(url);
            }
            logger.info("Loading default drivers from " + sb);

            boolean refresh = true;
            for (DriverDescriptor ds : configuredDrivers) {
                if (!isRegistered(ds)) {
                    try {
                        registerDriver(ds, refresh);
                        refresh = false;
                        logger.info(ds.getName() + "JDBC Driver found for " + ds.getDriverclassName());
                    } catch (Exception e) {
                        logger.info(ds.getName() + "JDBC Driver not found for " + ds.getDriverclassName());
                    }
                }
            }
        }
    }

    protected ExternalResourcesClassLoader getDefaultDriverLoader() throws Exception {
        if (defaultDriverLoader == null) {
            defaultDriverLoader  = doMakeDriverLoader(defaultDriversUrls);
            defaultDriverLoader.refreshClasspath();
        }

        return defaultDriverLoader;
    }

    protected ExternalResourcesClassLoader doMakeDriverLoader(Collection<String> classPath) {

        FileFilter filter = new FileFilter( ) {
            public boolean accept( File pathname ) {
                return pathname.isFile()
                        && isDriverFile(pathname.getName());
            }
        };

        return bundleContext != null ?
            new ExternalResourcesClassLoader(bundleContext, classPath, filter) :
            new ExternalResourcesClassLoader(getClass().getClassLoader(), classPath, filter) ;

    }

    public List<DriverDescriptor> getConfiguredDrivers() {
        return configuredDrivers;
    }

    public List<DriverDescriptor> getRegisteredDrivers() {

        List<DriverDescriptor> registered = new ArrayList<DriverDescriptor>();
        for (String driverClass : cachedDrivers.keySet()) {
            registered.add(getConfiguredDriver(driverClass));
        }
        return registered;
    }

    public void setConfiguredDrivers(List<DriverDescriptor> configuredDrivers) {
        this.configuredDrivers = configuredDrivers;
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
        DriverDescriptor ds = getConfiguredDriver(driverClass);
        if (ds == null) {

            List<String> classPath = new ArrayList<String>();
            classPath.addAll(driverClassPath);

            ds = new DriverDescriptor();

            ds.setName("Dynamically added driver : " + driverClass);
            ds.setDriverclassName(driverClass);
            ds.setJarFileNames(classPath);
            ds.setUrl(url);

            this.configuredDrivers.add(ds);
        }

        if (!isRegistered(ds)) {
            registerDriver(ds, false);
        }

        if (logger.isTraceEnabled())
            logger.trace("Calling DriverManager.getConnection. url=" + url);
        try {
            return DriverManager.getConnection(url, connectionProperties);
        } catch (Exception e) {
            throw new JDBCManagerException(e);
        }
    }

    protected DriverDescriptor getConfiguredDriver(String driverClass) {
        for (DriverDescriptor ds : configuredDrivers) {
            if (ds.getDriverclassName().equals(driverClass))
                return ds;
        }

        return null;
    }


    protected boolean isRegistered(DriverDescriptor ds) {
        return cachedDrivers.get(ds.getDriverclassName()) != null ;
    }



    /**
     * If driver is found in the drivers directory, its class is not accessible
     * in this class's ClassLoader. DriverManager will not allow this class to create
     * connections using such driver. To solve the problem, we create a wrapper Driver in
     * our class loader, and registration it with DriverManager
     *
     * @param driverDescriptor
     * @param refreshClassLoader
     * @throws JDBCManagerException
     */
    protected void registerDriver(DriverDescriptor driverDescriptor, boolean refreshClassLoader)
            throws JDBCManagerException {

        try {
            Driver driver = findDriver(driverDescriptor,  refreshClassLoader);
            if (driver != null) {
                try {
                    if (logger.isDebugEnabled())
                        logger.debug("Registering with DriverManager: wrapped driver for " + driverDescriptor.getDriverclassName());

                    DriverManager.registerDriver(new WrappedDriver(driver, driverDescriptor.getDriverclassName()));
                } catch (SQLException e) {
                    // This shouldn't happen
                    logger.error("Failed to registration wrapped driver instance: " + e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            throw new JDBCManagerException(e);
        }
    }

    protected Driver findDriver(DriverDescriptor driverDescriptor, boolean refreshClassLoader) throws Exception {

        String className = driverDescriptor.getDriverclassName();
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
            driverClass = loadDriver(driverDescriptor, true, refreshClassLoader);

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

    protected Class loadDriver(DriverDescriptor driverDescriptor, boolean refreshUrlsWhenFail, boolean refreshClassLoader) throws Exception {

        String className = driverDescriptor.getDriverclassName();
        assert className != null;

        ExternalResourcesClassLoader driverLoader = driverDescriptor.getDriverLoader();
        if (driverLoader == null || refreshClassLoader) {

            if (logger.isDebugEnabled())
                logger.debug("No loader configured for driver " + className);

            // No driver loader configured, use default or create one with provided jar file names
            if (driverDescriptor.getJarFileNames() != null &&  driverDescriptor.getJarFileNames().size() > 0) {

                if (logger.isDebugEnabled()) {
                    StringBuffer sb = new StringBuffer();
                    for (String jarFile : driverDescriptor.getJarFileNames()) {
                        sb.append(":").append(jarFile);
                    }
                    logger.debug("No loader configured for driver " + className + ", using class path " + sb.toString());
                }

                driverLoader = doMakeDriverLoader(driverDescriptor.getJarFileNames());
                driverLoader.refreshClasspath();
            } else {

                if (logger.isDebugEnabled())
                    logger.debug("No loader configured for driver " + className + ", using default loader");

                driverLoader = getDefaultDriverLoader();
                if (refreshClassLoader)
                    driverLoader.refreshClasspath();
            }

            driverDescriptor.setDriverLoader(driverLoader);

        }

        try {
            return driverLoader.loadClass(className);
        } catch (ClassNotFoundException e) {

            //re-scan resources.
            if (refreshUrlsWhenFail && defaultDriverLoader.refreshClasspath()) {

                if (logger.isDebugEnabled())
                    logger.debug("Cannot find dirver class, try again after refresh!");
                // New driver not found; try loading again
                return loadDriver(driverDescriptor, false, true);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("ExternalResourcesClassLoader failed to load class " + className + " : " + e.getMessage());
                logger.debug("refreshUrlsWhenFail: " + refreshUrlsWhenFail);
                //logger.error("driverClassPath: " + );

                StringBuffer sb = new StringBuffer();
                for (URL url : driverLoader.getURLs()) {
                    sb.append("[").append(url).append("]");
                }
                logger.debug("Registered URLs: " + sb.toString());

            }

            // no new driver found; give up
            logger.info("Driver class not found in drivers directory: " + className);
            return null;
        }

    }

    protected Driver getDriverInstance(Class driver) throws Exception {
        String driverName = driver.getName();

        if (!this.cachedDrivers.containsKey(driverName)) {

            Driver instance = null;
            try {
                instance = (Driver) driver.newInstance();
                } catch (Exception e) {
                throw new JDBCManagerException(e);
            }
            this.cachedDrivers.put(driverName, instance);
        }

        return cachedDrivers.get(driverName);
    }

    protected boolean isDriverFile(String fileName) {
        String lcName = fileName.toLowerCase();
        return lcName.endsWith(".jar") || lcName.endsWith(".zip");
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

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException();
        }

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
