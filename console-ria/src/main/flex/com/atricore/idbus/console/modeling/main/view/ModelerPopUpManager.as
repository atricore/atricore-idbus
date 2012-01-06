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
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.wikid.WikidCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.wikid.WikidCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.windows.WindowsIntegratedAuthnCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.windows.WindowsIntegratedAuthnCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.dbidentitysource.DbIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.dbidentitysource.DbIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.alfresco.AlfrescoExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.alfresco.AlfrescoExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache.ApacheExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache.ApacheExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.javaee.JavaEEExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.javaee.JavaEEExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jboss.JBossExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jboss.JBossExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jbossportal.JBossPortalExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jbossportal.JBossPortalExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.liferayportal.LiferayPortalExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.liferayportal.LiferayPortalExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.php.PHPExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.php.PHPExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.phpbb.PhpBBExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.phpbb.PhpBBExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.sharepoint2010.Sharepoint2010ExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.sharepoint2010.Sharepoint2010ExecutionEnvironmentCreateMediator;
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
import com.atricore.idbus.console.modeling.diagram.view.externalidp.ExternalIdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.externalidp.ExternalIdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.externalsp.ExternalServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.externalsp.ExternalServiceProviderCreateMediator;
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
import com.atricore.idbus.console.modeling.diagram.view.salesforce.SalesforceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.salesforce.SalesforceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.sp.ServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.sp.ServiceProviderCreateMediator;
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
    private var _identityProviderMediator:IdentityProviderCreateMediator;
    private var _serviceProviderMediator:ServiceProviderCreateMediator;
    private var _externalIdentityProviderMediator:ExternalIdentityProviderCreateMediator;
    private var _externalServiceProviderMediator:ExternalServiceProviderCreateMediator;
    private var _salesforceMediator:SalesforceCreateMediator;
    private var _googleAppsMediator:GoogleAppsCreateMediator;
    private var _sugarCRMMediator:SugarCRMCreateMediator;
    private var _identityVaultCreateMediator:IdentityVaultCreateMediator;
    private var _dbIdentitySourceCreateMediator:DbIdentitySourceCreateMediator;
    private var _ldapIdentitySourceCreateMediator:LdapIdentitySourceCreateMediator;
    private var _xmlIdentitySourceCreateMediator:XmlIdentitySourceCreateMediator;
    private var _jbossExecutionEnvironmentCreateMediator:JBossExecutionEnvironmentCreateMediator;
    private var _weblogicExecutionEnvironmentCreateMediator:WeblogicExecutionEnvironmentCreateMediator;
    private var _tomcatExecutionEnvironmentCreateMediator:TomcatExecutionEnvironmentCreateMediator;
    private var _jbossPortalExecutionEnvironmentCreateMediator:JBossPortalExecutionEnvironmentCreateMediator;
    private var _liferayPortalExecutionEnvironmentCreateMediator:LiferayPortalExecutionEnvironmentCreateMediator;
    private var _wasceExecutionEnvironmentCreateMediator:WASCEExecutionEnvironmentCreateMediator;
    private var _apacheExecutionEnvironmentCreateMediator:ApacheExecutionEnvironmentCreateMediator;
    private var _windowsIISExecutionEnvironmentCreateMediator:WindowsIISExecutionEnvironmentCreateMediator;
	private var _alfrescoExecutionEnvironmentCreateMediator:AlfrescoExecutionEnvironmentCreateMediator;
    private var _javaEEExecutionEnvironmentCreateMediator:JavaEEExecutionEnvironmentCreateMediator;
    private var _phpExecutionEnvironmentCreateMediator:PHPExecutionEnvironmentCreateMediator;
    private var _phpBBExecutionEnvironmentCreateMediator:PhpBBExecutionEnvironmentCreateMediator;
    private var _webserverExecutionEnvironmentCreateMediator:WebserverExecutionEnvironmentCreateMediator;
    private var _uploadProgressMediator:UploadProgressMediator;
    private var _buildApplianceMediator:BuildApplianceMediator;
    private var _deployApplianceMediator:DeployApplianceMediator;
    private var _activationCreateMediator:ActivationCreateMediator;
    private var _federatedConnectionCreateMediator:FederatedConnectionCreateMediator;
    private var _exportIdentityApplianceMediator:ExportIdentityApplianceMediator;
    private var _exportProviderCertificateMediator:ExportProviderCertificateMediator;
    private var _exportMetadataMediator:ExportMetadataMediator;
    private var _activationMediator:ExecEnvActivationMediator;
    private var _wikidCreateMediator:WikidCreateMediator;
    private var _directoryServiceCreateMediator:DirectoryServiceCreateMediator;
    private var _windowsIntegratedAuthnCreateMediator:WindowsIntegratedAuthnCreateMediator;
    private var _sharepoint2010ExecutionEnvironmentCreateMediator:Sharepoint2010ExecutionEnvironmentCreateMediator;

    // views
    private var _simpleSSOWizardView:SimpleSSOWizardView;
    private var _identityApplianceWizardView:IdentityApplianceWizardView;
    private var _identityProviderCreateForm:IdentityProviderCreateForm;
    private var _serviceProviderCreateForm:ServiceProviderCreateForm;
    private var _externalIdentityProviderCreateForm:ExternalIdentityProviderCreateForm;
    private var _externalServiceProviderCreateForm:ExternalServiceProviderCreateForm;
    private var _salesforceCreateForm:SalesforceCreateForm;
    private var _googleAppsCreateForm:GoogleAppsCreateForm;
    private var _sugarCRMCreateForm:SugarCRMCreateForm;
    private var _identityVaultCreateForm:IdentityVaultCreateForm;
    private var _dbIdentitySourceCreateForm:DbIdentitySourceCreateForm;
    private var _ldapIdentitySourceCreateForm:LdapIdentitySourceCreateForm;
    private var _xmlIdentitySourceCreateForm:XmlIdentitySourceCreateForm;
    private var _jbossExecutionEnvironmentCreateForm:JBossExecutionEnvironmentCreateForm;
    private var _weblogicExecutionEnvironmentCreateForm:WeblogicExecutionEnvironmentCreateForm;
    private var _tomcatExecutionEnvironmentCreateForm:TomcatExecutionEnvironmentCreateForm;
    private var _jbossPortalExecutionEnvironmentCreateForm:JBossPortalExecutionEnvironmentCreateForm;
    private var _liferayPortalExecutionEnvironmentCreateForm:LiferayPortalExecutionEnvironmentCreateForm;
    private var _wasceExecutionEnvironmentCreateForm:WASCEExecutionEnvironmentCreateForm;
    private var _apacheExecutionEnvironmentCreateForm:ApacheExecutionEnvironmentCreateForm;
    private var _windowsIISExecutionEnvironmentCreateForm:WindowsIISExecutionEnvironmentCreateForm;
    private var _alfrescoExecutionEnvironmentCreateForm:AlfrescoExecutionEnvironmentCreateForm;
    private var _javaEEExecutionEnvironmentCreateForm:JavaEEExecutionEnvironmentCreateForm;
    private var _phpExecutionEnvironmentCreateForm:PHPExecutionEnvironmentCreateForm;
    private var _phpBBExecutionEnvironmentCreateForm:PhpBBExecutionEnvironmentCreateForm;
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
    private var _activationView:ExecEnvActivationView;
    private var _wikidCreateForm:WikidCreateForm;
    private var _directoryServiceCreateForm:DirectoryServiceCreateForm;
    private var _windowsIntegratedAuthnCreateForm:WindowsIntegratedAuthnCreateForm;
    private var _sharepoint2010ExecutionEnvironmentCreateForm:Sharepoint2010ExecutionEnvironmentCreateForm;

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

    public function get identityProviderMediator():IdentityProviderCreateMediator {
        return _identityProviderMediator;
    }

    public function set identityProviderMediator(value:IdentityProviderCreateMediator):void {
        _identityProviderMediator = value;
    }

    public function get serviceProviderMediator():ServiceProviderCreateMediator {
        return _serviceProviderMediator;
    }

    public function set serviceProviderMediator(value:ServiceProviderCreateMediator):void {
        _serviceProviderMediator = value;
    }

    public function get externalIdentityProviderMediator():ExternalIdentityProviderCreateMediator {
        return _externalIdentityProviderMediator;
    }

    public function set externalIdentityProviderMediator(value:ExternalIdentityProviderCreateMediator):void {
        _externalIdentityProviderMediator = value;
    }

    public function get externalServiceProviderMediator():ExternalServiceProviderCreateMediator {
        return _externalServiceProviderMediator;
    }

    public function set externalServiceProviderMediator(value:ExternalServiceProviderCreateMediator):void {
        _externalServiceProviderMediator = value;
    }

    public function get salesforceMediator():SalesforceCreateMediator {
        return _salesforceMediator;
    }

    public function set salesforceMediator(value:SalesforceCreateMediator):void {
        _salesforceMediator = value;
    }

    public function get googleAppsMediator():GoogleAppsCreateMediator {
        return _googleAppsMediator;
    }

    public function set googleAppsMediator(value:GoogleAppsCreateMediator):void {
        _googleAppsMediator = value;
    }

    public function get sugarCRMMediator():SugarCRMCreateMediator {
        return _sugarCRMMediator;
    }

    public function set sugarCRMMediator(value:SugarCRMCreateMediator):void {
        _sugarCRMMediator = value;
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

    public function get jbossPortalExecutionEnvironmentCreateMediator():JBossPortalExecutionEnvironmentCreateMediator {
        return _jbossPortalExecutionEnvironmentCreateMediator;
    }

    public function set jbossPortalExecutionEnvironmentCreateMediator(value:JBossPortalExecutionEnvironmentCreateMediator):void {
        _jbossPortalExecutionEnvironmentCreateMediator = value;
    }

    public function get liferayPortalExecutionEnvironmentCreateMediator():LiferayPortalExecutionEnvironmentCreateMediator {
        return _liferayPortalExecutionEnvironmentCreateMediator;
    }

    public function set liferayPortalExecutionEnvironmentCreateMediator(value:LiferayPortalExecutionEnvironmentCreateMediator):void {
        _liferayPortalExecutionEnvironmentCreateMediator = value;
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

    public function get alfrescoExecutionEnvironmentCreateMediator():AlfrescoExecutionEnvironmentCreateMediator {
        return _alfrescoExecutionEnvironmentCreateMediator;
    }

    public function set alfrescoExecutionEnvironmentCreateMediator(value:AlfrescoExecutionEnvironmentCreateMediator):void {
        _alfrescoExecutionEnvironmentCreateMediator = value;
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

    public function get phpBBExecutionEnvironmentCreateMediator():PhpBBExecutionEnvironmentCreateMediator {
        return _phpBBExecutionEnvironmentCreateMediator;
    }

    public function set phpBBExecutionEnvironmentCreateMediator(value:PhpBBExecutionEnvironmentCreateMediator):void {
        _phpBBExecutionEnvironmentCreateMediator = value;
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


    public function get sharepoint2010ExecutionEnvironmentCreateMediator():Sharepoint2010ExecutionEnvironmentCreateMediator {
        return _sharepoint2010ExecutionEnvironmentCreateMediator;
    }

    public function set sharepoint2010ExecutionEnvironmentCreateMediator(value:Sharepoint2010ExecutionEnvironmentCreateMediator):void {
        _sharepoint2010ExecutionEnvironmentCreateMediator = value;
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
        identityProviderMediator.setViewComponent(_identityProviderCreateForm);
        identityProviderMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateServiceProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createServiceProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.sp");
        _popup.width = 690;
        _popup.height = 455;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_serviceProviderCreateForm);
    }

    private function createServiceProviderCreateForm():void {
        _serviceProviderCreateForm = new ServiceProviderCreateForm();
        _serviceProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleServiceProviderCreateFormCreated);
    }

    private function handleServiceProviderCreateFormCreated(event:FlexEvent):void {
        serviceProviderMediator.setViewComponent(_serviceProviderCreateForm);
        serviceProviderMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExternalIdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExternalIdentityProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.externalidp");
        _popup.width = 410;
        _popup.height = 190;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_externalIdentityProviderCreateForm);
        //on show call bindForm()
    }

    private function createExternalIdentityProviderCreateForm():void {
        _externalIdentityProviderCreateForm = new ExternalIdentityProviderCreateForm();
        _externalIdentityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalIdentityProviderCreateFormCreated);
    }

    private function handleExternalIdentityProviderCreateFormCreated(event:FlexEvent):void {
        externalIdentityProviderMediator.setViewComponent(_externalIdentityProviderCreateForm);
        externalIdentityProviderMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateExternalServiceProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createExternalServiceProviderCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.externalsp");
        _popup.width = 410;
        _popup.height = 190;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_externalServiceProviderCreateForm);
    }

    private function createExternalServiceProviderCreateForm():void {
        _externalServiceProviderCreateForm = new ExternalServiceProviderCreateForm();
        _externalServiceProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalServiceProviderCreateFormCreated);
    }

    private function handleExternalServiceProviderCreateFormCreated(event:FlexEvent):void {
        externalServiceProviderMediator.setViewComponent(_externalServiceProviderCreateForm);
        externalServiceProviderMediator.handleNotification(_lastWindowNotification);
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
        salesforceMediator.setViewComponent(_salesforceCreateForm);
        salesforceMediator.handleNotification(_lastWindowNotification);
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
        googleAppsMediator.setViewComponent(_googleAppsCreateForm);
        googleAppsMediator.handleNotification(_lastWindowNotification);
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
        sugarCRMMediator.setViewComponent(_sugarCRMCreateForm);
        sugarCRMMediator.handleNotification(_lastWindowNotification);
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

    public function showCreateJBossPortalExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createJBossPortalExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.jbportal");
        _popup.width = 500;
        _popup.height = 260;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_jbossPortalExecutionEnvironmentCreateForm);
    }

    private function createJBossPortalExecutionEnvironmentCreateForm():void {
        _jbossPortalExecutionEnvironmentCreateForm = new JBossPortalExecutionEnvironmentCreateForm();
        _jbossPortalExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleJBossPortalExecutionEnvironmentCreateFormCreated);
    }

    private function handleJBossPortalExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        jbossPortalExecutionEnvironmentCreateMediator.setViewComponent(_jbossPortalExecutionEnvironmentCreateForm);
        jbossPortalExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateLiferayPortalExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createLiferayPortalExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.liferay");
        _popup.width = 500;
        _popup.height = 310;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_liferayPortalExecutionEnvironmentCreateForm);
    }

    private function createLiferayPortalExecutionEnvironmentCreateForm():void {
        _liferayPortalExecutionEnvironmentCreateForm = new LiferayPortalExecutionEnvironmentCreateForm();
        _liferayPortalExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleLiferayPortalExecutionEnvironmentCreateFormCreated);
    }

    private function handleLiferayPortalExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        liferayPortalExecutionEnvironmentCreateMediator.setViewComponent(_liferayPortalExecutionEnvironmentCreateForm);
        liferayPortalExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
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

    public function showCreateAlfrescoExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createAlfrescoExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.alfresco");
        _popup.width = 500;
        _popup.height = 290;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_alfrescoExecutionEnvironmentCreateForm);
    }

    private function createAlfrescoExecutionEnvironmentCreateForm():void {
        _alfrescoExecutionEnvironmentCreateForm = new AlfrescoExecutionEnvironmentCreateForm();
        _alfrescoExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleAlfrescoExecutionEnvironmentCreateFormCreated);
    }

    private function handleAlfrescoExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        alfrescoExecutionEnvironmentCreateMediator.setViewComponent(_alfrescoExecutionEnvironmentCreateForm);
        alfrescoExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
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

    public function showCreatePhpBBExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createPhpBBExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.phpbb");
        _popup.width = 500;
        _popup.height = 290;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_phpBBExecutionEnvironmentCreateForm);
    }

    private function createPhpBBExecutionEnvironmentCreateForm():void {
        _phpBBExecutionEnvironmentCreateForm = new PhpBBExecutionEnvironmentCreateForm();
        _phpBBExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handlePhpBBExecutionEnvironmentCreateFormCreated);
    }

    private function handlePhpBBExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        phpBBExecutionEnvironmentCreateMediator.setViewComponent(_phpBBExecutionEnvironmentCreateForm);
        phpBBExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
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

    public function showCreateSharepoint2010ExecutionEnvironmentWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSharepoint2010ExecutionEnvironmentCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.env.sharepoint2010");
        _popup.width = 500;
        _popup.height = 350;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_sharepoint2010ExecutionEnvironmentCreateForm);
    }

    private function createSharepoint2010ExecutionEnvironmentCreateForm():void {
        _sharepoint2010ExecutionEnvironmentCreateForm = new Sharepoint2010ExecutionEnvironmentCreateForm();
        _sharepoint2010ExecutionEnvironmentCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSharepoint2010ExecutionEnvironmentCreateFormCreated);
    }

    private function handleSharepoint2010ExecutionEnvironmentCreateFormCreated(event:FlexEvent):void {
        sharepoint2010ExecutionEnvironmentCreateMediator.setViewComponent(_sharepoint2010ExecutionEnvironmentCreateForm);
        sharepoint2010ExecutionEnvironmentCreateMediator.handleNotification(_lastWindowNotification);
    }


    public function showCreateFederatedConnectionWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createFederatedConnectionCreateForm();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.popup.new.federated.connection");
        _popup.width = 640;
        _popup.height = 475;
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