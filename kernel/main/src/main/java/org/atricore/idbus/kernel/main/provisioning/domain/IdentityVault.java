package org.atricore.idbus.kernel.main.provisioning.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityVault {

    private static final long serialVersionUID = -2547286314596218707L;

    private long id;

    private String name;

    private String description;

    private String host;

    private int port;

    private String username;

    private String password;

    private List<IdentityPartition> partitions = new ArrayList<IdentityPartition>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public List<IdentityPartition> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<IdentityPartition> partitions) {
        this.partitions = partitions;
    }
}
