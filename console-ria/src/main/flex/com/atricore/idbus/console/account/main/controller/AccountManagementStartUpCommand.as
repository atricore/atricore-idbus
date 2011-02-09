/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.account.main.controller {
import com.atricore.idbus.console.account.main.AccountManagementMediator;
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import mx.messaging.Channel;
import mx.messaging.config.ServerConfig;

import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.interfaces.IIocProxy;

public class AccountManagementStartUpCommand extends AppSectionStartUpCommand {

    private var _serviceRegistry:IIocProxy;

    private var _groupsMediator:IIocMediator;
    private var _groupPropertiesMediator:IIocMediator;
    private var _addGroupMediator:IIocMediator;
    private var _editGroupMediator:IIocMediator;
    private var _searchGroupsMediator:IIocMediator;
    private var _usersMediator:IIocMediator;
    private var _userPropertiesMediator:IIocMediator;
    private var _addUserMediator:IIocMediator;
    private var _editUserMediator:IIocMediator;
    private var _searchUsersMediator:IIocMediator;
    private var _schemasMediator:IIocMediator;
    private var _schemasPropertiesMediator:IIocMediator;
    private var _addAttributeMediator:IIocMediator;
    private var _editAttributeMediator:IIocMediator;

    private var _addGroupCommand:IIocCommand;
    private var _addUserCommand:IIocCommand;
    private var _deleteGroupCommand:IIocCommand;
    private var _deleteUserCommand:IIocCommand;
    private var _editGroupCommand:IIocCommand;
    private var _editUserCommand:IIocCommand;
    private var _listGroupsCommand:IIocCommand;
    private var _listUsersCommand:IIocCommand;
    private var _searchGroupsCommand:IIocCommand;
    private var _searchUsersCommand:IIocCommand;
    private var _listSchemaAttributesCommand:IIocCommand;


    public function AccountManagementStartUpCommand() {
    }

    public function get accountManagementMediator():AccountManagementMediator {
        return appSectionMediator as AccountManagementMediator;
    }

    public function set accountManagementMediator(value:AccountManagementMediator):void {
        appSectionMediator = value;
    }

    override protected function setupMediators(ctx:BaseStartupContext):void {
        super.setupMediators(ctx);
        iocFacade.registerMediatorByConfigName(groupsMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(groupPropertiesMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(addGroupMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(editGroupMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(searchGroupsMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(usersMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(userPropertiesMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(addUserMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(editUserMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(searchUsersMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(schemasMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(schemasPropertiesMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(addAttributeMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(editAttributeMediator.getConfigName());
    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        super.setupCommands(ctx);
        iocFacade.registerCommandByConfigName(ApplicationFacade.ADD_GROUP, addGroupCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ADD_USER, addUserCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DELETE_GROUP, deleteGroupCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DELETE_USER, deleteUserCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EDIT_GROUP, editGroupCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EDIT_USER, editUserCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_GROUPS, listGroupsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_USERS, listUsersCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SEARCH_GROUPS, searchGroupsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SEARCH_USERS, searchUsersCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_SCHEMA_ATTRIBUTES, listSchemaAttributesCommand.getConfigName());
    }

    override protected function setupServices(ctx:BaseStartupContext):void{
        super.setupServices(ctx);
        var channel:Channel = ServerConfig.getChannel("my-amf");
        var registry:ServiceRegistry = serviceRegistry as ServiceRegistry;
        registry.setChannel(channel);

        registry.registerRemoteObjectService(ApplicationFacade.SCHEMAS_MANAGEMENT_SERVICE,
                ApplicationFacade.SCHEMAS_MANAGEMENT_SERVICE);
    }


    public function get serviceRegistry():IIocProxy {
        return _serviceRegistry;
    }

    public function set serviceRegistry(value:IIocProxy):void {
        _serviceRegistry = value;
    }

    public function get groupsMediator():IIocMediator {
        return _groupsMediator;
    }

    public function set groupsMediator(value:IIocMediator):void {
        _groupsMediator = value;
    }

    public function get groupPropertiesMediator():IIocMediator {
        return _groupPropertiesMediator;
    }

    public function set groupPropertiesMediator(value:IIocMediator):void {
        _groupPropertiesMediator = value;
    }

    public function get addGroupMediator():IIocMediator {
        return _addGroupMediator;
    }

    public function set addGroupMediator(value:IIocMediator):void {
        _addGroupMediator = value;
    }

    public function get editGroupMediator():IIocMediator {
        return _editGroupMediator;
    }

    public function set editGroupMediator(value:IIocMediator):void {
        _editGroupMediator = value;
    }

    public function get searchGroupsMediator():IIocMediator {
        return _searchGroupsMediator;
    }

    public function set searchGroupsMediator(value:IIocMediator):void {
        _searchGroupsMediator = value;
    }

    public function get usersMediator():IIocMediator {
        return _usersMediator;
    }

    public function set usersMediator(value:IIocMediator):void {
        _usersMediator = value;
    }

    public function get userPropertiesMediator():IIocMediator {
        return _userPropertiesMediator;
    }

    public function set userPropertiesMediator(value:IIocMediator):void {
        _userPropertiesMediator = value;
    }

    public function get addUserMediator():IIocMediator {
        return _addUserMediator;
    }

    public function set addUserMediator(value:IIocMediator):void {
        _addUserMediator = value;
    }

    public function get editUserMediator():IIocMediator {
        return _editUserMediator;
    }

    public function set editUserMediator(value:IIocMediator):void {
        _editUserMediator = value;
    }

    public function get searchUsersMediator():IIocMediator {
        return _searchUsersMediator;
    }

    public function set searchUsersMediator(value:IIocMediator):void {
        _searchUsersMediator = value;
    }

    public function get schemasMediator():IIocMediator {
        return _schemasMediator;
    }

    public function set schemasMediator(value:IIocMediator):void {
        _schemasMediator = value;
    }

    public function get schemasPropertiesMediator():IIocMediator {
        return _schemasPropertiesMediator;
    }

    public function set schemasPropertiesMediator(value:IIocMediator):void {
        _schemasPropertiesMediator = value;
    }

    public function get addAttributeMediator():IIocMediator {
        return _addAttributeMediator;
    }

    public function set addAttributeMediator(value:IIocMediator):void {
        _addAttributeMediator = value;
    }

    public function get editAttributeMediator():IIocMediator {
        return _editAttributeMediator;
    }

    public function set editAttributeMediator(value:IIocMediator):void {
        _editAttributeMediator = value;
    }

    public function get addGroupCommand():IIocCommand {
        return _addGroupCommand;
    }

    public function set addGroupCommand(value:IIocCommand):void {
        _addGroupCommand = value;
    }

    public function get addUserCommand():IIocCommand {
        return _addUserCommand;
    }

    public function set addUserCommand(value:IIocCommand):void {
        _addUserCommand = value;
    }

    public function get deleteGroupCommand():IIocCommand {
        return _deleteGroupCommand;
    }

    public function set deleteGroupCommand(value:IIocCommand):void {
        _deleteGroupCommand = value;
    }

    public function get deleteUserCommand():IIocCommand {
        return _deleteUserCommand;
    }

    public function set deleteUserCommand(value:IIocCommand):void {
        _deleteUserCommand = value;
    }

    public function get editGroupCommand():IIocCommand {
        return _editGroupCommand;
    }

    public function set editGroupCommand(value:IIocCommand):void {
        _editGroupCommand = value;
    }

    public function get editUserCommand():IIocCommand {
        return _editUserCommand;
    }

    public function set editUserCommand(value:IIocCommand):void {
        _editUserCommand = value;
    }

    public function get listGroupsCommand():IIocCommand {
        return _listGroupsCommand;
    }

    public function set listGroupsCommand(value:IIocCommand):void {
        _listGroupsCommand = value;
    }

    public function get listUsersCommand():IIocCommand {
        return _listUsersCommand;
    }

    public function set listUsersCommand(value:IIocCommand):void {
        _listUsersCommand = value;
    }

    public function get searchGroupsCommand():IIocCommand {
        return _searchGroupsCommand;
    }

    public function set searchGroupsCommand(value:IIocCommand):void {
        _searchGroupsCommand = value;
    }

    public function get searchUsersCommand():IIocCommand {
        return _searchUsersCommand;
    }

    public function set searchUsersCommand(value:IIocCommand):void {
        _searchUsersCommand = value;
    }

    public function get listSchemaAttributesCommand():IIocCommand {
        return _listSchemaAttributesCommand;
    }

    public function set listSchemaAttributesCommand(value:IIocCommand):void {
        _listSchemaAttributesCommand = value;
    }
}
}
