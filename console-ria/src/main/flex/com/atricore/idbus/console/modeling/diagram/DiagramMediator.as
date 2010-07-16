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

package com.atricore.idbus.console.modeling.diagram {
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdpChannelElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveServiceProviderElementRequest;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderChannelDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderDTO;

import com.atricore.idbus.console.services.dto.IdentityVaultDTO;
import com.atricore.idbus.console.services.dto.LocalProviderDTO;
import com.atricore.idbus.console.services.dto.ProviderDTO;

import com.atricore.idbus.console.services.dto.ServiceProviderChannelDTO;

import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

import flash.display.DisplayObject;
import flash.events.MouseEvent;

import mx.controls.Button;
import mx.core.ClassFactory;
import mx.core.Container;
import mx.utils.UIDUtil;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.util.Constants;
import com.atricore.idbus.console.modeling.browser.model.BrowserModelFactory;
import com.atricore.idbus.console.modeling.browser.model.BrowserNode;
import com.atricore.idbus.console.modeling.diagram.event.VNodeRemoveEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.model.GraphDataManager;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.renderers.edgelabel.BaseEdgeLabelRendered;
import com.atricore.idbus.console.modeling.diagram.renderers.node.NodeDetailedRenderer;
import com.atricore.idbus.console.modeling.main.ModelerView;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;
import org.un.cava.birdeye.ravis.graphLayout.data.Graph;
import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.layout.HierarchicalLayouter;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.graphLayout.visual.VisualGraph;
import org.un.cava.birdeye.ravis.graphLayout.visual.edgeRenderers.BaseEdgeRenderer;
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class
DiagramMediator extends Mediator {

    public static const BUNDLE:String = "console";

    public static const ORIENTATION_MENU_ITEM_INDEX:int = 3;

    private var _identityApplianceDiagram:VisualGraph;

    private var _identityAppliance:IdentityApplianceDTO;

    private var _applianceId:String;

    private var _autoFitEnabled:Boolean;

    private var _selectedOrientation:uint;

    private var zoomMax:Number = 2;

    private var zoomMin:Number = .25;

    private var _emptyNotationModel:XML;

    public static const NAME:String = "com.atricore.idbus.console.modeling.diagram.DiagramMediator";
    private var _currentlySelectedNode:INode;
    private var _projectProxy:ProjectProxy;

    public function DiagramMediator(viewComp:DiagramView) {
        super(NAME, viewComp);

        _identityApplianceDiagram = viewComp.identityApplianceDiagram;
        _identityApplianceDiagram.addEventListener(VNodeSelectedEvent.VNODE_SELECTED, nodeSelectedEventHandler);
        _identityApplianceDiagram.addEventListener(VNodeRemoveEvent.VNODE_REMOVE, nodeRemoveEventHandler);
        _emptyNotationModel = <Graph/>;
        _projectProxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.NOTE_DRAG_ELEMENT_TO_DIAGRAM,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_REMOVE
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE:
                updateIdentityAppliance();
                init();
                break;
            case ApplicationFacade.NOTE_DRAG_ELEMENT_TO_DIAGRAM:
                if (_currentlySelectedNode != null) {
                    var elementType:int = notification.getBody() as int;


                    switch (elementType) {
                        case DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE:
                            // assert that source end is an Identity Appliance
                            if (_currentlySelectedNode.data is IdentityApplianceDTO) {
                                var ownerIdentityAppliance:IdentityApplianceDTO = _currentlySelectedNode.data as IdentityApplianceDTO;

                                var cip:CreateIdentityProviderElementRequest = new CreateIdentityProviderElementRequest(
                                        ownerIdentityAppliance,
                                        _currentlySelectedNode.stringid
                                        );

                                // this notification will be grabbed by the modeler mediator which will open
                                // the corresponding form
                                sendNotification(ApplicationFacade.NOTE_CREATE_IDENTITY_PROVIDER_ELEMENT, cip);
                            }


                            break;
                        case DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE:
                            // assert that source end is an Identity Appliance
                            if (_currentlySelectedNode.data is IdentityApplianceDTO) {
                                var ownerIdentityAppliance:IdentityApplianceDTO = _currentlySelectedNode.data as IdentityApplianceDTO;

                                var csp:CreateServiceProviderElementRequest = new CreateServiceProviderElementRequest(
                                        ownerIdentityAppliance,
                                        _currentlySelectedNode.stringid
                                        );

                                // this notification will be grabbed by the modeler mediator which will open
                                // the corresponding form
                                sendNotification(ApplicationFacade.NOTE_CREATE_SERVICE_PROVIDER_ELEMENT, csp);
                            }


                            break;
                        case DiagramElementTypes.IDP_CHANNEL_ELEMENT_TYPE:
                            // assert that source end is an Identity Appliance
                            if (_currentlySelectedNode.data is ServiceProviderDTO) {
                                var ownerServiceProvider:ServiceProviderDTO = _currentlySelectedNode.data as ServiceProviderDTO;

                                var cidpc:CreateIdpChannelElementRequest = new CreateIdpChannelElementRequest(
                                        ownerServiceProvider,
                                        _currentlySelectedNode.stringid
                                        );
                                _projectProxy.currentIdentityApplianceElementOwner = ownerServiceProvider;
                                // this notification will be grabbed by the modeler mediator which will open
                                // the corresponding form
                                sendNotification(ApplicationFacade.NOTE_CREATE_IDP_CHANNEL_ELEMENT, cidpc);
                            }


                            break;
                    }
                }
                break;
            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED:
                toggleNodeOnByData(_identityApplianceDiagram, _projectProxy.currentIdentityApplianceElement);
                break;

            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED:
                _identityApplianceDiagram.dispatchEvent(new VGraphEvent(VGraphEvent.VGRAPH_CHANGED));
                break;
            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_REMOVE:
                if (_currentlySelectedNode != null) {
                    var elementType:int = notification.getBody() as int;

                    switch (elementType) {
                        case DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE:
                            var identityProvider:IdentityProviderDTO = _currentlySelectedNode.data as IdentityProviderDTO;

                            var rip:RemoveIdentityProviderElementRequest = new RemoveIdentityProviderElementRequest(identityProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.NOTE_REMOVE_IDENTITY_PROVIDER_ELEMENT, rip);
                            break;
                        case DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE:
                            var serviceProvider:ServiceProviderDTO = _currentlySelectedNode.data as ServiceProviderDTO;

                            var rsp:RemoveServiceProviderElementRequest = new RemoveServiceProviderElementRequest(serviceProvider);

                            // this notification will be grabbed by the modeler mediator which will invoke
                            // the corresponding command for processing the removal operation.
                            sendNotification(ApplicationFacade.NOTE_REMOVE_SERVICE_PROVIDER_ELEMENT, rsp);
                            break;
                    }
                }
                break;
        }

    }

    private function updateIdentityAppliance():void {

        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        _identityAppliance = proxy.currentIdentityAppliance;
    }


    protected function get view():ModelerView
    {
        return viewComponent as ModelerView;
    }


    private function init():void {
        resetGraph();
        updateGraph();
    }

    private function updateGraph() {

        if (_identityAppliance != null) {
            _applianceId = _identityAppliance.id.toString();
        } else {
            _applianceId = null;
        }

        resetGraph();

        if (_identityAppliance != null) {
            var identityApplianceDefinition:IdentityApplianceDefinitionDTO = _identityAppliance.idApplianceDefinition;

            var rootGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), _identityAppliance, null, true, Constants.IDENTITY_BUS_DEEP);

            if (identityApplianceDefinition.providers != null) {
                for (var i:int = 0; i < identityApplianceDefinition.providers.length; i++) {
                    var provider:ProviderDTO = identityApplianceDefinition.providers[i];
                    var providerGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), provider, rootGraphNode, true, Constants.PROVIDER_DEEP);
                    if (provider is LocalProviderDTO) {
                        var locProv:LocalProviderDTO = provider as LocalProviderDTO;
                        if (locProv.defaultChannel != null) {
                            var defChannelGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), locProv.defaultChannel, providerGraphNode, true, Constants.CHANNEL_DEEP);
                            var identityVault:IdentityVaultDTO = null;
                            if (locProv.defaultChannel is IdentityProviderChannelDTO) {
                                identityVault = IdentityProviderChannelDTO(locProv.defaultChannel).identityVault;
                            } else if (locProv.defaultChannel is ServiceProviderChannelDTO) {
                                identityVault = ServiceProviderChannelDTO(locProv.defaultChannel).identityVault;
                            }
                            if (identityVault != null) {
                                var identityVaultGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityVault, providerGraphNode, true, Constants.IDENTITY_VAULT_DEEP);
                            }
                        }
                        if (locProv.channels != null) {
                            for (var j:int = 0; j < locProv.channels.length; j++) {
                                var channel = locProv.channels[j];
                                var channelGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), channel, providerGraphNode, true, Constants.CHANNEL_DEEP);
                                var identityVault:IdentityVaultDTO = null;
                                if (channel is IdentityProviderChannelDTO) {
                                    identityVault = IdentityProviderChannelDTO(channel).identityVault;
                                } else if (channel is ServiceProviderChannelDTO) {
                                    identityVault = ServiceProviderChannelDTO(channel).identityVault;
                                }
                                if (identityVault != null) {
                                    var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityVault, true);
                                    var identityVaultGraphNode:IVisualNode = GraphDataManager.addVNodeAsChild(_identityApplianceDiagram, UIDUtil.createUID(), identityVault, providerGraphNode, true, Constants.IDENTITY_VAULT_DEEP);
                                }
                            }
                        }
                    }
                }
            }

            if (identityApplianceDefinition.identityVaults != null) {
                for (i = 0; i < identityApplianceDefinition.identityVaults.length; i++) {
                    var identityVaultNode:BrowserNode = BrowserModelFactory.createIdentityVaultNode(identityApplianceDefinition.identityVaults[i], true);
                }
            }

        }

    }


    private function resetGraph():void {
        _identityApplianceDiagram.graph = new Graph("Graph", true, _emptyNotationModel as XML);
        _identityApplianceDiagram.graph.purgeGraph();
        _identityApplianceDiagram.newNodesDefaultVisible = true;

        _autoFitEnabled = true;
        _selectedOrientation = HierarchicalLayouter.ORIENT_TOP_DOWN;
        var layouter:HierarchicalLayouter = new HierarchicalLayouter(_identityApplianceDiagram);
        layouter.autoFitEnabled = _autoFitEnabled;
        layouter.orientation = _selectedOrientation;
        _identityApplianceDiagram.layouter = layouter;

        _identityApplianceDiagram.edgeRenderer = new BaseEdgeRenderer(_identityApplianceDiagram.edgeDrawGraphics);

        var nodeRenderer:ClassFactory = new ClassFactory(NodeDetailedRenderer);
        _identityApplianceDiagram.itemRenderer = nodeRenderer;

        _identityApplianceDiagram.edgeLabelRenderer = new ClassFactory(BaseEdgeLabelRendered);

        _identityApplianceDiagram.draw();
        _identityApplianceDiagram.refresh();
    }

    private function nodeSelectedEventHandler(event:VNodeSelectedEvent):void
    {
        var node:INode = _identityApplianceDiagram.graph.nodeByStringId(event.vnodeId);

        toggleUnselectedNodesOff(_identityApplianceDiagram, event.target);

        if (node != null) {
            _currentlySelectedNode = node;
            _projectProxy.currentIdentityApplianceElement = node.data;
            sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED);
        }
    }

    private function nodeRemoveEventHandler(event:VNodeRemoveEvent):void
    {
        var node:INode = _identityApplianceDiagram.graph.nodeByStringId(event.vnodeId);
        var elementType:int;

        if (node != null) {
            _currentlySelectedNode = node;
            _projectProxy.currentIdentityApplianceElement = node.data;
            //need to add elementType in the notification body for delete func. to work properly
            if(node.data is IdentityApplianceDTO){
                elementType = DiagramElementTypes.IDENTITY_APPLIANCE_ELEMENT_TYPE;
            } else
            if(node.data is IdentityProviderDTO){
                elementType = DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE;
            } else
            if (node.data is ServiceProviderDTO){
                elementType = DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE;
            }
            //TODO - add other element types
            
            sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_REMOVE, elementType);
        }
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


}
}