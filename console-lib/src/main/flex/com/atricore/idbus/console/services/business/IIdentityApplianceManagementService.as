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

package com.atricore.idbus.console.services.business {

import com.atricore.idbus.console.services.spi.request.AddIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.request.AddResourceRequest;
import com.atricore.idbus.console.services.spi.request.DeployIdentityApplianceRequest;

import com.atricore.idbus.console.services.spi.request.ExportIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.request.ImportIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.request.ListIdentityAppliancesRequest;
import com.atricore.idbus.console.services.spi.request.LookupIdentityApplianceByIdRequest;
import com.atricore.idbus.console.services.spi.request.LookupResourceByIdRequest;
import com.atricore.idbus.console.services.spi.request.ManageIdentityApplianceLifeCycleRequest;
import com.atricore.idbus.console.services.spi.request.RemoveIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.request.UndeployIdentityApplianceRequest;

import com.atricore.idbus.console.services.spi.request.UpdateIdentityApplianceRequest;

import flash.events.IEventDispatcher;

import mx.rpc.AsyncToken;

import com.atricore.idbus.console.services.spi.request.CreateSimpleSsoRequest;

public interface IIdentityApplianceManagementService extends IEventDispatcher {

    	function deployIdentityAppliance(req:DeployIdentityApplianceRequest):AsyncToken;
    
		function undeployIdentityAppliance(req:UndeployIdentityApplianceRequest):AsyncToken;
    
    	function importIdentityAppliance(req:ImportIdentityApplianceRequest):AsyncToken;
    
    	function exportIdentityAppliance(req:ExportIdentityApplianceRequest):AsyncToken;

        function manageIdentityApplianceLifeCycle(req:ManageIdentityApplianceLifeCycleRequest):AsyncToken;

        function createSimpleSso(req:CreateSimpleSsoRequest):AsyncToken;

        function addIdentityAppliance(req:AddIdentityApplianceRequest):AsyncToken;
        function updateIdentityAppliance(req: UpdateIdentityApplianceRequest):AsyncToken;
        function lookupIdentityApplianceById(req:LookupIdentityApplianceByIdRequest):AsyncToken;
        function removeIdentityAppliance(req:RemoveIdentityApplianceRequest):AsyncToken;

        function listIdentityAppliances(req: ListIdentityAppliancesRequest):AsyncToken;

        function addResource(req:AddResourceRequest):AsyncToken;
        function lookupResourceById(req:LookupResourceByIdRequest):AsyncToken;

    }
}