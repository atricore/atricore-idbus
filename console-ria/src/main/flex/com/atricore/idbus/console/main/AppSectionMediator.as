/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.main {
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class AppSectionMediator extends IocMediator {

    private var _viewName:String ;

    public function AppSectionMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get viewName():String {
        return _viewName;
    }

    public function set viewName(value:String):void {
        _viewName = value;
    }
}
}
