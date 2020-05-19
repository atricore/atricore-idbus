package org.atricore.idbus.idojos.dbidentitystore;

import org.atricore.idbus.kernel.common.support.jdbc.JDBCDriverManager;
import org.atricore.idbus.kernel.common.support.jdbc.JDBCManagerException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DynamicJDBCIdentityStore extends AbstractDBIdentityStore {

    private JDBCDriverManager manager;

    private DataSource dataSource;


    private boolean pooledDatasource = false;
    private int acquireIncrement = 3;
    private int initialPoolSize = 3;
    private int minPoolSize = 3;
    private int maxPoolSize = 15;
    private int idleConnectionTestPeriod = 0;
    private int maxIdleTime = 0;


    /**
     * The connection username to use when trying to connect to the database.
     */
    protected String connectionUser = null;

    /**
     * The connection URL to use when trying to connect to the database.
     */
    protected String connectionPassword = null;


    /**
     * The connection URL to use when trying to connect to the database.
     */
    protected String connectionURL = null;

    protected List<String> classPath;

    /**
     * The JDBC driver to use.
     */
    protected String driverName = null;


    public String getConnectionUser() {
        return connectionUser;
    }

    public void setConnectionUser(String connectionUser) {
        this.connectionUser = connectionUser;
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public List<String> getClassPath() {
        return classPath;
    }

    public boolean isPooledDatasource() {
        return pooledDatasource;
    }

    public void setPooledDatasource(boolean pooledDatasource) {
        this.pooledDatasource = pooledDatasource;
    }

    public int getAcquireIncrement() {        return acquireIncrement;
    }

    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getIdleConnectionTestPeriod() {
        return idleConnectionTestPeriod;
    }

    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public void setClassPath(List<String> classPath) {
        this.classPath = classPath;
    }

    public JDBCDriverManager getManager() {
        return manager;
    }

    public void setManager(JDBCDriverManager manager) {
        this.manager = manager;
    }

    @Override
    public Connection getDBConnection() throws SSOIdentityException {

        // Open a new connection
        Properties props = new Properties();
        if (connectionUser != null && !"".equals(connectionUser))
            props.put("user", connectionUser);

        if (connectionPassword != null && !"".equals(connectionPassword))
            props.put("password", connectionPassword);

        try {

            if (dataSource == null) {

                DataSource ds = manager.getDataSource(driverName, connectionURL, props, classPath);

                if (pooledDatasource) {
                    Map overrideProps = new java.util.HashMap();
                    overrideProps.put("acquireIncrement", acquireIncrement);
                    overrideProps.put("initialPoolSize", initialPoolSize);
                    overrideProps.put("minPoolSize", minPoolSize);
                    overrideProps.put("maxPoolSize", maxPoolSize);
                    overrideProps.put("idleConnectionTestPeriod", idleConnectionTestPeriod);
                    overrideProps.put("maxIdleTime", maxIdleTime);

                    ds = manager.getPooledDataSource(ds, overrideProps);
                }

                this.dataSource = ds;
            }

            return dataSource.getConnection();

        } catch (JDBCManagerException e) {
            throw new SSOIdentityException(e);
        } catch (SQLException e) {
            throw new SSOIdentityException(e);
        }
    }

}
