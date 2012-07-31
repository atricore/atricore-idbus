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

package com.atricore.idbus.console.modeling.browser.model {
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.main.view.util.Constants;
import com.atricore.idbus.console.services.dto.Activation;
import com.atricore.idbus.console.services.dto.AlfrescoResource;
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.ColdfusionResource;
import com.atricore.idbus.console.services.dto.Connection;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.DirectoryAuthenticationService;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalOpenIDIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalSaml2IdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalWSFederationServiceProvider;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.GoogleAppsServiceProvider;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityApplianceUnit;
import com.atricore.idbus.console.services.dto.IdentityApplianceUnitType;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.InternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.JBossPortalResource;
import com.atricore.idbus.console.services.dto.JEEExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSO1Resource;
import com.atricore.idbus.console.services.dto.JOSSO2Resource;
import com.atricore.idbus.console.services.dto.JbossExecutionEnvironment;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.LiferayResource;
import com.atricore.idbus.console.services.dto.MicroStrategyResource;
import com.atricore.idbus.console.services.dto.OAuth2ServiceProvider;
import com.atricore.idbus.console.services.dto.PHPExecutionEnvironment;
import com.atricore.idbus.console.services.dto.PhpBBResource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.SalesforceServiceProvider;
import com.atricore.idbus.console.services.dto.ExternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.SasResource;
import com.atricore.idbus.console.services.dto.ServiceConnection;
import com.atricore.idbus.console.services.dto.ServiceResource;
import com.atricore.idbus.console.services.dto.SharepointResource;
import com.atricore.idbus.console.services.dto.SugarCRMServiceProvider;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WeblogicExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WebserverExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WikidAuthenticationService;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WindowsIntegratedAuthentication;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

public class BrowserModelFactory {

        public static function createIdentityApplianceNode(identityAppliance:IdentityAppliance, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var applianceNode:BrowserNode = new BrowserNode();
            applianceNode.id = identityAppliance.id;
            applianceNode.label = identityAppliance.idApplianceDefinition.name;
            applianceNode.type = Constants.IDENTITY_BUS_DEEP;
            applianceNode.data = identityAppliance;
            applianceNode.selectable = selectable;
            applianceNode.icon = EmbeddedIcons.identityApplianceMiniIcon;
            applianceNode.parentNode = parentNode;
            return applianceNode;
        }

        public static function createIdentityApplianceDefinitionNode(identityApplianceDefinition:IdentityApplianceDefinition, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var applianceDefinitionNode:BrowserNode = new BrowserNode();
            applianceDefinitionNode.id = identityApplianceDefinition.id;
            applianceDefinitionNode.label = identityApplianceDefinition.name;
            applianceDefinitionNode.type = Constants.IDENTITY_BUS_DEEP;
            applianceDefinitionNode.data = identityApplianceDefinition;
            applianceDefinitionNode.selectable = selectable;
            applianceDefinitionNode.icon = EmbeddedIcons.identityApplianceMiniIcon;
            applianceDefinitionNode.parentNode = parentNode;
            return applianceDefinitionNode;
        }

        public static function createIdentityApplianceUnitNode(identityApplianceUnit:IdentityApplianceUnit, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var applianceUnitNode:BrowserNode = new BrowserNode();
            applianceUnitNode.id = identityApplianceUnit.id;
            applianceUnitNode.label = identityApplianceUnit.name;
            applianceUnitNode.type = Constants.IDENTITY_BUS_UNIT_DEEP;
            applianceUnitNode.data = identityApplianceUnit;
            applianceUnitNode.selectable = selectable;
            applianceUnitNode.parentNode = parentNode;
            var type:IdentityApplianceUnitType = identityApplianceUnit.type;
            if (type.equals(IdentityApplianceUnitType.FEDERATION_UNIT)) {
                applianceUnitNode.icon = EmbeddedIcons.worldIcon;
            } else if (type.equals(IdentityApplianceUnitType.PROVISIONING_UNIT)) {
                applianceUnitNode.icon = EmbeddedIcons.usersIcon;
            }
            return applianceUnitNode;
        }

        public static function createProviderNode(provider:Provider, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var providerNode:BrowserNode = new BrowserNode();
            providerNode.id = provider.id;
            providerNode.label = provider.name;
            providerNode.type = Constants.PROVIDER_DEEP;
            providerNode.data = provider;
            providerNode.selectable = selectable;
            providerNode.parentNode = parentNode;
            if (provider is IdentityProvider) {
                providerNode.icon = EmbeddedIcons.idpMiniIcon;
            } else if (provider is InternalSaml2ServiceProvider) {
                providerNode.icon = EmbeddedIcons.saml2SpMiniIcon;
            } else if (provider is ExternalSaml2IdentityProvider) {
                providerNode.icon = EmbeddedIcons.externalSaml2IdpMiniIcon;
            } else if (provider is ExternalSaml2ServiceProvider) {
                if (provider is SalesforceServiceProvider) {
                    providerNode.icon = EmbeddedIcons.salesforceSpMiniIcon;
                } else
                if (provider is GoogleAppsServiceProvider) {
                    providerNode.icon = EmbeddedIcons.googleSpMiniIcon;
                } else
                if (provider is SugarCRMServiceProvider) {
                    providerNode.icon = EmbeddedIcons.sugarCRMSpMiniIcon;
                } else {
                    providerNode.icon = EmbeddedIcons.externalSaml2SpMiniIcon;
                }
            } else if (provider is ExternalOpenIDIdentityProvider) {
                providerNode.icon = EmbeddedIcons.externalOpenidIdpMiniIcon;
            } else if (provider is OAuth2ServiceProvider) {
                providerNode.icon = EmbeddedIcons.oauth2SpMiniIcon;
            } else if (provider is ExternalWSFederationServiceProvider) {
                providerNode.icon = EmbeddedIcons.externalWsFedSpMiniIcon;
            }

            return providerNode;
        }

        public static function createAuthenticationServiceNode(authnService:AuthenticationService, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var authnServiceNode:BrowserNode = new BrowserNode();
            authnServiceNode.id = Number(authnService.id);
            authnServiceNode.label = authnService.name;
            authnServiceNode.type = Constants.IDENTITY_VAULT_DEEP;
            authnServiceNode.data = authnService;
            authnServiceNode.selectable = selectable;
            authnServiceNode.parentNode = parentNode;
            if (authnService is WikidAuthenticationService) {
                authnServiceNode.icon = EmbeddedIcons.wikidAuthenticationServiceMiniIcon;
            } else if (authnService is DirectoryAuthenticationService) {
                authnServiceNode.icon = EmbeddedIcons.directoryAuthenticationServiceMiniIcon;
            } else if (authnService is WindowsIntegratedAuthentication) {
                authnServiceNode.icon = EmbeddedIcons.windowsAuthenticationServiceMiniIcon;
            }
            return authnServiceNode;
        }

        public static function createIdentityVaultNode(identityVault:IdentitySource, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var identityVaultNode:BrowserNode = new BrowserNode();
            identityVaultNode.id = Number(identityVault.id);
            identityVaultNode.label = identityVault.name;
            identityVaultNode.type = Constants.IDENTITY_VAULT_DEEP;
            identityVaultNode.data = identityVault;
            identityVaultNode.selectable = selectable;
            identityVaultNode.parentNode = parentNode;
            if(identityVault is DbIdentitySource){
                identityVaultNode.icon = EmbeddedIcons.dbIdentitySourceMiniIcon;
            } else if(identityVault is LdapIdentitySource){
                identityVaultNode.icon = EmbeddedIcons.ldapIdentitySourceMiniIcon;
            } else if(identityVault is XmlIdentitySource){
                identityVaultNode.icon = EmbeddedIcons.xmlIdentitySourceMiniIcon;
            } else {
                identityVaultNode.icon = EmbeddedIcons.vaultMiniIcon;
            }
            return identityVaultNode;
        }

        public static function createServiceResourceNode(serviceResource:ServiceResource, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var resourceNode:BrowserNode = new BrowserNode();
            resourceNode.id = Number(serviceResource.id);
            resourceNode.label = serviceResource.name;
            resourceNode.type = Constants.SERVICE_RESOURCE_DEEP;
            resourceNode.data = serviceResource;
            resourceNode.selectable = selectable;
            resourceNode.parentNode = parentNode;
            if (serviceResource is JOSSO1Resource) {
                resourceNode.icon = EmbeddedIcons.josso1ResourceMiniIcon;
            } else if (serviceResource is JOSSO2Resource) {
                resourceNode.icon = EmbeddedIcons.josso2ResourceMiniIcon;
            } else if (serviceResource is MicroStrategyResource) {
                resourceNode.icon = EmbeddedIcons.microStrategyResourceMiniIcon;
            } else if (serviceResource is SasResource) {
                resourceNode.icon = EmbeddedIcons.sasResourceMiniIcon;
            } else if (serviceResource is SharepointResource) {
                resourceNode.icon = EmbeddedIcons.sharepointResourceMiniIcon;
            } else if (serviceResource is ColdfusionResource) {
                resourceNode.icon = EmbeddedIcons.coldfusionResourceMiniIcon;
            } else if (serviceResource is AlfrescoResource){
                resourceNode.icon = EmbeddedIcons.alfrescoResourceMiniIcon;
            } else if (serviceResource is PhpBBResource) {
               resourceNode.icon = EmbeddedIcons.phpbbResourceMiniIcon;
            } else if (serviceResource is LiferayResource) {
                resourceNode.icon = EmbeddedIcons.liferayResourceMiniIcon;
            } else if (serviceResource is JBossPortalResource) {
                resourceNode.icon = EmbeddedIcons.jbossPortalResourceMiniIcon;
            }


            return resourceNode;
        }

        public static function createExecutionEnvironmentNode(executionEnvironment:ExecutionEnvironment, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var execEnvironmentNode:BrowserNode = new BrowserNode();
            execEnvironmentNode.id = Number(executionEnvironment.id);
            execEnvironmentNode.label = executionEnvironment.name;
            execEnvironmentNode.type = Constants.EXEC_ENVIRONMENT_DEEP;
            execEnvironmentNode.data = executionEnvironment;
            execEnvironmentNode.selectable = selectable;
            execEnvironmentNode.parentNode = parentNode;
            if(executionEnvironment is JbossExecutionEnvironment){
                execEnvironmentNode.icon = EmbeddedIcons.jbossEnvironmentMiniIcon;
            } else if(executionEnvironment is WeblogicExecutionEnvironment){
                execEnvironmentNode.icon = EmbeddedIcons.weblogicEnvironmentMiniIcon;
            } else if(executionEnvironment is TomcatExecutionEnvironment){
                execEnvironmentNode.icon = EmbeddedIcons.tomcatEnvironmentMiniIcon;                
            } else if(executionEnvironment is ApacheExecutionEnvironment){
                execEnvironmentNode.icon = EmbeddedIcons.apacheEnvironmentMiniIcon;
            }else if(executionEnvironment is WindowsIISExecutionEnvironment){
                execEnvironmentNode.icon = EmbeddedIcons.windowsEnvironmentMiniIcon;
            } else if (executionEnvironment is JEEExecutionEnvironment) {
                execEnvironmentNode.icon = EmbeddedIcons.javaEnvironmentMiniIcon;
            } else if (executionEnvironment is PHPExecutionEnvironment) {
                execEnvironmentNode.icon = EmbeddedIcons.phpEnvironmentMiniIcon;
            } else if (executionEnvironment is WebserverExecutionEnvironment) {
                execEnvironmentNode.icon = EmbeddedIcons.webEnvironmentMiniIcon;
            } else {
                execEnvironmentNode.icon = EmbeddedIcons.executionEnvironmentMiniIcon;
            }
            return execEnvironmentNode;
        }

        public static function createConnectionsNode(selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var connectionsNode:BrowserNode = new BrowserNode();
            var resourceManager:IResourceManager = ResourceManager.getInstance();
            connectionsNode.label = resourceManager.getString(AtricoreConsole.BUNDLE, "modelling.browser.connections");
            connectionsNode.type = Constants.CONNECTIONS_DEEP;
            connectionsNode.selectable = selectable;
            connectionsNode.parentNode = parentNode;
            connectionsNode.icon = EmbeddedIcons.connectionsMiniIcon;
            return connectionsNode;
        }

        public static function createConnectionNode(connection:Connection, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var connectionNode:BrowserNode = new BrowserNode();
            connectionNode.id = Number(connection.id);
            connectionNode.label = connection.name;
            connectionNode.type = Constants.CONNECTION_DEEP;
            connectionNode.data = connection;
            connectionNode.selectable = selectable;
            connectionNode.parentNode = parentNode;
            if (connection is FederatedConnection) {
                connectionNode.icon = EmbeddedIcons.federatedConnectionMiniIcon;
            } else if (connection is ServiceConnection) {
                connectionNode.icon = EmbeddedIcons.serviceConnectionMiniIcon;
            } else if (connection is Activation) {
                connectionNode.icon = EmbeddedIcons.activationMiniIcon;
            } else if (connection is IdentityLookup) {
                connectionNode.icon = EmbeddedIcons.identityLookupMiniIcon;
            } else if (connection is DelegatedAuthentication) {
                connectionNode.icon = EmbeddedIcons.identityVerificationMiniIcon;
            }
            return connectionNode;
        }
    }
}