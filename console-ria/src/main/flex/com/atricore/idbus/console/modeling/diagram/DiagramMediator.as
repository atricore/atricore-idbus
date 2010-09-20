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

package com.atricore.idbus.console.modeling.diagram
{
import com.atricore.idbus.console.components.CustomEdgeLabelRenderer;
import com.atricore.idbus.console.components.CustomEdgeRenderer;
import com.atricore.idbus.console.components.CustomVisualGraph;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.util.Constants;
import com.atricore.idbus.console.modeling.diagram.event.VEdgeRemoveEvent;
import com.atricore.idbus.console.modeling.diagram.event.VEdgeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeRemoveEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodesLinkedEvent;
import com.atricore.idbus.console.modeling.diagram.model.GraphDataManager;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDbIdentitySourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateExecutionEnvironmentElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateLdapIdentitySourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateXmlIdentitySourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityApplianceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.renderers.node.NodeDetailedRenderer;
import com.atricore.idbus.console.modeling.diagram.view.util.DiagramUtil;
import com.atricore.idbus.console.services.dto.Activation;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;

import flash.display.DisplayObject;
import flash.events.MouseEvent;

import flash.utils.Dictionary;

import mx.collections.ArrayCollection;
import mx.controls.Button;
import mx.core.ClassFactory;
import mx.core.Container;
import mx.utils.UIDUtil;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;
import org.un.cava.birdeye.ravis.enhancedGraphLayout.data.EnhancedGraph;
import org.un.cava.birdeye.ravis.graphLayout.data.IEdge;
import org.un.cava.birdeye.ravis.graphLayout.data.IGraph;
import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.layout.CircularLayouter;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.utils.TypeUtil;
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class DiagramMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    public static const ORIENTATION_MENU_ITEM_INDEX:int = 3;

    private var _identityApplianceDiagram:CustomVisualGraph;

    private var _identityAppliance:IdentityAppliance;

    private var _applianceId:String;

    private var _autoFitEnabled:Boolean;

    private var _selectedOrientation:uint;

    private var zoomMax:Number = 2;

    private var zoomMin:Number = .25;

    private var _emptyNotationModel:XML;

    private var _currentlySelectedNode:INode;
    private var _currentlySelectedEdge:IEdge;
    private var _projectProxy:ProjectProxy;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function DiagramMediator(name:String = null, viewComp:DiagramView = null) {
        super(name, viewComp);

    }


    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            _identityApplianceDiagram.removeEventListener(VNodeSelectedEvent.VNODE_SELECTED, nodeSelectedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodeRemoveEvent.VNODE_REMOVE, nodeRemoveEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.FEDERATED_CONNECTION_CREATED, federatedConnectionCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.ACTIVATION_CREATED, activationCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.IDENTITY_LOOKUP_CREATED, identityLookupCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VEdgeSelectedEvent.VEDGE_SELECTED, edgeSelectedEventHandler);
            _identityApplianceDiagram.removeEventListener(VEdgeRemoveEvent.VEDGE_REMOVE, edgeRemoveEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.LINKING_CANCELED, linkingCanceledEventHandler);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {

        _identityApplianceDiagram = view.identityApplianceDiagram;
        _identityApplianceDiagram.addEventListener(VNodeSelectedEvent.VNODE_SELECTED, nodeSelectedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodeRemoveEvent.VNODE_REMOVE, nodeRemoveEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.FEDERATED_CONNECTION_CREATED, federatedConnectionCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.ACTIVATION_CREATED, activationCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.IDENTITY_LOOKUP_CREATED, identityLookupCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VEdgeSelectedEvent.VEDGE_SELECTED, edgeSelectedEventHandler);
        _identityApplianceDiagram.addEventListener(VEdgeRemoveEvent.VEDGE_REMOVE, edgeRemoveEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.LINKING_CANCELED, linkingCanceledEventHandler);
        _emptyNotationModel = <Graph/>;

        resetGraph();
        updateGraph();

    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM,
            ApplicationFacade.DIAGRAM_ELEMENT_SELECTED,
            ApplicationFacade.DIAGRAM_ELEMENT_UPDATED,
            ApplicationFacade.DIAGRAM_ELEMENT_REMOVE
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.UPDATE_IDENTITY_APPLIANCE:
                updateIdentityAppliance();
                init();
                break;
            case ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM:
                var elementType:int = notification.getBody() as int;

                if (_applianceId != null && elementType == DiagramElementTypes.FEDERATED_CONNECTION_ELEMENT_TYPE) {
                    _identityApplianceDiagram.enterFederatedConnectionMode();
                    break;
                }
                if (_applianceId != null && elementType == DiagramElementTypes.ACTIVATION_ELEMENT_TYPE) {
                    _identityApplianceDiagram.enterActivationMode();
                    break;
                }
                if (_applianceId != null && elementType == DiagramElementTypes.IDENTITY_LOOKUP_ELEMENT_TYPE) {
                    _identityApplianceDiagram.enterIdentityLookupMode();
                    break;
                }

//                if (_currentlySelectedNode != null) {

                    switch (elementType) {
                        case DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE:
                            // assert that source end is an Identity Appliance
//                            if (_currentlySelectedNode.data is IdentityAppliance) {
//                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                               var ownerIdentityAppliance:IdentityAppliance = _identityAppliance;

                                var cip:CreateIdentityProviderElementRequest = new CreateIdentityProviderElementRequest(
                                        ownerIdentityAppliance,
//                                        _currentlySelectedNode.stringid
                                        null
                                        );

                                // this notification will be grabbed by the modeler mediator which will open
                                // the corresponding form
                                sendNotification(ApplicationFacade.CREATE_IDENTITY_PROVIDER_ELEMENT, cip);
//                            }


                            break;
                        case DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE:
                            // assert that source end is an Identity Appliance
//                            if (_currentlySelectedNode.data is IdentityAppliance) {
//                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                                ownerIdentityAppliance = _identityAppliance;

                                var csp:CreateServiceProviderElementRequest = new CreateServiceProviderElementRequest(
                                        ownerIdentityAppliance,
//                                        _currentlySelectedNode.stringid
                                        null
                                        );

                                // this notification will be grabbed by the modeler mediator which will open
                                // the corresponding form
                                sendNotification(ApplicationFacade.CREATE_SERVICE_PROVIDER_ELEMENT, csp);
//                            }


                            break;
//                        case DiagramElementTypes.IDP_CHANNEL_ELEMENT_TYPE:
//                            // assert that source end is an Identity Appliance
//                            if (_currentlySelectedNode != null && _currentlySelectedNode.data is ServiceProvider) {
//                                var ownerServiceProvider:ServiceProvider = _currentlySelectedNode.data as ServiceProvider;
//
//                                var cidpc:CreateIdpChannelElementRequest = new CreateIdpChannelElementRequest(
//                                        ownerServiceProvider,
//                                        _currentlySelectedNode.stringid
//                                        );
//                                _projectProxy.currentIdentityApplianceElementOwner = ownerServiceProvider;
//                                // this notification will be grabbed by the modeler mediator which will open
//                                // the corresponding form
//                                sendNotification(ApplicationFacade.CREATE_IDP_CHANNEL_ELEMENT, cidpc);
//                            }
//                            break;
//                        case DiagramElementTypes.SP_CHANNEL_ELEMENT_TYPE:
//                            // assert that source end is an Identity Appliance
//                            if (_currentlySelectedNode != null && _currentlySelectedNode.data is IdentityProvider) {
//                                var ownerIdentityProvider:IdentityProvider = _currentlySelectedNode.data as IdentityProvider;
//
//                                var csdpc:CreateSpChannelElementRequest = new CreateSpChannelElementRequest(
//                                        ownerIdentityProvider,
//                                        _currentlySelectedNode.stringid
//                                        );
//                                _projectProxy.currentIdentityApplianceElementOwner = ownerIdentityProvider;
//                                // this notification will be grabbed by the modeler mediator which will open
//                                // the corresponding form
//                                sendNotification(ApplicationFacade.CREATE_SP_CHANNEL_ELEMENT, csdpc);
//                            }
//                            break;
                        case DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE:
                            ownerIdentityAppliance = _identityAppliance;

                            var civ:CreateIdentityVaultElementRequest = new CreateIdentityVaultElementRequest(
                                    ownerIdentityAppliance, null);

                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_IDENTITY_VAULT_ELEMENT, civ);

                            break;
                        case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                            ownerIdentityAppliance = _identityAppliance;

                            var cdiv:CreateDbIdentitySourceElementRequest = new CreateDbIdentitySourceElementRequest(
                                    ownerIdentityAppliance, null);

                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_DB_IDENTITY_SOURCE_ELEMENT, cdiv);
//
                            break;
                        case DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE:
                            ownerIdentityAppliance = _identityAppliance;

                            var cliv:CreateLdapIdentitySourceElementRequest = new CreateLdapIdentitySourceElementRequest(
                                    ownerIdentityAppliance, null);

                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_LDAP_IDENTITY_SOURCE_ELEMENT, cliv);
                            
                            break;
                        case DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE:
                            ownerIdentityAppliance = _identityAppliance;

                            var cxiv:CreateXmlIdentitySourceElementRequest = new CreateXmlIdentitySourceElementRequest(
                                    ownerIdentityAppliance, null);

                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_XML_IDENTITY_SOURCE_ELEMENT, cxiv);
                            
                            break;
                        case DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var cjbeenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_JBOSS_EXECUTION_ENVIRONMENT_ELEMENT, cjbeenv);
                            break;
                        case DiagramElementTypes.WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var cweenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT, cweenv);
                            break;
                        case DiagramElementTypes.TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var cteenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT, cteenv);
                            break;
                        case DiagramElementTypes.JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var cjpeenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT, cjpeenv);
                            break;
                        case DiagramElementTypes.LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var clpeenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT, clpeenv);
                            break;
                        case DiagramElementTypes.WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var cwseenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT, cwseenv);
                            break;
                        case DiagramElementTypes.APACHE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var caeenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_APACHE_EXECUTION_ENVIRONMENT_ELEMENT, caeenv);
                            break;
                        case DiagramElementTypes.WINDOWS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var cwiiseenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                    );
                            _projectProxy.currentIdentityAppliance = _identityAppliance;
                            // this notification will be grabbed by the modeler mediator which will open
                            // the corresponding form
                            sendNotification(ApplicationFacade.CREATE_WINDOWS_IIS_EXECUTION_ENVIRONMENT_ELEMENT, cwiiseenv);
                            break;
                    }
//                }
                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_SELECTED:
                toggleNodeOnByData(_identityApplianceDiagram, _projectProxy.currentIdentityApplianceElement);
                break;

            case ApplicationFacade.DIAGRAM_ELEMENT_UPDATED:
                _identityApplianceDiagram.dispatchEvent(new VGraphEvent(VGraphEvent.VGRAPH_CHANGED));
                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_REMOVE:
                if (_currentlySelectedNode != null) {
                    var elementType:int = notification.getBody() as int;

                    switch (elementType) {
                        case DiagramElementTypes.IDENTITY_APPLIANCE_ELEMENT_TYPE:
                            var identityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                            var ria:RemoveIdentityApplianceElementRequest = new RemoveIdentityApplianceElementRequest(identityAppliance);
                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_APPLIANCE_ELEMENT, ria);
                            break;
                        case DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE:
                            var identityProvider:IdentityProvider = _currentlySelectedNode.data as IdentityProvider;

                            var rip:RemoveIdentityProviderElementRequest = new RemoveIdentityProviderElementRequest(identityProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_PROVIDER_ELEMENT, rip);
                            break;
                        case DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE:
                            var serviceProvider:ServiceProvider = _currentlySelectedNode.data as ServiceProvider;

                            var rsp:RemoveServiceProviderElementRequest = new RemoveServiceProviderElementRequest(serviceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_SERVICE_PROVIDER_ELEMENT, rsp);
                            break;
//                        case DiagramElementTypes.IDP_CHANNEL_ELEMENT_TYPE:
//                            var idpChannel:IdentityProviderChannel = _currentlySelectedNode.data as IdentityProviderChannel;
//
//                            var ridpc:RemoveIdpChannelElementRequest = new RemoveIdpChannelElementRequest(idpChannel);
//
//                            // this notification will be grabbed by the modeler mediator which will invoke
//                            // the corresponding command for processing the removal operation.
//                            sendNotification(ApplicationFacade.REMOVE_IDP_CHANNEL_ELEMENT, ridpc);
//                            break;
//                        case DiagramElementTypes.SP_CHANNEL_ELEMENT_TYPE:
//                            var spChannel:ServiceProviderChannel = _currentlySelectedNode.data as ServiceProviderChannel;
//
//                            var rspc:RemoveSpChannelElementRequest = new RemoveSpChannelElementRequest(spChannel);
//
//                            // this notification will be grabbed by the modeler mediator which will invoke
//                            // the corresponding command for processing the removal operation.
//                            sendNotification(ApplicationFacade.REMOVE_SP_CHANNEL_ELEMENT, rspc);
//                            break;
                        case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                            var identityVault:DbIdentitySource = _currentlySelectedNode.data as DbIdentitySource;

                            var riv:RemoveIdentityVaultElementRequest = new RemoveIdentityVaultElementRequest(identityVault);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_DB_IDENTITY_SOURCE_ELEMENT, riv);
                            break;
                    }
                }

                if (_currentlySelectedEdge != null) {
                    GraphDataManager.removeVEdge(_identityApplianceDiagram, _currentlySelectedEdge.vedge);
                    _currentlySelectedEdge = null;
                    sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                }

                break;
        }

    }

    private function updateIdentityAppliance():void {

        _identityAppliance = projectProxy.currentIdentityAppliance;
    }



    private function updateGraph() {

        if (_identityAppliance != null) {
            _applianceId = _identityAppliance.id.toString();
        } else {
            _applianceId = null;
        }

        resetGraph();

        var providerNodes:Dictionary = new Dictionary();

        if (_identityAppliance != null) {
            var identityApplianceDefinition:IdentityApplianceDefinition = _identityAppliance.idApplianceDefinition;

//            var rootGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), _identityAppliance, null, true, Constants.IDENTITY_BUS_DEEP);
//            rootGraphNode.isVisible = false;

            var vaultNodes:ArrayCollection = new ArrayCollection();
            if (identityApplianceDefinition.identitySources != null) {
                for(var k:int=0; k < identityApplianceDefinition.identitySources.length; k++){
                    var identityVaultGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityApplianceDefinition.identitySources[k], null, null, true, Constants.PROVIDER_DEEP);
                    vaultNodes.addItem(identityVaultGraphNode);
                }
            }

            var environmentNodes:ArrayCollection = new ArrayCollection();
            if (identityApplianceDefinition.executionEnvironments != null) {
                for(var l:int=0; l < identityApplianceDefinition.executionEnvironments.length; l++){
                    var execEnvGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityApplianceDefinition.executionEnvironments[l], null, null, true, Constants.PROVIDER_DEEP);
                    environmentNodes.addItem(execEnvGraphNode);
                }
            }

            if (identityApplianceDefinition.providers != null) {
                for (var i:int = 0; i < identityApplianceDefinition.providers.length; i++) {
                    var provider:Provider = identityApplianceDefinition.providers[i];
                    var providerGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), provider, null, null, true, Constants.PROVIDER_DEEP);
                    providerNodes[provider] = providerGraphNode;
                        var provider:Provider = identityApplianceDefinition.providers[i];
                        if (provider is FederatedProvider) {
                            var locProv:FederatedProvider = provider as FederatedProvider;
                                if(locProv.identityLookup != null && locProv.identityLookup.identitySource != null){
                                    var idSource:IdentitySource = locProv.identityLookup.identitySource;
                                    //TODO add identitySource and connection towards it
                                    var vaultExists:Boolean = false;
                                    for each (var tmpVaultGraphNode:IVisualNode in vaultNodes){
                                        if(tmpVaultGraphNode.data as IdentitySource == idSource){
                                            GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpVaultGraphNode, providerGraphNode, locProv.identityLookup);
                                            vaultExists = true;
                                        }
                                    }
                                    if(!vaultExists){
                                        var newVaultNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), idSource, providerGraphNode, locProv.identityLookup, true, Constants.IDENTITY_VAULT_DEEP);
                                        //if vault doesn't exist in the vaults array, add it so other providers can find it
                                        vaultNodes.addItem(newVaultNode);
                                    }

                                }
//                            }
                            if(locProv is ServiceProvider){
                                var sp:ServiceProvider = locProv as ServiceProvider;
                                if(sp.activation != null && sp.activation.executionEnv != null){  //check for execution environment
                                    var environmentExists:Boolean = false;
                                    for each (var tmpExecEnvGraphNode:IVisualNode in environmentNodes){
                                        if(tmpExecEnvGraphNode.data as ExecutionEnvironment == sp.activation.executionEnv){
                                            GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpExecEnvGraphNode, providerGraphNode, sp.activation);
                                            environmentExists = true;
                                        }
                                    }
                                    if(!environmentExists){
                                        var newExecEnvNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), sp.activation.executionEnv, providerGraphNode, sp.activation, true, Constants.IDENTITY_VAULT_DEEP);
                                        //if vault doesn't exist in the vaults array, add it so other providers can find it
                                        environmentNodes.addItem(newExecEnvNode);
                                    }

                                }
                            }
                        }
                    //}
                }
                //now we have all the providers added to the graph. Now we need to link them
                for (var j:int = 0; j < identityApplianceDefinition.providers.length; j++) {
                    if(identityApplianceDefinition.providers[j] is FederatedProvider){
                        var fedProvider:FederatedProvider = identityApplianceDefinition.providers[j] as FederatedProvider;
                        for each (var fedConnA:FederatedConnection in fedProvider.federatedConnectionsA){
                            var graphNodeRoleA:IVisualNode = providerNodes[fedProvider];
                            var graphNodeRoleB:IVisualNode = providerNodes[fedConnA.roleB];                            
                            if(!DiagramUtil.nodeLinkExists(graphNodeRoleA.node, graphNodeRoleB.node)){ //avoid double linking
                                GraphDataManager.linkVNodes(_identityApplianceDiagram, graphNodeRoleA, graphNodeRoleB, fedConnA);
                            }
                        }
                        for each (var fedConnB:FederatedConnection in fedProvider.federatedConnectionsB){
                            var graphNodeRoleA:IVisualNode = providerNodes[fedConnB.roleA];
                            var graphNodeRoleB:IVisualNode = providerNodes[fedProvider];                           
                            if(!DiagramUtil.nodeLinkExists(graphNodeRoleA.node, graphNodeRoleB.node)){ //avoid double linking
                                GraphDataManager.linkVNodes(_identityApplianceDiagram, graphNodeRoleA, graphNodeRoleB, fedConnB);
                            }
                        }
                    }
                }
            }
        }

    }


    private function resetGraph():void {
        var graph:IGraph = new EnhancedGraph("Graph", true);
		var vo:Object = TypeUtil.deserializeXMLString(_emptyNotationModel);
		EnhancedGraph(graph).initFromVO(vo);
		_identityApplianceDiagram.graph = graph;

        _identityApplianceDiagram.graph.purgeGraph();
        _identityApplianceDiagram.newNodesDefaultVisible = true;

        _autoFitEnabled = true;
//        _selectedOrientation = HierarchicalLayouter.ORIENT_TOP_DOWN;
//        var layouter:HierarchicalLayouter = new HierarchicalLayouter(_identityApplianceDiagram);
//        layouter.autoFitEnabled = _autoFitEnabled;
//        layouter.orientation = _selectedOrientation;
        var layouter:CircularLayouter = new CircularLayouter(_identityApplianceDiagram);
        layouter.autoFitEnabled = _autoFitEnabled;
//        layouter.orientation = _selectedOrientation;

        _identityApplianceDiagram.layouter = layouter;

        var nodeRenderer:ClassFactory = new ClassFactory(NodeDetailedRenderer);
        _identityApplianceDiagram.itemRenderer = nodeRenderer;

        _identityApplianceDiagram.edgeRenderer = new CustomEdgeRenderer(_identityApplianceDiagram.edgeDrawGraphics);
        _identityApplianceDiagram.edgeLabelRenderer = new ClassFactory(CustomEdgeLabelRenderer);

        /* set if edge labels should be displayed */
        _identityApplianceDiagram.displayEdgeLabels = true;
        _identityApplianceDiagram.displayNodeLabels = true;
        
        _identityApplianceDiagram.draw();
        _identityApplianceDiagram.refresh();

        _identityApplianceDiagram.exitConnectionMode();
    }

    private function nodeSelectedEventHandler(event:VNodeSelectedEvent):void
    {
        var node:INode = _identityApplianceDiagram.graph.nodeByStringId(event.vnodeId);

        toggleUnselectedNodesOff(_identityApplianceDiagram, event.target);
        unselectAllEdges();

        if (node != null) {
            _currentlySelectedNode = node;
            _projectProxy.currentIdentityApplianceElement = node.data;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_SELECTED);
        }
    }

    private function nodeRemoveEventHandler(event:VNodeRemoveEvent):void
    {
        var node:INode = _identityApplianceDiagram.graph.nodeByStringId(event.vnodeId);
        var elementType:int = -1;

        if (node != null) {
            _currentlySelectedNode = node;
            _projectProxy.currentIdentityApplianceElement = node.data;
            //need to add elementType in the notification body for delete func. to work properly
            if(node.data is IdentityAppliance){
                elementType = DiagramElementTypes.IDENTITY_APPLIANCE_ELEMENT_TYPE;
            } else
            if(node.data is IdentityProvider){
                elementType = DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE;
            } else
            if (node.data is ServiceProvider){
                elementType = DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE;
            } else
            if(node.data is DbIdentitySource){
                elementType = DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE;
            }
            //TODO - add other element types

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE, elementType);
        }
    }

    private function federatedConnectionCreatedEventHandler(event:VNodesLinkedEvent):void {
        var node1:IVisualNode = event.vnode1;
        var node2:IVisualNode = event.vnode2;

        var cfc:CreateFederatedConnectionElementRequest = new CreateFederatedConnectionElementRequest();
        cfc.roleA = node1.data as FederatedProvider;
        cfc.roleB = node2.data as FederatedProvider
        sendNotification(ApplicationFacade.CREATE_FEDERATED_CONNECTION, cfc);
    }

    private function identityLookupCreatedEventHandler(event:VNodesLinkedEvent):void {
        var node1:IVisualNode = event.vnode1;
        var node2:IVisualNode = event.vnode2;

        var cilr:CreateIdentityLookupElementRequest = new CreateIdentityLookupElementRequest();
        if(node1.data is Provider && node2.data is IdentitySource){
            cilr.provider = node1.data as FederatedProvider;
            cilr.identitySource = node2.data as IdentitySource;
        } else if (node1.data is IdentitySource && node2.data is Provider){
            cilr.provider = node2.data as FederatedProvider;
            cilr.identitySource = node1.data as IdentitySource;
        }
        sendNotification(ApplicationFacade.CREATE_IDENTITY_LOOKUP, cilr);
    }

    private function activationCreatedEventHandler(event:VNodesLinkedEvent):void {
        var node1:IVisualNode = event.vnode1;
        var node2:IVisualNode = event.vnode2;

        var car:CreateActivationElementRequest = new CreateActivationElementRequest();
        if(node1.data is ServiceProvider && node2.data is ExecutionEnvironment){
            car.sp = node1.data as ServiceProvider;
            car.executionEnvironment = node2.data as ExecutionEnvironment;
        } else if (node1.data is ExecutionEnvironment && node2.data is ServiceProvider){
            car.sp = node2.data as ServiceProvider;
            car.executionEnvironment = node1.data as ExecutionEnvironment;
        }
        sendNotification(ApplicationFacade.CREATE_ACTIVATION, car);
    }

    private function edgeSelectedEventHandler(event:VEdgeSelectedEvent):void {
        var edge:IEdge = event.edge;

        //var edge:IEdge = _identityApplianceDiagram.edgeByStringId(event.vedgeId);

        toggleUnselectedNodesOff(_identityApplianceDiagram, event.target);

        if (edge != null) {
            _currentlySelectedEdge = edge;
            _projectProxy.currentIdentityApplianceElement = edge.data.data;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_SELECTED);
        }
    }

    private function edgeRemoveEventHandler(event:VEdgeRemoveEvent):void {
        // edgeData is FederatedConnection, JOSSOActivation, etc.
        var edgeData:Object = event.data;
        var elementType:int = -1;
        
        if(edgeData is FederatedConnection){
            var fedConnection:FederatedConnection = edgeData as FederatedConnection;
            var rfc:RemoveFederatedConnectionElementRequest = new RemoveFederatedConnectionElementRequest(fedConnection);
            sendNotification(ApplicationFacade.REMOVE_FEDERATED_CONNECTION_ELEMENT, rfc);
        } else if (edgeData is Activation){
            var activation:JOSSOActivation = edgeData as JOSSOActivation;
            var ract:RemoveActivationElementRequest = new RemoveActivationElementRequest(activation);
            sendNotification(ApplicationFacade.REMOVE_ACTIVATION_ELEMENT, ract);
        } else if (edgeData is IdentityLookup){
            var identityLookup:IdentityLookup = edgeData as IdentityLookup;
            var ril:RemoveIdentityLookupElementRequest = new RemoveIdentityLookupElementRequest(identityLookup);
            sendNotification(ApplicationFacade.REMOVE_IDENTITY_LOOKUP_ELEMENT, ril);
        }
    }

    private function linkingCanceledEventHandler(event:VNodesLinkedEvent):void {
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
    }

    private function toggleUnselectedNodesOff(visualCompToCheck:Object, selectedItem:Object):void {

        for each(var obj:Object in visualCompToCheck.getChildren()) {
            if (obj is Container || obj is NodeDetailedRenderer) {
                toggleUnselectedNodesOff(obj, selectedItem);
            } else
            if (obj is Button) {
                    var button:Button = Button(obj);

                    if (button.parent != selectedItem) {
                        button.selected = false;
                    }

            }
        }
    }

    private function unselectAllEdges():void {
        _currentlySelectedEdge = null;
        for each (var edge:IEdge in _identityApplianceDiagram.graph.edges) {
            edge.vedge.lineStyle.color = 0xCCCCCC;
        }
        _identityApplianceDiagram.refresh();
    }

    private function toggleNodeOnByData(visualCompToCheck:Object, targetSemanticElement:Object):void {

        for each(var obj:Object in visualCompToCheck.getChildren()) {
            if (obj is Container && !(obj is NodeDetailedRenderer)) {
                toggleNodeOnByData(obj, targetSemanticElement);
            } else
            if (obj is NodeDetailedRenderer) {
                var renderer:NodeDetailedRenderer = NodeDetailedRenderer(obj);

                var diagramSemanticElement:Object = IVisualNode(renderer.data).data

                var elementFigure:Button = lookUpElementFigure(renderer) as Button;

                if (diagramSemanticElement == targetSemanticElement ) {
                    elementFigure.selected = true;
                } else {
                    elementFigure.selected = false;
                }
            }
        }
    }


    private function lookUpElementFigure(visualCompToCheck:Object):DisplayObject {
        var foundDiagramElement:Button;

        for each(var obj:Object in visualCompToCheck.getChildren()) {
            if (obj is Container || obj is NodeDetailedRenderer) {
                lookUpElementFigure(obj);
            } else
            if (obj is Button) {
                    foundDiagramElement = Button(obj);

            }
        }
        return foundDiagramElement;
    }

    private function findNotationElementBySemanticElement(semanticElement:Object):IVisualNode {
        var foundNode:IVisualNode;

        for each(var node:IVisualNode in _identityApplianceDiagram.graph.nodes) {
            if (node.data != null && node.data == semanticElement ) {
                foundNode = node;
                break;
            }
        }

        return foundNode;
    }



    /**
     * Event handler to be triggered in case the
     * layouter in VGraph has changed.
     * Only used to apply the current value to the new layouter.
     * */
    private function layouterChanged(e:VGraphEvent = null):void {
        _identityApplianceDiagram.layouter.autoFitEnabled = _autoFitEnabled;
        _identityApplianceDiagram.layouter.layoutChanged = true;
    }


    private function zoom(zoomValue:Number):void {
        if (zoomValue <= zoomMin) {
            zoomValue = zoomMin;
            return;
        } else {
            _identityApplianceDiagram.scale = zoomValue;
        }

        if (zoomValue >= zoomMax) {
            zoomValue = zoomMax;
            return;
        } else {
            _identityApplianceDiagram.scale = zoomValue;
        }
    }

    private function zoomWheel(event:MouseEvent):void {
        if (event.ctrlKey) {
            if (event.delta < 0) {
                zoom(_identityApplianceDiagram.scale - .10);
            } else if (event.delta > 0) {
                zoom(_identityApplianceDiagram.scale + .10);
            }
            //zoom(_vgraph.scale + (event.delta * 0.10));
        }
    }

    protected function get view():DiagramView
    {
        return viewComponent as DiagramView;
    }


}
}
