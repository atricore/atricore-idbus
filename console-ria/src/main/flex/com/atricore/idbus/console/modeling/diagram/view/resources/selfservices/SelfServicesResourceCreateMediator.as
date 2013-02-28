/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/25/13
 */
package com.atricore.idbus.console.modeling.diagram.view.resources.selfservices {
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.IdBusExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.SelfServicesResource;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.events.ValidationResultEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

public class SelfServicesResourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private static var _resourceName:String = "SELF_SERVICES";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _newResource:SelfServicesResource;

    //private var _locationValidator:Validator;

    public function SelfServicesResourceCreateMediator(name:String = null, viewComp:SelfServicesResourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleSelfServicesResourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        //_locationValidator = new URLValidator();
        //_locationValidator.required = true;

        //initLocation();
        view.btnOk.addEventListener(MouseEvent.CLICK, handleSelfServicesResourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.resourceName);
    }

    /*
    private function initLocation() {

        var location:Location = _projectProxy.currentIdentityAppliance.idApplianceDefinition.location;

        for (var i:int = 0; i < view.resourceProtocol.dataProvider.length; i++) {
            if (location.protocol == view.resourceProtocol.dataProvider[i].data) {
                view.resourceProtocol.selectedIndex = i;
                break;
            }
        }
        view.resourceDomain.text = location.host;
        view.resourcePort.text = location.port.toString() != "0" ? location.port.toString() : "";
        view.resourceContext.text = location.context;
        view.resourcePath.text = location.uri + "/";

    }
     */

    private function resetForm():void {
        view.resourceName.text = "";
        view.resourceDescription.text = "";
        /*
        view.resourceProtocol.selectedIndex = 0;
        view.resourceDomain.text = "";
        view.resourcePort.text = "";
        view.resourceContext.text = "";
        view.resourcePath.text = "";
        */

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var resource:SelfServicesResource = new SelfServicesResource();
        var resourceEE:IdBusExecutionEnvironment = new IdBusExecutionEnvironment();

        resource.name = view.resourceName.text;
        resource.description = view.resourceDescription.text;

        resourceEE.name = resource.name + "-captive-ee";
        resourceEE.description = resource.description +
                "Captive IdBus execution environment owned by Service Resource " + resource.name

        //resourceEE.type = ExecEnvType.valueOf(view.selectedHost.selectedItem.data);
        //resourceEE.installUri = view.homeDirectory.text;

        //if (resourceEE.type.name == ExecEnvType.REMOTE.name)
         //   resourceEE.location = view.location.text;
        //resourceEE.overwriteOriginalSetup = view.replaceConfFiles.selected;

        resourceEE.overwriteOriginalSetup = false;
        resourceEE.installDemoApps = false;
        resourceEE.platformId = "idbus";

        var resourceEEActivation : JOSSOActivation  = new JOSSOActivation();
        resourceEEActivation.name = resource.name.toLowerCase().replace(/\s+/g, "-") +
                "-" + resourceEE.name.toLowerCase().replace(/\s+/g, "-") + "-activation";
        resourceEEActivation.executionEnv = resourceEE;
        resourceEEActivation.resource = resource;

        resource.activation = resourceEEActivation;

        if(resourceEE.activations == null){
            resourceEE.activations = new ArrayCollection();
        }

        resourceEE.activations.addItem(resourceEEActivation);

        // set location
        /*
        var loc:Location = new Location();
        loc.protocol = view.resourceProtocol.labelDisplay.text;
        loc.host = view.resourceDomain.text;
        loc.port = parseInt(view.resourcePort.text);
        loc.context = view.resourceContext.text;
        loc.uri = view.resourcePath.text;
        resource.location = loc;
        */

        _newResource = resource;
    }

    private function handleSelfServicesResourceSave(event:MouseEvent):void {

        if (validate(true)) {
            save();
        }
    }

    private function save():void {
        bindModel();

        if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments == null){
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments = new ArrayCollection();
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.addItem(_newResource.activation.executionEnv);

        if (_projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources == null) {
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources = new ArrayCollection();
        }

        _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources.addItem(_newResource);
        _projectProxy.currentIdentityApplianceElement = _newResource;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        closeWindow();
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():SelfServicesResourceCreateForm {
        return viewComponent as SelfServicesResourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
    }

    }
}
