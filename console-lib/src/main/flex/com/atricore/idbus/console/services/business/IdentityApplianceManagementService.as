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


import flash.events.Event;

import mx.rpc.AsyncToken;
import mx.rpc.remoting.RemoteObject;

import com.atricore.idbus.console.services.spi.request.AddBindingProviderRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddIdentityApplianceDefinitionRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.request.AddIdentityProviderRequest;
import com.atricore.idbus.console.services.spi.request.AddServiceProviderRequest;
import com.atricore.idbus.console.services.spi.request.CreateSimpleSsoRequest;
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
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateIdentityApplianceRequest;

public class IdentityApplianceManagementService implements IIdentityApplianceManagementService {

        private var ro:RemoteObject;

        private static var _instance:IdentityApplianceManagementService;


        public function IdentityApplianceManagementService() {
            if (_instance != null)
                 throw new Error("Singleton can only be accessed through Singleton.instance");
            this.ro = new RemoteObject("identityApplianceManagementService");
            _instance = this;            
        }

        public static function get instance():IdentityApplianceManagementService {
            if (_instance == null)  _instance = new IdentityApplianceManagementService();
                return _instance;
        }

        public function deployIdentityAppliance(req:DeployIdentityApplianceRequest):AsyncToken {
            return ro.deployIdentityAppliance(req);
        }
        
        public function undeployIdentityAppliance(req:UndeployIdentityApplianceRequest):AsyncToken {
            return ro.undeployIdentityAppliance(req);
        }

//        public function createIdentityAppliance(req:CreateIdentityApplianceRequest):AsyncToken {
//            return ro.createIdentityAppliance(req);
//        }
//
//    	public function removeIdentityAppliance(req:RemoveIdentityApplianceRequest):AsyncToken {
//            return ro.removeIdentityAppliance(req);
//        }
//
//        public function listIdentityAppliances(req:ListIdentityAppliancesRequest):AsyncToken {
//            return ro.listIdentityAppliances(req);
//        }
//
//        public function findIdentityAppliancesById(req:FindIdentityApplianceByIdRequest):AsyncToken {
//            return ro.findIdentityAppliancesById(req);
//        }
//
//        public function findIdentityAppliancesByState(req:FindIdentityAppliancesByStateRequest):AsyncToken {
//            return ro.findIdentityAppliancesByState(req);
//        }
        
        public function importIdentityAppliance(req:ImportIdentityApplianceRequest):AsyncToken {
            return ro.importIdentityAppliance(req);
        }
        
        public function exportIdentityAppliance(req:ExportIdentityApplianceRequest):AsyncToken {
            return ro.ExportIdentityAppliance(req);
        }

        public function manageIdentityApplianceLifeCycle(req:ManageIdentityApplianceLifeCycleRequest):AsyncToken {
            return ro.manageIdentityApplianceLifeCycle(req);
        }
    
        public function createSimpleSso(req:CreateSimpleSsoRequest):AsyncToken {
            return ro.createSimpleSso(req);
        }


        public function addIdentityAppliance(req:AddIdentityApplianceRequest):AsyncToken {
            return ro.addIdentityAppliance(req);
        }

        public function lookupIdentityApplianceById(req:LookupIdentityApplianceByIdRequest):AsyncToken {
            return ro.lookupIdentityApplianceById(req);
        }

        public function removeIdentityAppliance(req:RemoveIdentityApplianceRequest):AsyncToken {

            return ro.removeIdentityAppliance(req);
        }

        public function listIdentityAppliances(req:ListIdentityAppliancesRequest):AsyncToken {
            var obj:AsyncToken = ro.listIdentityAppliances(req);
            return obj;
        }

        public function addIdentityApplianceDefinition(req: AddIdentityApplianceDefinitionRequest):AsyncToken {
            return ro.addIdentityApplianceDefinition(req);
        }


        public function updateApplianceDefinition(req: UpdateIdentityApplianceRequest):AsyncToken {
            return ro.updateApplianceDefinition(req);
        }


        public function lookupIdentityApplianceDefinitionById(req:LookupIdentityApplianceDefinitionByIdRequest):AsyncToken {
            return ro.lookupIdentityApplianceDefinitionById(req);
        }

        public function listIdentityApplianceDefinitions(req:ListIdentityApplianceDefinitionsRequest):AsyncToken {
            return ro.listIdentityApplianceDefinitions(req);
        }

        public function addIdentityProvider(req: AddIdentityProviderRequest):AsyncToken {
            return ro.addIdentityProvider(req);
        }

        public function addServiceProvider(req: AddServiceProviderRequest):AsyncToken {
            return ro.addServiceProvider(req);
        }

        public function addBindingProvider(req:AddBindingProviderRequest):AsyncToken {
            return ro.addBindingProvider(req);
        }

        public function addEventListener(type:String, listener:Function, useCapture:Boolean=false, priority:int=0, useWeakReference:Boolean=false):void {
            ro.addEventListener(type, listener, useCapture, priority, useWeakReference);
        }

        public function removeEventListener(type:String, listener:Function, useCapture:Boolean=false):void {
            ro.removeEventListener(type, listener, useCapture);
        }

        public function dispatchEvent(event:Event):Boolean {
            return ro.dispatchEvent(event);
        }

        public function hasEventListener(type:String):Boolean {
            return ro.hasEventListener(type);
        }

        public function willTrigger(type:String):Boolean {
            return ro.willTrigger(type);
        }
	}
}