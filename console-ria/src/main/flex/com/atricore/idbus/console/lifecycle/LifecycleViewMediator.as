package com.atricore.idbus.console.lifecycle
{

import com.atricore.idbus.console.lifecycle.controller.event.LifecycleGridButtonEvent;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.collections.HierarchicalData;
import mx.controls.Alert;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.events.CloseEvent;
import mx.events.DragEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class LifecycleViewMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    public function LifecycleViewMediator(name:String = null, viewComp:LifecycleView = null) {
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
            view.grdSavedAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
            view.grdSavedAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
            view.grdSavedAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
            view.grdCompiledAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
            view.grdCompiledAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
            view.grdCompiledAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
            view.grdCompiledAppliances.removeEventListener(DragEvent.DRAG_DROP, dropInSavedAppliance);
            view.grdDeployedAppliances.
                    (LifecycleGridButtonEvent.CLICK, handleGridButton);
            view.grdDeployedAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
            view.grdDeployedAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
            view.grdDeployedAppliances.removeEventListener(DragEvent.DRAG_DROP, dropInCompiledAppliance);
            view.grdDeployedAppliances.labelFunction = identityApplianceNameLabel;
            view.grdDisposedAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
            view.grdDisposedAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
            view.grdDisposedAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
            view.grdDisposedAppliances.removeEventListener(DragEvent.DRAG_DROP, dropInDeployedAppliance);
        }


        super.setViewComponent(viewComponent);

        /* TODO: invoke only upon the view is created
         init();
         */
    }

    private function init():void {

        // Saved Appliances Grid
        view.grdSavedAppliances.dataProvider = new HierarchicalData(projectProxy.identityApplianceList);
        view.grdSavedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdSavedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdSavedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.colSavedApplianceName.labelFunction = identityApplianceNameLabel;

        // Compiled Appliances Grid
        view.grdCompiledAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdCompiledAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdCompiledAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdCompiledAppliances.addEventListener(DragEvent.DRAG_DROP, dropInSavedAppliance);
        view.colCompiledApplianceName.labelFunction = identityApplianceNameLabel;

        // Deployed Appliances Grid
        view.grdDeployedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDeployedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_DROP, dropInCompiledAppliance);
        view.grdDeployedAppliances.labelFunction = identityApplianceNameLabel;
        view.colDeployedApplianceName.labelFunction = identityApplianceNameLabel;

        // Disposed Appliances Grid
        view.grdDisposedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDisposedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_DROP, dropInDeployedAppliance);
        view.grdDisposedAppliances.labelFunction = identityApplianceNameLabel;
        view.colDisposedApplianceName.labelFunction = identityApplianceNameLabel;

    }


    private function identityApplianceNameLabel(item:Object, column:AdvancedDataGridColumn):String {
        return (item as IdentityApplianceDTO).idApplianceDefinition.name;
    }

    /**
     * Disallow dragging of tasks
     */
    private function handleDragEnter(event:DragEvent):void {
        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        /* for preventing a drag 
         event.preventDefault();
         DragManager.showFeedback(DragManager.NONE);
         */
    }

    private function dropInSavedAppliance(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        // TODO: spawn a build
        trace("Building Appliances " + items);
    }

    private function dropInCompiledAppliance(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        // TODO: spawn a deployment
        trace("Deploying Appliances " + items);

    }

    private function dropInDeployedAppliance(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        // TODO: spawn an undeploy
        trace("Undeploying Appliances " + items);

    }


    override public function listNotificationInterests():Array {
        return [];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {

        }
    }


    private function getSelectionIndex(list:ArrayCollection, appliance:IdentityApplianceDTO):int {
        for (var index:int = 0; index < list.length; index++) {
            if (list.getItemAt(index).id == appliance.id) {
                return index;
            }
        }
        return -1;
    }

    private function handleGridButton(event:LifecycleGridButtonEvent):void {
        switch (event.action) {
            case LifecycleGridButtonEvent.ACTION_EDIT :
                var parent:IdentityApplianceDTO = event.data as IdentityApplianceDTO;
                break;
            case LifecycleGridButtonEvent.ACTION_REMOVE :
                var parent:IdentityApplianceDTO = event.data as IdentityApplianceDTO;
                Alert.show("Are you sure you want to delete this item?", "Confirm Removal", Alert.YES | Alert.NO, null, removeConfirmed, null, Alert.YES);
                break;
        }
    }

    private function handleGridDoubleClick(event:MouseEvent):void {
        var item:IdentityApplianceDTO = event.currentTarget.selectedItem as IdentityApplianceDTO;
        if (item) {
            // TODO: open in modeler
        }
    }

    private function removeConfirmed(event:CloseEvent):void {
        if (event.detail == Alert.YES) {
            // verify that a removal can be performed
        }
    }


    private function buildToolTip(row:Object):String {
        var appliance:IdentityApplianceDefinitionDTO = row as IdentityApplianceDefinitionDTO;
        return appliance ? appliance.name : "";
    }

    public function get view():LifecycleView {
        return viewComponent as LifecycleView;
    }

}
}