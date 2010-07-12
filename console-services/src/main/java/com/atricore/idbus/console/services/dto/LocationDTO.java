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

import java.io.Serializable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocationDTO implements Serializable {

    private long id;

    private String protocol;

    private String host;

    private int port;

    private String context;

    private String uri;

    private String locationAsString;
    private static final long serialVersionUID = -2122107248713729110L;

    public LocationDTO(){
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
        if (!(o instanceof LocationDTO)) return false;

        LocationDTO location = (LocationDTO) o;

        if(id == 0) return false;

        if (id != location.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
