/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main {
import com.atricore.idbus.console.base.app.BaseAppFacade;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionMediator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.settings.main.menu.MenuMediator;

import flash.events.Event;

import mx.core.IVisualElement;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

import spark.components.Panel;

public class SettingsMediator extends AppSectionMediator implements IDisposable {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _projectProxy:ProjectProxy;

    private var _menuMediator:MenuMediator;

    private var _created:Boolean;

    public function SettingsMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function get menuMediator():MenuMediator {
        return _menuMediator;
    }

    public function set menuMediator(value:MenuMediator):void {
        _menuMediator = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        (p_viewComponent as SettingsView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(p_viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;

        /* Remove unused title in account management panel */
        view.titleDisplay.width = 0;
        view.titleDisplay.height = 0;

        menuMediator.setViewComponent(view.menu);
    }

    override public function listNotificationInterests():Array {
        return [BaseAppFacade.APP_SECTION_CHANGE_START,
            BaseAppFacade.APP_SECTION_CHANGE_END,
            ApplicationFacade.SETTINGS_MENU_ELEMENT_SELECTED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case BaseAppFacade.APP_SECTION_CHANGE_START:
                var currentView:String = notification.getBody() as String;
                if (currentView == viewName) {
                    sendNotification(BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED);
                }
                break;
            case BaseAppFacade.APP_SECTION_CHANGE_END:
                var newView:String = notification.getBody() as String;
                if (newView == viewName) {
                    projectProxy.currentView = viewName;
                    sendNotification(ApplicationFacade.CLEAR_MSG);
                }
                break;
            case ApplicationFacade.SETTINGS_MENU_ELEMENT_SELECTED:
                var params:Array = notification.getBody() as Array;
                var settingsMenuEntryMediator:IIocMediator = iocFacade.container.getObject(params[0]) as IIocMediator;
                var settingsMenuEntryView:IVisualElement = iocFacade.container.getObject(params[1]) as IVisualElement;
                view.vPanel.removeAllElements();
                view.vPanel.addElement(settingsMenuEntryView);
                settingsMenuEntryMediator.setViewComponent(settingsMenuEntryView);
                break;
            default:
                // Let super mediator handle notifications.
                super.handleNotification(notification);
                break;
        }
    }

    public function dispose():void {
        _created = false;
    }

    protected function get view():SettingsView {
        return viewComponent as SettingsView;
    }

    protected function set view(lc:SettingsView):void {
        viewComponent = lc;
    }
}
}
