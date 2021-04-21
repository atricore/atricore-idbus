package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.json.GenericJson;

public class LinkedInUser extends GenericJson {

    @com.google.api.client.util.Key
    private String id;

    @com.google.api.client.util.Key
    private String firstName;

    @com.google.api.client.util.Key
    private String lastName;

    @com.google.api.client.util.Key
    private String emailAddress;

    @com.google.api.client.util.Key
    private String pictureUrl;

    @com.google.api.client.util.Key
    private String publicProfileUrl;

    @com.google.api.client.util.Key
    private int numConnections;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPublicProfileUrl() {
        return publicProfileUrl;
    }

    public void setPublicProfileUrl(String publicProfileUrl) {
        this.publicProfileUrl = publicProfileUrl;
    }

    public int getNumConnections() {
        return numConnections;
    }

    public void setNumConnections(int numConnections) {
        this.numConnections = numConnections;
    }
}
