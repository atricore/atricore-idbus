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

package org.atricore.idbus.kernel.main.mediation;

import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.planning.IdentityPlan;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: AbstractChannel.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public abstract class AbstractChannel implements Channel, Serializable {
    private String name;
    private String description;
    private String location;
    private Collection<IdentityMediationEndpoint> identityMediationEndpoints;
    private transient IdentityMediator identityMediator;
    private transient IdentityMediationUnitContainer identityMediationUnitContainer;
    private transient Collection<IdentityPlan> identityPlans;
    private transient ClaimChannel claimsProvider;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public IdentityMediationUnitContainer getUnitContainer() {
        return identityMediationUnitContainer;
    }

    public void setUnitContainer(IdentityMediationUnitContainer identityMediationUnitContainer) {
        this.identityMediationUnitContainer = identityMediationUnitContainer;
    }

    public IdentityMediator getIdentityMediator() {
        return identityMediator;
    }

    public void setIdentityMediator(IdentityMediator identityMediator) {
        this.identityMediator = identityMediator;
    }

    /**
     * @org.apache.xbean.Property alias="endpoints" nestedType="org.atricore.idbus.kernel.main.mediation.endpoint.Endpoint"
     *
     * @param identityMediationEndpoints
     */
    public void setEndpoints(Collection<IdentityMediationEndpoint> identityMediationEndpoints) {
        this.identityMediationEndpoints = identityMediationEndpoints;
    }

    public Collection<IdentityMediationEndpoint> getEndpoints() {
        return identityMediationEndpoints;
    }

    public Collection<IdentityPlan> getIdentityPlans() {
        return identityPlans;
    }

    /**
     * @org.apache.xbean.Property alias="plans" nestedType="org.atricore.idbus.kernel.planning.IdentityPlan"
     *
     * @param identityPlans
     */
    public void setIdentityPlans(Collection<IdentityPlan> identityPlans) {
        this.identityPlans = identityPlans;
    }

    public ClaimChannel getClaimsProvider() {
        return claimsProvider;
    }

    public void setClaimsProvider(ClaimChannel claimsProvider) {
        this.claimsProvider = claimsProvider;
    }

    @Override
    public String toString() {
        return super.toString() +
                "[name="+name+
                ",location="+location+"]";
    }

}
