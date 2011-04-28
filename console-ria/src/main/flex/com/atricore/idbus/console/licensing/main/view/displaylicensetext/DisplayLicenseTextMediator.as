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

package com.atricore.idbus.console.licensing.main.view.displaylicensetext {
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.dto.FeatureType;

import flash.events.MouseEvent;

import mx.events.CloseEvent;
import mx.utils.StringUtil;

import org.puremvc.as3.interfaces.INotification;

public class DisplayLicenseTextMediator extends IocFormMediator {

//    private var _uploadedFileName:String;
    private var _licenseText:String;

    public function DisplayLicenseTextMediator(name:String = null, viewComp:DisplayLicenseTextForm = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleOk);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleOk);
        view.licenseText.text = _licenseText
        view.licenseText.validateNow();
        view.parent.width = view.licenseText.textWidth + 40;
    }

    private function handleOk(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():DisplayLicenseTextForm {
        return viewComponent as DisplayLicenseTextForm;
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        var feature:FeatureType = notification.getBody() as FeatureType;
        licenseText = StringUtil.trimArrayElements(feature.licenseText, "\n");
        if (_licenseText.indexOf('\n') == 0)
            licenseText = _licenseText.substring(1, _licenseText.length);
        super.handleNotification(notification);
    }

    public function set licenseText(value:String):void {
        _licenseText = value;
    }
}
}