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

package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceLifeCycleAction;



public class ManageIdentityApplianceLifeCycleResponse extends AbstractManagementResponse {

    private IdentityApplianceLifeCycleAction action;

    private IdentityApplianceDTO appliance;

    public ManageIdentityApplianceLifeCycleResponse() {

    }

    public ManageIdentityApplianceLifeCycleResponse(IdentityApplianceLifeCycleAction action, IdentityApplianceDTO appliance) {
        this.action = action;
        this.appliance = appliance;
    }

    public IdentityApplianceLifeCycleAction getAction() {
        return action;
    }

    public void setAction(IdentityApplianceLifeCycleAction action) {
        this.action = action;
    }

    public IdentityApplianceDTO getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityApplianceDTO appliance) {
        this.appliance = appliance;
    }
}