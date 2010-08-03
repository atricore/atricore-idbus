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

package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceLifeCycleAction;


public class ManageIdentityApplianceLifeCycleResponse extends AbstractManagementResponse {

    private IdentityApplianceLifeCycleAction action;

    private IdentityAppliance appliance;

    public ManageIdentityApplianceLifeCycleResponse() {

    }

    public ManageIdentityApplianceLifeCycleResponse(IdentityApplianceLifeCycleAction action, IdentityAppliance appliance) {
        this.action = action;
        this.appliance = appliance;
    }

    public IdentityApplianceLifeCycleAction getAction() {
        return action;
    }

    public void setAction(IdentityApplianceLifeCycleAction action) {
        this.action = action;
    }

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }
}