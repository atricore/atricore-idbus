/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.menu {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.settings.main.menu.event.SettingsMenuEvent;
import com.atricore.idbus.console.settings.main.menu.model.SettingsMenuDrawer;
import com.atricore.idbus.console.settings.main.menu.model.SettingsMenuEntry;
import com.atricore.idbus.console.settings.main.menu.model.SettingsMenuRoot;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.supportClasses.ItemRenderer;

public class MenuMediator extends IocMediator {
    
    private var selectedItem:Object;
    
    private var _projectProxy:ProjectProxy;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function MenuMediator(name : String = null, viewComp:MenuView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {

        if (getViewComponent() != null) {
            view.removeEventListener(SettingsMenuEvent.CLICK, handleMenuClick);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {

        var smr:SettingsMenuRoot  = new SettingsMenuRoot();
        
        var settingsMenuDrawerNames:Array = iocFacade.container.getObjectNamesForType(SettingsMenuDrawer);

        settingsMenuDrawerNames.forEach(function(settingsMenuDrawerName:String, idx:int, arr:Array):void {

            // Get drawer object
            var settingsMenuDrawer:SettingsMenuDrawer =
                    iocFacade.container.getObject(settingsMenuDrawerName) as SettingsMenuDrawer;

            // register settings menu entry mediators
            for each (var settingsMenuEntry:SettingsMenuEntry in settingsMenuDrawer.children) {
                var settingsMenuEntryMediator:IIocMediator = iocFacade.container.getObject(settingsMenuEntry.mediatorName) as IIocMediator;
                iocFacade.registerMediatorByConfigName(settingsMenuEntryMediator.getConfigName());
                if (settingsMenuEntry.iconName == "license")
                    settingsMenuEntry.icon = EmbeddedIcons.licenseIcon;
                else if (settingsMenuEntry.iconName == "liveUpdate")
                    settingsMenuEntry.icon = EmbeddedIcons.liveUpdateIcon;
                else if (settingsMenuEntry.iconName == "httpService")
                    settingsMenuEntry.icon = EmbeddedIcons.httpSettingsIcon;
                else if (settingsMenuEntry.iconName == "sshService")
                    settingsMenuEntry.icon = EmbeddedIcons.sshSettingsIcon;
                else if (settingsMenuEntry.iconName == "persistenceService")
                    settingsMenuEntry.icon = EmbeddedIcons.persistenceSettingsIcon;
                else if (settingsMenuEntry.iconName == "managementService")
                    settingsMenuEntry.icon = EmbeddedIcons.managementSettingsIcon;
                else if (settingsMenuEntry.iconName == "aqmService")
                    settingsMenuEntry.icon = EmbeddedIcons.queueSettingsIcon;
                else if (settingsMenuEntry.iconName == "logService")
                    settingsMenuEntry.icon = EmbeddedIcons.logggingSettingsIcon;
                else if (settingsMenuEntry.iconName == "brandingService")
                    settingsMenuEntry.icon = EmbeddedIcons.brandingSettingsIcon;

            }

            smr.add(settingsMenuDrawer);
        });

        // Sort drawers
        function sortMenuDrawers(a:SettingsMenuDrawer, b:SettingsMenuDrawer):int {
            return a.viewPriority - b.viewPriority;
        }
        smr.children.sort(sortMenuDrawers);

        view.rptSettingsMenuRoot.dataProvider = smr;
        view.addEventListener(SettingsMenuEvent.CLICK, handleMenuClick);
    }

    public function handleMenuClick(event:SettingsMenuEvent) : void {
        switch (event.action) {
            case SettingsMenuEvent.ACTION_MENU_ITEM_CLICKED:
                // deselect previously selected item
                if (selectedItem != null && selectedItem != event.target) {
                    var uiComponentOldSel:ItemRenderer = selectedItem as ItemRenderer;
                    uiComponentOldSel.selected = false;
                    selectedItem = null;
                }
                // select new item
                selectedItem = event.target;
                var uiComponentSel:ItemRenderer = selectedItem as ItemRenderer;
                uiComponentSel.selected = true;
                var selectedSettingsMenuEntry:SettingsMenuEntry = event.data as SettingsMenuEntry;
                sendNotification(ApplicationFacade.SETTINGS_MENU_ELEMENT_SELECTED,
                        [selectedSettingsMenuEntry.mediatorName, selectedSettingsMenuEntry.viewName]);
                break;
        }
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }

    public function get view():MenuView {
        return viewComponent as MenuView;
    }
}
}
