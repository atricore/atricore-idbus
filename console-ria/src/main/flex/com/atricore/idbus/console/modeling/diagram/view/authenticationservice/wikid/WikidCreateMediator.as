package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.wikid {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.WikidAuthenticationService;

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

public class WikidCreateMediator extends IocFormMediator {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();
    
    private var _projectProxy:ProjectProxy;
    private var _newWikidAuthnService:WikidAuthenticationService;

    [Bindable]
    private var _caStoreFileRef:FileReference;

    [Bindable]
    public var _selectedCAStores:ArrayCollection;

    private var _uploadedCAStoreFile:ByteArray;
    private var _uploadedCAStoreFileName:String;

    [Bindable]
    private var _wcStoreFileRef:FileReference;

    [Bindable]
    public var _selectedWCStores:ArrayCollection;

    private var _uploadedWCStoreFile:ByteArray;
    private var _uploadedWCStoreFileName:String;

    public function WikidCreateMediator(name:String = null, viewComp:WikidCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleWikidSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);

            if (_caStoreFileRef != null) {
                _caStoreFileRef.removeEventListener(Event.SELECT, caStoreFileSelectHandler);
                _caStoreFileRef.removeEventListener(Event.COMPLETE, caStoreUploadCompleteHandler);
            }

            if (_wcStoreFileRef != null) {
                _wcStoreFileRef.removeEventListener(Event.SELECT, wcStoreFileSelectHandler);
                _wcStoreFileRef.removeEventListener(Event.COMPLETE, wcStoreUploadCompleteHandler);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        // upload bindings
        view.caStore.addEventListener(MouseEvent.CLICK, caStoreBrowseHandler);
        BindingUtils.bindProperty(view.caStore, "dataProvider", this, "_selectedCAStores");

        view.wcStore.addEventListener(MouseEvent.CLICK, wcStoreBrowseHandler);
        BindingUtils.bindProperty(view.wcStore, "dataProvider", this, "_selectedWCStores");

        view.btnOk.addEventListener(MouseEvent.CLICK, handleWikidSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.wikidName);
    }

    private function resetForm():void {
        view.wikidName.text = "wikid";
        view.wikidDescription.text = "";
        view.serverHost.text = "";
        view.serverPort.text = "";
        view.serverCode.text = "";
        view.caStorePass.text = "";
        view.wcStorePass.text = "";

        _caStoreFileRef = null;
        _selectedCAStores = new ArrayCollection();
        view.caStore.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.ca.store.browse");

        _wcStoreFileRef = null;
        _selectedWCStores = new ArrayCollection();
        view.wcStore.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.wc.store.browse");

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var wikidAuthnService:WikidAuthenticationService = new WikidAuthenticationService();

        wikidAuthnService.name = view.wikidName.text;
        wikidAuthnService.description = view.wikidDescription.text;
        
        wikidAuthnService.serverHost = view.serverHost.text;
        wikidAuthnService.serverPort = parseInt(view.serverPort.text);
        wikidAuthnService.serverCode = view.serverCode.text;

        // set Certificate Authority Store
        var caKeystore:Keystore = new Keystore();
        caKeystore.name = wikidAuthnService.name.toLowerCase().replace(/\s+/g, "-") + "-ca-store";
        caKeystore.displayName = wikidAuthnService.name + " Certificate Authority Store";
        caKeystore.password = view.caStorePass.text;
        caKeystore.type = "JKS";
        caKeystore.keystorePassOnly = true;
        var caResource:Resource = new Resource();
        caResource.name = _uploadedCAStoreFileName;
        caResource.displayName = _uploadedCAStoreFileName;
        caResource.uri = _uploadedCAStoreFileName;
        caResource.value = _uploadedCAStoreFile;
        caKeystore.store = caResource;
        wikidAuthnService.caStore = caKeystore;

        // set WiKID Client Store
        var wcKeystore:Keystore = new Keystore();
        wcKeystore.name = wikidAuthnService.name.toLowerCase().replace(/\s+/g, "-") + "-wc-store";
        wcKeystore.displayName = wikidAuthnService.name + " WiKID Client Store";
        wcKeystore.password = view.wcStorePass.text;
        wcKeystore.type = "PKCS#12";
        wcKeystore.keystorePassOnly = true;
        var wcResource:Resource = new Resource();
        if (_uploadedWCStoreFileName.lastIndexOf(".") > 0) {
            wcResource.name = _uploadedWCStoreFileName.substring(0, _uploadedWCStoreFileName.lastIndexOf("."));
        } else {
            wcResource.name = _uploadedWCStoreFileName;
        }
        wcResource.displayName = _uploadedWCStoreFileName;
        wcResource.uri = _uploadedWCStoreFileName;
        wcResource.value = _uploadedWCStoreFile;
        wcKeystore.store = wcResource;
        wikidAuthnService.wcStore = wcKeystore;

        _newWikidAuthnService = wikidAuthnService;
    }

    private function handleWikidSave(event:MouseEvent):void {
        if (validate(true)) {
            if (_selectedCAStores == null || _selectedCAStores.length == 0) {
                view.lblCAStoreMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.ca.store.upload.error");
                view.lblCAStoreMsg.setStyle("color", "Red");
                view.lblCAStoreMsg.includeInLayout = true;
                view.lblCAStoreMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            }
            if (_selectedWCStores == null || _selectedWCStores.length == 0) {
                view.lblWCStoreMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.wc.store.upload.error");
                view.lblWCStoreMsg.setStyle("color", "Red");
                view.lblWCStoreMsg.includeInLayout = true;
                view.lblWCStoreMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            }
            _caStoreFileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function saveWikidAuthnService():void {
        bindModel();
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.addItem(_newWikidAuthnService);
        _projectProxy.currentIdentityApplianceElement = _newWikidAuthnService;
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

    // upload functions
    private function caStoreBrowseHandler(event:MouseEvent):void {
        if (_caStoreFileRef == null) {
            _caStoreFileRef = new FileReference();
            _caStoreFileRef.addEventListener(Event.SELECT, caStoreFileSelectHandler);
            _caStoreFileRef.addEventListener(Event.COMPLETE, caStoreUploadCompleteHandler);
        }
        //var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        //var fileTypes:Array = new Array(fileFilter);
        //_caStoreFileRef.browse(fileTypes);
        _caStoreFileRef.browse();
    }

    private function caStoreFileSelectHandler(event:Event):void {
        view.caStore.prompt = null;
        _selectedCAStores = new ArrayCollection();
        _selectedCAStores.addItem(_caStoreFileRef.name);
        view.caStore.selectedIndex = 0;

        view.lblCAStoreMsg.text = "";
        view.lblCAStoreMsg.visible = false;
        view.lblCAStoreMsg.includeInLayout = false;
    }

    private function caStoreUploadCompleteHandler(event:Event):void {
        _uploadedCAStoreFile = _caStoreFileRef.data;
        _uploadedCAStoreFileName = _caStoreFileRef.name;

        _wcStoreFileRef.load();
    }

    private function wcStoreBrowseHandler(event:MouseEvent):void {
        if (_wcStoreFileRef == null) {
            _wcStoreFileRef = new FileReference();
            _wcStoreFileRef.addEventListener(Event.SELECT, wcStoreFileSelectHandler);
            _wcStoreFileRef.addEventListener(Event.COMPLETE, wcStoreUploadCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("PKCS#12(*.p12)", "*.p12");
        var fileTypes:Array = new Array(fileFilter);
        _wcStoreFileRef.browse(fileTypes);
    }

    private function wcStoreFileSelectHandler(event:Event):void {
        view.wcStore.prompt = null;
        _selectedWCStores = new ArrayCollection();
        _selectedWCStores.addItem(_wcStoreFileRef.name);
        view.wcStore.selectedIndex = 0;

        view.lblWCStoreMsg.text = "";
        view.lblWCStoreMsg.visible = false;
        view.lblWCStoreMsg.includeInLayout = false;
    }

    private function wcStoreUploadCompleteHandler(event:Event):void {
        _uploadedWCStoreFile = _wcStoreFileRef.data;
        _uploadedWCStoreFileName = _wcStoreFileRef.name;

        saveWikidAuthnService();
    }

    protected function get view():WikidCreateForm {
        return viewComponent as WikidCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.serverHostValidator);
        _validators.push(view.serverPortValidator);
        _validators.push(view.serverCodeValidator);
        _validators.push(view.caStorePassValidator);
        _validators.push(view.wcStorePassValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}