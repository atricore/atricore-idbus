/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.main.controller
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import mx.messaging.Channel;
import mx.messaging.config.ServerConfig;
import mx.rpc.IResponder;

import org.puremvc.as3.interfaces.*;
import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.interfaces.IIocProxy;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ApplicationStartUpCommand extends IocSimpleCommand implements IResponder {
    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.FAILURE";

    /* Proxies */
    private var _serviceRegistry:IIocProxy;
    private var _projectProxy:IIocProxy;
    private var _keystoreProxy:IIocProxy;
    private var _profileProxy:IIocProxy;
    private var _secureContextProxy:IIocProxy;

    /* Mediators */
    private var _applicationMediator:IIocMediator;
    private var _modelerMediator:IIocMediator;
    private var _browserMediator:IIocMediator;
    private var _diagramMediator:IIocMediator;
    private var _paletteMediator:IIocMediator;
    private var _propertySheetMediator:IIocMediator;
    private var _identityApplianceWizardMediator:IIocMediator;
    private var _manageCertificateMediator:IIocMediator;
    private var _identityProviderCreateMediator:IIocMediator;
    private var _serviceProviderCreateMediator:IIocMediator;
    private var _externalIdentityProviderCreateMediator:IIocMediator;
    private var _externalServiceProviderCreateMediator:IIocMediator;
//    private var _idpChannelCreateMediator:IIocMediator;
//    private var _spChannelCreateMediator:IIocMediator;
    private var _identityVaultCreateMediator:IIocMediator;
    private var _dbIdentitySourceCreateMediator:IIocMediator;
    private var _ldapIdentitySourceCreateMediator:IIocMediator;
    private var _xmlIdentitySourceCreateMediator:IIocMediator;
    private var _jbossExecutionEnvironmentCreateMediator:IIocMediator;
    private var _weblogicExecutionEnvironmentCreateMediator:IIocMediator;
    private var _tomcatExecutionEnvironmentCreateMediator:IIocMediator;
    private var _jbossPortalExecutionEnvironmentCreateMediator:IIocMediator;
    private var _liferayPortalExecutionEnvironmentCreateMediator:IIocMediator;
    private var _wasceExecutionEnvironmentCreateMediator:IIocMediator;
    private var _apacheExecutionEnvironmentCreateMediator:IIocMediator;
    private var _alfrescoExecutionEnvironmentCreateMediator:IIocMediator;
    private var _javaEEExecutionEnvironmentCreateMediator:IIocMediator;
    private var _phpBBExecutionEnvironmentCreateMediator:IIocMediator;
    private var _windowsIISExecutionEnvironmentCreateMediator:IIocMediator;
    private var _webserverExecutionEnvironmentCreateMediator:IIocMediator;
    private var _uploadProgressMediator:IIocMediator;
    private var _buildApplianceMediator:IIocMediator;
    private var _deployApplianceMediator:IIocMediator;
    private var _processingMediator:IIocMediator;
    private var _loginMediator:IIocMediator;
    private var _changePasswordMediator:IIocMediator;
    private var _setupWizardViewMediator:IIocMediator;
    private var _lifecycleViewMediator:IIocMediator;
    private var _simpleSSOWizardViewMediator:IIocMediator;
    private var _accountManagementMediator:IIocMediator;
    private var _liveUpdateMediator:IIocMediator;
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
    private var _activationCreateMediator:IIocMediator;
    private var _federatedConnectionCreateMediator:IIocMediator;
    private var _exportIdentityApplianceMediator:IIocMediator;


    /* Commands */
    private var _setupServerCommand:IIocCommand;
    private var _registerCommand:IIocCommand;
    private var _identityApplianceRemoveCommand:IIocCommand;
    private var _identityProviderRemoveCommand:IIocCommand;
    private var _serviceProviderRemoveCommand:IIocCommand;
    private var _externalIdentityProviderRemoveCommand:IIocCommand;
    private var _externalServiceProviderRemoveCommand:IIocCommand;
//    private var _idpChannelRemoveCommand:IIocCommand;
//    private var _spChannelRemoveCommand:IIocCommand;
    private var _lookupIdentityApplianceByIdCommand:IIocCommand;
    private var _uploadCommand:IIocCommand;
    private var _buildIdentityApplianceCommand:IIocCommand;
    private var _deployIdentityApplianceCommand:IIocCommand;
    private var _undeployIdentityApplianceCommand:IIocCommand;
    private var _startIdentityApplianceCommand:IIocCommand;
    private var _stopIdentityApplianceCommand:IIocCommand;
    private var _disposeIdentityApplianceCommand:IIocCommand;
    private var _loginCommand:IIocCommand;
    private var _changePasswordCommand:IIocCommand;
    private var _notFirstRunCommand:IIocCommand;
    private var _identityApplianceUpdateCommand:IIocCommand;
    private var _identityVaultRemoveCommand:IIocCommand;
    private var _activationRemoveCommand:IIocCommand;
    private var _identityLookupRemoveCommand:IIocCommand;
    private var _federatedConnectionRemoveCommand:IIocCommand;
    private var _executionEnvironmentRemoveCommand:IIocCommand;
    private var _createSimpleSSOIdentityApplianceCommand:IIocCommand;
    private var _identityApplianceListLoadCommand:IIocCommand;
    private var _identityApplianceCreateCommand:IIocCommand;
    private var _identityApplianceImportCommand:IIocCommand;
    private var _createSimpleSSOSetupCommand:IIocCommand;
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
    private var _activateExecEnvironmentCommand:IIocCommand;
    private var _createIdentityLookupCommand:IIocCommand;
    private var _folderExistsCommand:IIocCommand;
    private var _foldersExistsCommand:IIocCommand;
    private var _jdbcDriversListCommand:IIocCommand;
    private var _getMetadataInfoCommand:IIocCommand;
    private var _getCertificateInfoCommand:IIocCommand;


    public function get applicationMediator():IIocMediator {
        return _applicationMediator;
    }

    public function set applicationMediator(value:IIocMediator):void {
        _applicationMediator = value;
    }

    public function get modelerMediator():IIocMediator {
        return _modelerMediator;
    }

    public function set modelerMediator(value:IIocMediator):void {
        _modelerMediator = value;
    }

    public function get browserMediator():IIocMediator {
        return _browserMediator;
    }

    public function set browserMediator(value:IIocMediator):void {
        _browserMediator = value;
    }

    public function get paletteMediator():IIocMediator {
        return _paletteMediator;
    }

    public function set paletteMediator(value:IIocMediator):void {
        _paletteMediator = value;
    }

    public function get propertySheetMediator():IIocMediator {
        return _propertySheetMediator;
    }

    public function set propertySheetMediator(value:IIocMediator):void {
        _propertySheetMediator = value;
    }

    public function get identityApplianceWizardMediator():IIocMediator {
        return _identityApplianceWizardMediator;
    }

    public function set identityApplianceWizardMediator(value:IIocMediator):void {
        _identityApplianceWizardMediator = value;
    }

    public function get manageCertificateMediator():IIocMediator {
        return _manageCertificateMediator;
    }

    public function set manageCertificateMediator(value:IIocMediator):void {
        _manageCertificateMediator = value;
    }

    public function get identityProviderCreateMediator():IIocMediator {
        return _identityProviderCreateMediator;
    }

    public function set identityProviderCreateMediator(value:IIocMediator):void {
        _identityProviderCreateMediator = value;
    }

    public function get serviceProviderCreateMediator():IIocMediator {
        return _serviceProviderCreateMediator;
    }

    public function set serviceProviderCreateMediator(value:IIocMediator):void {
        _serviceProviderCreateMediator = value;
    }

    public function get externalIdentityProviderCreateMediator():IIocMediator {
        return _externalIdentityProviderCreateMediator;
    }

    public function set externalIdentityProviderCreateMediator(value:IIocMediator):void {
        _externalIdentityProviderCreateMediator = value;
    }

    public function get externalServiceProviderCreateMediator():IIocMediator {
        return _externalServiceProviderCreateMediator;
    }

    public function set externalServiceProviderCreateMediator(value:IIocMediator):void {
        _externalServiceProviderCreateMediator = value;
    }

    public function get identityVaultCreateMediator():IIocMediator {
        return _identityVaultCreateMediator;
    }

    public function set identityVaultCreateMediator(value:IIocMediator):void {
        _identityVaultCreateMediator = value;
    }

    public function get dbIdentitySourceCreateMediator():IIocMediator {
        return _dbIdentitySourceCreateMediator;
    }

    public function set dbIdentitySourceCreateMediator(value:IIocMediator):void {
        _dbIdentitySourceCreateMediator = value;
    }

    public function get ldapIdentitySourceCreateMediator():IIocMediator {
        return _ldapIdentitySourceCreateMediator;
    }

    public function set ldapIdentitySourceCreateMediator(value:IIocMediator):void {
        _ldapIdentitySourceCreateMediator = value;
    }

    public function get xmlIdentitySourceCreateMediator():IIocMediator {
        return _xmlIdentitySourceCreateMediator;
    }

    public function set xmlIdentitySourceCreateMediator(value:IIocMediator):void {
        _xmlIdentitySourceCreateMediator = value;
    }

    public function get jbossExecutionEnvironmentCreateMediator():IIocMediator {
        return _jbossExecutionEnvironmentCreateMediator;
    }

    public function set jbossExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _jbossExecutionEnvironmentCreateMediator = value;
    }

    public function get weblogicExecutionEnvironmentCreateMediator():IIocMediator {
        return _weblogicExecutionEnvironmentCreateMediator;
    }

    public function set weblogicExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _weblogicExecutionEnvironmentCreateMediator = value;
    }

    public function get tomcatExecutionEnvironmentCreateMediator():IIocMediator {
        return _tomcatExecutionEnvironmentCreateMediator;
    }

    public function set tomcatExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _tomcatExecutionEnvironmentCreateMediator = value;
    }

    public function get jbossPortalExecutionEnvironmentCreateMediator():IIocMediator {
        return _jbossPortalExecutionEnvironmentCreateMediator;
    }

    public function set jbossPortalExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _jbossPortalExecutionEnvironmentCreateMediator = value;
    }

    public function get liferayPortalExecutionEnvironmentCreateMediator():IIocMediator {
        return _liferayPortalExecutionEnvironmentCreateMediator;
    }

    public function set liferayPortalExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _liferayPortalExecutionEnvironmentCreateMediator = value;
    }

    public function get wasceExecutionEnvironmentCreateMediator():IIocMediator {
        return _wasceExecutionEnvironmentCreateMediator;
    }

    public function set wasceExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _wasceExecutionEnvironmentCreateMediator = value;
    }

    public function get apacheExecutionEnvironmentCreateMediator():IIocMediator {
        return _apacheExecutionEnvironmentCreateMediator;
    }

    public function set apacheExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _apacheExecutionEnvironmentCreateMediator = value;
    }

    public function get alfrescoExecutionEnvironmentCreateMediator():IIocMediator {
        return _alfrescoExecutionEnvironmentCreateMediator;
    }

    public function set alfrescoExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _alfrescoExecutionEnvironmentCreateMediator = value;
    }

    public function get javaEEExecutionEnvironmentCreateMediator():IIocMediator {
        return _javaEEExecutionEnvironmentCreateMediator;
    }

    public function set javaEEExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _javaEEExecutionEnvironmentCreateMediator = value;
    }

    public function get phpBBExecutionEnvironmentCreateMediator():IIocMediator {
        return _phpBBExecutionEnvironmentCreateMediator;
    }

    public function set phpBBExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _phpBBExecutionEnvironmentCreateMediator = value;
    }

    public function get windowsIISExecutionEnvironmentCreateMediator():IIocMediator {
        return _windowsIISExecutionEnvironmentCreateMediator;
    }

    public function set windowsIISExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _windowsIISExecutionEnvironmentCreateMediator = value;
    }

    public function get webserverExecutionEnvironmentCreateMediator():IIocMediator {
        return _webserverExecutionEnvironmentCreateMediator;
    }

    public function set webserverExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _webserverExecutionEnvironmentCreateMediator = value;
    }

    public function get uploadProgressMediator():IIocMediator {
        return _uploadProgressMediator;
    }

    public function set uploadProgressMediator(value:IIocMediator):void {
        _uploadProgressMediator = value;
    }

    public function get buildApplianceMediator():IIocMediator {
        return _buildApplianceMediator;
    }

    public function set buildApplianceMediator(value:IIocMediator):void {
        _buildApplianceMediator = value;
    }

    public function get deployApplianceMediator():IIocMediator {
        return _deployApplianceMediator;
    }

    public function set deployApplianceMediator(value:IIocMediator):void {
        _deployApplianceMediator = value;
    }

    public function get setupWizardViewMediator():IIocMediator {
        return _setupWizardViewMediator;
    }

    public function set setupWizardViewMediator(value:IIocMediator):void {
        _setupWizardViewMediator = value;
    }

    public function get lifecycleViewMediator():IIocMediator {
        return _lifecycleViewMediator;
    }

    public function set lifecycleViewMediator(value:IIocMediator):void {
        _lifecycleViewMediator = value;
    }

    public function get simpleSSOWizardViewMediator():IIocMediator {
        return _simpleSSOWizardViewMediator;
    }

    public function set simpleSSOWizardViewMediator(value:IIocMediator):void {
        _simpleSSOWizardViewMediator = value;
    }

    public function get accountManagementMediator():IIocMediator {
        return _accountManagementMediator;
    }

    public function set accountManagementMediator(value:IIocMediator):void {
        _accountManagementMediator = value;
    }

    public function get liveUpdateMediator():IIocMediator {
        return _liveUpdateMediator;
    }

    public function set liveUpdateMediator(value:IIocMediator):void {
        _liveUpdateMediator = value;
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

    public function get diagramMediator():IIocMediator {
        return _diagramMediator;
    }

    public function set diagramMediator(value:IIocMediator):void {
        _diagramMediator = value;
    }

    public function get processingMediator():IIocMediator {
        return _processingMediator;
    }

    public function set processingMediator(value:IIocMediator):void {
        _processingMediator = value;
    }

    public function get activationCreateMediator():IIocMediator {
        return _activationCreateMediator;
    }

    public function set activationCreateMediator(value:IIocMediator):void {
        _activationCreateMediator = value;
    }

    public function get federatedConnectionCreateMediator():IIocMediator {
        return _federatedConnectionCreateMediator;
    }

    public function set federatedConnectionCreateMediator(value:IIocMediator):void {
        _federatedConnectionCreateMediator = value;
    }

    public function get exportIdentityApplianceMediator():IIocMediator {
        return _exportIdentityApplianceMediator;
    }

    public function set exportIdentityApplianceMediator(value:IIocMediator):void {
        _exportIdentityApplianceMediator = value;
    }

    public function get serviceRegistry():IIocProxy {
        return _serviceRegistry;
    }

    public function set serviceRegistry(value:IIocProxy):void {
        _serviceRegistry = value;
    }

    public function get projectProxy():IIocProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:IIocProxy):void {
        _projectProxy = value;
    }

    public function get keystoreProxy():IIocProxy {
        return _keystoreProxy;
    }

    public function set keystoreProxy(value:IIocProxy):void {
        _keystoreProxy = value;
    }

    public function get profileProxy():IIocProxy {
        return _profileProxy;
    }

    public function set profileProxy(value:IIocProxy):void {
        _profileProxy = value;
    }

    public function get secureContextProxy():IIocProxy {
        return _secureContextProxy;
    }

    public function set secureContextProxy(value:IIocProxy):void {
        _secureContextProxy = value;
    }


    public function get loginMediator():IIocMediator {
        return _loginMediator;
    }

    public function set loginMediator(value:IIocMediator):void {
        _loginMediator = value;
    }

    public function get changePasswordMediator():IIocMediator {
        return _changePasswordMediator;
    }

    public function set changePasswordMediator(value:IIocMediator):void {
        _changePasswordMediator = value;
    }

    public function get setupServerCommand():IIocCommand {
        return _setupServerCommand;
    }

    public function set setupServerCommand(value:IIocCommand):void {
        _setupServerCommand = value;
    }

    public function get registerCommand():IIocCommand {
        return _registerCommand;
    }

    public function set registerCommand(value:IIocCommand):void {
        _registerCommand = value;
    }

    public function get identityApplianceRemoveCommand():IIocCommand {
        return _identityApplianceRemoveCommand;
    }

    public function set identityApplianceRemoveCommand(value:IIocCommand):void {
        _identityApplianceRemoveCommand = value;
    }

    public function get identityProviderRemoveCommand():IIocCommand {
        return _identityProviderRemoveCommand;
    }

    public function set identityProviderRemoveCommand(value:IIocCommand):void {
        _identityProviderRemoveCommand = value;
    }

    public function get serviceProviderRemoveCommand():IIocCommand {
        return _serviceProviderRemoveCommand;
    }

    public function set serviceProviderRemoveCommand(value:IIocCommand):void {
        _serviceProviderRemoveCommand = value;
    }

    public function get externalIdentityProviderRemoveCommand():IIocCommand {
        return _externalIdentityProviderRemoveCommand;
    }

    public function set externalIdentityProviderRemoveCommand(value:IIocCommand):void {
        _externalIdentityProviderRemoveCommand = value;
    }

    public function get externalServiceProviderRemoveCommand():IIocCommand {
        return _externalServiceProviderRemoveCommand;
    }

    public function set externalServiceProviderRemoveCommand(value:IIocCommand):void {
        _externalServiceProviderRemoveCommand = value;
    }

    public function get lookupIdentityApplianceByIdCommand():IIocCommand {
        return _lookupIdentityApplianceByIdCommand;
    }

    public function set lookupIdentityApplianceByIdCommand(value:IIocCommand):void {
        _lookupIdentityApplianceByIdCommand = value;
    }

    public function get uploadCommand():IIocCommand {
        return _uploadCommand;
    }

    public function set uploadCommand(value:IIocCommand):void {
        _uploadCommand = value;
    }

    public function get buildIdentityApplianceCommand():IIocCommand {
        return _buildIdentityApplianceCommand;
    }

    public function set buildIdentityApplianceCommand(value:IIocCommand):void {
        _buildIdentityApplianceCommand = value;
    }

    public function get deployIdentityApplianceCommand():IIocCommand {
        return _deployIdentityApplianceCommand;
    }

    public function set deployIdentityApplianceCommand(value:IIocCommand):void {
        _deployIdentityApplianceCommand = value;
    }

    public function get undeployIdentityApplianceCommand():IIocCommand {
        return _undeployIdentityApplianceCommand;
    }

    public function set undeployIdentityApplianceCommand(value:IIocCommand):void {
        _undeployIdentityApplianceCommand = value;
    }

    public function get startIdentityApplianceCommand():IIocCommand {
        return _startIdentityApplianceCommand;
    }

    public function set startIdentityApplianceCommand(value:IIocCommand):void {
        _startIdentityApplianceCommand = value;
    }

    public function get stopIdentityApplianceCommand():IIocCommand {
        return _stopIdentityApplianceCommand;
    }

    public function set stopIdentityApplianceCommand(value:IIocCommand):void {
        _stopIdentityApplianceCommand = value;
    }

    public function get disposeIdentityApplianceCommand():IIocCommand {
        return _disposeIdentityApplianceCommand;
    }

    public function set disposeIdentityApplianceCommand(value:IIocCommand):void {
        _disposeIdentityApplianceCommand = value;
    }

    public function get loginCommand():IIocCommand {
        return _loginCommand;
    }

    public function set loginCommand(value:IIocCommand):void {
        _loginCommand = value;
    }

    public function get changePasswordCommand():IIocCommand {
        return _changePasswordCommand;
    }

    public function set changePasswordCommand(value:IIocCommand):void {
        _changePasswordCommand = value;
    }

    public function get notFirstRunCommand():IIocCommand {
        return _notFirstRunCommand;
    }

    public function set notFirstRunCommand(value:IIocCommand):void {
        _notFirstRunCommand = value;
    }

    public function get identityApplianceUpdateCommand():IIocCommand {
        return _identityApplianceUpdateCommand;
    }

    public function set identityApplianceUpdateCommand(value:IIocCommand):void {
        _identityApplianceUpdateCommand = value;
    }

    public function get identityVaultRemoveCommand():IIocCommand {
        return _identityVaultRemoveCommand;
    }

    public function set identityVaultRemoveCommand(value:IIocCommand):void {
        _identityVaultRemoveCommand = value;
    }

    public function get activationRemoveCommand():IIocCommand {
        return _activationRemoveCommand;
    }

    public function set activationRemoveCommand(value:IIocCommand):void {
        _activationRemoveCommand = value;
    }

    public function get identityLookupRemoveCommand():IIocCommand {
        return _identityLookupRemoveCommand;
    }

    public function set identityLookupRemoveCommand(value:IIocCommand):void {
        _identityLookupRemoveCommand = value;
    }

    public function get federatedConnectionRemoveCommand():IIocCommand {
        return _federatedConnectionRemoveCommand;
    }

    public function set federatedConnectionRemoveCommand(value:IIocCommand):void {
        _federatedConnectionRemoveCommand = value;
    }

    public function get executionEnvironmentRemoveCommand():IIocCommand {
        return _executionEnvironmentRemoveCommand;
    }

    public function set executionEnvironmentRemoveCommand(value:IIocCommand):void {
        _executionEnvironmentRemoveCommand = value;
    }

    public function get createSimpleSSOIdentityApplianceCommand():IIocCommand {
        return _createSimpleSSOIdentityApplianceCommand;
    }

    public function set createSimpleSSOIdentityApplianceCommand(value:IIocCommand):void {
        _createSimpleSSOIdentityApplianceCommand = value;
    }

    public function get identityApplianceListLoadCommand():IIocCommand {
        return _identityApplianceListLoadCommand;
    }

    public function set identityApplianceListLoadCommand(value:IIocCommand):void {
        _identityApplianceListLoadCommand = value;
    }

    public function get identityApplianceCreateCommand():IIocCommand {
        return _identityApplianceCreateCommand;
    }

    public function set identityApplianceCreateCommand(value:IIocCommand):void {
        _identityApplianceCreateCommand = value;
    }

    public function get identityApplianceImportCommand():IIocCommand {
        return _identityApplianceImportCommand;
    }

    public function set identityApplianceImportCommand(value:IIocCommand):void {
        _identityApplianceImportCommand = value;
    }

    public function get createSimpleSSOSetupCommand():IIocCommand {
        return _createSimpleSSOSetupCommand;
    }

    public function set createSimpleSSOSetupCommand(value:IIocCommand):void {
        _createSimpleSSOSetupCommand = value;
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

    //    public function get createActivationCommand():IIocCommand {
    //        return _createActivationCommand;
    //    }
    //
    //    public function set createActivationCommand(value:IIocCommand):void {
    //        _createActivationCommand = value;
    //    }

    public function get createIdentityLookupCommand():IIocCommand {
        return _createIdentityLookupCommand;
    }

    public function set createIdentityLookupCommand(value:IIocCommand):void {
        _createIdentityLookupCommand = value;
    }

    public function get activateExecEnvironmentCommand():IIocCommand {
        return _activateExecEnvironmentCommand;
    }

    public function set activateExecEnvironmentCommand(value:IIocCommand):void {
        _activateExecEnvironmentCommand = value;
    }

    public function get folderExistsCommand():IIocCommand {
        return _folderExistsCommand;
    }

    public function set folderExistsCommand(value:IIocCommand):void {
        _folderExistsCommand = value;
    }

    public function get foldersExistsCommand():IIocCommand {
        return _foldersExistsCommand;
    }

    public function set foldersExistsCommand(value:IIocCommand):void {
        _foldersExistsCommand = value;
    }

    public function get jdbcDriversListCommand():IIocCommand {
        return _jdbcDriversListCommand;
    }

    public function set jdbcDriversListCommand(value:IIocCommand):void {
        _jdbcDriversListCommand = value;
    }

    public function get getMetadataInfoCommand():IIocCommand {
        return _getMetadataInfoCommand;
    }

    public function set getMetadataInfoCommand(value:IIocCommand):void {
        _getMetadataInfoCommand = value;
    }

    public function get getCertificateInfoCommand():IIocCommand {
        return _getCertificateInfoCommand;
    }

    public function set getCertificateInfoCommand(value:IIocCommand):void {
        _getCertificateInfoCommand = value;
    }

    override public function execute(note:INotification):void {
        var registry:ServiceRegistry = setupServiceRegistry();

        var app:AtricoreConsole = note.getBody() as AtricoreConsole;

        // first register commands (some commands are needed for mediator creation/initialization)
        iocFacade.registerCommandByConfigName(ApplicationFacade.SETUP_SERVER, setupServerCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOGIN, loginCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHANGE_PASSWORD, changePasswordCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.NOT_FIRST_RUN, notFirstRunCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.REGISTER, registerCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, createSimpleSSOIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_APPLIANCE, identityApplianceCreateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IMPORT_IDENTITY_APPLIANCE, identityApplianceImportCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_REMOVE, identityApplianceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_PROVIDER_REMOVE, identityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SERVICE_PROVIDER_REMOVE, serviceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXTERNAL_IDENTITY_PROVIDER_REMOVE, externalIdentityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXTERNAL_SERVICE_PROVIDER_REMOVE, externalServiceProviderRemoveCommand.getConfigName());
//        iocFacade.registerCommandByConfigName(ApplicationFacade.IDP_CHANNEL_REMOVE, idpChannelRemoveCommand.getConfigName());
//        iocFacade.registerCommandByConfigName(ApplicationFacade.SP_CHANNEL_REMOVE, spChannelRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_SOURCE_REMOVE, identityVaultRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATION_REMOVE, activationRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_LOOKUP_REMOVE, identityLookupRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.FEDERATED_CONNECTION_REMOVE, federatedConnectionRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXECUTION_ENVIRONMENT_REMOVE, executionEnvironmentRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, lookupIdentityApplianceByIdCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD, identityApplianceListLoadCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.UPLOAD, uploadCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, buildIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DEPLOY_IDENTITY_APPLIANCE, deployIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.UNDEPLOY_IDENTITY_APPLIANCE, undeployIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.START_IDENTITY_APPLIANCE, startIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.STOP_IDENTITY_APPLIANCE, stopIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DISPOSE_IDENTITY_APPLIANCE, disposeIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE, identityApplianceUpdateCommand.getConfigName());
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
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT, activateExecEnvironmentCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_LOOKUP, createIdentityLookupCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, folderExistsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, foldersExistsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_JDBC_DRIVERS, jdbcDriversListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_METADATA_INFO, getMetadataInfoCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_CERTIFICATE_INFO, getCertificateInfoCommand.getConfigName());

        // setup for first level mediators
        applicationMediator.setViewComponent(app);
        iocFacade.registerMediatorByConfigName(applicationMediator.getConfigName());

        loginMediator.setViewComponent(app.loginView);
        iocFacade.registerMediatorByConfigName(loginMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(modelerMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(lifecycleViewMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(accountManagementMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(liveUpdateMediator.getConfigName());

        // setup for second level modeler mediators
        iocFacade.registerMediatorByConfigName(browserMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(diagramMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(paletteMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(propertySheetMediator.getConfigName());

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

        // register mediators for popup managers - popup managers will wire the corresponding view to it
        iocFacade.registerMediatorByConfigName(setupWizardViewMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(simpleSSOWizardViewMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityApplianceWizardMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(manageCertificateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(serviceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalIdentityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalServiceProviderCreateMediator.getConfigName());
//        iocFacade.registerMediatorByConfigName(idpChannelCreateMediator.getConfigName());
//        iocFacade.registerMediatorByConfigName(spChannelCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityVaultCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(dbIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(ldapIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(xmlIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbossExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(weblogicExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(tomcatExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbossPortalExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(liferayPortalExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(wasceExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(apacheExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(alfrescoExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(javaEEExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(phpBBExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(windowsIISExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(webserverExecutionEnvironmentCreateMediator.getConfigName());

        iocFacade.registerMediatorByConfigName(uploadProgressMediator.getConfigName());
        //iocFacade.registerMediatorByConfigName(buildApplianceMediator.getConfigName());
        //iocFacade.registerMediatorByConfigName(deployApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(processingMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(activationCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(federatedConnectionCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportIdentityApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(changePasswordMediator.getConfigName());

        // IDENTITY_APPLIANCE_LIST_LOAD notification is sent from
        // modelerMediator.setViewComponent() -> modelerMediator.init()
        // but the identityApplianceListLoadCommand is registered after that, so we must send it again.
        // Maybe we should first register commands, then mediators (so they can be 100% ready
        // to catch notifications from commands), and then set views components?
        //sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);

    }

    protected function setupServiceRegistry():ServiceRegistry {
        var channel:Channel = ServerConfig.getChannel("my-amf");

        var registry:ServiceRegistry = serviceRegistry as ServiceRegistry;
        registry.setChannel(channel);
        registry.registerRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE, ApplicationFacade.USER_PROVISIONING_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE, ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.PROFILE_MANAGEMENT_SERVICE, ApplicationFacade.PROFILE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.SIGN_ON_SERVICE, ApplicationFacade.SIGN_ON_SERVICE);

        return registry;
    }

    public function result(data:Object):void {
        if (data.result == null) {
            sendNotification(FAILURE);
            return;
        }

        sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        sendNotification(FAILURE);

    }
}
}
