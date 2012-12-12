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

package com.atricore.idbus.console.modeling.main.view {
import com.atricore.idbus.console.main.BasePopUpManager;
import com.atricore.idbus.console.main.view.certificate.ManageCertificateMediator;
import com.atricore.idbus.console.main.view.certificate.ManageCertificateView;
import com.atricore.idbus.console.main.view.upload.UploadProgress;
import com.atricore.idbus.console.main.view.upload.UploadProgressMediator;
import com.atricore.idbus.console.modeling.diagram.view.activation.ActivationCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.activation.ActivationCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.directory.DirectoryServiceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.directory.DirectoryServiceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.domino.DominoCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.wikid.WikidCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.wikid.WikidCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.domino.DominoCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.domino.DominoCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.clientcert.ClientCertCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.clientcert.ClientCertCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.windows.WindowsIntegratedAuthnCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.windows.WindowsIntegratedAuthnCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.dbidentitysource.DbIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.dbidentitysource.DbIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.jbossepp.JBossEPPAuthenticationServiceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.jbossepp.JBossEPPAuthenticationServiceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.jbossepp.JBossEPPAuthenticationServiceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.alfresco.AlfrescoResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.alfresco.AlfrescoResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache.ApacheExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache.ApacheExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.coldfusion.ColdfusionResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.javaee.JavaEEExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.javaee.JavaEEExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jboss.JBossExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jboss.JBossExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.jbossportal.JBossPortalResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.jbossportal.JBossPortalResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.liferayportal.LiferayPortalResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.liferayportal.LiferayPortalResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.coldfusion.ColdfusionResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.microstrategy.MicroStrategyResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.microstrategy.MicroStrategyResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.php.PHPExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.php.PHPExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.phpbb.PhpBBResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.phpbb.PhpBBResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.sharepoint.SharepointResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.sharepoint.SharepointResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.tomcat.TomcatExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.tomcat.TomcatExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.wasce.WASCEExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.wasce.WASCEExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.weblogic.WeblogicExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.weblogic.WeblogicExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.webserver.WebserverExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.webserver.WebserverExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.windowsiis.WindowsIISExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.windowsiis.WindowsIISExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.federatedconnection.FederatedConnectionCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.federatedconnection.FederatedConnectionCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.googleapps.GoogleAppsCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.googleapps.GoogleAppsCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.identityvault.IdentityVaultCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.identityvault.IdentityVaultCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.ldapidentitysource.LdapIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.ldapidentitysource.LdapIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.oauth2.idp.OAuth2IdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.oauth2.idp.OAuth2IdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.oauth2.sp.OAuth2ServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.oauth2.sp.OAuth2ServiceProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.openid.idp.ExternalOpenIDIdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.openid.idp.ExternalOpenIDIdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.josso1.JOSSO1ResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.josso1.JOSSO1ResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.resources.josso2.JOSSO2ResourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.resources.josso2.JOSSO2ResourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.salesforce.SalesforceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.salesforce.SalesforceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.saml2.idp.external.ExternalSaml2IdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.saml2.idp.external.ExternalSaml2IdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.saml2.sp.external.ExternalSaml2ServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.saml2.sp.external.ExternalSaml2ServiceProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.saml2.sp.internal.InternalSaml2ServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.saml2.sp.internal.InternalSaml2ServiceProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.sugarcrm.SugarCRMCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.sugarcrm.SugarCRMCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.xmlidentitysource.XmlIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.xmlidentitysource.XmlIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceWizardView;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceView;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceView;
import com.atricore.idbus.console.modeling.main.view.export.ExportIdentityApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.export.ExportIdentityApplianceView;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardView;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.activation.ExecEnvActivationMediator;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.activation.ExecEnvActivationView;
import com.atricore.idbus.console.modeling.propertysheet.view.export.ExportAgentConfigMediator;
import com.atricore.idbus.console.modeling.propertysheet.view.export.ExportAgentConfigView;
import com.atricore.idbus.console.modeling.propertysheet.view.export.ExportMetadataMediator;
import com.atricore.idbus.console.modeling.propertysheet.view.export.ExportMetadataView;
import com.atricore.idbus.console.modeling.propertysheet.view.export.ExportProviderCertificateMediator;
import com.atricore.idbus.console.modeling.propertysheet.view.export.ExportProviderCertificateView;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class ModelerPopUpManager extends BasePopUpManager {

    // mediators
    private var _simpleSSOWizardMediator:SimpleSSOWizardViewMediator;
    private var _identityApplianceWizardMediator:IdentityApplianceWizardViewMediator;
    private var _manageCertificateMediator:ManageCertificateMediator;
    private var _identityProviderCreateMediator:IdentityProviderCreateMediator;
    private var _internalSaml2ServiceProviderCreateMediator:InternalSaml2ServiceProviderCreateMediator;
    private var _externalSaml2ServiceProviderCreateMediator:ExternalSaml2ServiceProviderCreateMediator;
    private var _externalSaml2IdentityProviderCreateMediator:ExternalSaml2IdentityProviderCreateMediator;
    private var _externalOpenIDIdentityProviderCreateMediator:ExternalOpenIDIdentityProviderCreateMediator;
    private var _oauth2IdentityProviderCreateMediator:OAuth2IdentityProviderCreateMediator;
    private var _oauth2ServiceProviderCreateMediator:OAuth2ServiceProviderCreateMediator;
    private var _salesforceCreateMediator:SalesforceCreateMediator;
    private var _googleAppsCreateMediator:GoogleAppsCreateMediator;
    private var _sugarCRMCreateMediator:SugarCRMCreateMediator;
    private var _identityVaultCreateMediator:IdentityVaultCreateMediator;
    private var _dbIdentitySourceCreateMediator:DbIdentitySourceCreateMediator;
    private var _ldapIdentitySourceCreateMediator:LdapIdentitySourceCreateMediator;
    private var _xmlIdentitySourceCreateMediator:XmlIdentitySourceCreateMediator;
    private var _josso1ResourceCreateMediator:JOSSO1ResourceCreateMediator;
    private var _josso2ResourceCreateMediator:JOSSO2ResourceCreateMediator;
    private var _jbossExecutionEnvironmentCreateMediator:JBossExecutionEnvironmentCreateMediator;
    private var _weblogicExecutionEnvironmentCreateMediator:WeblogicExecutionEnvironmentCreateMediator;
    private var _tomcatExecutionEnvironmentCreateMediator:TomcatExecutionEnvironmentCreateMediator;
    private var _jbossPortalResourceCreateMediator:JBossPortalResourceCreateMediator;
    private var _liferayPortalResourceCreateMediator:LiferayPortalResourceCreateMediator;
    private var _wasceExecutionEnvironmentCreateMediator:WASCEExecutionEnvironmentCreateMediator;
    private var _apacheExecutionEnvironmentCreateMediator:ApacheExecutionEnvironmentCreateMediator;
    private var _windowsIISExecutionEnvironmentCreateMediator:WindowsIISExecutionEnvironmentCreateMediator;
	private var _alfrescoResourceCreateMediator:AlfrescoResourceCreateMediator;
    private var _javaEEExecutionEnvironmentCreateMediator:JavaEEExecutionEnvironmentCreateMediator;
    private var _phpExecutionEnvironmentCreateMediator:PHPExecutionEnvironmentCreateMediator;
    private var _phpBBResourceCreateMediator:PhpBBResourceCreateMediator;
    private var _webserverExecutionEnvironmentCreateMediator:WebserverExecutionEnvironmentCreateMediator;
    private var _uploadProgressMediator:UploadProgressMediator;
    private var _buildApplianceMediator:BuildApplianceMediator;
    private var _deployApplianceMediator:DeployApplianceMediator;
    private var _activationCreateMediator:ActivationCreateMediator;
    private var _federatedConnectionCreateMediator:FederatedConnectionCreateMediator;
    private var _exportIdentityApplianceMediator:ExportIdentityApplianceMediator;
    private var _exportProviderCertificateMediator:ExportProviderCertificateMediator;
    private var _exportMetadataMediator:ExportMetadataMediator;
    private var _exportAgentConfigMediator:ExportAgentConfigMediator;
    private var _activationMediator:ExecEnvActivationMediator;
    private var _wikidCreateMediator:WikidCreateMediator;
    private var _dominoCreateMediator:DominoCreateMediator;
    private var _clientCertCreateMediator:ClientCertCreateMediator;
    private var _jbosseppAuthenticationCreateMediator:JBossEPPAuthenticationServiceCreateMediator;
    private var _directoryServiceCreateMediator:DirectoryServiceCreateMediator;
    private var _windowsIntegratedAuthnCreateMediator:WindowsIntegratedAuthnCreateMediator;
    private var _sharepointResourceCreateMediator:SharepointResourceCreateMediator;
    private var _coldfusionResourceCreateMediator:ColdfusionResourceCreateMediator;
    private var _microStrategyResourceCreateMediator:MicroStrategyResourceCreateMediator;

    // views
    private var _simpleSSOWizardView:SimpleSSOWizardView;
    private var _identityApplianceWizardView:IdentityApplianceWizardView;
    private var _identityProviderCreateForm:IdentityProviderCreateForm;
    private var _internalSaml2ServiceProviderCreateForm:InternalSaml2ServiceProviderCreateForm;
    private var _externalSaml2IdentityProviderCreateForm:ExternalSaml2IdentityProviderCreateForm;
    private var _externalSaml2ServiceProviderCreateForm:ExternalSaml2ServiceProviderCreateForm;
    private var _externalOpenIDIdentityProviderCreateForm:ExternalOpenIDIdentityProviderCreateForm;
    private var _oauth2IdentityProviderCreateForm:OAuth2IdentityProviderCreateForm;
    private var _oauth2ServiceProviderCreateForm:OAuth2ServiceProviderCreateForm;
    private var _salesforceCreateForm:SalesforceCreateForm;
    private var _googleAppsCreateForm:GoogleAppsCreateForm;
    private var _sugarCRMCreateForm:SugarCRMCreateForm;
    private var _identityVaultCreateForm:IdentityVaultCreateForm;
    private var _dbIdentitySourceCreateForm:DbIdentitySourceCreateForm;
    private var _ldapIdentitySourceCreateForm:LdapIdentitySourceCreateForm;
    private var _xmlIdentitySourceCreateForm:XmlIdentitySourceCreateForm;
    private var _josso1ResourceCreateForm:JOSSO1ResourceCreateForm;
    private var _josso2ResourceCreateForm:JOSSO2ResourceCreateForm;
    private var _jbossExecutionEnvironmentCreateForm:JBossExecutionEnvironmentCreateForm;
    private var _weblogicExecutionEnvironmentCreateForm:WeblogicExecutionEnvironmentCreateForm;
    private var _tomcatExecutionEnvironmentCreateForm:TomcatExecutionEnvironmentCreateForm;
    private var _jbossPortalResourceCreateForm:JBossPortalResourceCreateForm;
    private var _liferayPortalResourceCreateForm:LiferayPortalResourceCreateForm;
    private var _wasceExecutionEnvironmentCreateForm:WASCEExecutionEnvironmentCreateForm;
    private var _apacheExecutionEnvironmentCreateForm:ApacheExecutionEnvironmentCreateForm;
    private var _windowsIISExecutionEnvironmentCreateForm:WindowsIISExecutionEnvironmentCreateForm;
    private var _alfrescoResourceCreateForm:AlfrescoResourceCreateForm;
    private var _javaEEExecutionEnvironmentCreateForm:JavaEEExecutionEnvironmentCreateForm;
    private var _phpExecutionEnvironmentCreateForm:PHPExecutionEnvironmentCreateForm;
    private var _phpBBResourceCreateForm:PhpBBResourceCreateForm;
    private var _webserverExecutionEnvironmentCreateForm:WebserverExecutionEnvironmentCreateForm;
    private var _manageCertificateForm:ManageCertificateView;
    private var _uploadProgress:UploadProgress;
    private var _buildAppliance:BuildApplianceView;
    private var _deployAppliance:DeployApplianceView;
    private var _activationCreateForm:ActivationCreateForm;
    private var _federatedConnectionCreateForm:FederatedConnectionCreateForm;
    private var _exportIdentityApplianceView:ExportIdentityApplianceView;
    private var _exportProviderCertificateView:ExportProviderCertificateView;
    private var _exportMetadataView:ExportMetadataView;
    private var _exportAgentConfigView:ExportAgentConfigView;
    private var _activationView:ExecEnvActivationView;
    private var _wikidCreateForm:WikidCreateForm;
    private var _dominoCreateForm:DominoCreateForm;
    private var _clientCertCreateForm:ClientCertCreateForm;
    private var _jbossEppIdentitySourceCreateForm:JBossEPPAuthenticationServiceCreateForm;
    private var _directoryServiceCreateForm:DirectoryServiceCreateForm;
    private var _windowsIntegratedAuthnCreateForm:WindowsIntegratedAuthnCreateForm;
    private var _sharepoint2010ResourceCreateForm:SharepointResourceCreateForm;
    private var _coldfusionExecutionEnvironmentCreateForm:ColdfusionResourceCreateForm;
    private var _microStrategyResourceCreateForm:MicroStrategyResourceCreateForm;

    public function ModelerPopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "modelerPopup";
    }

    public function get simpleSSOWizardMediator():SimpleSSOWizardViewMediator {
        return _simpleSSOWizardMediator;
    }

    public function set simpleSSOWizardMediator(value:SimpleSSOWizardViewMediator):void {
        _simpleSSOWizardMediator = value;
    }

    public function get identityApplianceWizardMediator():IdentityApplianceWizardViewMediator {
        return _identityApplianceWizardMediator;
    }

    public function set identityApplianceWizardMediator(value:IdentityApplianceWizardViewMediator):void {
        _identityApplianceWizardMediator = value;
    }

    public function set manageCertificateMediator(value:ManageCertificateMediator):void {
        _manageCertificateMediator = value;
    }

    public function get manageCertificateMediator():ManageCertificateMediator {
        return _manageCertificateMediator;
    }

    public function get identityProviderCreateMediator():IdentityProviderCreateMediator {
        return _identityProviderCreateMediator;
    }

    public function set identityProviderCreateMediator(value:IdentityProviderCreateMediator):void {
        _identityProviderCreateMediator = value;
    }

    public function get externalSaml2ServiceProviderCreateMediator():ExternalSaml2ServiceProviderCreateMediator {
        return _externalSaml2ServiceProviderCreateMediator;
    }

    public function set externalSaml2ServiceProviderCreateMediator(value:ExternalSaml2ServiceProviderCreateMediator):void {
        _externalSaml2ServiceProviderCreateMediator = value;
    }

    public function get externalSaml2IdentityProviderCreateMediator():ExternalSaml2IdentityProviderCreateMediator {
        return _externalSaml2IdentityProviderCreateMediator;
    }

    public function set externalSaml2IdentityProviderCreateMediator(value:ExternalSaml2IdentityProviderCreateMediator):void {
        _externalSaml2IdentityProviderCreateMediator = value;
    }

    public function get internalSaml2ServiceProviderCreateMediator():InternalSaml2ServiceProviderCreateMediator {
        return _internalSaml2ServiceProviderCreateMediator;
    }

    public function set internalSaml2ServiceProviderCreateMediator(value:InternalSaml2ServiceProviderCreateMediator):void {
        _internalSaml2ServiceProviderCreateMediator = value;
    }

    public function get externalOpenIDIdentityProviderCreateMediator():ExternalOpenIDIdentityProviderCreateMediator {
        return _externalOpenIDIdentityProviderCreateMediator;
    }

    public function set externalOpenIDIdentityProviderCreateMediator(value:ExternalOpenIDIdentityProviderCreateMediator):void {
        _externalOpenIDIdentityProviderCreateMediator = value;
    }

    public function get oauth2IdentityProviderCreateMediator():OAuth2IdentityProviderCreateMediator {
        return _oauth2IdentityProviderCreateMediator;
    }

    public function set oauth2IdentityProviderCreateMediator(value:OAuth2IdentityProviderCreateMediator):void {
        _oauth2IdentityProviderCreateMediator = value;
    }

    public function get oauth2ServiceProviderCreateMediator():OAuth2ServiceProviderCreateMediator {
        return _oauth2ServiceProviderCreateMediator;
    }

    public function set oauth2ServiceProviderCreateMediator(value:OAuth2ServiceProviderCreateMediator):void {
        _oauth2ServiceProviderCreateMediator = value;
    }

    public function get salesforceCreateMediator():SalesforceCreateMediator {
        return _salesforceCreateMediator;
    }

    public function set salesforceCreateMediator(value:SalesforceCreateMediator):void {
        _salesforceCreateMediator = value;
    }

    public function get googleAppsCreateMediator():GoogleAppsCreateMediator {
        return _googleAppsCreateMediator;
    }

    public function set googleAppsCreateMediator(value:GoogleAppsCreateMediator):void {
        _googleAppsCreateMediator = value;
    }

    public function get sugarCRMCreateMediator():SugarCRMCreateMediator {
        return _sugarCRMCreateMediator;
    }

    public function set sugarCRMCreateMediator(value:SugarCRMCreateMediator):void {
        _sugarCRMCreateMediator = value;
    }

    public function get identityVaultCreateMediator():IdentityVaultCreateMediator {
        return _identityVaultCreateMediator;
    }

    public function set identityVaultCreateMediator(value:IdentityVaultCreateMediator):void {
        _identityVaultCreateMediator = value;
    }

    public function get dbIdentitySourceCreateMediator():DbIdentitySourceCreateMediator {
        return _dbIdentitySourceCreateMediator;
    }

    public function set dbIdentitySourceCreateMediator(value:DbIdentitySourceCreateMediator):void {
        _dbIdentitySourceCreateMediator = value;
    }

    public function get uploadProgressMediator():UploadProgressMediator {
        return _uploadProgressMediator;
    }

    public function set uploadProgressMediator(value:UploadProgressMediator):void {
        _uploadProgressMediator = value;
    }

    public function get buildApplianceMediator():BuildApplianceMediator {
        return _buildApplianceMediator;
    }

    public function set buildApplianceMediator(value:BuildApplianceMediator):void {
        _buildApplianceMediator = value;
    }

    public function get deployApplianceMediator():DeployApplianceMediator {
        return _deployApplianceMediator;
    }

    public function set deployApplianceMediator(value:DeployApplianceMediator):void {
        _deployApplianceMediator = value;
    }

    public function get ldapIdentitySourceCreateMediator():LdapIdentitySourceCreateMediator {
        return _ldapIdentitySourceCreateMediator;
    }

    public function set ldapIdentitySourceCreateMediator(value:LdapIdentitySourceCreateMediator):void {
        _ldapIdentitySourceCreateMediator = value;
    }

    public function get xmlIdentitySourceCreateMediator():XmlIdentitySourceCreateMediator {
        return _xmlIdentitySourceCreateMediator;
    }

    public function set xmlIdentitySourceCreateMediator(value:XmlIdentitySourceCreateMediator):void {
        _xmlIdentitySourceCreateMediator = value;
    }

    public function get josso1ResourceCreateMediator():JOSSO1ResourceCreateMediator {
        return _josso1ResourceCreateMediator;
    }

    public function set josso1ResourceCreateMediator(value:JOSSO1ResourceCreateMediator):void {
        _josso1ResourceCreateMediator = value;
    }

    public function get josso2ResourceCreateMediator():JOSSO2ResourceCreateMediator {
        return _josso2ResourceCreateMediator;
    }

    public function set josso2ResourceCreateMediator(value:JOSSO2ResourceCreateMediator):void {
        _josso2ResourceCreateMediator = value;
    }

    public function get jbossExecutionEnvironmentCreateMediator():JBossExecutionEnvironmentCreateMediator {
        return _jbossExecutionEnvironmentCreateMediator;
    }

    public function set jbossExecutionEnvironmentCreateMediator(value:JBossExecutionEnvironmentCreateMediator):void {
        _jbossExecutionEnvironmentCreateMediator = value;
    }

    public function get weblogicExecutionEnvironmentCreateMediator():WeblogicExecutionEnvironmentCreateMediator {
        return _weblogicExecutionEnvironmentCreateMediator;
    }

    public function set weblogicExecutionEnvironmentCreateMediator(value:WeblogicExecutionEnvironmentCreateMediator):void {
        _weblogicExecutionEnvironmentCreateMediator = value;
    }

    public function get tomcatExecutionEnvironmentCreateMediator():TomcatExecutionEnvironmentCreateMediator {
        return _tomcatExecutionEnvironmentCreateMediator;
    }

    public function set tomcatExecutionEnvironmentCreateMediator(value:TomcatExecutionEnvironmentCreateMediator):void {
        _tomcatExecutionEnvironmentCreateMediator = value;
    }

    public function get jbossPortalResourceCreateMediator():JBossPortalResourceCreateMediator {
        return _jbossPortalResourceCreateMediator;
    }

    public function set jbossPortalResourceCreateMediator(value:JBossPortalResourceCreateMediator):void {
        _jbossPortalResourceCreateMediator = value;
    }

    public function get liferayPortalResourceCreateMediator():LiferayPortalResourceCreateMediator {
        return _liferayPortalResourceCreateMediator;
    }

    public function set liferayPortalResourceCreateMediator(value:LiferayPortalResourceCreateMediator):void {
        _liferayPortalResourceCreateMediator = value;
    }

    public function get wasceExecutionEnvironmentCreateMediator():WASCEExecutionEnvironmentCreateMediator {
        return _wasceExecutionEnvironmentCreateMediator;
    }

    public function set wasceExecutionEnvironmentCreateMediator(value:WASCEExecutionEnvironmentCreateMediator):void {
        _wasceExecutionEnvironmentCreateMediator = value;
    }

    public function get windowsIISExecutionEnvironmentCreateMediator():WindowsIISExecutionEnvironmentCreateMediator {
        return _windowsIISExecutionEnvironmentCreateMediator;
    }

    public function set windowsIISExecutionEnvironmentCreateMediator(value:WindowsIISExecutionEnvironmentCreateMediator):void {
        _windowsIISExecutionEnvironmentCreateMediator = value;
    }

    public function get apacheExecutionEnvironmentCreateMediator():ApacheExecutionEnvironmentCreateMediator {
        return _apacheExecutionEnvironmentCreateMediator;
    }

    public function set apacheExecutionEnvironmentCreateMediator(value:ApacheExecutionEnvironmentCreateMediator):void {
        _apacheExecutionEnvironmentCreateMediator = value;
    }

    public function get alfrescoResourceCreateMediator():AlfrescoResourceCreateMediator {
        return _alfrescoResourceCreateMediator;
    }

    public function set alfrescoResourceCreateMediator(value:AlfrescoResourceCreateMediator):void {
        _alfrescoResourceCreateMediator = value;
    }

    public function get javaEEExecutionEnvironmentCreateMediator():JavaEEExecutionEnvironmentCreateMediator {
        return _javaEEExecutionEnvironmentCreateMediator;
    }

    public function set javaEEExecutionEnvironmentCreateMediator(value:JavaEEExecutionEnvironmentCreateMediator):void {
        _javaEEExecutionEnvironmentCreateMediator = value;
    }

    public function get phpExecutionEnvironmentCreateMediator():PHPExecutionEnvironmentCreateMediator {
        return _phpExecutionEnvironmentCreateMediator;
    }

    public function set phpExecutionEnvironmentCreateMediator(value:PHPExecutionEnvironmentCreateMediator):void {
        _phpExecutionEnvironmentCreateMediator = value;
    }

    public function get phpBBResourceCreateMediator():PhpBBResourceCreateMediator {
        return _phpBBResourceCreateMediator;
    }

    public function set phpBBResourceCreateMediator(value:PhpBBResourceCreateMediator):void {
        _phpBBResourceCreateMediator = value;
    }

    public function get webserverExecutionEnvironmentCreateMediator():WebserverExecutionEnvironmentCreateMediator {
        return _webserverExecutionEnvironmentCreateMediator;
    }

    public function set webserverExecutionEnvironmentCreateMediator(value:WebserverExecutionEnvironmentCreateMediator):void {
        _webserverExecutionEnvironmentCreateMediator = value;
    }

    public function get activationCreateMediator():ActivationCreateMediator {
        return _activationCreateMediator;
    }

    public function set activationCreateMediator(value:ActivationCreateMediator):void {
        _activationCreateMediator = value;
    }

    public function get federatedConnectionCreateMediator():FederatedConnectionCreateMediator {
        return _federatedConnectionCreateMediator;
    }

    public function set federatedConnectionCreateMediator(value:FederatedConnectionCreateMediator):void {
        _federatedConnectionCreateMediator = value;
    }

    public function get exportIdentityApplianceMediator():ExportIdentityApplianceMediator {
        return _exportIdentityApplianceMediator;
    }

    public function set exportIdentityApplianceMediator(value:ExportIdentityApplianceMediator):void {
        _exportIdentityApplianceMediator = value;
    }

    public function get exportProviderCertificateMediator():ExportProviderCertificateMediator {
        return _exportProviderCertificateMediator;
    }

    public function set exportProviderCertificateMediator(value:ExportProviderCertificateMediator):void {
        _exportProviderCertificateMediator = value;
    }

    public function get exportMetadataMediator():ExportMetadataMediator {
        return _exportMetadataMediator;
    }

    public function set exportMetadataMediator(value:ExportMetadataMediator):void {
        _exportMetadataMediator = value;
    }

    public function get exportAgentConfigMediator():ExportAgentConfigMediator {
        return _exportAgentConfigMediator;
    }

    public function set exportAgentConfigMediator(value:ExportAgentConfigMediator):void {
        _exportAgentConfigMediator = value;
    }

    public function get activationMediator():ExecEnvActivationMediator {
        return _activationMediator;
    }

    public function set activationMediator(value:ExecEnvActivationMediator):void {
        _activationMediator = value;
    }

    public function get wikidCreateMediator():WikidCreateMediator {
        return _wikidCreateMediator;
    }

    public function set wikidCreateMediator(value:WikidCreateMediator):void {
        _wikidCreateMediator = value;
    }

    public function get dominoCreateMediator():DominoCreateMediator {
        return _dominoCreateMediator;
    }

    public function set dominoCreateMediator(value:DominoCreateMediator):void {
        _dominoCreateMediator = value;
    }

    public function get clientCertCreateMediator():ClientCertCreateMediator {
        return _clientCertCreateMediator;
    }

    public function set clientCertCreateMediator(value:ClientCertCreateMediator):void {
        _clientCertCreateMediator = value;
    }

    public function get jbosseppAuthenticationCreateMediator():JBossEPPAuthenticationServiceCreateMediator {
        return _jbosseppAuthenticationCreateMediator;
    }

    public function set jbosseppAuthenticationCreateMediator(value:JBossEPPAuthenticationServiceCreateMediator):void {
        _jbosseppAuthenticationCreateMediator = value;
    }

    public function get directoryServiceCreateMediator():DirectoryServiceCreateMediator {
        return _directoryServiceCreateMediator;
    }

    public function set directoryServiceCreateMediator(value:DirectoryServiceCreateMediator):void {
        _directoryServiceCreateMediator = value;
    }


    public function get windowsIntegratedAuthnCreateMediator():WindowsIntegratedAuthnCreateMediator {
        return _windowsIntegratedAuthnCreateMediator;
    }

    public function set windowsIntegratedAuthnCreateMediator(value:WindowsIntegratedAuthnCreateMediator):void {
        _windowsIntegratedAuthnCreateMediator = value;
    }


    public function get sharepointResourceCreateMediator():SharepointResourceCreateMediator {
        return _sharepointResourceCreateMediator;
    }

    public function set sharepointResourceCreateMediator(value:SharepointResourceCreateMediator):void {
        _sharepointResourceCreateMediator = value;
    }

    public function get coldfusionResourceCreateMediator():ColdfusionResourceCreateMediator {
        return _coldfusionResourceCreateMediator;
    }

    public function set coldfusionResourceCreateMediator(value:ColdfusionResourceCreateMediator):void {
        _coldfusionResourceCreateMediator = value;
    }

    public function get microStrategyResourceCreateMediator():MicroStrategyResourceCreateMediator {
        return _microStrategyResourceCreateMediator;
    }

    public function set microStrategyResourceCreateMediator(value:MicroStrategyResourceCreateMediator):void {
        _microStrategyResourceCreateMediator = value;
    }

    public function showSimpleSSOWizardWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSimpleSSOWizardView();
        showWizard(_simpleSSOWizardView);
    }

    private function createSimpleSSOWizardView():void {
        _simpleSSOWizardView = new SimpleSSOWizardView();
        _simpleSSOWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleSimpleSSOWizardViewCreated);
    }

    private function handleSimpleSSOWizardViewCreated(event:FlexEvent):void {
        simpleSSOWizardMediator.setViewComponent(_simpleSSOWizardView);
        simpleSSOWizardMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createIdentityApplianceWizardView();
        showWizard(_identityApplianceWizardView);
    }

    private function createIdentityApplianceWizardView():void {
        _identityApplianceWizardView = new IdentityApplianceWizardView();
        _identityApplianceWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityApplianceWizardViewCreated);
    }

    private function handleIdentityApplianceWizardViewCreated(event:FlexEvent):void {
        identityApplianceWizardMediator.setViewComponent(_identityApplianceWizardView);
        identityApplianceWizardMediator.handleNotification(_lastWindowNotification);
    }
    
    public function showCreateIdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createIdentityProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.idp");
        _popup.width = 690;
        _popup.height = 515;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_identityProviderCreateForm);
        //on show call bindForm()
    }

    private function createIdentityProviderCreateForm():void {
        _identityProviderCreateForm = new IdentityProviderCreateForm();
        _identityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderCreateFormCreated);
    }

    private function handleIdentityProviderCreateFormCreated(event:FlexEvent):void {
        identityProviderCreateMediator.setViewComponent(_identityProviderCreateForm);
        identityProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateInternalSaml2ServiceProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createInternalSaml2ServiceProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.saml2sp");
        _popup.width = 690;
        _popup.height = 455;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_internalSaml2ServiceProviderCreateForm);
        //on show call bindForm()
    }

    private function createInternalSaml2ServiceProviderCreateForm():void {
        _internalSaml2ServiceProviderCreateForm = new InternalSaml2ServiceProviderCreateForm();
        _internalSaml2ServiceProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleInternalSaml2ServiceProviderCreateFormCreated);
    }

    private function handleInternalSaml2ServiceProviderCreateFormCreated(event:FlexEvent):void {
        internalSaml2ServiceProviderCreateMediator.setViewComponent(_internalSaml2ServiceProviderCreateForm);
        internalSaml2ServiceProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExternalSaml2IdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExternalSaml2IdentityProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.externalidp");
        _popup.width = 410;
        _popup.height = 190;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_externalSaml2IdentityProviderCreateForm);
        //on show call bindForm()
    }

    private function createExternalSaml2IdentityProviderCreateForm():void {
        _externalSaml2IdentityProviderCreateForm = new ExternalSaml2IdentityProviderCreateForm();
        _externalSaml2IdentityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalSaml2IdentityProviderCreateFormCreated);
    }

    private function handleExternalSaml2IdentityProviderCreateFormCreated(event:FlexEvent):void {
        externalSaml2IdentityProviderCreateMediator.setViewComponent(_externalSaml2IdentityProviderCreateForm);
        externalSaml2IdentityProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExternalSaml2ServiceProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExternalSaml2ServiceProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.externalsp");
        _popup.width = 410;
        _popup.height = 190;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_externalSaml2ServiceProviderCreateForm);
    }

    private function createExternalSaml2ServiceProviderCreateForm():void {
        _externalSaml2ServiceProviderCreateForm = new ExternalSaml2ServiceProviderCreateForm();
        _externalSaml2ServiceProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalSaml2ServiceProviderCreateFormCreated);
    }

    private function handleExternalSaml2ServiceProviderCreateFormCreated(event:FlexEvent):void {
        externalSaml2ServiceProviderCreateMediator.setViewComponent(_externalSaml2ServiceProviderCreateForm);
        externalSaml2ServiceProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExternalOpenIDIdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExternalOpenIDIdentityProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.openIDidp");
        _popup.width = 660;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_externalOpenIDIdentityProviderCreateForm);
        //on show call bindForm()
    }

    private function createExternalOpenIDIdentityProviderCreateForm():void {
        _externalOpenIDIdentityProviderCreateForm = new ExternalOpenIDIdentityProviderCreateForm();
        _externalOpenIDIdentityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalOpenIDIdentityProviderCreateFormCreated);
    }

    private function handleExternalOpenIDIdentityProviderCreateFormCreated(event:FlexEvent):void {
        externalOpenIDIdentityProviderCreateMediator.setViewComponent(_externalOpenIDIdentityProviderCreateForm);
        externalOpenIDIdentityProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateOAuth2IdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createOAuth2IdentityProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.oauth2idp");
        _popup.width = 660;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_oauth2IdentityProviderCreateForm);
        //on show call bindForm()
    }

    private function createOAuth2IdentityProviderCreateForm():void {
        _oauth2IdentityProviderCreateForm = new OAuth2IdentityProviderCreateForm();
        _oauth2IdentityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleOAuth2IdentityProviderCreateFormCreated);
    }

    private function handleOAuth2IdentityProviderCreateFormCreated(event:FlexEvent):void {
        oauth2IdentityProviderCreateMediator.setViewComponent(_oauth2IdentityProviderCreateForm);
        oauth2IdentityProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateOAuth2ServiceProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createOAuth2ServiceProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.oauth2sp");
        _popup.width = 660;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_oauth2ServiceProviderCreateForm);
        //on show call bindForm()
    }

    private function createOAuth2ServiceProviderCreateForm():void {
        _oauth2ServiceProviderCreateForm = new OAuth2ServiceProviderCreateForm();
        _oauth2ServiceProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleOAuth2ServiceProviderCreateFormCreated);
    }

    private function handleOAuth2ServiceProviderCreateFormCreated(event:FlexEvent):void {
        oauth2ServiceProviderCreateMediator.setViewComponent(_oauth2ServiceProviderCreateForm);
        oauth2ServiceProviderCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateSalesforceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSalesforceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.salesforce");
        _popup.width = 410;
        _popup.height = 220;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_salesforceCreateForm);
    }

    private function createSalesforceCreateForm():void {
        _salesforceCreateForm = new SalesforceCreateForm();
        _salesforceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSalesforceCreateFormCreated);
    }

    private function handleSalesforceCreateFormCreated(event:FlexEvent):void {
        salesforceCreateMediator.setViewComponent(_salesforceCreateForm);
        salesforceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateGoogleAppsWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createGoogleAppsCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.googleaps");
        _popup.width = 410;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_googleAppsCreateForm);
    }

    private function createGoogleAppsCreateForm():void {
        _googleAppsCreateForm = new GoogleAppsCreateForm();
        _googleAppsCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleGoogleAppsCreateFormCreated);
    }

    private function handleGoogleAppsCreateFormCreated(event:FlexEvent):void {
        googleAppsCreateMediator.setViewComponent(_googleAppsCreateForm);
        googleAppsCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateSugarCRMWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSugarCRMCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.sugarcrm");
        _popup.width = 410;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_sugarCRMCreateForm);
    }

    private function createSugarCRMCreateForm():void {
        _sugarCRMCreateForm = new SugarCRMCreateForm();
        _sugarCRMCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSugarCRMCreateFormCreated);
    }

    private function handleSugarCRMCreateFormCreated(event:FlexEvent):void {
        sugarCRMCreateMediator.setViewComponent(_sugarCRMCreateForm);
        sugarCRMCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateIdentityVaultWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createIdentityVaultCreateForm();
        _popup.title = "Create Identity Vault";
        _popup.width = 410;
        _popup.height = 140;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_identityVaultCreateForm);
    }

    private function createIdentityVaultCreateForm():void {
        _identityVaultCreateForm = new IdentityVaultCreateForm();
        _identityVaultCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityVaultCreateFormCreated);
    }

    private function handleIdentityVaultCreateFormCreated(event:FlexEvent):void {
        identityVaultCreateMediator.setViewComponent(_identityVaultCreateForm);
        identityVaultCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateDbIdentitySourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createDbIdentitySourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.dbsource");
        _popup.width = 540;
        _popup.height = 350;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_dbIdentitySourceCreateForm);
    }

    private function createDbIdentitySourceCreateForm():void {
        _dbIdentitySourceCreateForm = new DbIdentitySourceCreateForm();
        _dbIdentitySourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleDbIdentitySourceCreateFormCreated);
    }

    private function handleDbIdentitySourceCreateFormCreated(event:FlexEvent):void {
        dbIdentitySourceCreateMediator.setViewComponent(_dbIdentitySourceCreateForm);
        dbIdentitySourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateLdapIdentitySourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createLdapIdentitySourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.ldapsource");
        _popup.width = 500;
        _popup.height = 370;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_ldapIdentitySourceCreateForm);
    }

    private function createLdapIdentitySourceCreateForm():void {
        _ldapIdentitySourceCreateForm = new LdapIdentitySourceCreateForm();
        _ldapIdentitySourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleLdapIdentitySourceCreateFormCreated);
    }

    private function handleLdapIdentitySourceCreateFormCreated(event:FlexEvent):void {
        ldapIdentitySourceCreateMediator.setViewComponent(_ldapIdentitySourceCreateForm);
        ldapIdentitySourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateXmlIdentitySourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createXmlIdentitySourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.xmlsource");
        _popup.width = 410;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_xmlIdentitySourceCreateForm);
    }

    private function createXmlIdentitySourceCreateForm():void {
        _xmlIdentitySourceCreateForm = new XmlIdentitySourceCreateForm();
        _xmlIdentitySourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleXmlIdentitySourceCreateFormCreated);
    }

    private function handleXmlIdentitySourceCreateFormCreated(event:FlexEvent):void {
        xmlIdentitySourceCreateMediator.setViewComponent(_xmlIdentitySourceCreateForm);
        xmlIdentitySourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateJOSSO1ResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJOSSO1ResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.josso1Resource");
        _popup.width = 800;
        _popup.height = 200;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_josso1ResourceCreateForm);
        //on show call bindForm()
    }

    private function createJOSSO1ResourceCreateForm():void {
        _josso1ResourceCreateForm = new JOSSO1ResourceCreateForm();
        _josso1ResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJOSSO1ResourceCreateFormCreated);
    }

    private function handleJOSSO1ResourceCreateFormCreated(event:FlexEvent):void {
        josso1ResourceCreateMediator.setViewComponent(_josso1ResourceCreateForm);
        josso1ResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateJOSSO2ResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJOSSO2ResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.josso2Resource");
        _popup.width = 800;
        _popup.height = 170;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_josso2ResourceCreateForm);
        //on show call bindForm()
    }

    private function createJOSSO2ResourceCreateForm():void {
        _josso2ResourceCreateForm = new JOSSO2ResourceCreateForm();
        _josso2ResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJOSSO2ResourceCreateFormCreated);
    }

    private function handleJOSSO2ResourceCreateFormCreated(event:FlexEvent):void {
        josso2ResourceCreateMediator.setViewComponent(_josso2ResourceCreateForm);
        josso2ResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateWeblogicExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createWeblogicExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.weblogic");
        _popup.width = 500;
        _popup.height = 320;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_weblogicExecutionEnvironmentCreateForm);
    }

    private function createWeblogicExecutionEnvironmentCreateForm():void {
        _weblogicExecutionEnvironmentCreateForm = new WeblogicExecutionEnvironmentCreateForm();
        _weblogicExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleWeblogicExecutionEnvironmentCreateFormCreated);
    }

    private function handleWeblogicExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        weblogicExecutionEnvironmentCreateMediator.setViewComponent(_weblogicExecutionEnvironmentCreateForm);
        weblogicExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateTomcatExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createTomcatExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.tomcat");
        _popup.width = 500;
        _popup.height = 290;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_tomcatExecutionEnvironmentCreateForm);
    }

    private function createTomcatExecutionEnvironmentCreateForm():void {
        _tomcatExecutionEnvironmentCreateForm = new TomcatExecutionEnvironmentCreateForm();
        _tomcatExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleTomcatExecutionEnvironmentCreateFormCreated);
    }

    private function handleTomcatExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        tomcatExecutionEnvironmentCreateMediator.setViewComponent(_tomcatExecutionEnvironmentCreateForm);
        tomcatExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateJBossPortalResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJBossPortalResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.jbportal");
        _popup.width = 500;
        _popup.height = 260;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_jbossPortalResourceCreateForm);
    }

    private function createJBossPortalResourceCreateForm():void {
        _jbossPortalResourceCreateForm = new JBossPortalResourceCreateForm();
        _jbossPortalResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJBossPortalResourceCreateFormCreated);
    }

    private function handleJBossPortalResourceCreateFormCreated(event:FlexEvent):void {
        jbossPortalResourceCreateMediator.setViewComponent(_jbossPortalResourceCreateForm);
        jbossPortalResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateLiferayPortalResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createLiferayPortalResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.liferay");
        _popup.width = 500;
        _popup.height = 310;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_liferayPortalResourceCreateForm);
    }

    private function createLiferayPortalResourceCreateForm():void {
        _liferayPortalResourceCreateForm = new LiferayPortalResourceCreateForm();
        _liferayPortalResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleLiferayPortalResourceCreateFormCreated);
    }

    private function handleLiferayPortalResourceCreateFormCreated(event:FlexEvent):void {
        liferayPortalResourceCreateMediator.setViewComponent(_liferayPortalResourceCreateForm);
        liferayPortalResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateWASCEExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createWASCEExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.wasce");
        _popup.width = 500;
        _popup.height = 260;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_wasceExecutionEnvironmentCreateForm);
    }

    private function createWASCEExecutionEnvironmentCreateForm():void {
        _wasceExecutionEnvironmentCreateForm = new WASCEExecutionEnvironmentCreateForm();
        _wasceExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleWASCEExecutionEnvironmentCreateFormCreated);
    }

    private function handleWASCEExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        wasceExecutionEnvironmentCreateMediator.setViewComponent(_wasceExecutionEnvironmentCreateForm);
        wasceExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateApacheExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createApacheExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.apache");
        _popup.width = 495;//500
        _popup.height = 220; //260
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_apacheExecutionEnvironmentCreateForm);
    }

    private function createApacheExecutionEnvironmentCreateForm():void {
        _apacheExecutionEnvironmentCreateForm = new ApacheExecutionEnvironmentCreateForm();
        _apacheExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleApacheExecutionEnvironmentCreateFormCreated);
    }

    private function handleApacheExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        apacheExecutionEnvironmentCreateMediator.setViewComponent(_apacheExecutionEnvironmentCreateForm);
        apacheExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateWindowsIISExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createWindowsIISExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.iis");
        _popup.width = 500;//500
        _popup.height = 320; //260
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_windowsIISExecutionEnvironmentCreateForm);
    }

    private function createWindowsIISExecutionEnvironmentCreateForm():void {
        _windowsIISExecutionEnvironmentCreateForm = new WindowsIISExecutionEnvironmentCreateForm();
        _windowsIISExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleWindowsIISExecutionEnvironmentCreateFormCreated);
    }

    private function handleWindowsIISExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        windowsIISExecutionEnvironmentCreateMediator.setViewComponent(_windowsIISExecutionEnvironmentCreateForm);
        windowsIISExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateAlfrescoResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createAlfrescoResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.alfresco");
        _popup.width = 500;
        _popup.height = 290;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_alfrescoResourceCreateForm);
    }

    private function createAlfrescoResourceCreateForm():void {
        _alfrescoResourceCreateForm = new AlfrescoResourceCreateForm();
        _alfrescoResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleAlfrescoResourceCreateFormCreated);
    }

    private function handleAlfrescoResourceCreateFormCreated(event:FlexEvent):void {
        alfrescoResourceCreateMediator.setViewComponent(_alfrescoResourceCreateForm);
        alfrescoResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateJavaEEExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJavaEEExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.javaee");
        _popup.width = 495;//500
        _popup.height = 220; //260
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_javaEEExecutionEnvironmentCreateForm);
    }

    private function createJavaEEExecutionEnvironmentCreateForm():void {
        _javaEEExecutionEnvironmentCreateForm = new JavaEEExecutionEnvironmentCreateForm();
        _javaEEExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJavaEEExecutionEnvironmentCreateFormCreated);
    }

    private function handleJavaEEExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        javaEEExecutionEnvironmentCreateMediator.setViewComponent(_javaEEExecutionEnvironmentCreateForm);
        javaEEExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreatePHPExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createPHPExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.php");
        _popup.width = 495;
        _popup.height = 220;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_phpExecutionEnvironmentCreateForm);
    }

    private function createPHPExecutionEnvironmentCreateForm():void {
        _phpExecutionEnvironmentCreateForm = new PHPExecutionEnvironmentCreateForm();
        _phpExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handlePHPExecutionEnvironmentCreateFormCreated);
    }

    private function handlePHPExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        phpExecutionEnvironmentCreateMediator.setViewComponent(_phpExecutionEnvironmentCreateForm);
        phpExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreatePhpBBResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createPhpBBResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.phpbb");
        _popup.width = 500;
        _popup.height = 290;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_phpBBResourceCreateForm);
    }

    private function createPhpBBResourceCreateForm():void {
        _phpBBResourceCreateForm = new PhpBBResourceCreateForm();
        _phpBBResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handlePhpBBResourceCreateFormCreated);
    }

    private function handlePhpBBResourceCreateFormCreated(event:FlexEvent):void {
        phpBBResourceCreateMediator.setViewComponent(_phpBBResourceCreateForm);
        phpBBResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateWebserverExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createWebserverExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.web");
        _popup.width = 495;//500
        _popup.height = 240; //260
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_webserverExecutionEnvironmentCreateForm);
    }


    private function createWebserverExecutionEnvironmentCreateForm():void {
        _webserverExecutionEnvironmentCreateForm = new WebserverExecutionEnvironmentCreateForm();
        _webserverExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleWebserverExecutionEnvironmentCreateFormCreated);
    }

    private function handleWebserverExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        webserverExecutionEnvironmentCreateMediator.setViewComponent(_webserverExecutionEnvironmentCreateForm);
        webserverExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateSharepointResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSharepointResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.sharepoint2010");
        _popup.width = 800;
        _popup.height = 350;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_sharepoint2010ResourceCreateForm);
    }

    private function createSharepointResourceCreateForm():void {
        _sharepoint2010ResourceCreateForm = new SharepointResourceCreateForm();
        _sharepoint2010ResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSharepointResourceCreateFormCreated);
    }

    private function handleSharepointResourceCreateFormCreated(event:FlexEvent):void {
        sharepointResourceCreateMediator.setViewComponent(_sharepoint2010ResourceCreateForm);
        sharepointResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateColdfusionResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createColdfusionResourceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.coldfusion");
        _popup.width = 500;
        _popup.height = 260;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_coldfusionExecutionEnvironmentCreateForm);
    }

    private function createColdfusionResourceCreateForm():void {
        _coldfusionExecutionEnvironmentCreateForm = new ColdfusionResourceCreateForm();
        _coldfusionExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleColdfusionResourceCreateFormCreated);
    }

    private function handleColdfusionResourceCreateFormCreated(event:FlexEvent):void {
        coldfusionResourceCreateMediator.setViewComponent(_coldfusionExecutionEnvironmentCreateForm);
        coldfusionResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateMicroStrategyResourceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createMicroStrategyExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.microstrategy");
        _popup.width = 800;
        _popup.height = 200;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_microStrategyResourceCreateForm);
    }

    private function createMicroStrategyExecutionEnvironmentCreateForm():void {
        _microStrategyResourceCreateForm = new MicroStrategyResourceCreateForm();
        _microStrategyResourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleMicroStrategyResourceCreateFormCreated);
    }

    private function handleMicroStrategyResourceCreateFormCreated(event:FlexEvent):void {
        microStrategyResourceCreateMediator.setViewComponent(_microStrategyResourceCreateForm);
        microStrategyResourceCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateFederatedConnectionWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createFederatedConnectionCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.federated.connection");
        _popup.width = 640;
        _popup.height = 510;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_federatedConnectionCreateForm);
    }

    private function createFederatedConnectionCreateForm():void {
        _federatedConnectionCreateForm = new FederatedConnectionCreateForm();
        _federatedConnectionCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleFederatedConnectionCreateFormCreated);
    }

    private function handleFederatedConnectionCreateFormCreated(event:FlexEvent):void {
        federatedConnectionCreateMediator.setViewComponent(_federatedConnectionCreateForm);
        federatedConnectionCreateMediator.handleNotification(_lastWindowNotification);
        federatedConnectionCreateMediator.registerListeners();
    }

    public function showCreateActivationWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createActivationCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.josso.activation");
        _popup.width = 800;
        _popup.height = 200;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_activationCreateForm);
    }

    private function createActivationCreateForm():void {
        _activationCreateForm = new ActivationCreateForm();
        _activationCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleActivationCreateFormCreated);
    }

    private function handleActivationCreateFormCreated(event:FlexEvent):void {
        activationCreateMediator.setViewComponent(_activationCreateForm);
        activationCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateJBossExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJBossExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.jboss");
        _popup.width = 500;
        _popup.height = 320;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_jbossExecutionEnvironmentCreateForm);
    }

    private function createJBossExecutionEnvironmentCreateForm():void {
        _jbossExecutionEnvironmentCreateForm = new JBossExecutionEnvironmentCreateForm();
        _jbossExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJBossExecutionEnvironmentCreateFormCreated);
    }

    private function handleJBossExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        jbossExecutionEnvironmentCreateMediator.setViewComponent(_jbossExecutionEnvironmentCreateForm);
        jbossExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showManageCertificateWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createManageCertificateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.cert");
        _popup.width = 400;
        _popup.height = 480;
        showPopup(_manageCertificateForm);
    }

    private function createManageCertificateForm():void {
        _manageCertificateForm = new ManageCertificateView();
        _manageCertificateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleManageCertificateFormCreated);
    }

    private function handleManageCertificateFormCreated(event:FlexEvent):void {
        manageCertificateMediator.setViewComponent(_manageCertificateForm);
        manageCertificateMediator.handleNotification(_lastWindowNotification);
    }

    public function showUploadProgressWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createUploadProgressWindow();
        _progress.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.file.upload");
        _progress.width = 300;
        _progress.height = 140;
        //_progress.x = (_popupParent.width / 2) - 225;
        //_progress.y = 80;
        showProgress(_uploadProgress);
    }

    private function createUploadProgressWindow():void {
        _uploadProgress = new UploadProgress();
        _uploadProgress.addEventListener(FlexEvent.CREATION_COMPLETE, handleUploadProgressWindowCreated);
    }

    private function handleUploadProgressWindowCreated(event:FlexEvent):void {
        uploadProgressMediator.setViewComponent(_uploadProgress);
        uploadProgressMediator.handleNotification(_lastWindowNotification);
    }

    public function showBuildIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createBuildApplianceWindow();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.build.appliance");
        _popup.width = 430;
        _popup.height = 230;
        //_progress.x = (_popupParent.width / 2) - 225;
        //_progress.y = 80;
        showPopup(_buildAppliance);
    }

    private function createBuildApplianceWindow():void {
        _buildAppliance = new BuildApplianceView();
        _buildAppliance.addEventListener(FlexEvent.CREATION_COMPLETE, handleBuildApplianceWindowCreated);
    }

    private function handleBuildApplianceWindowCreated(event:FlexEvent):void {
        buildApplianceMediator.setViewComponent(_buildAppliance);
        buildApplianceMediator.handleNotification(_lastWindowNotification);
    }

    public function showDeployIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createDeployApplianceWindow();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.deploy.appliance");
        _popup.width = 430;
        _popup.height = 230;
        //_progress.x = (_popupParent.width / 2) - 225;
        //_progress.y = 80;
        showPopup(_deployAppliance);
    }

    private function createDeployApplianceWindow():void {
        _deployAppliance = new DeployApplianceView();
        _deployAppliance.addEventListener(FlexEvent.CREATION_COMPLETE, handleDeployApplianceWindowCreated);
    }

    private function handleDeployApplianceWindowCreated(event:FlexEvent):void {
        deployApplianceMediator.setViewComponent(_deployAppliance);
        deployApplianceMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExportIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExportIdentityApplianceView();
        _progress.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.export.appliance");
        _progress.width = 300;
        _progress.height = 150;
//        _popup.x = (_popupParent.width / 2) - 225;
//        _popup.y = 80;
        showProgress(_exportIdentityApplianceView);
    }

    private function createExportIdentityApplianceView():void {
        _exportIdentityApplianceView = new ExportIdentityApplianceView();
        _exportIdentityApplianceView.addEventListener(FlexEvent.CREATION_COMPLETE, handleExportIdentityApplianceViewCreated);
    }

    private function handleExportIdentityApplianceViewCreated(event:FlexEvent):void {
        exportIdentityApplianceMediator.setViewComponent(_exportIdentityApplianceView);
        exportIdentityApplianceMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExportProviderCertificateWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExportProviderCertificateView();
        _progress.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.export.prov.cert");
        _progress.width = 300;
        _progress.height = 150;
//        _popup.x = (_popupParent.width / 2) - 225;
//        _popup.y = 80;
        showProgress(_exportProviderCertificateView);
    }

    private function createExportProviderCertificateView():void {
        _exportProviderCertificateView = new ExportProviderCertificateView();
        _exportProviderCertificateView.addEventListener(FlexEvent.CREATION_COMPLETE, handleExportProviderCertificateViewCreated);
    }

    private function handleExportProviderCertificateViewCreated(event:FlexEvent):void {
        exportProviderCertificateMediator.setViewComponent(_exportProviderCertificateView);
        exportProviderCertificateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExportMetadataWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExportMetadataView();
        _progress.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.export.saml.meta");
        _progress.width = 300;
        _progress.height = 150;
//        _popup.x = (_popupParent.width / 2) - 225;
//        _popup.y = 80;
        showProgress(_exportMetadataView);
    }

    private function createExportMetadataView():void {
        _exportMetadataView = new ExportMetadataView();
        _exportMetadataView.addEventListener(FlexEvent.CREATION_COMPLETE, handleExportMetadataViewCreated);
    }

    private function handleExportMetadataViewCreated(event:FlexEvent):void {
        exportMetadataMediator.setViewComponent(_exportMetadataView);
        exportMetadataMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExportAgentConfigWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExportAgentConfigView();
        _progress.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.export.execEnv.config");
        _progress.width = 300;
        _progress.height = 150;
//        _popup.x = (_popupParent.width / 2) - 225;
//        _popup.y = 80;
        showProgress(_exportAgentConfigView);
    }

    private function createExportAgentConfigView():void {
        _exportAgentConfigView = new ExportAgentConfigView();
        _exportAgentConfigView.addEventListener(FlexEvent.CREATION_COMPLETE, handleExportAgentConfigViewCreated);
    }

    private function handleExportAgentConfigViewCreated(event:FlexEvent):void {
        exportAgentConfigMediator.setViewComponent(_exportAgentConfigView);
        exportAgentConfigMediator.handleNotification(_lastWindowNotification);
    }

    public function showActivationWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createActivationView();
        _progress.title = resourceManager.getString(AtricoreConsole.BUNDLE, "activation.confirm.title");
        _progress.width = 520;
        _progress.height = 130;
//        _popup.x = (_popupParent.width / 2) - 225;
//        _popup.y = 80;
        showProgress(_activationView);
    }

    private function createActivationView():void {
        _activationView = new ExecEnvActivationView();
        _activationView.addEventListener(FlexEvent.CREATION_COMPLETE, handleActivationViewCreated);
    }

    private function handleActivationViewCreated(event:FlexEvent):void {
        activationMediator.setViewComponent(_activationView);
        activationMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateWikidWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createWikidCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.wikid");
        _popup.width = 510;
        _popup.height = 375;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_wikidCreateForm);
    }

    private function createWikidCreateForm():void {
        _wikidCreateForm = new WikidCreateForm();
        _wikidCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleWikidCreateFormCreated);
    }

    private function handleWikidCreateFormCreated(event:FlexEvent):void {
        wikidCreateMediator.setViewComponent(_wikidCreateForm);
        wikidCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateDominoWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createDominoCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.dominoAuthn");
        _popup.width = 510;
        _popup.height = 225;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_dominoCreateForm);
    }

    private function createDominoCreateForm():void {
        _dominoCreateForm = new DominoCreateForm();
        _dominoCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleDominoCreateFormCreated);
    }

    private function handleDominoCreateFormCreated(event:FlexEvent):void {
        dominoCreateMediator.setViewComponent(_dominoCreateForm);
        dominoCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateClientCertWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createClientCertCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.clientCertAuthn");
        _popup.width = 510;
        _popup.height = 225;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_clientCertCreateForm);
    }

    private function createClientCertCreateForm():void {
        _clientCertCreateForm = new ClientCertCreateForm();
        _clientCertCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleClientCertCreateFormCreated);
    }

    private function handleClientCertCreateFormCreated(event:FlexEvent):void {
        clientCertCreateMediator.setViewComponent(_clientCertCreateForm);
        clientCertCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateJBossEPPAuthenticationServiceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJBossEPPAuthenticationServiceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.jbosseppAuthentication");
        _popup.width = 510;
        _popup.height = 225;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_jbossEppIdentitySourceCreateForm);
    }

    private function createJBossEPPAuthenticationServiceCreateForm():void {
        _jbossEppIdentitySourceCreateForm = new JBossEPPAuthenticationServiceCreateForm();
        _jbossEppIdentitySourceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJBossEPPAuthenticationServiceWindowCreateFormCreated);
    }

    private function handleJBossEPPAuthenticationServiceWindowCreateFormCreated(event:FlexEvent):void {
        jbosseppAuthenticationCreateMediator.setViewComponent(_jbossEppIdentitySourceCreateForm);
        jbosseppAuthenticationCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateDirectoryServiceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createDirectoryServiceCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.directoryService");
        _popup.width = 510;
        _popup.height = 405;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_directoryServiceCreateForm);
    }

    public function showCreateWindowsIntegratedAuthnWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createWindowsIntegratedAuthnCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.windowsIntegratedAuthn");
        _popup.width = 510;
        _popup.height = 485;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_windowsIntegratedAuthnCreateForm);
    }


    private function createDirectoryServiceCreateForm():void {
        _directoryServiceCreateForm = new DirectoryServiceCreateForm();
        _directoryServiceCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleDirectoryServiceCreateFormCreated);
    }

    private function createWindowsIntegratedAuthnCreateForm():void {
        _windowsIntegratedAuthnCreateForm = new WindowsIntegratedAuthnCreateForm();
        _windowsIntegratedAuthnCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleWindowsIntegratedAuthnCreateFormCreated);
    }


    private function handleDirectoryServiceCreateFormCreated(event:FlexEvent):void {
        directoryServiceCreateMediator.setViewComponent(_directoryServiceCreateForm);
        directoryServiceCreateMediator.handleNotification(_lastWindowNotification);
    }

    private function handleWindowsIntegratedAuthnCreateFormCreated(event:FlexEvent):void {
        windowsIntegratedAuthnCreateMediator.setViewComponent(_windowsIntegratedAuthnCreateForm);
        windowsIntegratedAuthnCreateMediator.handleNotification(_lastWindowNotification);
    }

}
}