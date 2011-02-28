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
import com.atricore.idbus.console.account.main.model.SchemasManagementProxy;
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.dto.Group;
import com.atricore.idbus.console.services.dto.schema.Attribute;
import com.atricore.idbus.console.services.dto.schema.AttributeValue;
import com.atricore.idbus.console.services.dto.schema.TypeDTOEnum;

import flash.display.DisplayObject;

import mx.collections.ArrayCollection;
import mx.collections.ArrayCollection;
import mx.containers.FormItem;
import mx.controls.DateField;
import mx.core.IVisualElement;
import mx.core.UIComponent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.DateValidator;
import mx.validators.EmailValidator;
import mx.validators.NumberValidator;
import mx.validators.StringValidator;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

import spark.components.TextInput;
import spark.components.supportClasses.GroupBase;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 2/11/11 - 4:50 PM
 */

public class ExtraAttributesMediator extends IocFormMediator
{
    private var resMan:IResourceManager = ResourceManager.getInstance();
    private var _schemasManagementProxy:SchemasManagementProxy;
    private var _attributesValues:ArrayCollection;

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
        if (schemasManagementProxy.attributesForEntity.length > 0)
            generateFormFields();
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

    override public function registerValidators():void {
    }

    override public function bindForm():void {
        resetValidation();

        for each (var attVal:AttributeValue in _attributesValues) {
            var iField:DisplayObject = view.extraSection.getChildByName(attVal.name);
            if ( iField is TextInput ) {
                (iField as TextInput).text = attVal.value as String;
            }
            else if ( iField is DateField ) {
                (iField as DateField).selectedDate = attVal.value as Date;
            }
            else if ( iField is MultiValuedField) {
                var values:ArrayCollection = new ArrayCollection(attVal.value.split(','));
                (iField as MultiValuedField).attributeValues  = values;
                (iField as MultiValuedField).bindForm();
            }
        }
    }

    override public function bindModel():void {
        _attributesValues = new ArrayCollection();

        if (schemasManagementProxy.attributesForEntity.length > 0) {
            for( var i:int = 0; i < view.extraSection.dataProvider.length; i++ ) {
                var formItem:FormItem = view.extraSection.dataProvider.getItemAt(i) as FormItem;
                var inputField:IVisualElement = formItem.getElementAt(0);
                var attributeVal:AttributeValue = new AttributeValue();
                attributeVal.id = (inputField as UIComponent).id as Number;
                attributeVal.name = (inputField as UIComponent).name;

                if ( inputField is TextInput ) {
                    var ti:TextInput = inputField as TextInput;
                    attributeVal.value = ti.text;
                }
                else if ( inputField is DateField ) {
                    var di:DateField = inputField as DateField;
                    attributeVal.value = di.selectedDate as String;
                }
                else if ( inputField is MultiValuedField) {
                    var mvField:MultiValuedField = inputField as MultiValuedField;
                    mvField.bindModel();
                    attributeVal.value = mvField.attributeValues.source.join(',');
                }
                _attributesValues.addItem(attributeVal);
            }
        }
    }

    public function generateFormFields():void {
        view.extraSection.dataProvider = new ArrayCollection();
        for each (var attr:Attribute in schemasManagementProxy.attributesForEntity) {
            var fItem:FormItem = new FormItem();
            fItem.name = attr.name;
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
            view.extraSection.dataProvider.addItem(fItem);
        }
        bindForm();
    }

    private function createStringField(fItem:FormItem,attr:Attribute):void {
        if (attr.multivalued) { //multivalued field - list
            fItem.addElement(new MultiValuedField(attr));
        }
        else { // single text field
            var textInput:TextInput = new TextInput();
            textInput.id = attr.name;
            textInput.width = 300;
            _validators.push(registerInputValidator(textInput,attr));
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
            _validators.push(registerInputValidator(numberInput,attr));
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
            dateInput.formatString = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.DATE_FORMAT');
            _validators.push(registerInputValidator(dateInput,attr));
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
            _validators.push(registerInputValidator(emailInput,attr));
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
            _validators.push(registerInputValidator(urlInput,attr));
            fItem.addElement(urlInput);
        }
    }

    private function registerInputValidator(comp:UIComponent,attr:Attribute):Validator {
        var _uiCompValidator:Validator;

        switch (attr.type.toString()) {
            case TypeDTOEnum.STRING.toString():
                _uiCompValidator = new StringValidator();
                break;
            case TypeDTOEnum.INT.toString():
                _uiCompValidator = new NumberValidator();
                break;
            case TypeDTOEnum.DATE.toString():
                _uiCompValidator = new DateValidator();
                (_uiCompValidator as DateValidator).inputFormat = (comp as DateField).formatString;
                break;
            case TypeDTOEnum.EMAIL.toString():
                _uiCompValidator = new EmailValidator();
                break;
            case TypeDTOEnum.URL.toString():
                _uiCompValidator = new URLValidator();
                break;
        }
        _uiCompValidator.source = comp;
        _uiCompValidator.required = attr.required;
        _uiCompValidator.property = "text";

        return _uiCompValidator;
    }

    public function get schemasManagementProxy():SchemasManagementProxy {
        return _schemasManagementProxy;
    }

    public function set schemasManagementProxy(value:SchemasManagementProxy):void {
        _schemasManagementProxy = value;
    }

    public function get getValidators():Array {
        return _validators;
    }

    public function get attributesValues():ArrayCollection {
        return _attributesValues;
    }

    public function set attributesValues(value:ArrayCollection):void {
        _attributesValues = value;
    }

    public function get view():ExtraAttributesTab
    {
        return viewComponent as ExtraAttributesTab;
    }

}
}
