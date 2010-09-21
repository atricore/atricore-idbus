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
import com.atricore.idbus.console.modeling.diagram.view.dbidentitysource.DbIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.dbidentitysource.DbIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache.ApacheExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache.ApacheExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jboss.JBossExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jboss.JBossExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jbossportal.JBossPortalExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.jbossportal.JBossPortalExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.liferayportal.LiferayPortalExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.liferayportal.LiferayPortalExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.tomcat.TomcatExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.tomcat.TomcatExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.wasce.WASCEExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.wasce.WASCEExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.weblogic.WeblogicExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.weblogic.WeblogicExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.windowsiis.WindowsIISExecutionEnvironmentCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.executionenvironment.windowsiis.WindowsIISExecutionEnvironmentCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.federatedconnection.FederatedConnectionCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.federatedconnection.FederatedConnectionCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.identityvault.IdentityVaultCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.identityvault.IdentityVaultCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.ldapidentitysource.LdapIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.ldapidentitysource.LdapIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.sp.ServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.sp.ServiceProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.xmlidentitysource.XmlIdentitySourceCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.xmlidentitysource.XmlIdentitySourceCreateMediator;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceView;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceView;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class ModelerPopUpManager extends BasePopUpManager {

    // mediators
    private var _manageCertificateMediator:ManageCertificateMediator;
    private var _identityProviderMediator:IdentityProviderCreateMediator;
    private var _serviceProviderMediator:ServiceProviderCreateMediator;
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
    private var _uploadProgressMediator:UploadProgressMediator;
    private var _buildApplianceMediator:BuildApplianceMediator;
    private var _deployApplianceMediator:DeployApplianceMediator;
    private var _activationCreateMediator:ActivationCreateMediator;
    private var _federatedConnectionCreateMediator:FederatedConnectionCreateMediator;

    // views
    private var _identityProviderCreateForm:IdentityProviderCreateForm;
    private var _serviceProviderCreateForm:ServiceProviderCreateForm;
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
    private var _manageCertificateForm:ManageCertificateView;
    private var _uploadProgress:UploadProgress;
    private var _buildAppliance:BuildApplianceView;
    private var _deployAppliance:DeployApplianceView;
    private var _activationCreateForm:ActivationCreateForm;
    private var _federatedConnectionCreateForm:FederatedConnectionCreateForm;

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "modelerPopup";
    }

    public function set manageCertificateMediator(value:ManageCertificateMediator) {
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

    public function showCreateIdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_identityProviderCreateForm) {
            createIdentityProviderCreateForm();
        } else {
            identityProviderMediator.initLocation();
        }
        _popup.title = "New Identity Provider Definition";
        _popup.width = 690;
        _popup.height = 455;
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
        if (!_serviceProviderCreateForm) {
            createServiceProviderCreateForm();
        } else {
            serviceProviderMediator.initLocation();
        }
        _popup.title = "New Service Provider Definition";
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

    public function showCreateIdentityVaultWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_identityVaultCreateForm) {
            createIdentityVaultCreateForm();
        }
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
        if (!_dbIdentitySourceCreateForm) {
            createDbIdentitySourceCreateForm();
        }
        _popup.title = "Create DB Identity Source";
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
        if (!_ldapIdentitySourceCreateForm) {
            createLdapIdentitySourceCreateForm();
        }
        _popup.title = "Create LDAP Identity Source";
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
        if (!_xmlIdentitySourceCreateForm) {
            createXmlIdentitySourceCreateForm();
        }
        _popup.title = "Create XML Identity Source";
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
        if (!_weblogicExecutionEnvironmentCreateForm) {
            createWeblogicExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create Weblogic Execution Environment";
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
        if (!_tomcatExecutionEnvironmentCreateForm) {
            createTomcatExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create Tomcat Execution Environment";
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
        if (!_jbossPortalExecutionEnvironmentCreateForm) {
            createJBossPortalExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create JBoss Portal Execution Environment";
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
        if (!_liferayPortalExecutionEnvironmentCreateForm) {
            createLiferayPortalExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create Liferay Portal Execution Environment";
        _popup.width = 500;
        _popup.height = 260;
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
        if (!_wasceExecutionEnvironmentCreateForm) {
            createWASCEExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create WASCE Execution Environment";
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
        if (!_apacheExecutionEnvironmentCreateForm) {
            createApacheExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create Apache Execution Environment";
        _popup.width = 500;
        _popup.height = 260;
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
        if (!_windowsIISExecutionEnvironmentCreateForm) {
            createWindowsIISExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create Windows IIS Execution Environment";
        _popup.width = 500;
        _popup.height = 260;
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

    public function showCreateFederatedConnectionWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_federatedConnectionCreateForm) {
            createFederatedConnectionCreateForm();
        } else {
            federatedConnectionCreateMediator.handleNotification(_lastWindowNotification);
        }
        _popup.title = "Create Federated Connection";
        _popup.width = 600;
        _popup.height = 395;
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
        if (!_activationCreateForm) {
            createActivationCreateForm();
        }
        _popup.title = "Create JOSSO Activation";
        _popup.width = 680;
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
        if (!_jbossExecutionEnvironmentCreateForm) {
            createJBossExecutionEnvironmentCreateForm();
        }
        _popup.title = "Create JBoss Execution Environment";
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
        _popup.title = "Manage Certificate";
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
        _progress.title = "File upload";
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
        _popup.title = "Build Identity Appliance";
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
        _popup.title = "Deploy Identity Appliance";
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

}
}