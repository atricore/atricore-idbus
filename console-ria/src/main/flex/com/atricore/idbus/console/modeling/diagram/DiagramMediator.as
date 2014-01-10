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
import com.atricore.idbus.console.base.diagram.DiagramElementTypes;
import com.atricore.idbus.console.components.CustomEdgeLabelRenderer;
import com.atricore.idbus.console.components.CustomEdgeRenderer;
import com.atricore.idbus.console.components.CustomVisualGraph;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.util.Constants;
import com.atricore.idbus.console.modeling.diagram.event.VEdgeRemoveEvent;
import com.atricore.idbus.console.modeling.diagram.event.VEdgeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeCreationEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeMovedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeRemoveEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodesLinkedEvent;
import com.atricore.idbus.console.modeling.diagram.model.GraphDataManager;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateBlackBoardResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDbIdentitySourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDbIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDelegatedAuthnElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDirectoryServiceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDominoResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateExecutionEnvironmentElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateExternalSaml2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateExternalSaml2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateExternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateExternalWSFederationServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateGoogleAppsElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDbIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateJBossEPPAuthenticationServiceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateJBossEPPResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateSelfServicesResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateJosso1ResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateJosso2ResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateLdapIdentitySourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateOpenIDIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateSalesforceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateInternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateInternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateServiceConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateSugarCRMElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateWikidElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDominoElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateClientCertElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateWindowsIntegratedAuthnElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateXmlIdentitySourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveBlackBoardResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDelegatedAuthnElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDirectoryServiceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDominoResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExecutionEnvironmentElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalSaml2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalWSFederationServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveGoogleAppsElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityApplianceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveInternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJBossEPPAuthenticationServiceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveLiferayResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJBossEPPResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJOSSO1ResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJOSSO2ResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveLiferayResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveOAuth2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveOAuth2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalOpenIDIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveSalesforceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalSaml2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveSelfServicesResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveServiceConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveSugarCRMElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveWikidElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDominoElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveClientCertElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveWindowsIntegratedAuthnElementRequest;
import com.atricore.idbus.console.modeling.diagram.renderers.node.NodeDetailedRenderer;
import com.atricore.idbus.console.modeling.diagram.view.util.DiagramUtil;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.Activation;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.BlackBoardResource;
import com.atricore.idbus.console.services.dto.CaptiveExecutionEnvironment;
import com.atricore.idbus.console.services.dto.CaptiveExecutionEnvironment;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.DbIdentityVault;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.DirectoryAuthenticationService;
import com.atricore.idbus.console.services.dto.DominoResource;
import com.atricore.idbus.console.services.dto.EmbeddedIdentityVault;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalSaml2IdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalWSFederationServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalWSFederationServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalWSFederationServiceProvider;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.GoogleAppsServiceProvider;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JBossEPPAuthenticationService;
import com.atricore.idbus.console.services.dto.JBossEPPResource;
import com.atricore.idbus.console.services.dto.JOSSO1Resource;
import com.atricore.idbus.console.services.dto.JOSSO2Resource;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.LiferayResource;
import com.atricore.idbus.console.services.dto.OAuth2IdentityProvider;
import com.atricore.idbus.console.services.dto.OAuth2ServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalOpenIDIdentityProvider;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.SalesforceServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.SelfServicesResource;
import com.atricore.idbus.console.services.dto.ServiceConnection;
import com.atricore.idbus.console.services.dto.InternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;
import com.atricore.idbus.console.services.dto.SugarCRMServiceProvider;
import com.atricore.idbus.console.services.dto.WikidAuthenticationService;
import com.atricore.idbus.console.services.dto.DominoAuthenticationService;
import com.atricore.idbus.console.services.dto.ClientCertAuthnService;
import com.atricore.idbus.console.services.dto.WindowsIntegratedAuthentication;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;

import flash.display.DisplayObject;
import flash.events.MouseEvent;
import flash.utils.Dictionary;
import flash.utils.setTimeout;

import mx.collections.ArrayCollection;
import mx.controls.Button;
import mx.core.ClassFactory;
import mx.core.Container;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.UIDUtil;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;
import org.un.cava.birdeye.ravis.enhancedGraphLayout.data.EnhancedGraph;
import org.un.cava.birdeye.ravis.graphLayout.data.IEdge;
import org.un.cava.birdeye.ravis.graphLayout.data.IGraph;
import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.data.Node;
import org.un.cava.birdeye.ravis.graphLayout.layout.BaseLayouter;
import org.un.cava.birdeye.ravis.graphLayout.layout.CircularLayouter;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualEdge;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.utils.TypeUtil;
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class DiagramMediator extends IocMediator implements IDisposable {

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

    private var _currentIdentityApplianceId:Number;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

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
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.SERVICE_CONNECTION_CREATED, serviceConnectionCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.ACTIVATION_CREATED, activationCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.IDENTITY_LOOKUP_CREATED, identityLookupCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.DELEGATED_AUTHENTICATION_CREATED, delegatedAuthenticationCreatedEventHandler);
            _identityApplianceDiagram.removeEventListener(VEdgeSelectedEvent.VEDGE_SELECTED, edgeSelectedEventHandler);
            _identityApplianceDiagram.removeEventListener(VEdgeRemoveEvent.VEDGE_REMOVE, edgeRemoveEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodesLinkedEvent.LINKING_CANCELED, linkingCanceledEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodeCreationEvent.OPEN_CREATION_FORM, openDialogElementCreationFormEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodeMovedEvent.VNODE_MOVED, nodeMovedEventHandler);
            _identityApplianceDiagram.removeEventListener(VNodeMovedEvent.ALL_VNODES_MOVED, allNodesMovedEventHandler);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        _identityApplianceDiagram = view.identityApplianceDiagram;
        _identityApplianceDiagram.addEventListener(VNodeSelectedEvent.VNODE_SELECTED, nodeSelectedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodeRemoveEvent.VNODE_REMOVE, nodeRemoveEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.FEDERATED_CONNECTION_CREATED, federatedConnectionCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.SERVICE_CONNECTION_CREATED, serviceConnectionCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.ACTIVATION_CREATED, activationCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.IDENTITY_LOOKUP_CREATED, identityLookupCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.DELEGATED_AUTHENTICATION_CREATED, delegatedAuthenticationCreatedEventHandler);
        _identityApplianceDiagram.addEventListener(VEdgeSelectedEvent.VEDGE_SELECTED, edgeSelectedEventHandler);
        _identityApplianceDiagram.addEventListener(VEdgeRemoveEvent.VEDGE_REMOVE, edgeRemoveEventHandler);
        _identityApplianceDiagram.addEventListener(VNodesLinkedEvent.LINKING_CANCELED, linkingCanceledEventHandler);
        _identityApplianceDiagram.addEventListener(VNodeCreationEvent.OPEN_CREATION_FORM, openDialogElementCreationFormEventHandler);
        _identityApplianceDiagram.addEventListener(VNodeMovedEvent.VNODE_MOVED, nodeMovedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodeMovedEvent.ALL_VNODES_MOVED, allNodesMovedEventHandler);
        _emptyNotationModel = <Graph/>;

        resetGraph();
        updateGraph();

        _currentIdentityApplianceId = Number.MIN_VALUE;


    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.PALETTE_ELEMENT_SELECTED,
            ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM,
            ApplicationFacade.DIAGRAM_ELEMENT_SELECTED,
            ApplicationFacade.DIAGRAM_ELEMENT_UPDATED,
            ApplicationFacade.DIAGRAM_ELEMENT_REMOVE,
            ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE,
            ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE,
            ApplicationFacade.REFRESH_DIAGRAM
            //ApplicationFacade.UPDATE_DIAGRAM_ELEMENTS_DATA
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.UPDATE_IDENTITY_APPLIANCE:
                updateIdentityAppliance();
                break;
            case ApplicationFacade.REFRESH_DIAGRAM:
                /*
                var redrawGraph:Boolean = notification.getBody() as Boolean;
                if (!redrawGraph && projectProxy.currentIdentityAppliance != null &&
                        projectProxy.currentIdentityAppliance.id == _currentIdentityApplianceId) {
                    updateGraphData();
                    unselectAllNodes();
                    unselectAllEdges();
                } else {
                    resetGraph();
                    updateGraph();
                }*/
                var circularLayout:Boolean = notification.getBody() as Boolean;
                resetGraph(circularLayout);
                updateGraph(circularLayout);
                if (_projectProxy.currentIdentityAppliance != null) {
                    _currentIdentityApplianceId = _projectProxy.currentIdentityAppliance.id;
                } else {
                    _currentIdentityApplianceId = Number.MIN_VALUE;
                }
                break;
            case ApplicationFacade.UPDATE_DIAGRAM_ELEMENTS_DATA:
                updateGraphData();
                break;
            case ApplicationFacade.PALETTE_ELEMENT_SELECTED:
                var paletteElementType:int = notification.getBody() as int;

                if (_applianceId != null) {
                    if (paletteElementType == DiagramElementTypes.FEDERATED_CONNECTION_ELEMENT_TYPE) {
                        _identityApplianceDiagram.enterFederatedConnectionMode();
                    } else if (paletteElementType == DiagramElementTypes.SERVICE_CONNECTION_ELEMENT_TYPE) {
                        _identityApplianceDiagram.enterServiceConnectionMode();
                    } else if (paletteElementType == DiagramElementTypes.ACTIVATION_ELEMENT_TYPE) {
                        _identityApplianceDiagram.enterActivationMode();
                    } else if (paletteElementType == DiagramElementTypes.IDENTITY_LOOKUP_ELEMENT_TYPE) {
                        _identityApplianceDiagram.enterIdentityLookupMode();
                    } else if (paletteElementType == DiagramElementTypes.DELEGATED_AUTHENTICATION_ELEMENT_TYPE) {
                        _identityApplianceDiagram.enterDelegatedAuthenticationMode();
                    } else {
                        _identityApplianceDiagram.enterNodeCreationMode(paletteElementType);
                    }
                }
                break;
            case ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM:
                var dragElementType:int = notification.getBody() as int;

                switch (dragElementType) {
                    case DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var idpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cip:CreateIdentityProviderElementRequest = new CreateIdentityProviderElementRequest(
                                idpOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_IDENTITY_PROVIDER_ELEMENT, cip);
                        //                            }


                        break;
                    case DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var spOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var csp:CreateInternalSaml2ServiceProviderElementRequest = new CreateInternalSaml2ServiceProviderElementRequest(
                                spOwnerAppliance ,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_INTERNAL_SAML_2_SERVICE_PROVIDER_ELEMENT, csp);
                        //                            }


                        break;
                    case DiagramElementTypes.EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var extIdpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var ceip:CreateExternalSaml2IdentityProviderElementRequest = new CreateExternalSaml2IdentityProviderElementRequest(
                                extIdpOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT, ceip);
                        //                            }


                        break;
                    case DiagramElementTypes.EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var extSpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cesp:CreateExternalSaml2ServiceProviderElementRequest = new CreateExternalSaml2ServiceProviderElementRequest(
                                extSpOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT, cesp);
                        //                            }


                        break;
                    case DiagramElementTypes.EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var openIdIdpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var coiip:CreateOpenIDIdentityProviderElementRequest = new CreateOpenIDIdentityProviderElementRequest(
                                openIdIdpOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_OPENID_IDENTITY_PROVIDER_ELEMENT, coiip);
                        //                            }


                        break;
                    case DiagramElementTypes.OAUTH_2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var oauth2IdpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var coa2ip:CreateOpenIDIdentityProviderElementRequest = new CreateOpenIDIdentityProviderElementRequest(
                                oauth2IdpOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_OAUTH_2_IDENTITY_PROVIDER_ELEMENT, coa2ip);
                        //                            }


                        break;
                    case DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var oauth2SpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var coa2sp:CreateInternalSaml2ServiceProviderElementRequest = new CreateInternalSaml2ServiceProviderElementRequest(
                                oauth2SpOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_OAUTH_2_SERVICE_PROVIDER_ELEMENT, coa2sp);
                        //                            }


                        break;
                    case DiagramElementTypes.EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var extWSFedSpOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cewsfedsp:CreateExternalWSFederationServiceProviderElementRequest = new CreateExternalWSFederationServiceProviderElementRequest(
                                extWSFedSpOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT, cewsfedsp);
                        //                            }
                        break;

                    case DiagramElementTypes.SALESFORCE_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var salesforceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var csf:CreateSalesforceElementRequest = new CreateSalesforceElementRequest(
                                salesforceOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_SALESFORCE_ELEMENT, csf);
                        //                            }


                        break;
                    case DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var googleAppsOnwerAppliance:IdentityAppliance = _identityAppliance;

                        var cga:CreateGoogleAppsElementRequest = new CreateGoogleAppsElementRequest(
                                googleAppsOnwerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_GOOGLE_APPS_ELEMENT, cga);
                        //                            }


                        break;
                    case DiagramElementTypes.SUGAR_CRM_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var sugarCRMOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cscrm:CreateSugarCRMElementRequest = new CreateSugarCRMElementRequest(
                                sugarCRMOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_SUGAR_CRM_ELEMENT, cscrm);
                        //                            }

                        break;
                    case DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE:
                        var idVaultOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var civ:CreateIdentityVaultElementRequest = new CreateIdentityVaultElementRequest(
                                idVaultOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_IDENTITY_VAULT_ELEMENT, civ);

                        break;
                    case DiagramElementTypes.DB_IDENTITY_VAULT_ELEMENT_TYPE:
                        var idDbVaultOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cdbiv:CreateDbIdentityVaultElementRequest = new CreateDbIdentityVaultElementRequest(
                                idDbVaultOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_DB_IDENTITY_VAULT_ELEMENT, cdbiv);

                        break;
                    case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                        var dbIdSourceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cdiv:CreateDbIdentitySourceElementRequest = new CreateDbIdentitySourceElementRequest(
                                dbIdSourceOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_DB_IDENTITY_SOURCE_ELEMENT, cdiv);
                        //
                        break;
                    case DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE:
                        var ldapIdSourceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cliv:CreateLdapIdentitySourceElementRequest = new CreateLdapIdentitySourceElementRequest(
                                ldapIdSourceOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_LDAP_IDENTITY_SOURCE_ELEMENT, cliv);

                        break;
                    case DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE:
                        var xmlIdSourceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cxiv:CreateXmlIdentitySourceElementRequest = new CreateXmlIdentitySourceElementRequest(
                                xmlIdSourceOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_XML_IDENTITY_SOURCE_ELEMENT, cxiv);

                        break;
                    case DiagramElementTypes.JOSSO1_RESOURCE_ELEMENT_TYPE:
                        var josso1ResourceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cj1r:CreateJosso1ResourceElementRequest = new CreateJosso1ResourceElementRequest(
                                josso1ResourceOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_JOSSO1_RESOURCE_ELEMENT, cj1r);

                        break;
                    case DiagramElementTypes.JOSSO2_RESOURCE_ELEMENT_TYPE:
                        var josso2ResourceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cj2r:CreateJosso2ResourceElementRequest = new CreateJosso2ResourceElementRequest(
                                josso2ResourceOwnerAppliance, null);

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_JOSSO2_RESOURCE_ELEMENT, cj2r);

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
                    case DiagramElementTypes.JBOSS_PORTAL_RESOURCE_ELEMENT_TYPE:
                        var cjpeenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT, cjpeenv);
                        break;
                    case DiagramElementTypes.LIFERAY_RESOURCE_ELEMENT_TYPE:
                        var clpeenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_LIFERAY_RESOURCE_ELEMENT, clpeenv);
                        break;
                    case DiagramElementTypes.JBOSSEPP_RESOURCE_ELEMENT_TYPE:
                        var cljbeer:CreateJBossEPPResourceElementRequest = new CreateJBossEPPResourceElementRequest(
                            _identityAppliance, null);
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_JBOSSEPP_RESOURCE_ELEMENT, cljbeer);
                        break;
                    case DiagramElementTypes.SELFSERVICES_RESOURCE_ELEMENT_TYPE:
                        var cslfsvcs:CreateSelfServicesResourceElementRequest = new CreateSelfServicesResourceElementRequest(
                                _identityAppliance, null);
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_SELFSERVICES_RESOURCE_ELEMENT, cslfsvcs);
                        break;
                    case DiagramElementTypes.DOMINO_RESOURCE_ELEMENT_TYPE:
                        var cdomres:CreateDominoResourceElementRequest = new CreateDominoResourceElementRequest(
                                _identityAppliance, null);
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_DOMINO_RESOURCE_ELEMENT, cdomres);
                        break;
                    case DiagramElementTypes.BLACKBOARD_RESOURCE_ELEMENT_TYPE:
                        var cbbres:CreateBlackBoardResourceElementRequest = new CreateBlackBoardResourceElementRequest(
                                _identityAppliance, null);
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_BLACKBOARD_RESOURCE_ELEMENT, cbbres);
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
                    case DiagramElementTypes.ALFRESCO_RESOURCE_ELEMENT_TYPE:
                        var calfenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT, calfenv);
                        break;
                    case DiagramElementTypes.JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                        var cjavaenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT, cjavaenv);
                        break;
                    case DiagramElementTypes.PHP_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                        var cphpenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_PHP_EXECUTION_ENVIRONMENT_ELEMENT, cphpenv);
                        break;
                    case DiagramElementTypes.PHPBB_RESOURCE_ELEMENT_TYPE:
                        var cphpbbenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT, cphpbbenv);
                        break;
                    case DiagramElementTypes.WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                        var cwebcontenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT, cwebcontenv);
                        break;

                    case DiagramElementTypes.SHAREPOINT_RESOURCE_ELEMENT_TYPE:
                        var cshpoint2010contenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_SHAREPOINT2010_EXECUTION_ENVIRONMENT_ELEMENT, cshpoint2010contenv);
                        break;

                    case DiagramElementTypes.COLDFUSION_RESOURCE_ELEMENT_TYPE:
                         var coldfusioncontenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                                 );
                         _projectProxy.currentIdentityAppliance = _identityAppliance;
                         // this notification will be grabbed by the modeler mediator which will open
                         // the corresponding form
                         sendNotification(ApplicationFacade.CREATE_COLDFUSION_EXECUTION_ENVIRONMENT_ELEMENT, coldfusioncontenv);
                         break;

                    case DiagramElementTypes.MICROSTRATEGY_RESOURCE_ELEMENT_TYPE:
                        var mseenv:CreateExecutionEnvironmentElementRequest = new CreateExecutionEnvironmentElementRequest(
                        );
                        _projectProxy.currentIdentityAppliance = _identityAppliance;
                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_MICROSTRATEGY_EXECUTION_ENVIRONMENT_ELEMENT, mseenv);
                        break;

                    case DiagramElementTypes.WIKID_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var wikidOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cwikid:CreateWikidElementRequest = new CreateWikidElementRequest(
                                wikidOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_WIKID_ELEMENT, cwikid);
                        //                            }

                        break;

                    case DiagramElementTypes.DOMINO_ELEMENT_TYPE:
                        var dominoOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cdomino:CreateDominoElementRequest = new CreateDominoElementRequest(
                                dominoOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_DOMINO_ELEMENT, cdomino);
                        break;
                    case DiagramElementTypes.CLIENTCERT_ELEMENT_TYPE:
                        var clientCertOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cclientCert:CreateClientCertElementRequest = new CreateClientCertElementRequest(
                                clientCertOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_CLIENTCERT_ELEMENT, cclientCert);
                        break;
                    case DiagramElementTypes.JBOSSEPP_AUTHENTICATION_ELEMENT_TYPE:
                        var jbosseppOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cJbossEpp:CreateJBossEPPAuthenticationServiceElementRequest = new CreateJBossEPPAuthenticationServiceElementRequest(
                                jbosseppOwnerAppliance,
                                //                                        _currentlySelectedNode.stringid
                                null
                        );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_JBOSSEPP_IDENTITYSOURCE_ELEMENT, cJbossEpp);
                        break;

                    case DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var directoryServiceOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cdirservice:CreateDirectoryServiceElementRequest = new CreateDirectoryServiceElementRequest(
                                directoryServiceOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_DIRECTORY_SERVICE_ELEMENT, cdirservice);
                        //                            }

                        break;
                    case DiagramElementTypes.WINDOWS_INTEGRATED_AUTHN_ELEMENT_TYPE:
                        // assert that source end is an Identity Appliance
                        //                            if (_currentlySelectedNode.data is IdentityAppliance) {
                        //                                var ownerIdentityAppliance:IdentityAppliance = _currentlySelectedNode.data as IdentityAppliance;
                        var winIntegratedAuthnOwnerAppliance:IdentityAppliance = _identityAppliance;

                        var cwinauthn:CreateWindowsIntegratedAuthnElementRequest = new CreateWindowsIntegratedAuthnElementRequest(
                                winIntegratedAuthnOwnerAppliance,
                            //                                        _currentlySelectedNode.stringid
                                null
                                );

                        // this notification will be grabbed by the modeler mediator which will open
                        // the corresponding form
                        sendNotification(ApplicationFacade.CREATE_WINDOWS_INTEGRATED_AUTHN_ELEMENT, cwinauthn);
                        //                            }

                        break;

                }

                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_SELECTED:
                toggleGraphElementOnByData(_projectProxy.currentIdentityApplianceElement);
                break;

            case ApplicationFacade.DIAGRAM_ELEMENT_UPDATED:
                _identityApplianceDiagram.dispatchEvent(new VGraphEvent(VGraphEvent.VGRAPH_CHANGED));
                updateGraphTitle();
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
                        case DiagramElementTypes.EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                            var externalIdentityProvider:ExternalSaml2IdentityProvider = _currentlySelectedNode.data as ExternalSaml2IdentityProvider;

                            var reip:RemoveExternalSaml2IdentityProviderElementRequest = new RemoveExternalSaml2IdentityProviderElementRequest(externalIdentityProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT, reip);
                            break;
                        case DiagramElementTypes.EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT_TYPE:
                            var externalServiceProvider:ExternalSaml2ServiceProvider = _currentlySelectedNode.data as ExternalSaml2ServiceProvider;

                            var resp:RemoveExternalSaml2ServiceProviderElementRequest = new RemoveExternalSaml2ServiceProviderElementRequest(externalServiceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT, resp);
                            break;
                        case DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                            var saml2ServiceProvider:InternalSaml2ServiceProvider = _currentlySelectedNode.data as InternalSaml2ServiceProvider;

                            var rsip:RemoveInternalSaml2ServiceProviderElementRequest = new RemoveInternalSaml2ServiceProviderElementRequest(saml2ServiceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_SAML2_SERVICE_PROVIDER_ELEMENT, rsip);
                            break;
                        case DiagramElementTypes.EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE:
                            var openIDIdentityProvider:ExternalOpenIDIdentityProvider = _currentlySelectedNode.data as ExternalOpenIDIdentityProvider;

                            var roidip:RemoveExternalOpenIDIdentityProviderElementRequest = new RemoveExternalOpenIDIdentityProviderElementRequest(openIDIdentityProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT, roidip);
                            break;
                        case DiagramElementTypes.OAUTH_2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                            var oauth2IdentityProvider:OAuth2IdentityProvider = _currentlySelectedNode.data as OAuth2IdentityProvider;

                            var roa2ip:RemoveOAuth2IdentityProviderElementRequest = new RemoveOAuth2IdentityProviderElementRequest(oauth2IdentityProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_OAUTH2_IDENTITY_PROVIDER_ELEMENT, roa2ip);
                            break;
                        case DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                            var oauth2ServiceProvider:OAuth2ServiceProvider = _currentlySelectedNode.data as OAuth2ServiceProvider;

                            var roa2sp:RemoveOAuth2ServiceProviderElementRequest = new RemoveOAuth2ServiceProviderElementRequest(oauth2ServiceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_OAUTH2_SERVICE_PROVIDER_ELEMENT, roa2sp);
                            break;
                        case DiagramElementTypes.EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT_TYPE:
                            var externalWSFedServiceProvider:ExternalWSFederationServiceProvider = _currentlySelectedNode.data as ExternalWSFederationServiceProvider;

                            var rewsfedsp:RemoveExternalWSFederationServiceProviderElementRequest = new RemoveExternalWSFederationServiceProviderElementRequest(externalWSFedServiceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT, rewsfedsp);
                            break;
                        case DiagramElementTypes.SALESFORCE_ELEMENT_TYPE:
                            var salesforceProvider:SalesforceServiceProvider = _currentlySelectedNode.data as SalesforceServiceProvider;

                            var rsf:RemoveSalesforceElementRequest = new RemoveSalesforceElementRequest(salesforceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_SALESFORCE_ELEMENT, rsf);
                            break;
                        case DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE:
                            var googleAppsProvider:GoogleAppsServiceProvider = _currentlySelectedNode.data as GoogleAppsServiceProvider;

                            var rga:RemoveGoogleAppsElementRequest = new RemoveGoogleAppsElementRequest(googleAppsProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_GOOGLE_APPS_ELEMENT, rga);
                            break;
                        case DiagramElementTypes.SUGAR_CRM_ELEMENT_TYPE:
                            var sugarCRMProvider:SugarCRMServiceProvider = _currentlySelectedNode.data as SugarCRMServiceProvider;

                            var rscrm:RemoveSugarCRMElementRequest = new RemoveSugarCRMElementRequest(sugarCRMProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_SUGAR_CRM_ELEMENT, rscrm);
                            break;
                        case DiagramElementTypes.DB_IDENTITY_VAULT_ELEMENT_TYPE:
                            var dbIdentityVault:DbIdentityVault = _currentlySelectedNode.data as DbIdentityVault;

                            var rdbiv:RemoveIdentityVaultElementRequest = new RemoveIdentityVaultElementRequest(dbIdentityVault);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT, rdbiv);
                            break;
                        case DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE:
                            var identityVault:EmbeddedIdentityVault = _currentlySelectedNode.data as EmbeddedIdentityVault;

                            var riv:RemoveIdentityVaultElementRequest = new RemoveIdentityVaultElementRequest(identityVault);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT, riv);
                            break;
                        case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                            var dbIdentitySource:DbIdentitySource = _currentlySelectedNode.data as DbIdentitySource;

                            var rdiv:RemoveIdentityVaultElementRequest = new RemoveIdentityVaultElementRequest(dbIdentitySource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT, rdiv);
                            break;
                        case DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE:
                            var ldapIdentitySource:LdapIdentitySource = _currentlySelectedNode.data as LdapIdentitySource;

                            var rliv:RemoveIdentityVaultElementRequest = new RemoveIdentityVaultElementRequest(ldapIdentitySource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT, rliv);
                            break;
                        case DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE:
                            var xmlIdentitySource:XmlIdentitySource = _currentlySelectedNode.data as XmlIdentitySource;

                            var rxiv:RemoveIdentityVaultElementRequest = new RemoveIdentityVaultElementRequest(xmlIdentitySource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT, rxiv);
                            break;
                        case DiagramElementTypes.JOSSO1_RESOURCE_ELEMENT_TYPE:
                            var josso1Resource:JOSSO1Resource = _currentlySelectedNode.data as JOSSO1Resource;

                            var rj1r:RemoveJOSSO1ResourceElementRequest = new RemoveJOSSO1ResourceElementRequest(josso1Resource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_JOSSO1_RESOURCE_ELEMENT, rj1r);
                            break;
                        case DiagramElementTypes.JOSSO2_RESOURCE_ELEMENT_TYPE:
                            var josso2Resource:JOSSO2Resource = _currentlySelectedNode.data as JOSSO2Resource;

                            var rj2r:RemoveJOSSO2ResourceElementRequest = new RemoveJOSSO2ResourceElementRequest(josso2Resource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_JOSSO2_RESOURCE_ELEMENT, rj2r);
                            break;
                        case DiagramElementTypes.JBOSSEPP_RESOURCE_ELEMENT_TYPE:
                            var jbosseppResource:JBossEPPResource = _currentlySelectedNode.data as JBossEPPResource;

                            var rjbeppr:RemoveJBossEPPResourceElementRequest = new RemoveJBossEPPResourceElementRequest(jbosseppResource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_JBOSSEPP_RESOURCE_ELEMENT, rjbeppr);
                            break;
                        case DiagramElementTypes.LIFERAY_RESOURCE_ELEMENT_TYPE:
                            var liferayResource:LiferayResource = _currentlySelectedNode.data as LiferayResource;

                            var liferayr:RemoveLiferayResourceElementRequest = new RemoveLiferayResourceElementRequest(liferayResource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_LIFERAY_RESOURCE_ELEMENT, liferayr);
                            break;

                        case DiagramElementTypes.SELFSERVICES_RESOURCE_ELEMENT_TYPE:
                            var selfServicesResource:SelfServicesResource = _currentlySelectedNode.data as SelfServicesResource;

                            var rslfsvcsr:RemoveSelfServicesResourceElementRequest = new RemoveSelfServicesResourceElementRequest(selfServicesResource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_SELFSERVICES_RESOURCE_ELEMENT, rslfsvcsr);
                            break;

                        case DiagramElementTypes.DOMINO_RESOURCE_ELEMENT_TYPE:
                            var dominoResource:DominoResource = _currentlySelectedNode.data as DominoResource;

                            var rdomres:RemoveDominoResourceElementRequest = new RemoveDominoResourceElementRequest(dominoResource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_DOMINO_RESOURCE_ELEMENT, rdomres);
                            break;

                        case DiagramElementTypes.BLACKBOARD_RESOURCE_ELEMENT_TYPE:
                            var blackboardResource:BlackBoardResource = _currentlySelectedNode.data as BlackBoardResource;

                            var rbbres:RemoveBlackBoardResourceElementRequest = new RemoveBlackBoardResourceElementRequest(blackboardResource);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_BLACKBOARD_RESOURCE_ELEMENT, rbbres);
                            break;

                        case DiagramElementTypes.EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                            var execEnv:ExecutionEnvironment = _currentlySelectedNode.data as ExecutionEnvironment;

                            var rev:RemoveExecutionEnvironmentElementRequest = new RemoveExecutionEnvironmentElementRequest(execEnv);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_EXECUTION_ENVIRONMENT_ELEMENT, rev);
                            break;
                        case DiagramElementTypes.WIKID_ELEMENT_TYPE:
                            var wikidService:WikidAuthenticationService = _currentlySelectedNode.data as WikidAuthenticationService;

                            var rwikid:RemoveWikidElementRequest = new RemoveWikidElementRequest(wikidService);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_WIKID_ELEMENT, rwikid);
                            break;
                        case DiagramElementTypes.DOMINO_ELEMENT_TYPE:
                            var dominoService:DominoAuthenticationService = _currentlySelectedNode.data as DominoAuthenticationService;

                            var rdomino:RemoveDominoElementRequest = new RemoveDominoElementRequest(dominoService);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_DOMINO_ELEMENT, rdomino);
                            break;
                        case DiagramElementTypes.CLIENTCERT_ELEMENT_TYPE:
                            var clientCertService:ClientCertAuthnService = _currentlySelectedNode.data as ClientCertAuthnService;

                            var rclientCert:RemoveClientCertElementRequest = new RemoveClientCertElementRequest(clientCertService);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_CLIENTCERT_ELEMENT, rclientCert);
                            break;
                        case DiagramElementTypes.JBOSSEPP_AUTHENTICATION_ELEMENT_TYPE:
                            var jbosseppas:JBossEPPAuthenticationService = _currentlySelectedNode.data as JBossEPPAuthenticationService;

                            var rjbosseppas:RemoveJBossEPPAuthenticationServiceElementRequest =
                                    new RemoveJBossEPPAuthenticationServiceElementRequest(jbosseppas);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_JBOSSEPP_AUTHENTICATION_SERVICE_ELEMENT, rjbosseppas);
                            break;
                        case DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE:
                            var directoryService:DirectoryAuthenticationService = _currentlySelectedNode.data as DirectoryAuthenticationService;

                            var rdirservice:RemoveDirectoryServiceElementRequest = new RemoveDirectoryServiceElementRequest(directoryService);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_DIRECTORY_SERVICE_ELEMENT, rdirservice);
                            break;
                        case DiagramElementTypes.WINDOWS_INTEGRATED_AUTHN_ELEMENT_TYPE:
                            var winIntegratedAuthn:WindowsIntegratedAuthentication = _currentlySelectedNode.data as WindowsIntegratedAuthentication;

                            var rwinauthn:RemoveWindowsIntegratedAuthnElementRequest = new RemoveWindowsIntegratedAuthnElementRequest(winIntegratedAuthn);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.REMOVE_WINDOWS_INTEGRATED_AUTHN_ELEMENT, rwinauthn);
                            break;

                    }
                }

                /*if (_currentlySelectedEdge != null) {
                 GraphDataManager.removeVEdge(_identityApplianceDiagram, _currentlySelectedEdge.vedge, true);
                 _currentlySelectedEdge = null;
                 sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                 }*/

                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE:
                var element:Object = projectProxy.currentIdentityApplianceElement;
                if (element is IdentityLookup) {
                    var identityLookup1:IdentityLookup = element as IdentityLookup;
                    var providerNode:IVisualNode = findNodeElementBySemanticElement(identityLookup1.provider);
                    var identitySourceNode:IVisualNode = findNodeElementBySemanticElement(identityLookup1.identitySource);
                    GraphDataManager.linkVNodes(_identityApplianceDiagram, identitySourceNode, providerNode,
                            identityLookup1,EmbeddedIcons.identityLookupMiniIcon,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "identity.lookup.connection"));                    
                    _identityApplianceDiagram.exitConnectionMode();
                } else if (element is FederatedConnection) {
                    var federatedConnection1:FederatedConnection = element as FederatedConnection;
                    var provider1Node:IVisualNode = findNodeElementBySemanticElement(federatedConnection1.roleA);
                    var provider2Node:IVisualNode = findNodeElementBySemanticElement(federatedConnection1.roleB);
                    GraphDataManager.linkVNodes(_identityApplianceDiagram, provider2Node, provider1Node,
                            federatedConnection1, EmbeddedIcons.federatedConnectionMiniIcon,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "federated.connection"));
                    _identityApplianceDiagram.exitConnectionMode();
                } else if (element is ServiceConnection) {
                    var serviceConnection1:ServiceConnection = element as ServiceConnection;
                    var provider3Node:IVisualNode = findNodeElementBySemanticElement(serviceConnection1.sp);
                    var resourceNode:IVisualNode = findNodeElementBySemanticElement(serviceConnection1.resource);
                    GraphDataManager.linkVNodes(_identityApplianceDiagram, resourceNode, provider3Node,
                            serviceConnection1,EmbeddedIcons.serviceConnectionMiniIcon,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "service.connection"));
                    _identityApplianceDiagram.exitConnectionMode();
                } else if (element is JOSSOActivation) {
                    var activation1:JOSSOActivation = element as JOSSOActivation;
                    var rNode:IVisualNode = findNodeElementBySemanticElement(activation1.resource);
                    var execEnvNode:IVisualNode = findNodeElementBySemanticElement(activation1.executionEnv);

                    if (!(execEnvNode is CaptiveExecutionEnvironment)) {
                        GraphDataManager.linkVNodes(_identityApplianceDiagram, execEnvNode, rNode,
                                activation1, EmbeddedIcons.activationMiniIcon,
                                resourceManager.getString(AtricoreConsole.BUNDLE, "activation.connection"));
                    }
                    _identityApplianceDiagram.exitConnectionMode();

                } else if (element is DelegatedAuthentication) {
                    var delegatedAuthentication1:DelegatedAuthentication = element as DelegatedAuthentication;
                    var idpNode:IVisualNode = findNodeElementBySemanticElement(delegatedAuthentication1.idp);
                    var authnServiceNode:IVisualNode = findNodeElementBySemanticElement(delegatedAuthentication1.authnService);
                    GraphDataManager.linkVNodes(_identityApplianceDiagram, authnServiceNode, idpNode,
                            delegatedAuthentication1,EmbeddedIcons.identityVerificationMiniIcon,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "delegated.authentication.connection"));
                    _identityApplianceDiagram.exitConnectionMode();
                } else {
                    GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), element, null, null, null, null, true, Constants.IDENTITY_BUS_DEEP);
                }

                view.callLater(function ():void {
                    sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_SELECTED);
                });

                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE:
                var element1:Object = notification.getBody();
                if (element1 is IdentityLookup) {
                    var identityLookup2:IdentityLookup = element1 as IdentityLookup;
                    var edge1:IVisualEdge = findEdgeElementBySemanticElement(identityLookup2);
                    GraphDataManager.removeVEdge(_identityApplianceDiagram, edge1, true);
                } else if (element1 is FederatedConnection) {
                    var federatedConnection2:FederatedConnection = element1 as FederatedConnection;
                    var edge2:IVisualEdge = findEdgeElementBySemanticElement(federatedConnection2);
                    GraphDataManager.removeVEdge(_identityApplianceDiagram, edge2, true);
                } else if (element1 is JOSSOActivation) {
                    var activation2:JOSSOActivation = element1 as JOSSOActivation;
                    var edge3:IVisualEdge = findEdgeElementBySemanticElement(activation2);
                    GraphDataManager.removeVEdge(_identityApplianceDiagram, edge3, true);
                } else if (element1 is DelegatedAuthentication) {
                    var delegatedAuthentication2:DelegatedAuthentication = element1 as DelegatedAuthentication;
                    var edge4:IVisualEdge = findEdgeElementBySemanticElement(delegatedAuthentication2);
                    GraphDataManager.removeVEdge(_identityApplianceDiagram, edge4, true);
                } else if (element1 is ServiceConnection) {
                    var serviceConnection2:ServiceConnection = element1 as ServiceConnection;
                    var edge5:IVisualEdge = findEdgeElementBySemanticElement(serviceConnection2);
                    GraphDataManager.removeVEdge(_identityApplianceDiagram, edge5, true);
                } else {
                    var node:IVisualNode = findNodeElementBySemanticElement(element1);
                    GraphDataManager.removeNode(_identityApplianceDiagram, node, true);
                }
                unselectAllNodes();
                unselectAllEdges();
                break;
        }

    }

    private function updateIdentityAppliance():void {

        _identityAppliance = projectProxy.currentIdentityAppliance;
    }

    private function updateGraph(circularLayout:Boolean = false):void {

        if (_identityAppliance != null) {
            _applianceId = _identityAppliance.id.toString();
        } else {
            _applianceId = null;
        }

        resetGraph(circularLayout);
        updateGraphTitle();

        var providerNodes:Dictionary = new Dictionary();

        if (_identityAppliance != null) {
            
            view.graphViewport.horizontalScrollPosition = (view.graphCanvas.width - view.graphScroller.horizontalScrollBar.width) / 2;
            view.graphViewport.verticalScrollPosition = (view.graphCanvas.height - view.graphScroller.verticalScrollBar.height) / 2;
            view.graphScroller.visible = true;

            view.graphCanvas.redrawGraphGrid();
            view.graphCanvas.drawGrid = true;

            var identityApplianceDefinition:IdentityApplianceDefinition = _identityAppliance.idApplianceDefinition;

            //            var rootGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), _identityAppliance, null, true, Constants.IDENTITY_BUS_DEEP);
            //            rootGraphNode.isVisible = false;

            var vaultNodes:ArrayCollection = new ArrayCollection();
            if (identityApplianceDefinition.identitySources != null) {
                for(var k:int=0; k < identityApplianceDefinition.identitySources.length; k++){
                    var identityVaultGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityApplianceDefinition.identitySources[k], null, null, null, null, true, Constants.IDENTITY_VAULT_DEEP);
                    vaultNodes.addItem(identityVaultGraphNode);
                }
            }

            var environmentNodes:ArrayCollection = new ArrayCollection();
            if (identityApplianceDefinition.executionEnvironments != null) {
                for(var l:int=0; l < identityApplianceDefinition.executionEnvironments.length; l++){
                    if (!(identityApplianceDefinition.executionEnvironments[l] is CaptiveExecutionEnvironment)) {
                        var execEnvGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityApplianceDefinition.executionEnvironments[l], null, null, null, null, true, Constants.EXEC_ENVIRONMENT_DEEP);
                        environmentNodes.addItem(execEnvGraphNode);
                    }
                }
            }

            var serviceResourceNodes:ArrayCollection = new ArrayCollection();
            if (identityApplianceDefinition.serviceResources != null) {
                for(var r:int=0; r < identityApplianceDefinition.serviceResources.length; r++){
                    var serviceResourceGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityApplianceDefinition.serviceResources[r], null, null, null, null, true, Constants.SERVICE_RESOURCE_DEEP);
                    serviceResourceNodes.addItem(serviceResourceGraphNode);
                    if (identityApplianceDefinition.serviceResources[r].activation != null) {
                        for each (var tmpExecEnvGraphNode:IVisualNode in environmentNodes) {
                            var ee : ExecutionEnvironment = tmpExecEnvGraphNode.data as ExecutionEnvironment;
                            var sr : ServiceResource = identityApplianceDefinition.serviceResources[r];
                            if ((!(ee is CaptiveExecutionEnvironment)) && ee == sr.activation.executionEnv) {
                                GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpExecEnvGraphNode, serviceResourceGraphNode,
                                        sr.activation, EmbeddedIcons.activationIcon,
                                        resourceManager.getString(AtricoreConsole.BUNDLE, "activation.connection"));
                                break;
                            }
                        }
                    }
                }
            }

            var authnServiceNodes:ArrayCollection = new ArrayCollection();
            if (identityApplianceDefinition.authenticationServices != null) {
                for(var m:int=0; m < identityApplianceDefinition.authenticationServices.length; m++){
                    var authnServiceGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityApplianceDefinition.authenticationServices[m], null, null, null, null, true, Constants.AUTHENTICATION_SERVICE_DEEP);
                    authnServiceNodes.addItem(authnServiceGraphNode);
                }
            }

            if (identityApplianceDefinition.providers != null) {
                for (var i:int = 0; i < identityApplianceDefinition.providers.length; i++) {
                    var provider:Provider = identityApplianceDefinition.providers[i];
                    var providerGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), provider, null, null, null, null, true, Constants.PROVIDER_DEEP);
                    providerNodes[provider] = providerGraphNode;

                    if (provider is FederatedProvider) {
                        var locProv:FederatedProvider = provider as FederatedProvider;
                        if(locProv.identityLookups != null){
                            for each (var identityLookup:IdentityLookup in locProv.identityLookups) {
                                var idSource:IdentitySource = identityLookup.identitySource;

                                if (idSource == null)
                                    continue;
                                //TODO add identitySource and connection towards it
                                var vaultExists:Boolean = false;
                                for each (var tmpVaultGraphNode:IVisualNode in vaultNodes){
                                    if(tmpVaultGraphNode.data as IdentitySource == idSource){
                                        GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpVaultGraphNode, providerGraphNode,
                                                identityLookup ,EmbeddedIcons.identityLookupIcon,
                                                resourceManager.getString(AtricoreConsole.BUNDLE, "identity.lookup.connection"));

                                        vaultExists = true;
                                    }
                                }
                                if(!vaultExists){
                                    var newVaultNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), idSource, providerGraphNode,
                                            identityLookup, EmbeddedIcons.identityLookupIcon,
                                            resourceManager.getString(AtricoreConsole.BUNDLE, "identity.lookup.connection"),
                                            true, Constants.IDENTITY_VAULT_DEEP);
                                    //if vault doesn't exist in the vaults array, add it so other providers can find it
                                    vaultNodes.addItem(newVaultNode);
                                }

                            }
                        }
                        //                            }
                        if(locProv is InternalSaml2ServiceProvider){
                            var sp:InternalSaml2ServiceProvider = locProv as InternalSaml2ServiceProvider;

                            if (sp.serviceConnection != null && sp.serviceConnection.resource != null) {  //check for service resource
                                var resourceExists:Boolean = false;
                                for each (var tmpServiceResourceGraphNode:IVisualNode in serviceResourceNodes) {
                                    if (tmpServiceResourceGraphNode.data as ServiceResource == sp.serviceConnection.resource) {
                                        GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpServiceResourceGraphNode, providerGraphNode,
                                                sp.serviceConnection, EmbeddedIcons.serviceConnectionIcon,
                                                resourceManager.getString(AtricoreConsole.BUNDLE, "service.connection"));
                                        resourceExists = true;
                                    }
                                }
                                if (!resourceExists) {
                                    var newServiceResourceNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), sp.serviceConnection.resource, providerGraphNode,
                                            sp.serviceConnection, EmbeddedIcons.serviceConnectionIcon,
                                            resourceManager.getString(AtricoreConsole.BUNDLE, "service.connection"),
                                            true, Constants.SERVICE_RESOURCE_DEEP);
                                    //if service resource doesn't exist in the resources array, add it so other providers can find it
                                    serviceResourceNodes.addItem(newServiceResourceNode);
                                }
                            }

                            /*if(sp.activation != null && sp.activation.executionEnv != null){  //check for execution environment
                                var environmentExists:Boolean = false;
                                for each (var tmpExecEnvGraphNode:IVisualNode in environmentNodes){
                                    if(tmpExecEnvGraphNode.data as ExecutionEnvironment == sp.activation.executionEnv){
                                        GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpExecEnvGraphNode, providerGraphNode,
                                                sp.activation, EmbeddedIcons.connectionActivationIcon,
                                                resourceManager.getString(AtricoreConsole.BUNDLE, "activation.connection"));
                                        environmentExists = true;
                                    }
                                }
                                if(!environmentExists){
                                    var newExecEnvNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), sp.activation.executionEnv, providerGraphNode,
                                            sp.activation,EmbeddedIcons.connectionActivationIcon,
                                            resourceManager.getString(AtricoreConsole.BUNDLE, "activation.connection"), 
                                            true, Constants.EXEC_ENVIRONMENT_DEEP);
                                    //if vault doesn't exist in the vaults array, add it so other providers can find it
                                    environmentNodes.addItem(newExecEnvNode);
                                }
                            }*/
                        }
                        if (locProv is IdentityProvider) {
                            var idp:IdentityProvider = locProv as IdentityProvider;
                            if (idp.delegatedAuthentications != null) {
                                for each (var delegatedAuthentication:DelegatedAuthentication in idp.delegatedAuthentications) {
                                    var authnServiceExists:Boolean = false;
                                    for each (var tmpAuthnServiceGraphNode:IVisualNode in authnServiceNodes) {  //check for authn. service
                                        if (tmpAuthnServiceGraphNode.data as AuthenticationService == delegatedAuthentication.authnService) {
                                            GraphDataManager.linkVNodes(_identityApplianceDiagram, tmpAuthnServiceGraphNode, providerGraphNode,
                                                    delegatedAuthentication, EmbeddedIcons.identityVerificationIcon,
                                                    resourceManager.getString(AtricoreConsole.BUNDLE, "delegated.authentication.connection"));
                                            authnServiceExists = true;
                                        }
                                    }
                                    if (!authnServiceExists) {
                                        var newAuthnServiceNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram,
                                                UIDUtil.createUID(), delegatedAuthentication.authnService, providerGraphNode,
                                                delegatedAuthentication, EmbeddedIcons.identityVerificationIcon,
                                                resourceManager.getString(AtricoreConsole.BUNDLE, "delegated.authentication.connection"),
                                                true, Constants.AUTHENTICATION_SERVICE_DEEP);
                                        //if authn. service doesn't exist in the authn. service array, add it so other providers can find it
                                        authnServiceNodes.addItem(newAuthnServiceNode);
                                    }
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
                            var graphNodeRoleA1:IVisualNode = providerNodes[fedProvider];
                            var graphNodeRoleB1:IVisualNode = providerNodes[fedConnA.roleB];
                            if(!DiagramUtil.nodeLinkExists(graphNodeRoleA1.node, graphNodeRoleB1.node)){ //avoid double linking
                                GraphDataManager.linkVNodes(_identityApplianceDiagram, graphNodeRoleA1, graphNodeRoleB1,
                                        fedConnA, EmbeddedIcons.federatedConnectionIcon,
                                        resourceManager.getString(AtricoreConsole.BUNDLE, "federated.connection"));
                            }
                        }
                        for each (var fedConnB:FederatedConnection in fedProvider.federatedConnectionsB){
                            var graphNodeRoleA2:IVisualNode = providerNodes[fedConnB.roleA];
                            var graphNodeRoleB2:IVisualNode = providerNodes[fedProvider];
                            if(!DiagramUtil.nodeLinkExists(graphNodeRoleA2.node, graphNodeRoleB2.node)){ //avoid double linking
                                GraphDataManager.linkVNodes(_identityApplianceDiagram, graphNodeRoleA2, graphNodeRoleB2,
                                        fedConnB, EmbeddedIcons.federatedConnectionIcon,
                                        resourceManager.getString(AtricoreConsole.BUNDLE, "federated.connection"));
                            }
                        }
                    }
                }
            }

            if (_identityApplianceDiagram.layouter is CircularLayouter) {
                // make sure circular positioning finished
                setTimeout(updateElementsPosition, 1000);
            }
            
            var layouter:BaseLayouter = new BaseLayouter(_identityApplianceDiagram);
            layouter.autoFitEnabled = _autoFitEnabled;
            _identityApplianceDiagram.layouter = layouter;
        } else {
            view.graphCanvas.drawGrid = false;
            view.graphCanvas.removeGraphGrid();
            view.graphScroller.visible = false;
        }

    }

    private function updateGraphData():void {
        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;
        if (identityAppliance != null) {
            updateGraphTitle();

            var identityApplianceDefinition:IdentityApplianceDefinition = identityAppliance.idApplianceDefinition;

            if (identityApplianceDefinition.identitySources != null) {
                for each (var identitySource:IdentitySource in identityApplianceDefinition.identitySources) {
                    updateGraphNodeData(identitySource);
                }
            }

            if (identityApplianceDefinition.executionEnvironments != null) {
                for each (var execEnv:ExecutionEnvironment in identityApplianceDefinition.executionEnvironments) {
                    updateGraphNodeData(execEnv);
                }
            }

            if (identityApplianceDefinition.serviceResources != null) {
                for each (var serviceResource:ServiceResource in identityApplianceDefinition.serviceResources) {
                    updateGraphNodeData(serviceResource);
                    if (serviceResource.activation != null && serviceResource.activation.executionEnv != null &&
                        (!(serviceResource.activation.executionEnv is CaptiveExecutionEnvironment))) {
                        updateGraphEdgeData(serviceResource.activation);
                    }
                }
            }

            if (identityApplianceDefinition.authenticationServices != null) {
                for each (var authenticationService:AuthenticationService in identityApplianceDefinition.authenticationServices) {
                    updateGraphNodeData(authenticationService);
                }
            }

            if (identityApplianceDefinition.providers != null) {
                for each (var provider:Provider in identityApplianceDefinition.providers) {
                    updateGraphNodeData(provider);
                    if (provider is FederatedProvider) {
                        var locProv:FederatedProvider = provider as FederatedProvider;
                        if (locProv.identityLookups != null) {
                            for each (var identityLookup:IdentityLookup in locProv.identityLookups) {
                                var idSource:IdentitySource = identityLookup.identitySource;

                                if (idSource == null)
                                    continue;

                                updateGraphEdgeData(identityLookup);
                            }
                        }
                        if (locProv is InternalSaml2ServiceProvider) {
                            var sp:InternalSaml2ServiceProvider = locProv as InternalSaml2ServiceProvider;
                            if (sp.serviceConnection != null) {
                                updateGraphEdgeData(sp.serviceConnection);
                            }
                        }
                        if (locProv is IdentityProvider) {
                            var idp:IdentityProvider = locProv as IdentityProvider;
                            if (idp.delegatedAuthentications != null) {
                                for each (var delegatedAuthentication:DelegatedAuthentication in idp.delegatedAuthentications) {
                                    updateGraphEdgeData(delegatedAuthentication);
                                }
                            }
                        }
                        for each (var fedConnA:FederatedConnection in locProv.federatedConnectionsA) {
                            updateGraphEdgeData(fedConnA);
                        }
                        for each (var fedConnB:FederatedConnection in locProv.federatedConnectionsB) {
                            updateGraphEdgeData(fedConnB);
                        }
                    }
                }
            }
        }
    }

    private function updateGraphNodeData(data:Object):void {
        for each (var node:INode in _identityApplianceDiagram.graph.nodes) {
            if (node.data != null && node.data.name == data.name) {
                node.data = data;
                node.vnode.data = data;
                break;
            }
        }
    }

    private function updateGraphEdgeData(data:Object):void {
        for each (var edge:IEdge in _identityApplianceDiagram.graph.edges) {
            if (edge.data != null && edge.data.data != null && edge.data.data.name == data.name) {
                edge.data.data = data;
                edge.vedge.data.data = data;
                break;
            }
        }
    }

    private function updateGraphTitle():void {
        if (projectProxy.currentIdentityAppliance != null) {
            view.title = projectProxy.currentIdentityAppliance.idApplianceDefinition.name;
        } else {
            view.title = "";
        }
    }

    private function resetGraph(circularLayout:Boolean = false):void {
        var graph:IGraph = new EnhancedGraph("Graph", true);
        var vo:Object = TypeUtil.deserializeXMLString(_emptyNotationModel);
        EnhancedGraph(graph).initFromVO(vo);
        _identityApplianceDiagram.graph = graph;

        _identityApplianceDiagram.graph.purgeGraph();
        _identityApplianceDiagram.newNodesDefaultVisible = true;

        _autoFitEnabled = true;

        if (circularLayout) {
            var circularLayouter:CircularLayouter = new CircularLayouter(_identityApplianceDiagram);
            circularLayouter.disableAnimation = true;
            circularLayouter.autoFitEnabled = _autoFitEnabled;
            _identityApplianceDiagram.layouter = circularLayouter;
        } else {
            var baseLayouter:BaseLayouter = new BaseLayouter(_identityApplianceDiagram);
            baseLayouter.autoFitEnabled = _autoFitEnabled;
            _identityApplianceDiagram.layouter = baseLayouter;
        }

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
        view.graphCanvas.drawGrid = false;
        view.graphCanvas.removeGraphGrid();
        _currentlySelectedNode = null;
        _currentlySelectedEdge = null;

        _identityApplianceDiagram.resetGraph();
        
        view.title = "";
    }

    private function nodeSelectedEventHandler(event:VNodeSelectedEvent):void
    {
        var node:INode = _identityApplianceDiagram.graph.nodeByStringId(event.vnodeId);

        if (node != null) {
            _projectProxy.currentIdentityApplianceElement = node.data;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_SELECTED);
        }
    }

    private function nodeMovedEventHandler(event:VNodeMovedEvent):void {
        var node:INode = _identityApplianceDiagram.graph.nodeByStringId(event.vnodeId);
        if (node != null) {
            node.data.x = node.vnode.viewX + (0.2 * node.vnode.view.width) / 2.4;
            node.data.y = node.vnode.viewY + (0.2 * node.vnode.view.height) / 2.4;;
        }
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED, "nodesMoved");
    }

    private function allNodesMovedEventHandler(event:VNodeMovedEvent):void {
        for each (var node:INode in _identityApplianceDiagram.graph.nodes) {
            if (node.vnode.view is NodeDetailedRenderer) {
                node.data.x = node.vnode.viewX;
                node.data.y = node.vnode.viewY;
            }
        }
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED, "nodesMoved");
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

                // Providers :
            } else if (node.data is IdentityProvider) {
                elementType = DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is InternalSaml2ServiceProvider) {
                elementType = DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is ExternalSaml2IdentityProvider) {
                elementType = DiagramElementTypes.EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is ExternalSaml2ServiceProvider) {
                elementType = DiagramElementTypes.EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is ExternalOpenIDIdentityProvider) {
                elementType = DiagramElementTypes.EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is OAuth2ServiceProvider) {
                elementType = DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is ExternalWSFederationServiceProvider) {
                elementType = DiagramElementTypes.EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT_TYPE;
            } else if (node.data is SalesforceServiceProvider) {

                // Cloud Providers :
                elementType = DiagramElementTypes.SALESFORCE_ELEMENT_TYPE;
            } else if (node.data is GoogleAppsServiceProvider) {
                elementType = DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE;
            } else if (node.data is SugarCRMServiceProvider) {
                elementType = DiagramElementTypes.SUGAR_CRM_ELEMENT_TYPE;

                // Authentication :
            } else if (node.data is WikidAuthenticationService) {
                elementType = DiagramElementTypes.WIKID_ELEMENT_TYPE;
            } else if (node.data is DirectoryAuthenticationService) {
                elementType = DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE;
            } else if (node.data is WindowsIntegratedAuthentication) {
                elementType = DiagramElementTypes.WINDOWS_INTEGRATED_AUTHN_ELEMENT_TYPE;
            } else if (node.data is DominoAuthenticationService) {
                elementType = DiagramElementTypes.DOMINO_ELEMENT_TYPE;
            } else if (node.data is ClientCertAuthnService) {
                elementType = DiagramElementTypes.CLIENTCERT_ELEMENT_TYPE;
            } else if (node.data is DirectoryAuthenticationService) {

                //Identity Sources
            } else if (node.data is DbIdentitySource) {
                elementType = DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE;
            } else if (node.data is EmbeddedIdentityVault) {
                elementType = DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE;
            } else if (node.data is DbIdentityVault) {
                elementType = DiagramElementTypes.DB_IDENTITY_VAULT_ELEMENT_TYPE;
            } else if (node.data is XmlIdentitySource) {
                elementType = DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE;
            } else if (node.data is LdapIdentitySource) {
                elementType = DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE;
            } else if (node.data is JBossEPPAuthenticationService) {
                elementType = DiagramElementTypes.JBOSSEPP_AUTHENTICATION_ELEMENT_TYPE;
                // Resources
            } else if (node.data is JBossEPPResource) {
                elementType = DiagramElementTypes.JBOSSEPP_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is LiferayResource) {
                elementType = DiagramElementTypes.LIFERAY_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is SelfServicesResource) {
                elementType = DiagramElementTypes.SELFSERVICES_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is DominoResource) {
                elementType = DiagramElementTypes.DOMINO_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is BlackBoardResource) {
                elementType = DiagramElementTypes.BLACKBOARD_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is JOSSO1Resource) {
                elementType = DiagramElementTypes.JOSSO1_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is JOSSO2Resource) {
                elementType = DiagramElementTypes.JOSSO2_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is ServiceResource) {
                elementType = DiagramElementTypes.SERVICE_RESOURCE_ELEMENT_TYPE;
            } else if (node.data is ExecutionEnvironment) {
                elementType = DiagramElementTypes.EXECUTION_ENVIRONMENT_ELEMENT_TYPE;
            }
            // TODO : Add new elements not already supported

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE, elementType);
        }
    }

    private function federatedConnectionCreatedEventHandler(event:VNodesLinkedEvent):void {
        var node1:IVisualNode = event.vnode1;
        var node2:IVisualNode = event.vnode2;

        var cfc:CreateFederatedConnectionElementRequest = new CreateFederatedConnectionElementRequest();
        cfc.roleA = node1.node.data as FederatedProvider;
        cfc.roleB = node2.node.data as FederatedProvider
        sendNotification(ApplicationFacade.CREATE_FEDERATED_CONNECTION, cfc);
    }

    private function serviceConnectionCreatedEventHandler(event:VNodesLinkedEvent):void {
        var node1:IVisualNode = event.vnode1;
        var node2:IVisualNode = event.vnode2;

        var csc:CreateServiceConnectionElementRequest = new CreateServiceConnectionElementRequest();
        if(node1.data is InternalSaml2ServiceProvider && node2.data is ServiceResource) {
            csc.sp = node1.data as InternalSaml2ServiceProvider;
            csc.resource = node2.data as ServiceResource;
        } else if (node1.data is ServiceResource && node2.data is InternalSaml2ServiceProvider) {
            csc.sp = node2.data as InternalSaml2ServiceProvider;
            csc.resource = node1.data as ServiceResource;
        }
        sendNotification(ApplicationFacade.CREATE_SERVICE_CONNECTION, csc);
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
        if(node1.data is ServiceResource && node2.data is ExecutionEnvironment){
            car.serviceResource = node1.data as ServiceResource;
            car.executionEnvironment = node2.data as ExecutionEnvironment;
        } else if (node1.data is ExecutionEnvironment && node2.data is ServiceResource){
            car.serviceResource = node2.data as ServiceResource;
            car.executionEnvironment = node1.data as ExecutionEnvironment;
        }
        sendNotification(ApplicationFacade.CREATE_ACTIVATION, car);
    }

    private function delegatedAuthenticationCreatedEventHandler(event:VNodesLinkedEvent):void {
        var node1:IVisualNode = event.vnode1;
        var node2:IVisualNode = event.vnode2;

        var cdar:CreateDelegatedAuthnElementRequest = new CreateDelegatedAuthnElementRequest();
        if (node1.data is IdentityProvider && node2.data is AuthenticationService) {
            cdar.idp = node1.data as IdentityProvider;
            cdar.authnService = node2.data as AuthenticationService;
        } else if (node1.data is AuthenticationService && node2.data is IdentityProvider){
            cdar.idp = node2.data as IdentityProvider;
            cdar.authnService = node1.data as AuthenticationService;
        }
        sendNotification(ApplicationFacade.CREATE_DELEGATED_AUTHENTICATION, cdar);
    }

    private function edgeSelectedEventHandler(event:VEdgeSelectedEvent):void {
        var edge:IEdge = event.edge;

        if (edge != null) {
            unselectAllNodes();
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
        } else if (edgeData is ServiceConnection){
            var serviceConnection:ServiceConnection = edgeData as ServiceConnection;
            var rsce:RemoveServiceConnectionElementRequest = new RemoveServiceConnectionElementRequest(serviceConnection);
            sendNotification(ApplicationFacade.REMOVE_SERVICE_CONNECTION_ELEMENT, rsce);
        } else if (edgeData is Activation){
            var activation:JOSSOActivation = edgeData as JOSSOActivation;
            var ract:RemoveActivationElementRequest = new RemoveActivationElementRequest(activation);
            sendNotification(ApplicationFacade.REMOVE_ACTIVATION_ELEMENT, ract);
        } else if (edgeData is IdentityLookup){
            var identityLookup:IdentityLookup = edgeData as IdentityLookup;
            var ril:RemoveIdentityLookupElementRequest = new RemoveIdentityLookupElementRequest(identityLookup);
            sendNotification(ApplicationFacade.REMOVE_IDENTITY_LOOKUP_ELEMENT, ril);
        } else if (edgeData is DelegatedAuthentication){
            var delegatedAuthentication:DelegatedAuthentication = edgeData as DelegatedAuthentication;
            var rda:RemoveDelegatedAuthnElementRequest = new RemoveDelegatedAuthnElementRequest(delegatedAuthentication);
            sendNotification(ApplicationFacade.REMOVE_DELEGATED_AUTHENTICATION_ELEMENT, rda);
        }
    }

    private function linkingCanceledEventHandler(event:VNodesLinkedEvent):void {
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
    }

    private function openDialogElementCreationFormEventHandler(event:VNodeCreationEvent):void {
        sendNotification(ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM, event.elementType);
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

    private function unselectAllNodes():void {
        for each (var node:INode in _identityApplianceDiagram.graph.nodes) {
            if (node.vnode.view is NodeDetailedRenderer) {
                (node.vnode.view as NodeDetailedRenderer).nodeBtn.selected = false;
            }
        }
        _currentlySelectedNode = null;
    }

    private function unselectAllEdges():void {
        for each (var edge:IEdge in _identityApplianceDiagram.graph.edges) {
            edge.vedge.lineStyle.color = 0xCCCCCC;
        }
        _currentlySelectedEdge = null;
        _identityApplianceDiagram.refresh();
    }

    private function toggleGraphElementOnByData(semanticElement:Object):void {
        _currentlySelectedNode = null;
        _currentlySelectedEdge = null;

        for each (var node:INode in _identityApplianceDiagram.graph.nodes) {
            if (node.data != null && node.data == semanticElement) {
                (node.vnode.view as NodeDetailedRenderer).nodeBtn.selected = true;
                _currentlySelectedNode = node;
            } else {
                (node.vnode.view as NodeDetailedRenderer).nodeBtn.selected = false;
            }
        }

        for each (var edge:IEdge in _identityApplianceDiagram.graph.edges) {
            if (edge.data != null && edge.data.data == semanticElement) {
                _currentlySelectedEdge = edge;
                edge.vedge.lineStyle.color = 0x6B8E23;
            } else {
                edge.vedge.lineStyle.color = 0xCCCCCC;
            }
        }

        _identityApplianceDiagram.refresh();
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

    private function findNodeElementBySemanticElement(semanticElement:Object):IVisualNode {
        var foundNode:IVisualNode;

        for each (var node:INode in _identityApplianceDiagram.graph.nodes) {
            if (node.data != null && node.data == semanticElement) {
                foundNode = node.vnode;
                break;
            }
        }

        return foundNode;
    }

    private function findEdgeElementBySemanticElement(semanticElement:Object):IVisualEdge {
        var foundEdge:IVisualEdge;

        for each (var edge:IEdge in _identityApplianceDiagram.graph.edges) {
            if (edge.data != null && edge.data.data == semanticElement) {
                foundEdge = edge.vedge;
                break;
            }
        }

        return foundEdge;
    }

    /*
     * Update all elements position and silently save identity appliance
     * (this is used in case identity appliance was created using SSO wizard,
     * because positions will be known at the end of circular layout animation).
     */
    private function updateElementsPosition():void {
        for each (var node:INode in _identityApplianceDiagram.graph.nodes) {
            if (node.vnode.view is NodeDetailedRenderer) {
                node.data.x = node.vnode.viewX;
                node.data.y = node.vnode.viewY;
            }
        }
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE, true);
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

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null
        resetGraph();
        _currentIdentityApplianceId= Number.MIN_VALUE;
        view.graphScroller.visible = false;
    }
}
}
