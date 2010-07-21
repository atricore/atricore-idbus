package com.atricore.idbus.console.management
{

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;

import com.atricore.idbus.console.management.controller.event.ManagementGridButtonEvent;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.collections.HierarchicalData;
import mx.controls.Alert;
import mx.controls.ComboBox;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.events.CloseEvent;
import mx.events.DragEvent;
import mx.managers.DragManager;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class ManagementViewMediator extends Mediator {
    public static const NAME:String = "ManagementViewMediator";

    private var _proxy:ProjectProxy;

    public function ManagementViewMediator(viewComp:ManagementView) {
        super(NAME, viewComp);
        _proxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;

        // Saved Appliances Grid
        viewComp.grdSavedAppliances.dataProvider = new HierarchicalData(_proxy.identityApplianceList);
        viewComp.grdSavedAppliances.addEventListener(ManagementGridButtonEvent.CLICK, handleGridButton);
        viewComp.grdSavedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        viewComp.grdSavedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        viewComp.colSavedApplianceName.labelFunction = identityApplianceNameLabel;

        // Compiled Appliances Grid
        viewComp.grdCompiledAppliances.addEventListener(ManagementGridButtonEvent.CLICK, handleGridButton);
        viewComp.grdCompiledAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        viewComp.grdCompiledAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        viewComp.grdCompiledAppliances.addEventListener(DragEvent.DRAG_DROP, dropInSavedAppliance);
        viewComp.colCompiledApplianceName.labelFunction = identityApplianceNameLabel;

        // Deployed Appliances Grid
        viewComp.grdDeployedAppliances.addEventListener(ManagementGridButtonEvent.CLICK, handleGridButton);
        viewComp.grdDeployedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        viewComp.grdDeployedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        viewComp.grdDeployedAppliances.addEventListener(DragEvent.DRAG_DROP, dropInCompiledAppliance);
        viewComp.grdDeployedAppliances.labelFunction = identityApplianceNameLabel;
        viewComp.colDeployedApplianceName.labelFunction = identityApplianceNameLabel;

        // Disposed Appliances Grid
        viewComp.grdDisposedAppliances.addEventListener(ManagementGridButtonEvent.CLICK, handleGridButton);
        viewComp.grdDisposedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        viewComp.grdDisposedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        viewComp.grdDisposedAppliances.addEventListener(DragEvent.DRAG_DROP, dropInDeployedAppliance);
        viewComp.grdDisposedAppliances.labelFunction = identityApplianceNameLabel;
        viewComp.colDisposedApplianceName.labelFunction = identityApplianceNameLabel;


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

    private function handleGridButton(event:ManagementGridButtonEvent):void {
        switch (event.action) {
            case ManagementGridButtonEvent.ACTION_ADD_CHILD :
                var parent:IdentityApplianceDTO = event.data as IdentityApplianceDTO;
                break;
            case ManagementGridButtonEvent.ACTION_REMOVE :
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

    public function get view():ManagementView {
        return viewComponent as ManagementView;
    }

}
}