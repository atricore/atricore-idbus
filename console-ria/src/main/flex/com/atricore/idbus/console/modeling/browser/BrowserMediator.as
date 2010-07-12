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
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;

import com.atricore.idbus.console.services.dto.IdentityProviderChannelDTO;
import com.atricore.idbus.console.services.dto.IdentityVaultDTO;
import com.atricore.idbus.console.services.dto.LocalProviderDTO;
import com.atricore.idbus.console.services.dto.ProviderDTO;

import com.atricore.idbus.console.services.dto.ServiceProviderChannelDTO;

import flash.events.Event;

import com.atricore.idbus.console.components.AutoSizeTree;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.browser.model.BrowserModelFactory;
import com.atricore.idbus.console.modeling.browser.model.BrowserNode;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class BrowserMediator extends Mediator {
    public static const NAME:String = "com.atricore.idbus.console.modeling.browser.BrowserMediator";
    private var _applianceBrowser:AutoSizeTree;
    private var _applianceRootNode;
    private var _identityAppliance:IdentityApplianceDTO;
    private var _projectProxy:ProjectProxy;

    public function BrowserMediator(viewComp:BrowserView) {
        super(NAME, viewComp);

        _projectProxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));

        _applianceBrowser = viewComp;
        _applianceBrowser.validateNow();
        _applianceBrowser.addEventListener(Event.CHANGE, onTreeChange);

    }

    private function onTreeChange(event:Event):void {
        var selectedItem:BrowserNode = event.currentTarget.selectedItem;

        _projectProxy.currentIdentityApplianceElement = selectedItem.data;
        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED)
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE:
            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED:
                // TODO: Dispatch change event to renderer so that it can update item's labels & icons without
                // TODO: recreating the tree view. 
                updateIdentityAppliance();
                bindApplianceBrowser();
                break;
            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED:
                var treeNode:BrowserNode  = findDataTreeNodeByData(_applianceRootNode, _projectProxy.currentIdentityApplianceElement );
                trace("Tree Node: " + treeNode);
                if (treeNode != null) {
                    _applianceBrowser.selectedItem = treeNode;
                }
                break;
        }

    }

    private function updateIdentityAppliance():void {

        _identityAppliance = _projectProxy.currentIdentityAppliance;
    }

    private function bindApplianceBrowser():void {

        if (_identityAppliance != null) {
            var identityApplianceDefinition:IdentityApplianceDefinitionDTO = _identityAppliance.idApplianceDefinition;

            _applianceRootNode = BrowserModelFactory.createIdentityApplianceNode(_identityAppliance, true);

            if (identityApplianceDefinition.providers != null) {
                for (var i:int = 0; i < identityApplianceDefinition.providers.length; i++) {
                    var provider:ProviderDTO = identityApplianceDefinition.providers[i];
                    var providerNode:BrowserNode = BrowserModelFactory.createProviderNode(provider, true);

                    if (provider is LocalProviderDTO) {
                        var locProv:LocalProviderDTO = provider as LocalProviderDTO;
                        if (locProv.defaultChannel != null) {
                            var identityVault:IdentityVaultDTO = null;
                            if (locProv.defaultChannel is IdentityProviderChannelDTO) {
                                identityVault = IdentityProviderChannelDTO(locProv.defaultChannel).identityVault;
                            } else if (locProv.defaultChannel is ServiceProviderChannelDTO) {
                                identityVault = ServiceProviderChannelDTO(locProv.defaultChannel).identityVault;
                            }
                            if (identityVault != null) {
                                var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityVault, true);
                                providerNode.addChild(identityVaultNode);
                            }
                        }
                        if (locProv.channels != null) {
                            for (var j:int = 0; j < locProv.channels.length; j++) {
                                var channel = locProv.channels[j];
                                var identityVault:IdentityVaultDTO = null;
                                if (channel is IdentityProviderChannelDTO) {
                                    identityVault = IdentityProviderChannelDTO(channel).identityVault;
                                } else if (channel is ServiceProviderChannelDTO) {
                                    identityVault = ServiceProviderChannelDTO(channel).identityVault;
                                }
                                if (identityVault != null) {
                                    var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityVault, true);
                                    providerNode.addChild(identityVaultNode);
                                }
                            }
                        }
                    }
                    _applianceRootNode.addChild(providerNode);
                }
            }

            if (identityApplianceDefinition.identityVaults != null) {
                for (i = 0; i < identityApplianceDefinition.identityVaults.length; i++) {
                    var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityApplianceDefinition.identityVaults[i], true);
                    _applianceRootNode.addChild(identityVaultNode);
                }
            }
            _applianceBrowser.dataProvider = _applianceRootNode;
            _applianceBrowser.validateNow();
            _applianceBrowser.callLater(expandCollapseTree, [true]);
        }


    }

    private function expandCollapseTree(open:Boolean):void {
        _applianceBrowser.expandChildrenOf(_applianceRootNode, open);
    }


    private function findDataTreeNodeByData(node:BrowserNode, data:Object):BrowserNode {
        var targetTreeNode:BrowserNode;

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