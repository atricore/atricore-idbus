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

package com.atricore.idbus.console.modeling.diagram.view.activation {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;

import com.atricore.idbus.console.modeling.diagram.model.request.CreateActivationElementRequest;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSOActivation;

import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class ActivationCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private var _sp:ServiceProvider;
    private var _execEnv:ExecutionEnvironment;

    private var _jossoActivation:JOSSOActivation;

    public function ActivationCreateMediator(name:String = null, viewComp:ActivationCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleJOSSOActivationSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleJOSSOActivationSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    private function resetForm():void {
        view.activationName.text = "";
        view.activationDescription.text = "";
        view.activationProtocol.selectedIndex = 0;
        view.activationDomain.text = "";
        view.activationPort.text = "";
        view.activationContext.text = "";
        view.activationPartnerAppId.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var activation:JOSSOActivation = new JOSSOActivation();

        activation.name = view.activationName.text;
        activation.description = view.activationDescription.text;
        //location
        var loc:Location = new Location();
        loc.protocol = view.activationProtocol.labelDisplay.text;
        loc.host = view.activationDomain.text;
        loc.port = parseInt(view.activationPort.text);
        loc.context = view.activationContext.text;

        activation.partnerAppId = view.activationPartnerAppId.text;

        activation.partnerAppLocation = loc;

        _jossoActivation = activation;
    }

    private function handleJOSSOActivationSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _jossoActivation.sp = _sp;
            _jossoActivation.executionEnv = _execEnv;

            if(_execEnv.activations == null){
                _execEnv.activations = new ArrayCollection();
            }
            _execEnv.activations.addItem(_jossoActivation);
            _sp.activation = _jossoActivation;
            
            _projectProxy.currentIdentityApplianceElement = _jossoActivation;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
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

    protected function get view():ActivationCreateForm
    {
        return viewComponent as ActivationCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.appIdValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.pathValidator);
    }


    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
        var car:CreateActivationElementRequest = notification.getBody() as CreateActivationElementRequest;
        _sp = car.sp;
        _execEnv = car.executionEnvironment;

    }
}
}