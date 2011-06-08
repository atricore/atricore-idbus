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

package com.atricore.idbus.console.branding.ce {

public class EmbeddedIcons {

    public function EmbeddedIcons() {
    }

    // Providers
    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/identity_provider.png")]
    public static var idpMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/service_provider.png")]
    public static var spMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/external_identity_provider.png")]
    public static var externalIdpMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/external_service_provider.png")]
    public static var externalSpMiniIcon:Class;

    // Authentication
    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/directory_service.png")]
    public static var directoryServiceMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/win_integrated_authn.png")]
    public static var windowsIntegratedAuthnMiniIcon:Class;

    // Identity sources
    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/identity_vault.png")]
    public static var vaultMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/ldap_identity_source.png")]
    public static var ldapIdentitySourceMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/database_identity_source.png")]
    public static var dbIdentitySourceMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/xml_identity_source.png")]
    public static var xmlIdentitySourceMiniIcon:Class;

    // Connections
    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/connection.png")]
    public static var connectionFederatedMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/activation.png")]
    public static var connectionActivationMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/identity_lookup.png")]
    public static var connectionIdentityLookupMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/identity_verification.png")]
    public static var connectionDelegatedAuthnMiniIcon:Class;
    
    // Execution environments
    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/alfresco_execution_environment.png")]
    public static var alfrescoEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/apache_web_server_execution_environment.png")]
    public static var apacheEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/java_ee_execution_environment.png")]
    public static var javaEnvironmentMiniIcon:Class;
    
    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/jboss_as_execution_environment.png")]
    public static var jbossEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/liferay_execution_environment.png")]
    public static var liferayEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/apache_web_server_execution_environment.png")]
    public static var phpEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/phpbb_execution_environment.png")]
    public static var phpbbEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/tomcat_execution_environment.png")]
    public static var tomcatEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/web_server_execution_environment.png")]
    public static var webEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/weblogic_execution_environment.png")]
    public static var weblogicEnvironmentMiniIcon:Class;

    [Bindable]
    [Embed(source="/assets/icons/notation/22x22/websphere_execution_environment.png")]
    public static var websphereEnvironmentMiniIcon:Class;

}
}