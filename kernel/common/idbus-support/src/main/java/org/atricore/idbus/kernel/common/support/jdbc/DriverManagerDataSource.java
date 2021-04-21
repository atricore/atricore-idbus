package org.atricore.idbus.kernel.common.support.jdbc;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverManagerDataSource implements DataSource
{
    /** Name of the database driver. */
    private final String driverName;

    private final Collection<String> driverClassPath;

    /** URL for the database. */
    private final String url;

    /** ClassLoader resolver to use for class loading */
    private final JDBCDriverManager mgr;

    /** the user name **/
    private final String userName;

    /** the password **/
    private final String password ;

    /** connection properties **/
    private final Properties props;

    /**
     * Constructor.
     * @param driverName Class name of the JDBC driver.
     * @param url URL of the data source.
     * @param mgr JDBCDriverManager to use for loading issues
     **/
    public DriverManagerDataSource(String driverName, String url, String userName, String password, JDBCDriverManager mgr)
    {
        this.driverClassPath = null;
        this.props = null;
        this.driverName = driverName;
        this.url = url;
        this.mgr = mgr;
        this.userName = userName;
        this.password = password;

        if (driverName != null)
        {
            try
            {
                //preferable to use ClassLoaderResolver
                //because the driver may be loaded by another loader
                //then the loader used by Class.forName (callerClassloader)
                DriverDescriptor dd = mgr.getConfiguredDriver(driverName);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Constructor.
     * @param driverName Class name of the JDBC driver.
     * @param url URL of the data source.
     * @param mgr JDBCDriverManager to use for loading issues
     **/
    public DriverManagerDataSource(String driverName, String url, Properties props, Collection<String> driverClassPath, JDBCDriverManager mgr)
    {
        this.driverClassPath = driverClassPath;
        this.driverName = driverName;
        this.url = url;
        this.mgr = mgr;
        this.props = props;
        this.userName = null;
        this.password = null;

        if (driverName != null)
        {
            try
            {
                //preferable to use ClassLoaderResolver
                //because the driver may be loaded by another loader
                //then the loader used by Class.forName (callerClassloader)
                DriverDescriptor dd = mgr.getConfiguredDriver(driverName);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * Accessor for a JDBC connection for this data source.
     * @return The connection
     * @throws java.sql.SQLException Thrown when an error occurs obtaining the connection.
     */
    public Connection getConnection()
    throws SQLException
    {
        if (props != null) {
            try {
                return mgr.getConnection(driverName, url, props, driverClassPath);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        return getConnection(this.userName, this.password);
    }

    /**
     * Accessor for a JDBC connection for this data source, specifying username and password.
     * @param userName User name for the data source (this user name is ignored)
     * @param password Password for the data source (this password is ignored)
     * @return The connection
     * @throws SQLException Thrown when an error occurs obtaining the connection.
     */
    public Connection getConnection(String userName, String password)
    throws SQLException
    {
        try
        {
            Properties info = props != null ? props : new Properties();
            if( userName != null && !"".equals(userName))
            {
                info.put("user", this.userName);
            }
            if( password != null && !"".equals(password))
            {
                info.put("password", this.password);
            }

            return mgr.getConnection(driverName, url, info, driverClassPath);

        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Accessor for the LogWriter of the driver manager.
     * @return The Log Writer
     */
    public PrintWriter getLogWriter()
    {
        return DriverManager.getLogWriter();
    }

    /**
     * Mutator for the LogWriter of the driver manager.
     * @param out The Log Writer
     */
    public void setLogWriter(PrintWriter out)
    {
        DriverManager.setLogWriter(out);
    }

    /**
     * Accessor for the Login timeout for the driver manager.
     * @return The login timeout (seconds)
     */
    public int getLoginTimeout()
    {
        return DriverManager.getLoginTimeout();
    }

    /**
     * Mutator for the Login timeout for the driver manager.
     * @param seconds The login timeout (seconds)
     */
    public void setLoginTimeout(int seconds)
    {
        DriverManager.setLoginTimeout(seconds);
    }

    /**
     * Equality operator.
     * @param obj The object to compare against.
     * @return Whether the objects are equal.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof DriverManagerDataSource))
        {
            return false;
        }

        DriverManagerDataSource dmds = (DriverManagerDataSource) obj;
        if (driverName == null)
        {
            if (dmds.driverName != null)
            {
                return false;
            }
        }
        else if (!driverName.equals(dmds.driverName))
        {
            return false;
        }

        if (url == null)
        {
            if (dmds.url != null)
            {
                return false;
            }
        }
        else if (!url.equals(dmds.url))
        {
            return false;
        }

        return true;
    }

    /**
     * Hashcode operator.
     * @return The Hashcode for this object.
     */
    public int hashCode()
    {
        return (driverName == null ? 0 : driverName.hashCode()) ^ (url == null ? 0 : url.hashCode());
    }

    // Implementation of JDBC 4.0's Wrapper interface

    public Object unwrap(Class iface) throws SQLException
    {
        if (!DataSource.class.equals(iface))
        {
            throw new SQLException("DataSource of type [" + getClass().getName() +
                   "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName() + "]");
        }
        return this;
    }

    public boolean isWrapperFor(Class iface) throws SQLException
    {
        return DataSource.class.equals(iface);
    }


}
