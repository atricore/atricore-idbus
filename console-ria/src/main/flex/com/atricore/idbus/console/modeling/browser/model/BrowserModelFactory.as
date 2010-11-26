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
import com.atricore.idbus.console.services.dto.AlfrescoExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;
import com.atricore.idbus.console.services.dto.BindingProvider;
import com.atricore.idbus.console.services.dto.Connection;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalServiceProvider;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityApplianceUnit;
import com.atricore.idbus.console.services.dto.IdentityApplianceUnitType;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JbossExecutionEnvironment;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WeblogicExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;

public class BrowserModelFactory {

        public static function createIdentityApplianceNode(identityAppliance:IdentityAppliance, selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var applianceNode:BrowserNode = new BrowserNode();
            applianceNode.id = identityAppliance.id;
            applianceNode.label = identityAppliance.idApplianceDefinition.name;
            applianceNode.type = Constants.IDENTITY_BUS_DEEP;
            applianceNode.data = identityAppliance;
            applianceNode.selectable = selectable;
            applianceNode.icon = EmbeddedIcons.busMiniIcon;
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
            applianceDefinitionNode.icon = EmbeddedIcons.busMiniIcon;
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
            if (provider is ServiceProvider) {
                providerNode.icon = EmbeddedIcons.spMiniIcon;
            } else if (provider is IdentityProvider) {
                providerNode.icon = EmbeddedIcons.idpMiniIcon;
            } else if (provider is ExternalServiceProvider) {
                providerNode.icon = EmbeddedIcons.externalSpMiniIcon;
            } else if (provider is ExternalIdentityProvider) {
                providerNode.icon = EmbeddedIcons.externalIdpMiniIcon;
            } else if (provider is BindingProvider) {
                providerNode.icon = EmbeddedIcons.bpMiniIcon;
            }
            return providerNode;
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
            } else if (executionEnvironment is AlfrescoExecutionEnvironment){
                execEnvironmentNode.icon = EmbeddedIcons.alfrescoEnvironmentMiniIcon; 
            } else {
                execEnvironmentNode.icon = EmbeddedIcons.executionEnvironmentMiniIcon;
            }
            return execEnvironmentNode;
        }

        public static function createConnectionsNode(selectable:Boolean, parentNode:BrowserNode):BrowserNode {
            var connectionsNode:BrowserNode = new BrowserNode();
            connectionsNode.label = "connections";
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
                connectionNode.icon = EmbeddedIcons.connectionFederatedMiniIcon;
            } else if (connection is Activation) {
                connectionNode.icon = EmbeddedIcons.connectionActivationMiniIcon;
            } else if (connection is IdentityLookup) {
                connectionNode.icon = EmbeddedIcons.connectionIdentityLookupMiniIcon;
            }
            return connectionNode;
        }
    }
}