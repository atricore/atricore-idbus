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

import flash.events.IEventDispatcher;

import mx.rpc.AsyncToken;

import org.atricore.idbus.capabilities.management.main.spi.request.AddBindingProviderRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddIdentityApplianceDefinitionRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddIdentityApplianceRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddIdentityProviderRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddServiceProviderRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.CreateSimpleSsoRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.DeployIdentityApplianceRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.ExportIdentityApplianceRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.ImportIdentityApplianceRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.ListIdentityApplianceDefinitionsRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.ListIdentityAppliancesRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.LookupIdentityApplianceByIdRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.LookupIdentityApplianceDefinitionByIdRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.ManageIdentityApplianceLifeCycleRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.RemoveIdentityApplianceRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UndeployIdentityApplianceRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateApplianceDefinitionRequest;

public interface IIdentityApplianceManagementService extends IEventDispatcher {

    	function deployIdentityAppliance(req:DeployIdentityApplianceRequest):AsyncToken;
    
		function undeployIdentityAppliance(req:UndeployIdentityApplianceRequest):AsyncToken;

//    	function createIdentityAppliance(req:CreateIdentityApplianceRequest):AsyncToken;
//
//    	function removeIdentityAppliance(req:RemoveIdentityApplianceRequest):AsyncToken;
//
//    	function listIdentityAppliances(req:ListIdentityAppliancesRequest):AsyncToken;
//
//    	function findIdentityAppliancesById(req:FindIdentityApplianceByIdRequest):AsyncToken;
//
//    	function findIdentityAppliancesByState(req:FindIdentityAppliancesByStateRequest):AsyncToken;
    
    	function importIdentityAppliance(req:ImportIdentityApplianceRequest):AsyncToken;
    
    	function exportIdentityAppliance(req:ExportIdentityApplianceRequest):AsyncToken;

        function manageIdentityApplianceLifeCycle(req:ManageIdentityApplianceLifeCycleRequest):AsyncToken;

        function createSimpleSso(req:CreateSimpleSsoRequest):AsyncToken;

        function addBindingProvider(req:AddBindingProviderRequest):AsyncToken;
        function lookupIdentityApplianceDefinitionById(req:LookupIdentityApplianceDefinitionByIdRequest):AsyncToken;

        function addIdentityAppliance(req:AddIdentityApplianceRequest):AsyncToken;

        function lookupIdentityApplianceById(req:LookupIdentityApplianceByIdRequest):AsyncToken;
        function removeIdentityAppliance(req:RemoveIdentityApplianceRequest):AsyncToken;

        function listIdentityAppliances(req: ListIdentityAppliancesRequest):AsyncToken;

        function addIdentityProvider(req: AddIdentityProviderRequest):AsyncToken;
        function addServiceProvider(req: AddServiceProviderRequest):AsyncToken;

        function addIdentityApplianceDefinition(req: AddIdentityApplianceDefinitionRequest):AsyncToken;
        function updateApplianceDefinition(req: UpdateApplianceDefinitionRequest):AsyncToken;
        function listIdentityApplianceDefinitions(req: ListIdentityApplianceDefinitionsRequest):AsyncToken;
    }
}