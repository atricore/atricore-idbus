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
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;
import com.atricore.idbus.console.services.dto.BindingProvider;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentityApplianceUnit;
import com.atricore.idbus.console.services.dto.IdentityApplianceUnitType;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JbossExecutionEnvironment;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WeblogicExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;

public class BrowserModelFactory {

        public static function createIdentityApplianceNode(identityAppliance:IdentityAppliance, selectable:Boolean):BrowserNode {
            var applianceNode:BrowserNode = new BrowserNode();
            applianceNode.id = identityAppliance.id;
            applianceNode.label = identityAppliance.idApplianceDefinition.name;
            applianceNode.type = Constants.IDENTITY_BUS_DEEP;
            applianceNode.data = identityAppliance;
            applianceNode.selectable = selectable;
            applianceNode.icon = EmbeddedIcons.busMiniIcon;
            return applianceNode;
        }

        public static function createIdentityApplianceDefinitionNode(identityApplianceDefinition:IdentityApplianceDefinition, selectable:Boolean):BrowserNode {
            var applianceDefinitionNode:BrowserNode = new BrowserNode();
            applianceDefinitionNode.id = identityApplianceDefinition.id;
            applianceDefinitionNode.label = identityApplianceDefinition.name;
            applianceDefinitionNode.type = Constants.IDENTITY_BUS_DEEP;
            applianceDefinitionNode.data = identityApplianceDefinition;
            applianceDefinitionNode.selectable = selectable;
            applianceDefinitionNode.icon = EmbeddedIcons.busMiniIcon;
            return applianceDefinitionNode;
        }

        public static function createIdentityApplianceUnitNode(identityApplianceUnit:IdentityApplianceUnit, selectable:Boolean):BrowserNode {
            var applianceUnitNode:BrowserNode = new BrowserNode();
            applianceUnitNode.id = identityApplianceUnit.id;
            applianceUnitNode.label = identityApplianceUnit.name;
            applianceUnitNode.type = Constants.IDENTITY_BUS_UNIT_DEEP;
            applianceUnitNode.data = identityApplianceUnit;
            applianceUnitNode.selectable = selectable;
            var type:IdentityApplianceUnitType = identityApplianceUnit.type;
            if (type.equals(IdentityApplianceUnitType.FEDERATION_UNIT)) {
                applianceUnitNode.icon = EmbeddedIcons.worldIcon;
            } else if (type.equals(IdentityApplianceUnitType.PROVISIONING_UNIT)) {
                applianceUnitNode.icon = EmbeddedIcons.usersIcon;
            }
            return applianceUnitNode;
        }

        public static function createProviderNode(provider:Provider, selectable:Boolean):BrowserNode {
            var providerNode:BrowserNode = new BrowserNode();
            providerNode.id = provider.id;
            providerNode.label = provider.name;
            providerNode.type = Constants.PROVIDER_DEEP;
            providerNode.data = provider;
            providerNode.selectable = selectable;
            if (provider is ServiceProvider) {
                providerNode.icon = EmbeddedIcons.spMiniIcon;
            } else if (provider is IdentityProvider) {
                providerNode.icon = EmbeddedIcons.idpMiniIcon;
            } else if (provider is BindingProvider) {
                providerNode.icon = EmbeddedIcons.bpMiniIcon;
            }
            return providerNode;
        }

        public static function createIdentityVaultNode(identityVault:IdentitySource, selectable:Boolean):BrowserNode {
            var identityVaultNode:BrowserNode = new BrowserNode();
            identityVaultNode.id = Number(identityVault.id);
            identityVaultNode.label = identityVault.name;
            identityVaultNode.type = Constants.IDENTITY_VAULT_DEEP;
            if(identityVault is DbIdentitySource){
                identityVaultNode.icon = EmbeddedIcons.dbIdentitySourceMiniIcon;
            } else if(identityVault is LdapIdentitySource){
                identityVaultNode.icon = EmbeddedIcons.ldapIdentitySourceMiniIcon;
            } else {
                identityVaultNode.icon = EmbeddedIcons.vaultMiniIcon;
            }
//            identityVaultNode.icon = EmbeddedIcons.vaultMiniIcon;
            identityVaultNode.data = identityVault;
            identityVaultNode.selectable = selectable;
            return identityVaultNode;
        }

        public static function createExecutionEnvironmentNode(executionEnvironment:ExecutionEnvironment, selectable:Boolean):BrowserNode {
            var execEnvironmentNode:BrowserNode = new BrowserNode();
            execEnvironmentNode.id = Number(executionEnvironment.id);
            execEnvironmentNode.label = executionEnvironment.name;
            execEnvironmentNode.type = Constants.EXEC_ENVIRONMENT_DEEP;
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
            } else {
                execEnvironmentNode.icon = EmbeddedIcons.executionEnvironmentMiniIcon;
            }
            return execEnvironmentNode;
        }
    }
}