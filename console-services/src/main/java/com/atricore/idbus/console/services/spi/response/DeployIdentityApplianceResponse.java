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
import org.atricore.idbus.capabilities.management.main.spi.response.AbstractManagementResponse;


public class DeployIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityApplianceDTO appliance;

    private Boolean deployed;

    public DeployIdentityApplianceResponse() {

    }

    public DeployIdentityApplianceResponse(IdentityApplianceDTO appliance, boolean deployed) {
        this.appliance = appliance;
        this.deployed = deployed;
    }

    public Boolean getDeployed() {
		return deployed;
	}
	
	public void setDeployed(Boolean deployed) {
		this.deployed = deployed;
	}

    public IdentityApplianceDTO getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityApplianceDTO appliance) {
        this.appliance = appliance;
    }
}
