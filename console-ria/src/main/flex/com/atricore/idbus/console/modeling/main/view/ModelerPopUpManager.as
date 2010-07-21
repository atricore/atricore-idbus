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
import com.atricore.idbus.console.modeling.diagram.view.dbidentityvault.DbIdentityVaultWizardView;
import com.atricore.idbus.console.modeling.diagram.view.dbidentityvault.DbIdentityVaultWizardViewMediator;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.idpchannel.IDPChannelCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.idpchannel.IDPChannelCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.sp.ServiceProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.sp.ServiceProviderCreateMediator;
import com.atricore.idbus.console.modeling.diagram.view.spchannel.SPChannelCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.spchannel.SPChannelCreateMediator;
import com.atricore.idbus.console.modeling.main.ModelerView;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceView;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceView;

import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class ModelerPopUpManager extends BasePopUpManager {

    private var _identityProviderCreateForm:IdentityProviderCreateForm;
    private var _serviceProviderCreateForm:ServiceProviderCreateForm;
    private var _idpChannelCreateForm:IDPChannelCreateForm;
    private var _spChannelCreateForm:SPChannelCreateForm;
    private var _dbIdentityVaultWizardView:DbIdentityVaultWizardView;
    private var _manageCertificateForm:ManageCertificateView;
    private var _uploadProgress:UploadProgress;
    private var _buildAppliance:BuildApplianceView;
    private var _deployAppliance:DeployApplianceView;

    public function ModelerPopUpManager(facade:IFacade, modeler:ModelerView) {
        super(facade, modeler);
        _popup.styleName = "modelerPopup";
    }
    
    public function showCreateIdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_identityProviderCreateForm) {
           createIdentityProviderCreateForm();
        }
        _popup.title = "Create Identity Provider";
        _popup.width = 690;
        _popup.height = 510;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_identityProviderCreateForm);
    }

    private function createIdentityProviderCreateForm():void {
        _identityProviderCreateForm = new IdentityProviderCreateForm();
        _identityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderCreateFormCreated);
    }

    private function handleIdentityProviderCreateFormCreated(event:FlexEvent):void {
        var mediator:IdentityProviderCreateMediator = new IdentityProviderCreateMediator(_identityProviderCreateForm);
        _facade.removeMediator(IdentityProviderCreateMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateServiceProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_serviceProviderCreateForm) {
           createServiceProviderCreateForm();
        }
        _popup.title = "Create Service Provider";
        _popup.width = 690;
        _popup.height = 550;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_serviceProviderCreateForm);
    }

    private function createServiceProviderCreateForm():void {
        _serviceProviderCreateForm = new ServiceProviderCreateForm();
        _serviceProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleServiceProviderCreateFormCreated);
    }

    private function handleServiceProviderCreateFormCreated(event:FlexEvent):void {
        var mediator:ServiceProviderCreateMediator = new ServiceProviderCreateMediator(_serviceProviderCreateForm);
        _facade.removeMediator(ServiceProviderCreateMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateIdpChannelWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_idpChannelCreateForm) {
           createIdpChannelCreateForm();
        }
        _popup.title = "Create Identity Provider Channel";
        _popup.width = 690;
        _popup.height = 550;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_idpChannelCreateForm);
    }

    private function createIdpChannelCreateForm():void {
        _idpChannelCreateForm = new IDPChannelCreateForm();
        _idpChannelCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdpChannelCreateFormCreated);
    }

    private function handleIdpChannelCreateFormCreated(event:FlexEvent):void {
        var mediator:IDPChannelCreateMediator = new IDPChannelCreateMediator(_idpChannelCreateForm);
        _facade.removeMediator(IDPChannelCreateMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateSpChannelWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_spChannelCreateForm) {
           createSpChannelCreateForm();
        }
        _popup.title = "Create Service Provider Channel";
        _popup.width = 690;
        _popup.height = 510;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_spChannelCreateForm);
    }

    private function createSpChannelCreateForm():void {
        _spChannelCreateForm = new SPChannelCreateForm();
        _spChannelCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSpChannelCreateFormCreated);
    }

    private function handleSpChannelCreateFormCreated(event:FlexEvent):void {
        var mediator:SPChannelCreateMediator = new SPChannelCreateMediator(_spChannelCreateForm);
        _facade.removeMediator(SPChannelCreateMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateDbIdentityVaultWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createDbIdentityVaultWizardView();
        showWizard(_dbIdentityVaultWizardView);
    }

    private function createDbIdentityVaultWizardView():void {
        _dbIdentityVaultWizardView = new DbIdentityVaultWizardView();
        _dbIdentityVaultWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleDbIdentityVaultWizardViewCreated);
    }

    private function handleDbIdentityVaultWizardViewCreated(event:FlexEvent):void {
        var mediator:DbIdentityVaultWizardViewMediator = new DbIdentityVaultWizardViewMediator(_dbIdentityVaultWizardView);
        _facade.removeMediator(DbIdentityVaultWizardViewMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showManageCertificateWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createManageCertificateForm();
        _popup.title = "Manage Certificate";
        _popup.width = 400;
        _popup.height = 480;
        //_popup.x = (_popupParent.width / 2) - 225;
        //_popup.y = 80;
        showPopup(_manageCertificateForm);
    }
    
    private function createManageCertificateForm():void {
        _manageCertificateForm = new ManageCertificateView();
        _manageCertificateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleManageCertificateFormCreated);
    }

    private function handleManageCertificateFormCreated(event:FlexEvent):void {
        var mediator:ManageCertificateMediator = new ManageCertificateMediator(_manageCertificateForm);
        _facade.removeMediator(ManageCertificateMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showUploadProgressWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createUploadProgressWindow();
        _progress.title = "File upload";
        _progress.width = 300;
        _progress.height = 170;
        //_progress.x = (_popupParent.width / 2) - 225;
        //_progress.y = 80;
        showProgress(_uploadProgress);
    }

    private function createUploadProgressWindow():void {
        _uploadProgress = new UploadProgress();
        _uploadProgress.addEventListener(FlexEvent.CREATION_COMPLETE, handleUploadProgressWindowCreated);
    }

    private function handleUploadProgressWindowCreated(event:FlexEvent):void {
        var mediator:UploadProgressMediator = new UploadProgressMediator(_uploadProgress);
        _facade.removeMediator(UploadProgressMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
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
        var mediator:BuildApplianceMediator = new BuildApplianceMediator(_buildAppliance);
        _facade.removeMediator(BuildApplianceMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
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
        var mediator:DeployApplianceMediator = new DeployApplianceMediator(_deployAppliance);
        _facade.removeMediator(DeployApplianceMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }
}
}