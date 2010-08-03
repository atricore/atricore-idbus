package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class Location implements Serializable {

    private long id;

    private String protocol;

    private String host;

    private int port;

    private String context;

    private String uri;

    private String locationAsString;
    private static final long serialVersionUID = -2122107248713729110L;

    public Location(){
        protocol = "";
        host = "";
        port = 80;
        context = "";
        uri = "";
    }



    public void setLocationAsString(String locationAsString){
//        throw new UnsupportedOperationException("Cannot change location as a string!");
    }

    public String getLocationAsString(){
        String portString = (port == 80 || port == 443 ? "" :  ":" + port);
        String contextString = (context.startsWith("/") ? context.substring(1) : context);
        contextString = (contextString.endsWith("/") ? contextString.substring(0, contextString.length() - 1) : contextString);
        return protocol + "://" + host + portString + "/" +  contextString + "/" + uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return getLocationAsString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location that = (Location) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
