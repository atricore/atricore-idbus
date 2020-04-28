package org.atricore.idbus.idojos.dbidentitystore;

import org.atricore.idbus.kernel.common.support.jdbc.JDBCDriverManager;
import org.atricore.idbus.kernel.common.support.jdbc.JDBCManagerException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DynamicJDBCIdentityStore extends AbstractDBIdentityStore {

    private JDBCDriverManager manager;

    private DataSource dataSource;

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
                dataSource = manager.getDataSource(driverName, connectionURL, props, classPath);
            }

            return dataSource.getConnection();

        } catch (JDBCManagerException e) {
            throw new SSOIdentityException(e);
        } catch (SQLException e) {
            throw new SSOIdentityException(e);
        }
    }

}
