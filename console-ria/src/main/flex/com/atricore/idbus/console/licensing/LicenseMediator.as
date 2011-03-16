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

package com.atricore.idbus.console.licensing
{
import com.atricore.idbus.console.licensing.main.controller.GetLicenseCommand;
import com.atricore.idbus.console.licensing.main.controller.UpdateLicenseCommand;
import com.atricore.idbus.console.licensing.main.model.LicenseProxy;
import com.atricore.idbus.console.licensing.main.view.LicensingPopUpManager;
import com.atricore.idbus.console.licensing.main.view.updatelicense.UpdateLicenseMediator;
import com.atricore.idbus.console.main.ApplicationFacade;

import com.atricore.idbus.console.services.dto.FeatureType;
import com.atricore.idbus.console.services.dto.LicensedFeatureType;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.FlexEvent;

import mx.resources.IResourceManager;

import mx.resources.ResourceManager;

import mx.utils.StringUtil;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocFacade;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.Button;
import spark.components.HGroup;
import spark.components.Label;

public class LicenseMediator extends IocMediator implements IDisposable {


    private var _popupManager:LicensingPopUpManager;

    private var _licenseProxy:LicenseProxy;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    //commands
    private var _updateLicenseCommand:UpdateLicenseCommand;
    private var _getLicenseCommand:GetLicenseCommand;

    //mediators
    private var _updateLicenseMediator:UpdateLicenseMediator;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    private var _created:Boolean;    

    public function LicenseMediator(name:String = null, viewComp:LicenseView = null) {
        super(name, viewComp);
    }

    public function get popupManager():LicensingPopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:LicensingPopUpManager):void {
        _popupManager = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
//            Hidding update button for now [ATCON-342]
//            view.btnUpdateLicense.removeEventListener(MouseEvent.CLICK, handleUpdateLicenseButton);
        }

        (viewComponent as LicenseView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;
        
        popupManager.init(iocFacade, view);
//        Hidding update button for now [ATCON-342]
//        view.btnUpdateLicense.addEventListener(MouseEvent.CLICK, handleUpdateLicenseButton);
        init();
    }

    private function init():void {
        (facade as IIocFacade).registerMediatorByConfigName(updateLicenseMediator.getConfigName());
        (facade as IIocFacade).registerCommandByConfigName(ApplicationFacade.UPDATE_LICENSE, updateLicenseCommand.getConfigName());
        if(!(facade as IIocFacade).hasCommand(getLicenseCommand.getConfigName())){
            (facade as IIocFacade).registerCommandByConfigName(ApplicationFacade.GET_LICENSE, getLicenseCommand.getConfigName());
        }
        if (_created) {
            /* Remove unused title in account management panel */
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;
            sendNotification(ApplicationFacade.GET_LICENSE);
        }

    }

    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.LICENSE_VIEW_SELECTED,
            UpdateLicenseCommand.SUCCESS,
            UpdateLicenseCommand.FAILURE,
            ApplicationFacade.DISPLAY_UPDATE_LICENSE,
            ApplicationFacade.DISPLAY_EULA_TEXT,
            GetLicenseCommand.SUCCESS,
            GetLicenseCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case UpdateLicenseCommand.SUCCESS :
                // do nothing AppMediator is taking care of it
                break;
            case UpdateLicenseCommand.FAILURE :
                handleActivationFailure(notification);
                break;
            case ApplicationFacade.LICENSE_VIEW_SELECTED:
                init();
                break;
            case ApplicationFacade.DISPLAY_UPDATE_LICENSE:
                popupManager.showUpdateLicenseWindow(notification);
                break;
            case ApplicationFacade.DISPLAY_EULA_TEXT:
                popupManager.showLicenseTextWindow(notification);
                break;
            case GetLicenseCommand.SUCCESS:
                displayLicenseInfo();
                break;
            case GetLicenseCommand.FAILURE:
                //TODO show error - this should not happen
                break;
        }
    }
    
//    Hidding update button for now [ATCON-342]
//    public function handleUpdateLicenseButton(event:Event):void {
//        sendNotification(ApplicationFacade.DISPLAY_UPDATE_LICENSE);
//    }

    public function handleActivationFailure(notification:INotification):void {
        var errMsg:String = notification.getBody() as String;
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, errMsg);
    }

    public function handleViewEulaButton(event:Event):void {        
        sendNotification(ApplicationFacade.DISPLAY_EULA_TEXT, StringUtil.trim(_licenseProxy.license.eula));
    }

    public function handleViewLicenseButton(event:Event):void {
        var btnId:String = (event.currentTarget as Button).id;
        var tmpId:String;
        var tmpFeature:FeatureType;
        for each (var licFeature:LicensedFeatureType in _licenseProxy.license.licensedFeature) {
            for each (var feature:FeatureType in licFeature.feature) {
                tmpId = feature.group + feature.name;
                if(btnId == tmpId){
                   tmpFeature = feature;
                    break;
                }
            }
        }
        sendNotification(ApplicationFacade.DISPLAY_EULA_TEXT, StringUtil.trim(tmpFeature.licenseText));
    }

    public function displayLicenseInfo():void {
        var paddingBottom:Number = 5;
        var lblWidth:Number = 150;
        view.licenseInfo.removeAllElements();

        //GENERAL EULA
        if(_licenseProxy.license.eula != null) {
            var hgroup:HGroup = new HGroup();
            hgroup.paddingBottom = paddingBottom;
            var textLbl:Label = new Label();
            textLbl.width = lblWidth;
            textLbl.text = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.generaleula') + ":";
            hgroup.addElement(textLbl);
            var btn:Button = new Button();
            btn.label = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.vieweula');
            btn.addEventListener(MouseEvent.CLICK, handleViewEulaButton);
            hgroup.addElement(btn);
//            textLbl = new Label();
//            textLbl.text = StringUtil.trim(_licenseProxy.license.eula);
//            hgroup.addElement(textLbl);
            view.licenseInfo.addElement(hgroup);
        }

        //LICENSE OWNER
        if(_licenseProxy.license.organization != null && _licenseProxy.license.organization.owner != null){
            hgroup = new HGroup();
            hgroup.paddingBottom = paddingBottom;
            textLbl = new Label();
            textLbl.width = lblWidth;
            textLbl.text = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.owner') + ":";
            hgroup.addElement(textLbl);
            textLbl = new Label();
            textLbl.text = _licenseProxy.license.organization.owner;
            hgroup.addElement(textLbl);
            view.licenseInfo.addElement(hgroup);
        }

        //ORGANIZATION
        if(_licenseProxy.license.organization != null){
            hgroup = new HGroup();
            hgroup.paddingBottom = paddingBottom;
            textLbl = new Label();
            textLbl.width = lblWidth;
            textLbl.text = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.organization') + ":";
            hgroup.addElement(textLbl);
            textLbl = new Label();
            textLbl.text = _licenseProxy.license.organization.organizationName;
            hgroup.addElement(textLbl);
            view.licenseInfo.addElement(hgroup);
        }

        //FEATURES
        for each (var licFeature:LicensedFeatureType in _licenseProxy.license.licensedFeature) {
            for each (var feature:FeatureType in licFeature.feature) {
                //feature name
                hgroup = new HGroup();
                hgroup.paddingBottom = paddingBottom;
                textLbl = new Label();
                textLbl.width = lblWidth;
                textLbl.text = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.feature') + ":";
                hgroup.addElement(textLbl);
                textLbl = new Label();
                textLbl.text = feature.name;
                hgroup.addElement(textLbl);
                view.licenseInfo.addElement(hgroup);

                //feature expire date
                hgroup = new HGroup();
                hgroup.paddingBottom = paddingBottom;
                textLbl = new Label();
                textLbl.width = lblWidth;
                textLbl.text = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.expires') + ":";
                hgroup.addElement(textLbl);
                textLbl = new Label();
                textLbl.text = licFeature.expirationDate.toDateString();
                hgroup.addElement(textLbl);
                view.licenseInfo.addElement(hgroup);

                //feature license text
                if(feature.licenseText != null){
                    hgroup = new HGroup();
                    hgroup.paddingBottom = paddingBottom;
                    textLbl = new Label();
                    textLbl.width = lblWidth;
                    textLbl.text = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.licensetext') + ":";
                    hgroup.addElement(textLbl);
                    btn = new Button();
                    btn.label = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.viewlicense');
                    btn.id = feature.group + feature.name;
                    btn.addEventListener(MouseEvent.CLICK, handleViewLicenseButton);
                    hgroup.addElement(btn);
//                    textLbl = new Label();
//                    textLbl.text = StringUtil.trim(feature.licenseText);
//                    hgroup.addElement(textLbl);
                    view.licenseInfo.addElement(hgroup);
                }
            }
        }
//        view.eula.text = _licenseProxy.license.eula;
//        view.organization.text = _licenseProxy.license.organization.organizationName;
//        view.ownerName.text = _licenseProxy.license.organization.owner;

//        var licFeature:LicensedFeatureType = _licenseProxy.license.licensedFeature.getItemAt(0) as LicensedFeatureType;
//        view.feature.text = licFeature.feature.getItemAt(0).name;
//        view.expirationDate.text = licFeature.expirationDate.toDateString();
    }

    protected function get view():LicenseView
    {
        return viewComponent as LicenseView;
    }

//    protected function set view(amv:LicenseView):void
//    {
//        viewComponent = amv;
//    }

    public function set licenseProxy(value:LicenseProxy):void {
        _licenseProxy = value;
    }

    public function get updateLicenseCommand():UpdateLicenseCommand {
        return _updateLicenseCommand;
    }

    public function set updateLicenseCommand(value:UpdateLicenseCommand):void {
        _updateLicenseCommand = value;
    }

    public function get updateLicenseMediator():UpdateLicenseMediator {
        return _updateLicenseMediator;
    }

    public function set updateLicenseMediator(value:UpdateLicenseMediator):void {
        _updateLicenseMediator = value;
    }

    public function get getLicenseCommand():GetLicenseCommand {
        return _getLicenseCommand;
    }

    public function set getLicenseCommand(value:GetLicenseCommand):void {
        _getLicenseCommand = value;
    }

    public function dispose():void {
        // Clean up

        setViewComponent(null);
    }    
}
}
