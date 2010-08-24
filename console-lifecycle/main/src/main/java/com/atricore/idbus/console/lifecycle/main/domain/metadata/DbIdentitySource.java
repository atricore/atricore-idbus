package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class DbIdentitySource extends IdentitySource {

    // TODO : JDBCU/Datasource setup

    protected String admin;
    protected String password;
    //TODO
    protected String connectionUrl;
    protected int port;
    protected String schema;
    protected String connectionName;
    
    protected String driverName;

    private String userQueryString;
    private String rolesQueryString;
    private String credentialsQueryString;
    private String userPropertiesQueryString;
    private String resetCredentialDml;
    private String relayCredentialQueryString;

    protected Resource driver;
    private static final long serialVersionUID = 952431562576391535L;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Resource getDriver() {
        return driver;
    }

    public void setDriver(Resource driver) {
        this.driver = driver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUserQueryString() {
        return userQueryString;
    }

    public void setUserQueryString(String userQueryString) {
        this.userQueryString = userQueryString;
    }

    public String getRolesQueryString() {
        return rolesQueryString;
    }

    public void setRolesQueryString(String rolesQueryString) {
        this.rolesQueryString = rolesQueryString;
    }

    public String getCredentialsQueryString() {
        return credentialsQueryString;
    }

    public void setCredentialsQueryString(String credentialsQueryString) {
        this.credentialsQueryString = credentialsQueryString;
    }

    public String getUserPropertiesQueryString() {
        return userPropertiesQueryString;
    }

    public void setUserPropertiesQueryString(String userPropertiesQueryString) {
        this.userPropertiesQueryString = userPropertiesQueryString;
    }

    public String getResetCredentialDml() {
        return resetCredentialDml;
    }

    public void setResetCredentialDml(String resetCredentialDml) {
        this.resetCredentialDml = resetCredentialDml;
    }

    public String getRelayCredentialQueryString() {
        return relayCredentialQueryString;
    }

    public void setRelayCredentialQueryString(String relayCredentialQueryString) {
        this.relayCredentialQueryString = relayCredentialQueryString;
    }
}
