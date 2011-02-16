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

package com.atricore.idbus.console.account.main.view.extraattributes {
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.dto.Group;
import com.atricore.idbus.console.services.dto.schema.Attribute;
import com.atricore.idbus.console.services.dto.schema.TypeDTOEnum;

import mx.containers.FormItem;
import mx.controls.DateField;
import mx.core.UIComponent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.DateValidator;
import mx.validators.EmailValidator;
import mx.validators.NumberValidator;
import mx.validators.StringValidator;

import org.puremvc.as3.interfaces.INotification;

import spark.components.TextInput;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 2/11/11 - 4:50 PM
 */

public class ExtraAttributesMediator extends IocFormMediator
{
    private var resMan:IResourceManager = ResourceManager.getInstance();
    private var _accountManagementProxy:AccountManagementProxy;

    public function ExtraAttributesMediator(name:String = null, viewComp:ExtraAttributesTab = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        if (accountManagementProxy.attributesForEntity.length > 0)
            generateFormFields();
    }

    override public function registerValidators():void {
    }

    override public function listNotificationInterests():Array {
        return [];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            default:
                break;
        }
    }

    override public function bindForm():void {
        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var newGroupDef:Group = new Group();
    }

    public function generateFormFields():void {
        for each (var attr:Attribute in accountManagementProxy.attributesForEntity) {
            var fItem:FormItem = new FormItem();
            fItem.label = attr.name;
            fItem.required = attr.required;
            fItem.setStyle("labelWidth", 100);
            switch (attr.type.toString()) {
                case TypeDTOEnum.STRING.toString():
                    createStringField(fItem,attr);
                    break;
                case TypeDTOEnum.INT.toString():
                    createNumberField(fItem,attr);
                    break;
                case TypeDTOEnum.DATE.toString():
                    createDateField(fItem,attr);
                    break;
                case TypeDTOEnum.EMAIL.toString():
                    createEmailField(fItem,attr);
                    break;
                case TypeDTOEnum.URL.toString():
                    createUrlField(fItem,attr);
                    break;
            }
            view.extraSection.addElement(fItem);
        }
    }

    private function createStringField(fItem:FormItem,attr:Attribute):void {
        if (attr.multivalued) { //multivalued field - list
            fItem.addElement(new MultiValuedField(attr));
        }
        else { // single text field
            var textInput:TextInput = new TextInput();
            textInput.id = attr.name;
            textInput.width = 300;
            _validators.push(plainTextValidator(textInput,attr));
            fItem.addElement(textInput);
        }
    }

    private function createNumberField(fItem:FormItem,attr:Attribute):void {
        if (attr.multivalued) { //multivalued field - list
            fItem.addElement(new MultiValuedField(attr));
        }
        else { // single text field
            var numberInput:TextInput = new TextInput();
            numberInput.id = attr.name;
            numberInput.width = 300;
            _validators.push(numberValidator(numberInput,attr));
            fItem.addElement(numberInput);
        }
    }

    private function createDateField(fItem:FormItem,attr:Attribute):void {
        if (attr.multivalued) { //multivalued field - list
            fItem.addElement(new MultiValuedField(attr));
        }
        else { // single text field
            var dateInput:DateField = new DateField();
            dateInput.id = attr.name;
            dateInput.width = 100;
            _validators.push(dateValidator(dateInput,attr));
            fItem.addElement(dateInput);
        }
    }

    private function createEmailField(fItem:FormItem,attr:Attribute):void {
        if (attr.multivalued) { //multivalued field - list
            fItem.addElement(new MultiValuedField(attr));
        }
        else { // single text field
            var emailInput:TextInput = new TextInput();
            emailInput.id = attr.name;
            emailInput.width = 300;
            _validators.push(emailValidator(emailInput,attr));
            fItem.addElement(emailInput);
        }
    }

    private function createUrlField(fItem:FormItem,attr:Attribute):void {
        if (attr.multivalued) { //multivalued field - list
            fItem.addElement(new MultiValuedField(attr));
        }
        else { // single text field
            var urlInput:TextInput = new TextInput();
            urlInput.id = attr.name;
            urlInput.width = 300;
            _validators.push(urlValidator(urlInput,attr));
            fItem.addElement(urlInput);
        }
    }

    private function plainTextValidator(comp:UIComponent,attr:Attribute):StringValidator {
        var strVal:StringValidator = new StringValidator();
        strVal.property = "text";
        strVal.required = attr.required;
        strVal.source = comp;
        return strVal;
    }

    private function numberValidator(comp:UIComponent,attr:Attribute):NumberValidator {
        var numVal:NumberValidator = new NumberValidator();
        numVal.property = "text";
        numVal.required = attr.required;
        numVal.source = comp;
        return numVal;
    }

    private function dateValidator(comp:UIComponent,attr:Attribute):DateValidator {
        var dateVal:DateValidator = new DateValidator();
        dateVal.property = "text";
        dateVal.required = attr.required;
        dateVal.source = comp;
        dateVal.inputFormat = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.DATE_FORMAT');
        return dateVal;
    }

    private function emailValidator(comp:UIComponent,attr:Attribute):EmailValidator {
        var emailVal:EmailValidator = new EmailValidator();
        emailVal.property = "text";
        emailVal.required = attr.required;
        emailVal.source = comp;
        return emailVal;
    }

    private function urlValidator(comp:UIComponent,attr:Attribute):URLValidator {
        var urlVal:URLValidator = new URLValidator();
        urlVal.property = "text";
        urlVal.required = attr.required;
        urlVal.source = comp;
        return urlVal;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    protected function get view():ExtraAttributesTab
    {
        return viewComponent as ExtraAttributesTab;
    }

}
}