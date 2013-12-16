package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DbIdentityVault extends IdentityVault {

    private static final long serialVersionUID = 5566249322155917763L;

    private String username;

    private String password;

    private boolean externalDB;

    private String driverName;

    private String connectionUrl;

    public boolean isExternalDB() {
        return externalDB;
    }

    public void setExternalDB(boolean externalDB) {
        this.externalDB = externalDB;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
