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
import com.atricore.idbus.console.services.dto.Activation;
import com.atricore.idbus.console.services.dto.Connection;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

import flash.events.Event;
import flash.utils.Dictionary;

import mx.collections.ArrayCollection;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class BrowserMediator extends IocMediator implements IDisposable {

    private var _applianceRootNode:BrowserNode;

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
        if (_selectedItem != event.currentTarget.selectedItem) {
            _selectedItem = event.currentTarget.selectedItem;

            projectProxy.currentIdentityApplianceElement = _selectedItem.data;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_SELECTED);
        }
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
                updateElementData();
                //bindApplianceBrowser();
                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_SELECTED:
                var treeNode:BrowserNode = findDataTreeNodeByData(_applianceRootNode, projectProxy.currentIdentityApplianceElement);
                trace("Tree Node: " + treeNode);
                if (treeNode != null) {
                    _selectedItem = treeNode;
                    expandParentNodes(_selectedItem);
                    view.selectedItem = _selectedItem;
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

            _applianceRootNode = BrowserModelFactory.createIdentityApplianceNode(_identityAppliance, true, null);

            var idSourceConnections:Dictionary = new Dictionary();  //[idSource, array of connections]
            var serviceResourceConnections:Dictionary = new Dictionary();  //[serviceResource, array of connections]
            var execEnvConnections:Dictionary = new Dictionary();  //[execEnv, array of connections]
            var authnServiceConnections:Dictionary = new Dictionary();  //[authnService, array of connections]

            if (identityApplianceDefinition.providers != null) {
                for (var i:int = 0; i < identityApplianceDefinition.providers.length; i++) {
                    var provider:Provider = identityApplianceDefinition.providers[i];
                    var providerNode:BrowserNode = BrowserModelFactory.createProviderNode(provider, true, _applianceRootNode);
                    
                    var connectionsNode:BrowserNode = BrowserModelFactory.createConnectionsNode(false, providerNode);
                    var connectionsNodeAdded:Boolean = false;

                    if (provider is FederatedProvider) {
                        var locProv:FederatedProvider = provider as FederatedProvider;
                        // add federated connections to connections node
                        if (locProv.federatedConnectionsA != null && locProv.federatedConnectionsA.length > 0) {
                            // add connections node to provider if not already added
                            if (!connectionsNodeAdded) {
                                providerNode.addChild(connectionsNode);
                                connectionsNodeAdded = true;
                            }
                            for each (var fedConnA:FederatedConnection in locProv.federatedConnectionsA) {
                                connectionsNode.addChild(BrowserModelFactory.createConnectionNode(fedConnA, true, connectionsNode));
                            }
                        }
                        if (locProv.federatedConnectionsB != null && locProv.federatedConnectionsB.length > 0) {
                            // add connections node to provider if not already added
                            if (!connectionsNodeAdded) {
                                providerNode.addChild(connectionsNode);
                                connectionsNodeAdded = true;
                            }
                            for each (var fedConnB:FederatedConnection in locProv.federatedConnectionsB) {
                                connectionsNode.addChild(BrowserModelFactory.createConnectionNode(fedConnB, true, connectionsNode));
                            }
                        }
                        // add identity source to provider node and identity lookup to connections node
                        if (locProv.identityLookup != null && locProv.identityLookup.identitySource != null) {
                            // add connections node to provider if not already added
                            if (!connectionsNodeAdded) {
                                providerNode.addChild(connectionsNode);
                                connectionsNodeAdded = true;
                            }
                            var idSource:IdentitySource = locProv.identityLookup.identitySource;
                            var newIdentityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(idSource, true, providerNode);
                            providerNode.addChild(newIdentityVaultNode);
                            // add identityLookup to connections node
                            var identityLookupNode:BrowserNode = BrowserModelFactory.createConnectionNode(locProv.identityLookup, true, connectionsNode);
                            connectionsNode.addChild(identityLookupNode);
                            // add identityLookup to idSourceConnections map
                            var idLookupConnections:ArrayCollection = idSourceConnections[idSource];
                            if (idLookupConnections == null) {
                                idLookupConnections = new ArrayCollection();
                            }
                            idLookupConnections.addItem(locProv.identityLookup);
                            idSourceConnections[idSource] = idLookupConnections;
                        }
                        if (locProv is ServiceProvider) {
                            var sp:ServiceProvider = locProv as ServiceProvider;

                            // add service resource to provider node and service connection to connections node
                            if (sp.serviceConnection != null && sp.serviceConnection.resource != null) {
                                // add connections node to provider if not already added
                                if (!connectionsNodeAdded) {
                                    providerNode.addChild(connectionsNode);
                                    connectionsNodeAdded = true;
                                }
                                var serviceResource:ServiceResource = sp.serviceConnection.resource;
                                var newServiceResourceNode:BrowserNode = BrowserModelFactory.createServiceResourceNode(serviceResource, true, providerNode);
                                providerNode.addChild(newServiceResourceNode);
                                // add serviceConnection to connections node
                                var serviceConnectionNode:BrowserNode = BrowserModelFactory.createConnectionNode(sp.serviceConnection, true, connectionsNode);
                                connectionsNode.addChild(serviceConnectionNode);
                                // add serviceConnection to serviceResourceConnections map
                                var serviceConnections:ArrayCollection = serviceResourceConnections[serviceResource];
                                if (serviceConnections == null) {
                                    serviceConnections = new ArrayCollection();
                                }
                                serviceConnections.addItem(sp.serviceConnection);
                                serviceResourceConnections[serviceResource] = serviceConnections;
                            }

                            /*// add execution environment to service provider node and activation to connections node
                            if (sp.activation != null && sp.activation.executionEnv != null) {
                                // add connections node to provider if not already added
                                if (!connectionsNodeAdded) {
                                    providerNode.addChild(connectionsNode);
                                    connectionsNodeAdded = true;
                                }
                                // add execution environment to provider node
                                var executionNode1:BrowserNode = BrowserModelFactory.createExecutionEnvironmentNode(sp.activation.executionEnv, true, providerNode);
                                providerNode.addChild(executionNode1);
                                // add activation to connections node
                                var activationNode:BrowserNode = BrowserModelFactory.createConnectionNode(sp.activation, true, connectionsNode);
                                connectionsNode.addChild(activationNode);
                                // add activation to execEnvConnections map
                                var activationConnections:ArrayCollection = execEnvConnections[sp.activation.executionEnv];
                                if (activationConnections == null) {
                                    activationConnections = new ArrayCollection();
                                }
                                activationConnections.addItem(sp.activation);
                                execEnvConnections[sp.activation.executionEnv] = activationConnections;
                            }*/
                        }
                        // add authentication service to identity provider node and delegated authn. connection to connections node
                        if (locProv is IdentityProvider) {
                            var idp:IdentityProvider = locProv as IdentityProvider;
                            if (idp.delegatedAuthentications != null && idp.delegatedAuthentications.length > 0) {
                                // add connections node to provider if not already added
                                if (!connectionsNodeAdded) {
                                    providerNode.addChild(connectionsNode);
                                    connectionsNodeAdded = true;
                                }
                                // add authentication services to provider node
                                for each (var delegatedAuthentication:DelegatedAuthentication in idp.delegatedAuthentications) {
                                    var authnServiceNode1:BrowserNode = BrowserModelFactory.createAuthenticationServiceNode(delegatedAuthentication.authnService, true, providerNode);
                                    providerNode.addChild(authnServiceNode1);
                                    // add delegated authn. connection to connections node
                                    var delegatedAuthnNode:BrowserNode = BrowserModelFactory.createConnectionNode(delegatedAuthentication, true, connectionsNode);
                                    connectionsNode.addChild(delegatedAuthnNode);
                                    // add delegated authn. connection to authnServiceConnections map
                                    var delegatedAuthnConnections:ArrayCollection = authnServiceConnections[delegatedAuthentication.authnService];
                                    if (delegatedAuthnConnections == null) {
                                        delegatedAuthnConnections = new ArrayCollection();
                                    }
                                    delegatedAuthnConnections.addItem(delegatedAuthentication);
                                    authnServiceConnections[delegatedAuthentication.authnService] = delegatedAuthnConnections;
                                }
                            }
                        }
                    }
                    _applianceRootNode.addChild(providerNode);
                }
            }

            if (identityApplianceDefinition.identitySources != null) {
                for (i = 0; i < identityApplianceDefinition.identitySources.length; i++) {
                    var identityVaultNode1:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityApplianceDefinition.identitySources[i], true, _applianceRootNode);
                    _applianceRootNode.addChild(identityVaultNode1);
                    // set connections
                    var connections1:ArrayCollection = idSourceConnections[identityApplianceDefinition.identitySources[i]];
                    if (connections1 != null && connections1.length > 0) {
                        var identitySourceConnectionsNode:BrowserNode = BrowserModelFactory.createConnectionsNode(false, identityVaultNode1);
                        for each (var connection1:Connection in connections1) {
                            identitySourceConnectionsNode.addChild(BrowserModelFactory.createConnectionNode(connection1, true, identitySourceConnectionsNode));
                        }
                        identityVaultNode1.addChild(identitySourceConnectionsNode);
                    }
                }
            }
            if (identityApplianceDefinition.executionEnvironments != null) {
                for (var j:int = 0; j < identityApplianceDefinition.executionEnvironments.length; j++) {
                    var executionNode2:BrowserNode = BrowserModelFactory.createExecutionEnvironmentNode(identityApplianceDefinition.executionEnvironments[j], true, _applianceRootNode);
                    _applianceRootNode.addChild(executionNode2);
                    // set connections
                    if (identityApplianceDefinition.executionEnvironments[j].activations != null &&
                            identityApplianceDefinition.executionEnvironments[j].activations.length > 0) {
                        var execEnvConnectionsNode:BrowserNode = BrowserModelFactory.createConnectionsNode(false,executionNode2);
                        for each (var activation:Activation in identityApplianceDefinition.executionEnvironments[j].activations) {
                            execEnvConnectionsNode.addChild(BrowserModelFactory.createConnectionNode(activation, true, execEnvConnectionsNode));
                            // add activation to serviceResourceConnections map
                            var serviceResourceConnections2:ArrayCollection = serviceResourceConnections[activation.resource];
                            if (serviceResourceConnections2 == null) {
                                serviceResourceConnections2 = new ArrayCollection();
                            }
                            serviceResourceConnections2.addItem(activation);
                            serviceResourceConnections[activation.resource] = serviceResourceConnections2;
                        }
                        executionNode2.addChild(execEnvConnectionsNode);
                    }
                        /*var connections2:ArrayCollection = execEnvConnections[identityApplianceDefinition.executionEnvironments[j]];
                        if (connections2 != null && connections2.length > 0) {
                            var execEnvConnectionsNode:BrowserNode = BrowserModelFactory.createConnectionsNode(false,executionNode2 );
                            for each (var connection2:Connection in connections2) {
                                execEnvConnectionsNode.addChild(BrowserModelFactory.createConnectionNode(connection2, true, execEnvConnectionsNode));
                            }
                            executionNode2.addChild(execEnvConnectionsNode);
                        }*/
                }
            }
            if (identityApplianceDefinition.serviceResources != null) {
                for (i = 0; i < identityApplianceDefinition.serviceResources.length; i++) {
                    var serviceResourceNode:BrowserNode = BrowserModelFactory.createServiceResourceNode(identityApplianceDefinition.serviceResources[i], true, _applianceRootNode);
                    _applianceRootNode.addChild(serviceResourceNode);
                    // set connections
                    var sResourceConnections:ArrayCollection = serviceResourceConnections[identityApplianceDefinition.serviceResources[i]];
                    if (sResourceConnections != null && sResourceConnections.length > 0) {
                        var serviceResourceConnectionsNode:BrowserNode = BrowserModelFactory.createConnectionsNode(false, serviceResourceNode);
                        for each (var sResourceConnection:Connection in sResourceConnections) {
                            serviceResourceConnectionsNode.addChild(BrowserModelFactory.createConnectionNode(sResourceConnection, true, serviceResourceConnectionsNode));
                        }
                        serviceResourceNode.addChild(serviceResourceConnectionsNode);
                    }
                }
            }
            if (identityApplianceDefinition.authenticationServices != null) {
                for (var k:int = 0; k < identityApplianceDefinition.authenticationServices.length; k++) {
                    var authnServiceNode2:BrowserNode = BrowserModelFactory.createAuthenticationServiceNode(identityApplianceDefinition.authenticationServices[k], true, _applianceRootNode);
                    _applianceRootNode.addChild(authnServiceNode2);
                    // set connections
                    var connections3:ArrayCollection = authnServiceConnections[identityApplianceDefinition.authenticationServices[k]];
                    if (connections3 != null && connections3.length > 0) {
                        var authnServiceConnectionsNode:BrowserNode = BrowserModelFactory.createConnectionsNode(false, authnServiceNode2);
                        for each (var connection3:Connection in connections3) {
                            authnServiceConnectionsNode.addChild(BrowserModelFactory.createConnectionNode(connection3, true, authnServiceConnectionsNode));
                        }
                        authnServiceNode2.addChild(authnServiceConnectionsNode);
                    }
                }
            }
            view.dataProvider = _applianceRootNode;
            view.validateNow();
            //view.callLater(expandCollapseTree, [true]);
            view.callLater(expandTree, [_applianceRootNode]);
        } else {
            view.dataProvider = null;
            view.validateNow();
        }


    }

    private function updateElementData():void {
        if (view.selectedItem != null) {
            var elementData:Object = projectProxy.currentIdentityApplianceElement;
            var selectedItemName:String = view.selectedItem.label;
            if (_applianceRootNode != null && elementData != null && selectedItemName != null) {
                updateNodeData(_applianceRootNode, selectedItemName, elementData);
                view.invalidateDisplayList();
            }
        }
    }

    private function updateNodeData(currentNode:BrowserNode, nodeName:String, newData:Object):void {
        if (currentNode.label == nodeName && currentNode.data != null) {
            currentNode.label = newData.name;
            currentNode.data = newData;
        }
        if (currentNode.children != null) {
            for each (var childNode:BrowserNode in currentNode.children) {
                updateNodeData(childNode, nodeName, newData);
            }
        }
    }

    private function expandCollapseTree(open:Boolean):void {
        view.expandChildrenOf(_applianceRootNode, open);
    }

    private function expandTree(startNode:BrowserNode):void {
        view.expandItem(startNode, true);
        for each (var currentNode:BrowserNode in startNode.children) {
            if (currentNode.data is Provider) {
                view.expandItem(currentNode, true);
            }
        }
        var treeNode:BrowserNode = findDataTreeNodeByData(_applianceRootNode, projectProxy.currentIdentityApplianceElement);
        if (treeNode != null) {
            _selectedItem = treeNode;
            expandParentNodes(_selectedItem);
            view.selectedItem = _selectedItem;
        }
    }

    private function expandParentNodes(node:BrowserNode):void {
        var currentParent:BrowserNode = node.parentNode;
        while (currentParent != null) {
            view.expandItem(currentParent, true);
            currentParent = currentParent.parentNode;
        }
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

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null


        //(_applianceRootNode as BrowserNode).removeChildren();
        view.dataProvider = null;
        view.validateNow();
        view.selectedItem = null;
        _applianceRootNode = null;
        _identityAppliance = null;
        _selectedItem = null;
    }

}
}