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

package org.atricore.idbus.kernel.main.federation.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;

import java.util.HashSet;
import java.util.Set;

/**
 * @org.apache.xbean.XBean element="cot"
 *
 * Defines a Circle of Trust of trusted partners .
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: CircleOfTrustImpl.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class CircleOfTrustImpl implements CircleOfTrust {

    private static final Log logger = LogFactory.getLog(CircleOfTrustImpl.class);

    private Set<FederatedProvider> providers = new HashSet<FederatedProvider>();

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<FederatedProvider> getProviders() {
        return providers;
    }

    public void setProviders(Set<FederatedProvider> providers) {
        this.providers = providers;
    }


}
