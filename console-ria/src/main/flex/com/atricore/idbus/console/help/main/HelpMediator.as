package com.atricore.idbus.console.help.main {
import com.atricore.idbus.console.base.app.BaseAppFacade;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionMediator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;

import flash.events.Event;

import mx.events.FlexEvent;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;

public class HelpMediator extends AppSectionMediator implements IDisposable {

    private var _projectProxy:ProjectProxy;

    private var _created:Boolean;

    public function HelpMediator(name:String = null, viewComp:HelpView = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        (viewComponent as HelpView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;

        /* Remove unused title in account management panel */
        view.titleDisplay.width = 0;
        view.titleDisplay.height = 0;

        view.helpIFrame.visible = true;
    }

    override public function listNotificationInterests():Array {
        return [BaseAppFacade.APP_SECTION_CHANGE_START,
            BaseAppFacade.APP_SECTION_CHANGE_END];
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
            default:
                // Let super mediator handle notifications.
                super.handleNotification(notification);
                break;
        }
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        _created = false;
    }

    protected function get view():HelpView {
        return viewComponent as HelpView;
    }

    protected function set view(hv:HelpView):void {
        viewComponent = hv;
    }
}
}