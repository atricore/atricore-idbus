package com.atricore.idbus.console.lifecycle.main
{
import com.atricore.idbus.console.components.CustomDataGrid;
import com.atricore.idbus.console.lifecycle.main.controller.event.LifecycleGridButtonEvent;
import com.atricore.idbus.console.main.AppSectionMediator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityApplianceElementRequest;
import com.atricore.idbus.console.modeling.main.ModelerViewFactory;
import com.atricore.idbus.console.modeling.main.controller.BuildIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.DeployIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.DisposeIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceListLoadCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceRemoveCommand;
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

public class LifecycleMediator extends AppSectionMediator implements IDisposable {

    private var _projectProxy:ProjectProxy;

    private var _removedApplianceId:Number;

    private var _created:Boolean;

    public function LifecycleMediator(name:String = null, viewComp:LifecycleView = null) {
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
        view.grdStagedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdStagedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdStagedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdStagedAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdStagedAppliances.addEventListener(DragEvent.DRAG_DROP, handleDropInStagedGrid);
        view.colStagedApplianceName.labelFunction = identityApplianceNameLabel;

        // Deployed Appliances Grid
        view.grdDeployedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        view.grdDeployedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdDeployedAppliances.addEventListener(DragEvent.DRAG_DROP, handleDropInDeployedGrid);
        view.colDeployedApplianceName.labelFunction = identityApplianceNameLabel;

        // Disposed Appliances Grid
        view.grdDisposedAppliances.addEventListener(LifecycleGridButtonEvent.CLICK, handleGridButton);
        //view.grdDisposedAppliances.addEventListener(MouseEvent.DOUBLE_CLICK, handleGridDoubleClick);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_ENTER, handleDragEnter);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_OVER, handleDragOver);
        view.grdDisposedAppliances.addEventListener(DragEvent.DRAG_DROP, handleDropInDisposedGrid);
        view.grdDisposedAppliances.labelFunction = identityApplianceNameLabel;
        view.colDisposedApplianceName.labelFunction = identityApplianceNameLabel;

        initGrids();
    }

    public function initGrids():void {
        if (_created) {
            var savedAppliances:ArrayCollection = new ArrayCollection();
            var stagedAppliances:ArrayCollection = new ArrayCollection();
            var deployedAppliances:ArrayCollection = new ArrayCollection();
            var disposedAppliances:ArrayCollection = new ArrayCollection();

            var savedAppliancesSelectedIndex:int = -1;
            var stagedAppliancesSelectedIndex:int = -1;
            var deployedAppliancesSelectedIndex:int = -1;
            var disposedAppliancesSelectedIndex:int = -1;

            // reset appliance(s) selection
            view.grdSavedAppliances.selectedIndex = savedAppliancesSelectedIndex;
            view.grdStagedAppliances.selectedIndex = stagedAppliancesSelectedIndex;
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
                    if (appliance.state == IdentityApplianceState.STAGED.name) {
                        stagedAppliances.addItem(appliance);
                        //if (selected) {
                        //    //stagedAppliancesSelectedIndex = stagedAppliances.length - 1;
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
            view.grdStagedAppliances.dataProvider = new HierarchicalData(stagedAppliances);
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
            //view.grdStagedAppliances.selectedIndex = stagedAppliancesSelectedIndex;
            //view.grdDeployedAppliances.selectedIndex = deployedAppliancesSelectedIndex;
            //view.grdDisposedAppliances.selectedIndex = disposedAppliancesSelectedIndex;
        }
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        _created = false;
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

    private function handleDropInStagedGrid(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;
        var sourceGrid:CustomDataGrid = event.dragInitiator as CustomDataGrid;

        if (sourceGrid.id == "grdSavedAppliances") {
            trace("Building Appliances " + items);

            sendNotification(ProcessingMediator.START, "Building appliance ...");
            for (var i:int = 0; i < items.length; i++) {
                var a1:IdentityAppliance = items[i] as IdentityAppliance;
                sendNotification(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, [a1.id.toString(), false]);
            }

        } else if (sourceGrid.id == "grdDeployedAppliances") {

            trace("Undeploying Appliances " + items);

            sendNotification(ProcessingMediator.START, "Undeploying appliance ...");
            for (var j:int = 0; j < items.length; j++) {
                var a2:IdentityAppliance = items[j] as IdentityAppliance;
                sendNotification(ApplicationFacade.UNDEPLOY_IDENTITY_APPLIANCE, a2.id.toString());
            }
        }
    }

    private function handleDropInDeployedGrid(event:DragEvent):void {

        var items:Array = event.dragSource.dataForFormat('treeDataGridItems') as Array;

        trace("Deploying Appliances " + items);

        sendNotification(ProcessingMediator.START, "Deploying appliance ...");
        for (var i:int = 0; i < items.length; i++) {
            var appliance:IdentityAppliance = items[i] as IdentityAppliance;
            sendNotification(ApplicationFacade.DEPLOY_IDENTITY_APPLIANCE, [appliance.id.toString(), false]);
        }
    }

    private function handleDropInDisposedGrid(event:DragEvent):void {

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
                (sourceGrid.id == "grdSavedAppliances" && targetGrid.id != "grdStagedAppliances") ||
                (sourceGrid.id == "grdStagedAppliances" && targetGrid.id == "grdSavedAppliances") ||
                (sourceGrid.id == "grdDeployedAppliances" && targetGrid.id != "grdStagedAppliances")) {
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
            if ((items[0] as IdentityAppliance).state != IdentityApplianceState.STAGED.name) {
                event.preventDefault();
                DragManager.showFeedback(DragManager.NONE);
                return;
            }
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.APP_SECTION_CHANGE_START,
            ApplicationFacade.APP_SECTION_CHANGE_END,
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
            IdentityApplianceListLoadCommand.SUCCESS,
            IdentityApplianceRemoveCommand.SUCCESS,
            IdentityApplianceRemoveCommand.FAILURE,
            ApplicationFacade.LOGOUT];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.APP_SECTION_CHANGE_START:
                var currentView:String = notification.getBody() as String;
                if (currentView == viewName) {
                    sendNotification(ApplicationFacade.APP_SECTION_CHANGE_CONFIRMED);
                }
                break;
            case ApplicationFacade.APP_SECTION_CHANGE_END:
                var newView:String = notification.getBody() as String;
                if (newView == viewName) {
                    projectProxy.currentView = viewName;
                    sendNotification(ApplicationFacade.CLEAR_MSG);
                    initGrids();
                }
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
            case IdentityApplianceListLoadCommand.SUCCESS:
                initGrids();
                break;
            case IdentityApplianceRemoveCommand.SUCCESS:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(false, true);
                    sendNotification(ProcessingMediator.STOP);
                    _removedApplianceId = Number.MIN_VALUE;
                }
                break;
            case IdentityApplianceRemoveCommand.FAILURE:
                if (projectProxy.currentView == viewName) {
                    updateAppliancesList(true);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            "There was an error removing appliance.");
                    _removedApplianceId = Number.MIN_VALUE;
                }
                break;
            case ApplicationFacade.LOGOUT:
                this.dispose();
                break;
            default:
                super.handleNotification(notification);
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

        var appliance:IdentityAppliance = null;

        switch (event.action) {
            case LifecycleGridButtonEvent.ACTION_EDIT :
                appliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Opening identity appliance...");
                projectProxy.currentIdentityAppliance = null;
                //sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.APP_SECTION_CHANGE, ModelerViewFactory.VIEW_NAME);
                sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, appliance.id.toString());
                break;
            case LifecycleGridButtonEvent.ACTION_REMOVE :

                appliance = event.data as IdentityAppliance;
                if(appliance.state.toString() == IdentityApplianceState.PROJECTED.toString()
                        || appliance.state.toString() == IdentityApplianceState.DISPOSED.toString()){
                    Alert.show("Are you sure you want to delete this item?", "Confirm Removal", Alert.YES | Alert.NO, null,
                              function(event:CloseEvent):void {
                                    if (event.detail == Alert.YES) {
                                        // verify that a removal can be performed
                                        _removedApplianceId = appliance.id;
                                        sendNotification(ProcessingMediator.START, "Removing appliance ...");
                                        var ria:RemoveIdentityApplianceElementRequest = new RemoveIdentityApplianceElementRequest(appliance);
                                        sendNotification(ApplicationFacade.REMOVE_IDENTITY_APPLIANCE_ELEMENT, ria);
                                    }
                    }, null, Alert.YES);
                } else {
                    Alert.show("You can only delete projected and disposed appliances", "Removal information", Alert.OK);
                }
                break;
            case LifecycleGridButtonEvent.ACTION_START :
                appliance = event.data as IdentityAppliance;
                if(appliance.state.toString() == IdentityApplianceState.DEPLOYED.toString()){
                    sendNotification(ProcessingMediator.START, "Starting appliance ...");
                    sendNotification(ApplicationFacade.START_IDENTITY_APPLIANCE, appliance.id.toString());
                } else {
                    Alert.show("Appliance is already started", "Information", Alert.OK);
                }
                break;
            case LifecycleGridButtonEvent.ACTION_STOP :
                appliance = event.data as IdentityAppliance;
                if(appliance.state.toString() == IdentityApplianceState.STARTED.toString()){
                    sendNotification(ProcessingMediator.START, "Stopping appliance ...");
                    sendNotification(ApplicationFacade.STOP_IDENTITY_APPLIANCE, appliance.id.toString());
                } else {
                    Alert.show("Appliance is already stopped", "Information", Alert.OK);
                }
                break;
            case LifecycleGridButtonEvent.ACTION_UNDEPLOY :
                appliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Undeploying appliance ...");
                sendNotification(ApplicationFacade.UNDEPLOY_IDENTITY_APPLIANCE, appliance.id.toString());                
                break;
            case LifecycleGridButtonEvent.ACTION_BUILD :
                appliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Rebuilding appliance ...");
                sendNotification(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, [appliance.id.toString(), false]);
                break;
            case LifecycleGridButtonEvent.ACTION_DISPOSE :
                appliance = event.data as IdentityAppliance;
                sendNotification(ProcessingMediator.START, "Disposing appliance ...");
                sendNotification(ApplicationFacade.DISPOSE_IDENTITY_APPLIANCE, appliance.id.toString());
                break;
        }
    }

    private function handleGridDoubleClick(event:MouseEvent):void {
        var appliance:IdentityAppliance = event.currentTarget.selectedItem as IdentityAppliance;
        if (appliance != null) {
            sendNotification(ProcessingMediator.START, "Opening identity appliance...");
            projectProxy.currentIdentityAppliance = null;
            sendNotification(ApplicationFacade.APP_SECTION_CHANGE, ModelerViewFactory.VIEW_NAME);
            sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, appliance.id.toString());
        }
    }

    private function buildToolTip(row:Object):String {
        var appliance:IdentityApplianceDefinition = row as IdentityApplianceDefinition;
        return appliance ? appliance.name : "";
    }

    private function updateAppliancesList(error:Boolean, remove:Boolean = false):void {
        if (!error) {
            var modifiedAppliance:IdentityAppliance = projectProxy.commandResultIdentityAppliance;
            if (modifiedAppliance != null) {
                for (var i:int = 0; i < projectProxy.identityApplianceList.length; i++) {
                    var a1:IdentityAppliance = projectProxy.identityApplianceList[i] as IdentityAppliance;
                    if (modifiedAppliance.id == a1.id) {
                        projectProxy.identityApplianceList[i] = modifiedAppliance;
                        break;
                    }
                }
            }

            if (remove) {
                for (var j:int = 0; j < projectProxy.identityApplianceList.length; j++) {
                    var a2:IdentityAppliance = projectProxy.identityApplianceList[j] as IdentityAppliance;
                    if (_removedApplianceId == a2.id) {
                        projectProxy.identityApplianceList.removeItemAt(j);
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