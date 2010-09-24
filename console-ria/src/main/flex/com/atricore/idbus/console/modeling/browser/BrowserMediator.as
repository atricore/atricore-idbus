/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
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

package com.atricore.idbus.console.modeling.browser {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.browser.model.BrowserModelFactory;
import com.atricore.idbus.console.modeling.browser.model.BrowserNode;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import flash.events.Event;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class BrowserMediator extends IocMediator {
    private var _applianceRootNode;
    private var _identityAppliance:IdentityAppliance;
    private var _projectProxy:ProjectProxy;

    private var _selectedItem:BrowserNode;

    public function BrowserMediator(name:String = null, viewComp:BrowserView = null) {
        super(name, viewComp);
    }


    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.removeEventListener(Event.CHANGE, onTreeChange);
        }

        super.setViewComponent(viewComponent);

        init();

    }

    private function init():void {
        view.validateNow();
        view.addEventListener(Event.CHANGE, onTreeChange);
    }

    private function onTreeChange(event:Event):void {
        _selectedItem = event.currentTarget.selectedItem;

        projectProxy.currentIdentityApplianceElement = _selectedItem.data;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_SELECTED)
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.DIAGRAM_ELEMENT_SELECTED,
            ApplicationFacade.DIAGRAM_ELEMENT_UPDATED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.UPDATE_IDENTITY_APPLIANCE:
                updateIdentityAppliance();
                bindApplianceBrowser();
                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_UPDATED:
                // TODO: Dispatch change event to renderer so that it can update item's labels & icons without
                // TODO: recreating the tree view. 
                updateIdentityAppliance();
                bindApplianceBrowser();
                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_SELECTED:
                var treeNode:BrowserNode = findDataTreeNodeByData(_applianceRootNode, projectProxy.currentIdentityApplianceElement);
                trace("Tree Node: " + treeNode);
                if (treeNode != null) {
                    view.selectedItem = treeNode;
                }
                break;
        }

    }

    private function updateIdentityAppliance():void {

        _identityAppliance = _projectProxy.currentIdentityAppliance;
    }

    private function bindApplianceBrowser():void {

        if (_identityAppliance != null) {
            var identityApplianceDefinition:IdentityApplianceDefinition = _identityAppliance.idApplianceDefinition;

            _applianceRootNode = BrowserModelFactory.createIdentityApplianceNode(_identityAppliance, true);

            if (identityApplianceDefinition.providers != null) {
                for (var i:int = 0; i < identityApplianceDefinition.providers.length; i++) {
                    var provider:Provider = identityApplianceDefinition.providers[i];
                    var providerNode:BrowserNode = BrowserModelFactory.createProviderNode(provider, true);

                    if (provider is FederatedProvider) {
                        var locProv:FederatedProvider = provider as FederatedProvider;
                        if(locProv.identityLookup != null && locProv.identityLookup.identitySource != null){
                            var idSource:IdentitySource = locProv.identityLookup.identitySource;
                            var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(idSource, true);
                            providerNode.addChild(identityVaultNode);
                        }
                        if(locProv is ServiceProvider){
                            var sp:ServiceProvider = locProv as ServiceProvider;
                            if(sp.activation != null && sp.activation.executionEnv != null){
                                var executionNode:BrowserNode = BrowserModelFactory.createExecutionEnvironmentNode(sp.activation.executionEnv, true);
                                providerNode.addChild(executionNode);
                            }
                        }
                    }
                    _applianceRootNode.addChild(providerNode);
                }
            }

            if (identityApplianceDefinition.identitySources != null) {
                for (i = 0; i < identityApplianceDefinition.identitySources.length; i++) {
                    var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityApplianceDefinition.identitySources[i], true);
                    _applianceRootNode.addChild(identityVaultNode);
                }
            }
            if (identityApplianceDefinition.executionEnvironments != null) {
                for (var j:int = 0; j < identityApplianceDefinition.executionEnvironments.length; j++) {
                    var executionNode:BrowserNode = BrowserModelFactory.createExecutionEnvironmentNode(identityApplianceDefinition.executionEnvironments[j], true);
                    _applianceRootNode.addChild(executionNode);
                }
            }
            view.dataProvider = _applianceRootNode;
            view.validateNow();
            view.callLater(expandCollapseTree, [true]);
        } else {
            view.dataProvider = null;
            view.validateNow();
        }


    }

    private function expandCollapseTree(open:Boolean):void {
        view.expandChildrenOf(_applianceRootNode, open);
    }


    private function findDataTreeNodeByData(node:BrowserNode, data:Object):BrowserNode {
        var targetTreeNode:BrowserNode;

        if (_selectedItem != null && _selectedItem.data == data) {
            return _selectedItem;
        }
        
        if (node.data == data) {
            targetTreeNode = node;
        } else {
            for each(var currentNode:BrowserNode in node.children) {
                if (currentNode.data == data) {
                    targetTreeNode = currentNode;
                    break;
                }
                if (currentNode.childsLength() > 0) {
                    targetTreeNode = findDataTreeNodeByData(currentNode, data);

                    if (targetTreeNode != null)
                        break;
                }
            }

        }

        return targetTreeNode;
    }

    protected function get view():BrowserView
    {
        return viewComponent as BrowserView;
    }

}
}