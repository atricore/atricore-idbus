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

package org.atricore.idbus.capabilities.management.main.transform.transformers;

import org.atricore.idbus.capabilities.management.main.domain.metadata.LocalProvider;
import org.atricore.idbus.capabilities.management.main.domain.metadata.Location;
import org.atricore.idbus.capabilities.management.main.exception.TransformException;
import org.atricore.idbus.capabilities.management.main.transform.TransformEvent;
import org.atricore.idbus.capabilities.management.main.transform.Transformer;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

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


    protected String resolveLocationUrl(LocalProvider provider) {
        if (provider.getLocation() == null) {
            return "";
        }
        
        Location l = provider.getLocation();
        // TODO : Location al = provider.getApplinaceDefinition().getLocation();

        return resolveLocationUrl(l);
    }

    protected String resolveLocationBaseUrl(LocalProvider provider) {
        if (provider.getLocation() == null) {
            return "";
        }
        
        // TODO : Location al = provider.getApplinaceDefinition().getLocation();

        Location l = provider.getLocation();

        return resolveLocationBaseUrl(l);
    }

    protected String resolveLocationUrl(Location location) {
        if (location == null) {
            return "";
        }

        String portString = (location.getPort() == 80 || location.getPort() == 443 ? "" :  ":" + location.getPort());
        String contextString = (location.getContext().startsWith("/") ? location.getContext().substring(1) : location.getContext());
        contextString = (contextString.endsWith("/") ? contextString.substring(0, contextString.length() - 1) : contextString);

        return location.getProtocol() + "://" + location.getHost() + portString + "/" +  contextString + "/" +
                (location.getUri() != null ? location.getUri() : "");
    }

    protected String resolveLocationBaseUrl(Location location) {
        if (location == null) {
            return "";
        }

        String portString = (location.getPort() == 80 || location.getPort() == 443 ? "" :  ":" + location.getPort());

        return location.getProtocol()+ "://" + location.getHost() + portString;
    }

    protected String normalizeBeanName(String name) {
        String regex = "[ .]";
        return name.replaceAll(regex, "-").toLowerCase();
    }

}
