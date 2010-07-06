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

package com.atricore.idbus.console.modeling.propertysheet {
import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.ViewStack;
import mx.controls.TabBar;
import mx.events.FlexEvent;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.propertysheet.view.appliance.IdentityApplianceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderCoreSection;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityProvider;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class PropertySheetMediator extends Mediator {
    public static const NAME:String = "PropertySheetMediator";
    private var _tabbedPropertiesTabBar:TabBar;
    private var _propertySheetsViewStack:ViewStack;
    private var _iaCoreSection:IdentityApplianceCoreSection;
    private var _ipCoreSection:IdentityProviderCoreSection;
    private var _projectProxy:ProjectProxy;
    private var _currentIdentityApplianceElement:Object;

    public function PropertySheetMediator(viewComp:PropertySheetView) {
        super(NAME, viewComp);
        _tabbedPropertiesTabBar =  viewComp.tabbedPropertiesTabBar
        _propertySheetsViewStack = viewComp.propertySheetsViewStack;
        _projectProxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE,
            ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE:
                clearPropertyTabs();
                break;
            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED:
                if (_projectProxy.currentIdentityApplianceElement is IdentityAppliance) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityAppliancePropertyTabs();
                } else
                if (_projectProxy.currentIdentityApplianceElement is IdentityProvider) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityProviderPropertyTabs();
                }
                break;
        }

    }

    protected function enableIdentityAppliancePropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _iaCoreSection = new IdentityApplianceCoreSection();
        corePropertyTab.addChild(_iaCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _iaCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityApplianceCorePropertyTabRollOut);

     }

     private function handleCorePropertyTabCreationComplete(event:Event):void {
         var identityAppliance:IdentityAppliance;

         // fetch appliance object
         var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
         identityAppliance = proxy.currentIdentityAppliance;

         // bind view
         _iaCoreSection.applianceName.text = identityAppliance.idApplianceDefinition.name;
     }

    private function handleIdentityApplianceCorePropertyTabRollOut(e:Event):void {
        trace(e);
        // bind model
        // fetch appliance object
        var identityAppliance:IdentityAppliance;
        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        identityAppliance = proxy.currentIdentityAppliance;

        identityAppliance.idApplianceDefinition.name = _iaCoreSection.applianceName.text;
        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED);
    }


    protected function enableIdentityProviderPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _ipCoreSection = new IdentityProviderCoreSection();
        corePropertyTab.addChild(_ipCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _ipCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityProviderCorePropertyTabRollOut);

     }

     private function handleIdentityProviderCorePropertyTabCreationComplete(event:Event):void {
         var identityProvider:IdentityProvider;

         identityProvider = _currentIdentityApplianceElement as IdentityProvider;

         // bind view
         _ipCoreSection.identityProviderName.text = identityProvider.name;
     }


    private function handleIdentityProviderCorePropertyTabRollOut(e:Event):void {
        // bind model
        var identityProvider:IdentityProvider;

        identityProvider = _currentIdentityApplianceElement as IdentityProvider;

        identityProvider.name = _ipCoreSection.identityProviderName.text;
        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED);
    }

    protected function clearPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

     }

    protected function get view():PropertySheetView
    {
        return viewComponent as PropertySheetView;
    }


}
}