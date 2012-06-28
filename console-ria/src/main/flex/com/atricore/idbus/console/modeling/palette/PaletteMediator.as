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

package com.atricore.idbus.console.modeling.palette {
import com.atricore.idbus.console.base.palette.PaletteItemProvider;
import com.atricore.idbus.console.base.palette.model.PaletteEntry;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.palette.event.PaletteEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.ButtonBar;

import spark.components.ButtonBarButton;

import spark.components.supportClasses.ItemRenderer;

public class PaletteMediator extends IocMediator {
    private var selectedIndex:int;
    private var selectedItem:Object;
    public static const DESELECT_PALETTE_ELEMENT:String = "deselectPaletteElement";

    private var _projectProxy:ProjectProxy;

    private var _paletteProvider:PaletteItemProvider;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function get paletteProvider():PaletteItemProvider {
        return _paletteProvider;
    }

    public function set paletteProvider(value:PaletteItemProvider):void {
        _paletteProvider = value;
    }

    public function PaletteMediator(name : String = null, viewComp:PaletteView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {

        if (getViewComponent() != null) {
            view.removeEventListener(PaletteEvent.CLICK, handlePaletteClick);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        // bind view to palette model
        view.rptPaletteRoot.dataProvider = paletteProvider.getPalette();
        view.addEventListener(PaletteEvent.CLICK, handlePaletteClick);

    }

    public function handlePaletteClick(event : PaletteEvent) : void {
        //selectedItem = event.target;
        switch(event.action) {
            case PaletteEvent.ACTION_PALETTE_ITEM_CLICKED :
                /*
                var uiComponentSel:ButtonBar = selectedItem as ButtonBar;
                uiComponentSel.selected = true;
                */
                if (projectProxy.currentIdentityAppliance != null) {
                    var selectedPaletteEntry:PaletteEntry = event.data as PaletteEntry;
                    //sendNotification(ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM, selectedPaletteEntry.elementType);
                    sendNotification(ApplicationFacade.PALETTE_ELEMENT_SELECTED, selectedPaletteEntry.elementType);
                }
                break;
        }
    }

    override public function listNotificationInterests():Array {
        return [super.listNotificationInterests(),
            DESELECT_PALETTE_ELEMENT
        ];
    }

    override public function handleNotification(notification:INotification):void {
        //        super.handleNotification(notification);
        switch (notification.getName()) {
            case DESELECT_PALETTE_ELEMENT:
                if(selectedItem != null){
                    var uiComponentSel:ItemRenderer = selectedItem as ItemRenderer;
                    uiComponentSel.selected = false;
                    selectedItem = null;
                }
                break;
        }
    }

    public function get view():PaletteView {
        return viewComponent as PaletteView;
    }

}
}