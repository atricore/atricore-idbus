package com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.activation {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.ActivateExecutionEnvironmentRequest;
import com.atricore.idbus.console.services.dto.CaptiveExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ServiceResource;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class ExecEnvActivationMediator extends IocFormMediator {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();
    
    private var _projectProxy:ProjectProxy;

    private var _reactivate:Boolean;
    private var _replaceConfFiles:Boolean;
    private var _installSamples:Boolean;
    
    public function ExecEnvActivationMediator(name:String = null, viewComp:ExecEnvActivationView = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleActivation);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        var currentExecEnv:ExecutionEnvironment = getCurrentExecEnv();

        if (currentExecEnv.type.name == ExecEnvType.REMOTE.name) {
            view.userPassGroup.includeInLayout = true;
            view.userPassGroup.visible = true;
            view.parent.height += 85;
        }

        /*if (currentExecEnv.type.name == ExecEnvType.LOCAL.name) {
            view.activationText.setStyle("paddingTop", 15);
            view.userPassGroup.height = 0;
        }*/
        view.activationText.text = currentExecEnv.name + " " +
                resourceManager.getString(AtricoreConsole.BUNDLE, "activation.confirm.line1") + "\n" +
                resourceManager.getString(AtricoreConsole.BUNDLE, "activation.confirm.line2");
        if (currentExecEnv.type.name == ExecEnvType.REMOTE.name) {
            view.userPassGroup.visible = true;
        }
        view.btnOk.addEventListener(MouseEvent.CLICK, handleActivation);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    private function resetForm():void {
        view.username.text = "";
        view.password.text = "";

        _reactivate = false;
        _replaceConfFiles = false;
        _installSamples = false;
        
        FormUtility.clearValidationErrors(_validators);
    }

    private function handleActivation(event:MouseEvent):void {
        if (validate(true)) {
            var currentExecEnv:ExecutionEnvironment = getCurrentExecEnv();
            var activateExecEnvReq:ActivateExecutionEnvironmentRequest = new ActivateExecutionEnvironmentRequest();
            activateExecEnvReq.reactivate = _reactivate;
            activateExecEnvReq.replaceConfFiles = _replaceConfFiles;
            activateExecEnvReq.executionEnvironment = currentExecEnv;
            activateExecEnvReq.installSamples = _installSamples;
            if (currentExecEnv.type.name == ExecEnvType.REMOTE.name) {
                activateExecEnvReq.username = view.username.text;
                activateExecEnvReq.password = view.password.text;
            }

            closeWindow();
            sendNotification(ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT, activateExecEnvReq);
        }
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
        if (getCurrentExecEnv() is CaptiveExecutionEnvironment) {
            sendNotification(ApplicationFacade.RESET_RESOURCE_ACTIVATION);
        } else {
            sendNotification(ApplicationFacade.RESET_EXEC_ENV_ACTIVATION);
        }
    }

    protected function get view():ExecEnvActivationView {
        return viewComponent as ExecEnvActivationView;
    }


    override public function registerValidators():void {
        var currentExecEnv:ExecutionEnvironment = getCurrentExecEnv();
        if (currentExecEnv.type.name == ExecEnvType.REMOTE.name) {
            _validators.push(view.usernameValidator);
            _validators.push(view.passwordValidator);
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.DISPLAY_ACTIVATION_DIALOG];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_ACTIVATION_DIALOG:
                var params:Array = notification.getBody() as Array;
                _reactivate = params[0];
                _replaceConfFiles = params[1];
                _installSamples = params[2];
                break;
        }
    }

    private function getCurrentExecEnv():ExecutionEnvironment {
        var currentExecEnv:ExecutionEnvironment;

        if (projectProxy.currentIdentityApplianceElement is ExecutionEnvironment) {
            currentExecEnv = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
        } else
        if (projectProxy.currentIdentityApplianceElement is ServiceResource) {
            currentExecEnv = (projectProxy.currentIdentityApplianceElement as ServiceResource).activation.executionEnv;
        }

        return currentExecEnv;
    }
}
}