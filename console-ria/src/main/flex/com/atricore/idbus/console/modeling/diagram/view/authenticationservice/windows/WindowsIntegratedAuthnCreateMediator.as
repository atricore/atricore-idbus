/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.windows {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.WindowsIntegratedAuthentication;

import flash.events.Event;

import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;

import mx.collections.ArrayCollection;

import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class WindowsIntegratedAuthnCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newWindowsIntegratedAuthnService:WindowsIntegratedAuthentication;

    private var _uploadedFile:ByteArray;
    private var _uploadedFileName:String;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    public function WindowsIntegratedAuthnCreateMediator(name:String = null, viewComp:WindowsIntegratedAuthnCreateForm = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent()) { // TODO : Is this 'if' ok, the view will be set later ...
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleWindowsIntegratedAuthnSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);

            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            }

        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleWindowsIntegratedAuthnSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.nodeName);

        view.domain.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.serviceClass.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.host.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.port.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.serviceName.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.overwriteKerberosSetup.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);

        // upload bindings
        view.keyTabFile.addEventListener(MouseEvent.CLICK, browseHandler);
        BindingUtils.bindProperty(view.keyTabFile, "dataProvider", this, "_selectedFiles");

    }

    private function resetForm():void {
        view.nodeName.text = "";
        view.description.text = "";

        view.protocol.selectedIndex = 0;
        view.domain.text = "myCompanyDomain";
        view.serviceClass.selectedIndex = 0;
        view.host.text = "";
        view.port.text = "8081";
        view.serviceName.text = "";
        view.overwriteKerberosSetup.selected = true;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.keyTabFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.keytab.file");
        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindForm():void {

        view.nodeName.text = "";
        view.description.text = "";
        view.protocol.selectedIndex = 0;
        view.domain.text = "myCompanyDomain";
        view.serviceClass.selectedIndex = 0;
        view.host.text = "";
        view.port.text = "8081";
        view.serviceName.text = "";
        view.overwriteKerberosSetup.selected = true;

        FormUtility.clearValidationErrors(_validators);

    }

    override public function bindModel():void {
        var windowsIntegratedAuthn:WindowsIntegratedAuthentication = new WindowsIntegratedAuthentication();

        windowsIntegratedAuthn.name = view.nodeName.text;
        windowsIntegratedAuthn.description = view.description.text;

        windowsIntegratedAuthn.protocol = view.protocol.selectedItem.data;
        windowsIntegratedAuthn.domain = view.domain.text;
        windowsIntegratedAuthn.serviceClass = view.serviceClass.selectedItem.data;
        windowsIntegratedAuthn.host = view.host.text;
        windowsIntegratedAuthn.port = parseInt(view.port.text);
        windowsIntegratedAuthn.serviceName = view.serviceName.text;
        windowsIntegratedAuthn.overwriteKerberosSetup = view.overwriteKerberosSetup.selected;

        var resource:Resource = new Resource();
        resource.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
        resource.displayName = _uploadedFileName;
        resource.uri = _uploadedFileName;
        resource.value = _uploadedFile;
        windowsIntegratedAuthn.keyTab = resource;

        _newWindowsIntegratedAuthnService = windowsIntegratedAuthn;
    }

    private function handleWindowsIntegratedAuthnSPNAttributeChange(e:Event):void {
        var spn:String = "";
        spn = view.serviceClass.selectedItem.data + "/" + view.host.text;
        if (view.port.text != null && view.port.text != "")
             spn += ":" + view.port.text;
        if (view.serviceName != null && view.serviceName.text != "") {
            spn += "/" + view.serviceName.text;
        }
        spn += "@" + view.domain.text;

        view.servicePrincipalName.text = spn;

    }

    private function handleWindowsIntegratedAuthnSave(event:MouseEvent):void {
        if (validate(true)) {
            if (_selectedFiles == null || _selectedFiles.length == 0) {
                view.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.keytab.file.error");
                view.lblUploadMsg.setStyle("color", "Red");
                view.lblUploadMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            } else {
                _fileRef.load();
            }
        }
        else {
            event.stopImmediatePropagation();
        }

    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    // upload functions
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        }
        var fileFilterKtb:FileFilter = new FileFilter("KeyTab (*.keytab)", "*.keytab");
        var fileFilterAll:FileFilter = new FileFilter("All", "*.*");
        var fileTypes:Array = new Array(fileFilterKtb, fileFilterAll);

        _fileRef.browse(fileTypes);
    }

    private function fileSelectHandler(evt:Event):void {
        view.keyTabFile.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.keyTabFile.selectedIndex = 0;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.keyTabFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.metadata.file");

        saveWindowsIntegratedAuthn();

    }

    private function saveWindowsIntegratedAuthn():void {
        bindModel();
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.addItem(_newWindowsIntegratedAuthnService);
        _projectProxy.currentIdentityApplianceElement = _newWindowsIntegratedAuthnService;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        closeWindow();
    }



    protected function get view():WindowsIntegratedAuthnCreateForm {
        return viewComponent as WindowsIntegratedAuthnCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.hostValidator);
        _validators.push(view.portValidator);
        _validators.push(view.serviceNameValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

     override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
        bindForm();
    }
}
}