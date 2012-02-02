/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.capabilities.sts.main;

import org.atricore.idbus.kernel.main.authn.SecurityToken;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SecurityTokenProcessingContext.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class SecurityTokenProcessingContext {

    private Map<String, Object> properties = new HashMap<String, Object>();

    private Set<SecurityToken> emittedTokens = new HashSet<SecurityToken>();

    public Set<SecurityToken> getEmittedTokens() {
        return emittedTokens;
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public Object removeProperty(String name) {
        return properties.remove(name);
    }

    public Object setProperty(String name, Object value) {
        return properties.put(name, value);
    }

    public Collection<String> getPropertyNames()  {
        return properties.keySet();
    }

}
