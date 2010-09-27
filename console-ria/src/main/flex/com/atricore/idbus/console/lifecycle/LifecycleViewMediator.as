package com.atricore.idbus.console.lifecycle
{
import com.atricore.idbus.console.components.CustomDataGrid;
import com.atricore.idbus.console.lifecycle.controller.event.LifecycleGridButtonEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.controller.BuildIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.DeployIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.DisposeIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.StartIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.StopIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.UndeployIdentityApplianceCommand;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityApplianceState;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.collections.HierarchicalData;
import mx.controls.AdvancedDataGrid;
import mx.controls.Alert;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.events.CloseEvent;
import mx.events.DragEvent;

import mx.events.FlexEvent;

import mx.managers.DragManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class LifecycleViewMediator extends IocMediator implements IDisposable {

    public static const viewName:String = "LifecycleView";

    private var _projectProxy:ProjectProxy;

    private var _created:Boolean;

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
        (viewComponent as LifecycleView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;

        /* Remove unused title in lifecycle management panel */
        view.titleDisplay.width = 0;
        view.titleDisplay.height = 0;

        // Saved Appliances Grid
        view.grdSavedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdSavedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdSavedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdSavedAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.colSavedApplianceName.labelFunction = identityApplianceNameLabel;

        // Compiled Appliances Grid
        view.grdCompiledAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdCompiledAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdCompiledAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdCompiledAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdCompiledAppliances.addEventListener(DragEvent.DRAG_DROP, dropInSavedOrDeployedAppliance);
        view.colCompiledApplianceName.labelFunction = identityApplianceNameLabel;

        // Deployed Appliances Grid
        view.grdDeployedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDeployedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_DROP, dropInCompiledAppliance);
        view.colDeployedApplianceName.labelFunction = identityApplianceNameLabel;

        // Disposed Appliances Grid
        view.grdDisposedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDisposedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_DROP, dropInDeployedAppliance);
        view.grdDisposedAppliances.labelFunction = identityApplianceNameLabel;
        view.colDisposedApplianceName.labelFunction = identityApplianceNameLabel;

        initGrids();
    }

    public function initGrids():void {
        if (_created) {
            var savedAppliances:ArrayCollection = new ArrayCollection();
            var compiledAppliances:ArrayCollection = new ArrayCollection();
            var deployedAppliances:ArrayCollection = new ArrayCollection();
            var disposedAppliances:ArrayCollection = new ArrayCollection();

            var savedAppliancesSelectedIndex:int = -1;
            var compiledAppliancesSelectedIndex:int = -1;
            var deployedAppliancesSelectedIndex:int = -1;
            var disposedAppliancesSelectedIndex:int = -1;

            // reset appliance(s) selection
            var grid:AdvancedDataGrid = view.grdSavedAppliances;
            view.grdSavedAppliances.selectedIndex = savedAppliancesSelectedIndex;
            view.grdCompiledAppliances.selectedIndex = compiledAppliancesSelectedIndex;
            view.grdDeployedAppliances.selectedIndex = deployedAppliancesSelectedIndex;
            view.grdDisposedAppliances.selectedIndex = disposedAppliancesSelectedIndex;

            if (projectProxy.identityApplianceList != null) {
                for (var i:int = 0; i < projectProxy.identityApplianceList.length; i++) {
                    var appliance:IdentityAppliance = projectProxy.identityApplianceList[i] as IdentityAppliance;
                    var selected:Boolean = false;
                    if (projectProxy.currentIdentityAppliance != null &&
                            appliance.id == projectProxy.currentIdentityAppliance.id) {
                        selected = true;
                    }
                    if (appliance.state != IdentityApplianceState.DISPOSED.name) {
                        savedAppliances.addItem(appliance);
                        if (selected) {
                            savedAppliancesSelectedIndex = savedAppliances.length - 1;
                        }
                    }
                    if (appliance.state == IdentityApplianceState.BUILT.name) {
                        compiledAppliances.addItem(appliance);
                        //if (selected) {
                        //    //compiledAppliancesSelectedIndex = compiledAppliances.length - 1;
                        //}
                    } else if (appliance.state == IdentityApplianceState.DEPLOYED.name ||
                            appliance.state == IdentityApplianceState.STARTED.name) {
                        deployedAppliances.addItem(appliance);
                        //if (selected) {
                        //    deployedAppliancesSelectedIndex = deployedAppliances.length - 1;
                        //}
                    } else if (appliance.state == IdentityApplianceState.DISPOSED.name) {
                        disposedAppliances.addItem(appliance);
                        //if (selected) {
                        //    disposedAppliancesSelectedIndex = disposedAppliances.length - 1;
                        //}
                    }
                }
            }

            view.grdSavedAppliances.dataProvider = new HierarchicalData(savedAppliances);
            view.grdCompiledAppliances.dataProvider = new HierarchicalData(compiledAppliances);
            view.grdDeployedAppliances.dataProvider = new HierarchicalData(deployedAppliances);
            view.grdDisposedAppliances.dataProvider = new HierarchicalData(disposedAppliances);

            // When dataProvider is changed (e.g. after the second lifecycle view openinig),
            // setting selectedIndex is not working without calling validateNow().
            // This might be slow for large datasets?
            // Using callLater() function instead of this is not working as expected.
            // Another solution would include extending the grid and
            // overriding set dataProvider function.
            view.grdSavedAppliances.validateNow();

            view.grdSavedAppliances.selectedIndex = savedAppliancesSelectedIndex;
            //view.grdCompiledAppliances.selectedIndex = compiledAppliancesSelectedIndex;
            //view.grdDeployedAppliances.selectedIndex = deployedAppliancesSelectedIndex;
            //view.grdDisposedAppliances.selectedIndex = disposedAppliancesSelectedIndex;
        }
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        view.grdSavedAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdSavedAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdSavedAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdSavedAppliances.removeEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdCompiledAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdCompiledAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdCompiledAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdCompiledAppliances.removeEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdCompiledAppliances.removeEventListener(DragEvent.DRAG_DROP, dropInSavedOrDeployedAppliance);
        view.grdDeployedAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDeployedAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDeployedAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDeployedAppliances.removeEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdDeployedAppliances.removeEventListener(DragEvent.DRAG_DROP, dropInCompiledAppliance);
        view.grdDeployedAppliances.labelFunction = identityApplianceNameLabel;
        view.grdDisposedAppliances.removeEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDisposedAppliances.removeEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDisposedAppliances.removeEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDisposedAppliances.removeEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdDisposedAppliances.removeEventListener(DragEvent.DRAG_DROP, dropInDeployedAppliance);

        view = null;
    }


    private function identityApplianceNameLabel(item:Object, column:AdvancedDataGridColumn):String {
        return (item as IdentityAppliance).idApplianceDefinition.name;
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

    private function dropInSavedOrDeployedAppliance(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;
        var sourceGrid:CustomDataGrid = event.dragInitiator as CustomDataGrid;

        if (sourceGrid.id == "grdSavedAppliances") {
            trace("Building Appliances " + items);

            sendNotification(ProcessingMediator.START, "Building appliance ...");
            for (var i:int = 0; i < items.length; i++) {
                var appliance:IdentityAppliance = items[i] as IdentityAppliance;
                sendNotification(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, [appliance.id.toString(), false]);
            }
        } else if (sourceGrid.id == "grdDeployedAppliances") {
            trace("Undeploying Appliances " + items);

            sendNotification(ProcessingMediator.START, "Undeploying appliance ...");
            for (var i:int = 0; i < items.length; i++) {
                var appliance:IdentityAppliance = items[i] as IdentityAppliance;
                sendNotification(ApplicationFacade.UNDEPLOY_IDENTITY_APPLIANCE, appliance.id.toString());
            }
        }
    }

    private function dropInCompiledAppliance(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        trace("Deploying Appliances " + items);

        sendNotification(ProcessingMediator.START, "Deploying appliance ...");
        for (var i:int = 0; i < items.length; i++) {
            var appliance:IdentityAppliance = items[i] as IdentityAppliance;
            sendNotification(ApplicationFacade.DEPLOY_IDENTITY_APPLIANCE, [appliance.id.toString(), false]);
        }
    }

    private function dropInDeployedAppliance(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        trace("Disposing Appliances " + items);

        sendNotification(ProcessingMediator.START, "Disposing appliance ...");
        for (var i:int = 0; i < items.length; i++) {
            var appliance:IdentityAppliance = items[i] as IdentityAppliance;
            sendNotification(ApplicationFacade.DISPOSE_IDENTITY_APPLIANCE, appliance.id.toString());
        }
    }

    private function handleDragOver(event:DragEvent):void {
        var sourceGrid:CustomDataGrid = event.dragInitiator as CustomDataGrid;
        var targetGrid:CustomDataGrid = event.currentTarget as CustomDataGrid;

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        if ((sourceGrid.id == targetGrid.id) ||
                (sourceGrid.id == "grdSavedAppliances" && targetGrid.id != "grdCompiledAppliances") ||
                (sourceGrid.id == "grdCompiledAppliances" && targetGrid.id != "grdDeployedAppliances") ||
                (sourceGrid.id == "grdDeployedAppliances" && targetGrid.id == "grdSavedAppliances")) {
            event.preventDefault();
            DragManager.showFeedback(DragManager.NONE);
            return;
        }

        if (sourceGrid.id == "grdSavedAppliances") {
            if ((items[0] as IdentityAppliance).state != IdentityApplianceState.PROJECTED.name) {
                event.preventDefault();
                DragManager.showFeedback(DragManager.NONE);
                return;
            }
        }

        if (targetGrid.id == "grdDisposedAppliances") {
            if ((items[0] as IdentityAppliance).state == IdentityApplianceState.STARTED.name) {
                event.preventDefault();
                DragManager.showFeedback(DragManager.NONE);
                return;
            }
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.LIFECYCLE_VIEW_SELECTED,
            BuildIdentityApplianceCommand.SUCCESS,
            BuildIdentityApplianceCommand.FAILURE,
            DeployIdentityApplianceCommand.SUCCESS,
            DeployIdentityApplianceCommand.FAILURE,
            StartIdentityApplianceCommand.SUCCESS,
            StartIdentityApplianceCommand.FAILURE,
            StopIdentityApplianceCommand.SUCCESS,
            StopIdentityApplianceCommand.FAILURE,
            UndeployIdentityApplianceCommand.SUCCESS,
            UndeployIdentityApplianceCommand.FAILURE,
            DisposeIdentityApplianceCommand.SUCCESS,
            DisposeIdentityApplianceCommand.FAILURE,
            ApplicationFacade.LOGOUT];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.LIFECYCLE_VIEW_SELECTED:
                projectProxy.currentView = viewName;
                sendNotification(ApplicationFacade.CLEAR_MSG);
                initGrids();
                break;
            case BuildIdentityApplianceCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false);
                    sendNotification(ProcessingMediator.STOP);
                    //                    sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                    //                            "Appliance has been successfully built.");
                }
                break;
            case BuildIdentityApplianceCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error building appliance.");
                }
                break;
            case DeployIdentityApplianceCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false);
                    sendNotification(ProcessingMediator.STOP);
                    //                    sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                    //                            "Appliance has been successfully deployed.");
                }
                break;
            case DeployIdentityApplianceCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error deploying appliance.");
                }
                break;
            case UndeployIdentityApplianceCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false);
                    sendNotification(ProcessingMediator.STOP);
                    //                    sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                    //                            "Appliance has been successfully undeployed.");
                }
                break;
            case UndeployIdentityApplianceCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error uneploying appliance.");
                }
                break;
            case StartIdentityApplianceCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false);
                    sendNotification(ProcessingMediator.STOP);
                    //                    sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                    //                            "Appliance has been successfully started.");
                }
                break;
            case StartIdentityApplianceCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error starting appliance.");
                }
                break;
            case StopIdentityApplianceCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false);
                    sendNotification(ProcessingMediator.STOP);
                    //                    sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                    //                            "Appliance has been successfully stopped.");
                }
                break;
            case StopIdentityApplianceCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error stopping appliance.");
                }
                break;
            case DisposeIdentityApplianceCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false);
                    sendNotification(ProcessingMediator.STOP);
                    // sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                    //       "Appliance has been successfully disposed.");
                }
                break;
            case DisposeIdentityApplianceCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error disposing appliance.");
                }
                break;

            case ApplicationFacade.LOGOUT:

                break;
        }
    }


    private function getSelectionIndex(list:ArrayCollection, appliance:IdentityAppliance):int {
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
                var appliance:IdentityAppliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Opening identity appliance...");
                projectProxy.currentIdentityAppliance = null;
                sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, appliance.id.toString());
                break;
            case LifecycleGridButtonEvent.ACTION_REMOVE :
                var appliance:IdentityAppliance = event.data as IdentityAppliance;
                Alert.show("Are you sure you want to delete this item?", "Confirm Removal", Alert.YES | Alert.NO, null, removeConfirmed, null, Alert.YES);
                break;
            case LifecycleGridButtonEvent.ACTION_START :
                var appliance:IdentityAppliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Starting appliance ...");
                sendNotification(ApplicationFacade.START_IDENTITY_APPLIANCE, appliance.id.toString());
                break;
            case LifecycleGridButtonEvent.ACTION_STOP :
                var appliance:IdentityAppliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Stopping appliance ...");
                sendNotification(ApplicationFacade.STOP_IDENTITY_APPLIANCE, appliance.id.toString());
                break;
            case LifecycleGridButtonEvent.ACTION_UNDEPLOY :
                var appliance:IdentityAppliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Undeploying appliance ...");
                sendNotification(ApplicationFacade.UNDEPLOY_IDENTITY_APPLIANCE, appliance.id.toString());
                break;
            case LifecycleGridButtonEvent.ACTION_BUILD :
                var appliance:IdentityAppliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Rebuilding appliance ...");
                sendNotification(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, [appliance.id.toString(), false]);
                break;
        }
    }

    private function handleGridDoubleClick(event:MouseEvent):void {
        var item:IdentityAppliance = event.currentTarget.selectedItem as IdentityAppliance;
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
        var appliance:IdentityApplianceDefinition = row as IdentityApplianceDefinition;
        return appliance ? appliance.name : "";
    }

    private function updateAppliancesList(error:Boolean):void {
        if (!error) {
            var modifiedAppliance:IdentityAppliance = projectProxy.commandResultIdentityAppliance;
            if (modifiedAppliance != null) {
                for (var i:int = 0; i < projectProxy.identityApplianceList.length; i++) {
                    var appliance:IdentityAppliance = projectProxy.identityApplianceList[i] as IdentityAppliance;
                    if (modifiedAppliance.id == appliance.id) {
                        projectProxy.identityApplianceList[i] = modifiedAppliance;
                        break;
                    }
                }
            }
        }
        initGrids();

        // TODO: retrieve the list instead of modifying the existing one?
        //sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
    }

    protected function get view():LifecycleView {
        return viewComponent as LifecycleView;
    }

    protected function set view(lc:LifecycleView):void {
        viewComponent = lc;
    }

}
}