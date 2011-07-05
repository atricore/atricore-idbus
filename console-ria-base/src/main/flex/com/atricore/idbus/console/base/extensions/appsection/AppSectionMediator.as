/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.base.extensions.appsection {
import com.atricore.idbus.console.base.app.BaseAppFacade;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class AppSectionMediator extends IocMediator {

    private var _viewFactory:AppSectionViewFactory;

    private var _viewPriority:int = 0;

    public function AppSectionMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get viewName():String{
        return _viewFactory.viewName;
    }

    public function get viewFactory():AppSectionViewFactory {
        return _viewFactory;
    }

    public function set viewFactory(value:AppSectionViewFactory):void {
        _viewFactory = value;
    }


    public function get viewPriority():int {
        return _viewPriority;
    }

    public function set viewPriority(value:int):void {
        _viewPriority = value;
    }

    override public function listNotificationInterests():Array {
        return [BaseAppFacade.APP_SECTION_CHANGE_START,
            BaseAppFacade.APP_SECTION_CHANGE_END];
    }


    override public function handleNotification(notification:INotification):void {
         switch (notification.getName()) {
             case BaseAppFacade.APP_SECTION_CHANGE_START:
                     var currentView:String = notification.getBody() as String;
                    // by default, accept APP_SECTION_CHANGE
                     if (currentView == viewName) {
                         sendNotification(BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED);
                     }
                break;
             default:
                break;
         }
    }


}
}
