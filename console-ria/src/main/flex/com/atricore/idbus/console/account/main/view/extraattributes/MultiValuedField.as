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
import com.atricore.idbus.console.components.IconButton;
import com.atricore.idbus.console.components.IconButtonSkin;
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.services.dto.schema.Attribute;
import com.atricore.idbus.console.services.dto.schema.TypeDTOEnum;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.controls.DataGrid;
import mx.controls.DateField;
import mx.controls.Label;
import mx.core.ClassFactory;
import mx.core.UIComponent;
import mx.events.FlexEvent;
import mx.events.ListEvent;
import mx.events.ValidationResultEvent;
import mx.formatters.DateFormatter;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.DateValidator;
import mx.validators.EmailValidator;
import mx.validators.NumberValidator;
import mx.validators.StringValidator;
import mx.validators.Validator;

import spark.components.HGroup;
import spark.components.TextInput;
import spark.components.VGroup;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 2/11/11 - 4:50 PM
 */

public class MultiValuedField extends VGroup
{
    private var resMan:IResourceManager = ResourceManager.getInstance();

    private var _attribute:Attribute;
    private var _valuesList:DataGrid = new DataGrid();

    private var _uiInputComp:UIComponent;
    private var _uiCompValidator:Validator;
    private var _addBtn:IconButton = new IconButton();
    private var _delBtn:IconButton = new IconButton();

    public function MultiValuedField(attr:Attribute) {
        super();
        _attribute = attr;
        _valuesList.dataProvider = new ArrayCollection();
        if (_attribute.type.toString() == TypeDTOEnum.DATE.toString()) {
            _uiInputComp = new DateField();
            _uiInputComp.addEventListener(FlexEvent.VALUE_COMMIT , uiDateFieldChangeHandler);
        }
        else {
            _uiInputComp = new TextInput();
            _uiInputComp.addEventListener(Event.CHANGE , uiInputKeyChangeHandler);
        }

        registerInputValidators();

        // Create button for adding new values to list
        _addBtn.label ="+";
        _addBtn.setStyle("skinClass",Class(IconButtonSkin));
        _addBtn.setStyle("iconUp", EmbeddedIcons.generalAddIcon);
        _addBtn.setStyle("isLinkButton", "false");
        _addBtn.addEventListener(MouseEvent.CLICK,handleAddClick);
        _addBtn.enabled = false;
        // Create button for deleting values from list
        _delBtn.label ="-";
        _delBtn.setStyle("skinClass",Class(IconButtonSkin));
        _delBtn.setStyle("iconUp", EmbeddedIcons.generalRemoveIcon);
        _delBtn.setStyle("isLinkButton", "false");
        _delBtn.addEventListener(MouseEvent.CLICK,handleDeleteClick);
        _delBtn.enabled = false;

        _valuesList.headerHeight = 0;
        _valuesList.rowCount = 3;
        _valuesList.addEventListener(ListEvent.ITEM_CLICK, handleListClick);

        _valuesList.width = 300;
        this.width = 310;
        _uiInputComp.width = 245;
        _addBtn.width = 22;
        _delBtn.width = 22;

        var editFormGroup:HGroup = new HGroup();
        editFormGroup.addElement(_uiInputComp);
        editFormGroup.addElement(_addBtn);
        editFormGroup.addElement(_delBtn);
        this.addElement(_valuesList);
        this.addElement(editFormGroup);
    }

    private function handleAddClick(e:MouseEvent):void {
        var event:ValidationResultEvent  = _uiCompValidator.validate();

        if ( event.type==ValidationResultEvent.VALID ) {
            if (_uiInputComp is DateField &&
                    (_uiInputComp as DateField).selectedDate != null)          {
                var date:Object = (_uiInputComp as DateField).selectedDate;
                _valuesList.dataProvider.addItem({date:date});
                _valuesList.columns[0].itemRenderer = dateItemRenderer(date as Date);
                (_uiInputComp as DateField).data = null;
                _uiCompValidator.source.errorString = "";
                _addBtn.enabled = false;
            }

            else if (_uiInputComp is TextInput &&
                    (_uiInputComp as TextInput).text !="") {
                var valStr:String = (_uiInputComp as TextInput).text;
                _valuesList.dataProvider.addItem({value:valStr});
                (_uiInputComp as TextInput).text = "";
                _uiCompValidator.source.errorString = "";
                _addBtn.enabled = false;
            }
        }
    }

    private function handleDeleteClick(e:MouseEvent):void {
        _valuesList.dataProvider.removeItemAt(_valuesList.selectedIndex);
        _delBtn.enabled = false;
    }

    // Setup item click event
    private function handleListClick(e:ListEvent):void {
        var selectedObject:Object = e.currentTarget.selectedItem;
        if (selectedObject != null)
            _delBtn.enabled = true;
    }

    private function uiDateFieldChangeHandler(event:Event):void {
        if ( (_uiInputComp as DateField).selectedDate != null)
            _addBtn.enabled = true;
        else
            _addBtn.enabled = false;
    }

    private function uiInputKeyChangeHandler(event:Event):void {
        if ( (_uiInputComp as TextInput).text !="")
            _addBtn.enabled = true;
        else
            _addBtn.enabled = false;
    }

    private function registerInputValidators():void {
        switch (attribute.type.toString()) {
            case TypeDTOEnum.STRING.toString():
                _uiCompValidator = new StringValidator();
                break;
            case TypeDTOEnum.INT.toString():
                _uiCompValidator = new NumberValidator();
                break;
            case TypeDTOEnum.DATE.toString():
                _uiCompValidator = new DateValidator();
                break;
            case TypeDTOEnum.EMAIL.toString():
                _uiCompValidator = new EmailValidator();
                break;
            case TypeDTOEnum.URL.toString():
                _uiCompValidator = new URLValidator();
                break;
        }
        _uiCompValidator.source = _uiInputComp;
        _uiCompValidator.required = true;
        _uiCompValidator.property = "text";
    }

    private function dateItemRenderer(date:Date):ClassFactory {
        var iRenderer:ClassFactory = new ClassFactory(Label);
        var dfmt:DateFormatter = new DateFormatter();
        dfmt.formatString = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.DATE_FORMAT');
        iRenderer.properties = {text: dfmt.format(date) };
        return iRenderer;
    }

    public function bindForm():void {
        for each (var val:Object in attribute.value) {
            _valuesList.dataProvider.addItem(val);
        }
    }

    public function bindModel():void {
        _attribute.value = ArrayCollection(_valuesList.dataProvider);
    }

    public function get attribute():Attribute {
        return _attribute;
    }

    public function set attribute(value:Attribute):void {
        _attribute = value;
    }

    public function get valuesList():DataGrid {
        return _valuesList;
    }

    public function set valuesList(value:DataGrid):void {
        _valuesList = value;
    }
}
}
