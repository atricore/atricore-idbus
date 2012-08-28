/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.controller {
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.settings.main.SettingsMediator;

import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

public class SettingsStartUpCommand extends AppSectionStartUpCommand {

    private var _menuMediator:IIocMediator;
    private var _editCustomBrandingViewMediator:IIocMediator;

    private var _getServiceConfigCommand:IIocCommand;
    private var _updateServiceConfigCommand:IIocCommand;
    private var _lookupBrandingCommand:IIocCommand;
    private var _listBrandingsCommand:IIocCommand;
    private var _createBrandingCommand:IIocCommand;
    private var _updateBrandingCommand:IIocCommand;
    private var _removeBrandingCommand:IIocCommand;
    private var _activateBrandingChangesCommand:IIocCommand;

    public function SettingsStartUpCommand() {
    }

    public function get settingsMediator():SettingsMediator {
        return appSectionMediator as SettingsMediator;
    }

    public function set settingsMediator(value:SettingsMediator):void {
        appSectionMediator = value;
    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        super.setupCommands(ctx);
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_SERVICE_CONFIG, getServiceConfigCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.UPDATE_SERVICE_CONFIG, updateServiceConfigCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOOKUP_BRANDING, lookupBrandingCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_BRANDINGS, listBrandingsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_BRANDING, createBrandingCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EDIT_BRANDING, updateBrandingCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.REMOVE_BRANDING, removeBrandingCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATE_BRANDING_CHANGES, activateBrandingChangesCommand.getConfigName());
    }

    override protected function setupMediators(ctx:BaseStartupContext):void {
        super.setupMediators(ctx);
        iocFacade.registerMediatorByConfigName(menuMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(editCustomBrandingViewMediator.getConfigName());
    }

    public function get menuMediator():IIocMediator {
        return _menuMediator;
    }

    public function set menuMediator(value:IIocMediator):void {
        _menuMediator = value;
    }

    public function get editCustomBrandingViewMediator():IIocMediator {
        return _editCustomBrandingViewMediator;
    }

    public function set editCustomBrandingViewMediator(value:IIocMediator):void {
        _editCustomBrandingViewMediator = value;
    }

    public function get getServiceConfigCommand():IIocCommand {
        return _getServiceConfigCommand;
    }

    public function set getServiceConfigCommand(value:IIocCommand):void {
        _getServiceConfigCommand = value;
    }

    public function get updateServiceConfigCommand():IIocCommand {
        return _updateServiceConfigCommand;
    }

    public function set updateServiceConfigCommand(value:IIocCommand):void {
        _updateServiceConfigCommand = value;
    }

    public function get lookupBrandingCommand():IIocCommand {
        return _lookupBrandingCommand;
    }

    public function set lookupBrandingCommand(value:IIocCommand):void {
        _lookupBrandingCommand = value;
    }

    public function get listBrandingsCommand():IIocCommand {
        return _listBrandingsCommand;
    }

    public function set listBrandingsCommand(value:IIocCommand):void {
        _listBrandingsCommand = value;
    }

    public function get createBrandingCommand():IIocCommand {
        return _createBrandingCommand;
    }

    public function set createBrandingCommand(value:IIocCommand):void {
        _createBrandingCommand = value;
    }

    public function get updateBrandingCommand():IIocCommand {
        return _updateBrandingCommand;
    }

    public function set updateBrandingCommand(value:IIocCommand):void {
        _updateBrandingCommand = value;
    }

    public function get removeBrandingCommand():IIocCommand {
        return _removeBrandingCommand;
    }

    public function set removeBrandingCommand(value:IIocCommand):void {
        _removeBrandingCommand = value;
    }

    public function get activateBrandingChangesCommand():IIocCommand {
        return _activateBrandingChangesCommand;
    }

    public function set activateBrandingChangesCommand(value:IIocCommand):void {
        _activateBrandingChangesCommand = value;
    }
}
}
