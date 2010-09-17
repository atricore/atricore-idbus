/*
 * Copyright (c) 2010., Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.Channel;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Location;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Provider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Resource;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.Transformer;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractTransformer implements Transformer {

    protected UUIDGenerator idGen = new UUIDGenerator();

    public boolean accept(TransformEvent event) {
        return false;
    }

    public void before(TransformEvent event) throws TransformException {

    }

    public Object after(TransformEvent event) throws TransformException {
        return null;
    }

    protected void resolveResource(Resource resource) throws IOException {

        InputStream is = getClass().getResourceAsStream(resource.getUri());
        if (is != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
                byte[] buff = new byte[4096];
                int read = is.read(buff, 0, 4096);
                while (read > 0) {
                    baos.write(buff, 0, read);
                    read = is.read(buff, 0, 4096);
                }
                resource.setValue(baos.toByteArray());

            } finally {
                if (is != null) try { is.close(); } catch (IOException e) {/**/}
            }
        }

    }

    protected String resolveLocationUrl(Provider provider, Channel channel) {

        Location cl = channel.getLocation();
        Location pl = provider.getLocation();
        Location al = provider.getIdentityAppliance().getLocation();

        String location = "";

        if (cl != null)
            location = resolveLocationUrl(cl);

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (pl != null)
            location = resolveLocationUrl(pl) + location;

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (al != null)
            location = resolveLocationUrl(al) + location;

        return location;

    }

    protected String resolveLocationUrl(Provider provider) {

        Location pl = provider.getLocation();
        Location al = provider.getIdentityAppliance().getLocation();

        String location = "";

        if (pl != null)
            location = resolveLocationUrl(pl);

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (al != null)
            location = resolveLocationUrl(al) + location;

        return location;
    }

    protected String resolveLocationBaseUrl(Provider provider) {
        if (provider.getLocation() == null) {
            return "";
        }
        // TODO : Location al = provider.getApplinaceDefinition().getLocation();
        Location l = provider.getLocation();
        return resolveLocationBaseUrl(l);
    }

    protected String resolveLocationPath(Location location) {
        if (location == null) {
            return "";
        }

        String contextString = "";
        if (location.getContext() != null) {
            contextString = (location.getContext().startsWith("/") ? location.getContext().substring(1) : location.getContext());
            contextString = (contextString.endsWith("/") ? contextString.substring(0, contextString.length() - 1) : contextString);
            contextString = "/" + contextString;
        }


        String uriString = "";
        if (location.getUri() != null) {
            uriString = "/" +
            (location.getUri() != null ? location.getUri() : "");

            if (uriString.startsWith("//"))
                uriString = uriString.substring(1);
        }

        return contextString + uriString;

    }

    protected String resolveLocationUrl(Location location) {
        if (location == null) {
            return "";
        }

        String path = resolveLocationPath(location);

        return  resolveLocationBaseUrl(location) + path;
    }

    protected String resolveLocationBaseUrl(Location location) {

        if (location == null) {
            return "";
        }

        String portString = "";
        if (location.getPort() > 0)
            portString = location.getPort() + "";

        String protocolString = "";
        if (location.getProtocol() != null) {
            protocolString = location.getProtocol() + "://";
            // For HTTP, remove default ports
            if (location.getProtocol().equalsIgnoreCase("http"))
                portString = (location.getPort() == 80 ? "" :  ":" + location.getPort());
            if (location.getProtocol().equalsIgnoreCase("https"))
                portString = (location.getPort() == 443 ? "" :  ":" + location.getPort());
        }

        String hostString = "";
        if (location.getHost() != null)
            hostString = location.getHost();

        return protocolString  + hostString + portString;
    }

    protected String normalizeBeanName(String name) {
        String regex = "[ .]";
        return name.replaceAll(regex, "-").toLowerCase();
    }

}
