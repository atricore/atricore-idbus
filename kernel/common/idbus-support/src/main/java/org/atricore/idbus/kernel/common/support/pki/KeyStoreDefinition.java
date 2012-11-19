package org.atricore.idbus.kernel.common.support.pki;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/14/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeyStoreDefinition {

    private String id;

    private String description;

    private String location;

    private String type;

    private String password;

    public KeyStoreDefinition(String id, String description, String location, String passphrase) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.password = passphrase;
        this.type = "JKS";
    }

    public KeyStoreDefinition(String id, String description, String location, String passphrase, String type) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.password = passphrase;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
