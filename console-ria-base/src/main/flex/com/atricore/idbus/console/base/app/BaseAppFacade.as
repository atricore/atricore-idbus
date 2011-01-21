package com.atricore.idbus.console.base.app {
import org.springextensions.actionscript.puremvc.interfaces.IIocFacade;
import org.springextensions.actionscript.puremvc.patterns.facade.IocFacade;

public class BaseAppFacade extends IocFacade implements IIocFacade {

    public static const APP_SECTION_CHANGE:String = "AppSectionChange";
    public static const APP_SECTION_CHANGE_START:String = "AppSectionChangeStart";
    public static const APP_SECTION_CHANGE_CONFIRMED:String = "AppSectionChangeConfirmed";
    public static const APP_SECTION_CHANGE_REJECTED:String = "AppSectionChangeRejected";
    public static const APP_SECTION_CHANGE_END:String = "appSectionChangeEnd";

    public function BaseAppFacade(p_configuration:* = null) {
        super(p_configuration);
    }
}
}