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

package com.atricore.idbus.console.main {
import com.adobe.components.SizeableTitleWindow;
import com.atricore.idbus.console.components.wizard.Wizard;
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingView;

import flash.display.DisplayObject;
import flash.events.Event;

import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.effects.Effect;
import mx.effects.Iris;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.PopUpManager;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class BasePopUpManager {

    protected var _app:AtricoreConsole;

    protected var _lastWindowNotification:INotification;
    protected var _popup:SizeableTitleWindow;
    protected var _popupVisible:Boolean = false;
    protected var _progress:SizeableTitleWindow;
    protected var _progressVisible:Boolean = false;
    protected var _popupParent:UIComponent;
    protected var _wizard:Wizard;

    protected var _popUpOpenEffect:Effect;
    protected var _popUpCloseEffect:Effect;

    protected var _progressOpenEffect:Effect;
    protected var _progressCloseEffect:Effect;

    protected var _wizardOpenEffect:Effect;
    protected var _wizardCloseEffect:Effect;

    protected var _facade:IFacade;
    
    protected var _processingMediator:ProcessingMediator;
    protected var _processingView:ProcessingView;


    public function init(facade:IFacade, popupParent:UIComponent):void {
        _facade = facade;
        _popupParent = popupParent;
        
        _popup = new SizeableTitleWindow();
        _popup.styleName = "";
        _popup.verticalScrollPolicy = "off";
        _popup.horizontalScrollPolicy = "off";
        _popup.showCloseButton = true;
        _popup.addEventListener(CloseEvent.CLOSE, handleHidePopup);
        createPopUpOpenCloseEffects();

        _progress = new SizeableTitleWindow();
        _progress.styleName = "";
        _progress.verticalScrollPolicy = "off";
        _progress.horizontalScrollPolicy = "off";
        _progress.showCloseButton = false;
        _progress.addEventListener(CloseEvent.CLOSE, handleHideProgress);
        createProgressOpenCloseEffects();

        createWizardOpenCloseEffects();
    }


    public function get processingMediator():ProcessingMediator {
        return _processingMediator;
    }

    public function set processingMediator(value:ProcessingMediator):void {
        _processingMediator = value;
    }

    protected function createPopUpOpenCloseEffects():void {
        var irisOpen:Iris = new Iris(_popup);
        irisOpen.scaleXFrom = 0;
        irisOpen.scaleYFrom = 0;
        irisOpen.scaleXTo = 1;
        irisOpen.scaleYTo = 1;
        irisOpen.duration = 200;
        _popUpOpenEffect = irisOpen;
        var irisClose:Iris = new Iris(_popup);
        irisClose.scaleXFrom = 1;
        irisClose.scaleYFrom = 1;
        irisClose.scaleXTo = 0;
        irisClose.scaleYTo = 0;
        irisClose.duration = 200;
        _popUpCloseEffect = irisClose;
    }

    protected function createProgressOpenCloseEffects():void {
        var irisOpen:Iris = new Iris(_progress);
        irisOpen.scaleXFrom = 0;
        irisOpen.scaleYFrom = 0;
        irisOpen.scaleXTo = 1;
        irisOpen.scaleYTo = 1;
        irisOpen.duration = 200;
        _progressOpenEffect = irisOpen;
        var irisClose:Iris = new Iris(_progress);
        irisClose.scaleXFrom = 1;
        irisClose.scaleYFrom = 1;
        irisClose.scaleXTo = 0;
        irisClose.scaleYTo = 0;
        irisClose.duration = 200;
        _progressCloseEffect = irisClose;
    }

    protected function createWizardOpenCloseEffects():void {
        var irisOpen:Iris = new Iris(_wizard);
        irisOpen.scaleXFrom = 0;
        irisOpen.scaleYFrom = 0;
        irisOpen.scaleXTo = 1;
        irisOpen.scaleYTo = 1;
        irisOpen.duration = 200;
        _wizardOpenEffect = irisOpen;
        var irisClose:Iris = new Iris(_wizard);
        irisClose.scaleXFrom = 1;
        irisClose.scaleYFrom = 1;
        irisClose.scaleXTo = 0;
        irisClose.scaleYTo = 0;
        irisClose.duration = 200;
        _wizardCloseEffect = irisClose;
    }

    protected function handleHidePopup(event:Event):void {
        PopUpManager.removePopUp(_popup);
        _popup.removeAllChildren();
        _popUpCloseEffect.end();
        _popUpCloseEffect.play();
        _popupVisible = false;
    }

    public function handleHideProgress(event:Event):void {
        PopUpManager.removePopUp(_progress);
        _progress.removeAllChildren();
        _progressCloseEffect.end();
        _progressCloseEffect.play();
        _progressVisible = false;
    }

    protected function handleHideWizard(event:Event):void {
        PopUpManager.removePopUp(_wizard);
        _wizardCloseEffect.end();
        _wizardCloseEffect.play();
    }

    protected function showPopup(child:UIComponent):void {
        if (_popupVisible) {
            _popup.removeAllChildren();
        }
        else {
            //PopUpManager.addPopUp(_popup, _popupParent, true);
            PopUpManager.addPopUp(_popup, FlexGlobals.topLevelApplication as DisplayObject, true);
            PopUpManager.centerPopUp(_popup);
            _popupVisible = true;
            _popUpOpenEffect.end();
            _popUpOpenEffect.play();
        }
        _popup.addChild(child);
    }

    protected function showProgress(child:UIComponent):void {
        if (_progressVisible) {
            _progress.removeAllChildren();
        }
        else {
            //PopUpManager.addPopUp(_progress, _popupParent, true);
            PopUpManager.addPopUp(_progress, FlexGlobals.topLevelApplication as DisplayObject, true);
            PopUpManager.centerPopUp(_progress);
            _progressVisible = true;
            _progressOpenEffect.end();
            _progressOpenEffect.play();
        }
        _progress.addChild(child);
    }

    protected function showWizard(wizard:Wizard):void {
        _wizard = wizard;
        _wizard.x = (_popupParent.width / 2) - 225;
        _wizard.y = 80;
        _wizard.styleName = "mainWizard";
        _wizard.verticalScrollPolicy = "off";
        _wizard.horizontalScrollPolicy = "off";
        _wizard.showCloseButton = true;
        _wizard.addEventListener(CloseEvent.CLOSE, handleHideWizard);
        _wizard.addEventListener(WizardEvent.WIZARD_CANCEL, handleHideWizard);
        _wizard.addEventListener(FlexEvent.CREATION_COMPLETE, onWizardCreationComplete, false, 0);

        //PopUpManager.addPopUp(_wizard, _popupParent, true);
        PopUpManager.addPopUp(_wizard, FlexGlobals.topLevelApplication as DisplayObject, true);
        PopUpManager.centerPopUp(_wizard);
        _wizardOpenEffect.end();
        _wizardOpenEffect.play();
    }

    /**
     * This handler will center the popup AFTER its dimensions are properly set.
     * @param event
     */
    private function onWizardCreationComplete(event:Event):void {
        PopUpManager.centerPopUp(event.currentTarget as Wizard);
    }

    public function showProcessingWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createProcessingWindow();
        _progress.title = "Progress";
        _progress.width = 300;
        _progress.height = 200;
        //_progress.x = (_popupParent.width / 2) - 225;
        //_progress.y = 80;
        showProgress(_processingView);
    }

    private function createProcessingWindow():void {
        _processingView = new ProcessingView();
        _processingView.addEventListener(FlexEvent.CREATION_COMPLETE, handleProcessingWindowCreated);
    }

    private function handleProcessingWindowCreated(event:FlexEvent):void {
        processingMediator.setViewComponent(_processingView);
        processingMediator.handleNotification(_lastWindowNotification);
    }

    public function hideProcessingWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        handleHideProgress(null);
    }
}
}