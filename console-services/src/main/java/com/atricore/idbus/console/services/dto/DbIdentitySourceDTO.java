/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.dto;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class DbIdentitySourceDTO extends IdentitySourceDTO {

    protected String admin;
    protected String password;
    protected String connectionUrl;

    protected String driverName;

    private String userQueryString;
    private String rolesQueryString;
    private String credentialsQueryString;
    private String userPropertiesQueryString;
    private String resetCredentialDml;
    private String relayCredentialQueryString;
    
    protected ResourceDTO driver;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public ResourceDTO getDriver() {
        return driver;
    }

    public void setDriver(ResourceDTO driver) {
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
