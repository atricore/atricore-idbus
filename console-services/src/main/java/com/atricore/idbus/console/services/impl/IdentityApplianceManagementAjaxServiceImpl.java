/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.JDBCDriverDescriptor;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.impl.ValidationError;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.services.dto.*;
import com.atricore.idbus.console.services.spi.IdentityApplianceManagementAjaxService;
import com.atricore.idbus.console.services.spi.IdentityServerException;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import org.dozer.DozerBeanMapper;

import java.io.File;
import java.util.*;

/**
 * Author: Dejan Maric
 */
public class IdentityApplianceManagementAjaxServiceImpl implements IdentityApplianceManagementAjaxService {

    private IdentityApplianceManagementService idApplianceManagementService;
    private DozerBeanMapper dozerMapper;

    public BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.BuildIdentityApplianceRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.BuildIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.BuildIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.buildIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, BuildIdentityApplianceResponse.class);
    }

    public DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.DeployIdentityApplianceRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.DeployIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.DeployIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.deployIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, DeployIdentityApplianceResponse.class);
    }

    public UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.UndeployIdentityApplianceRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.UndeployIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UndeployIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.undeployIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, UndeployIdentityApplianceResponse.class);
    }

    public StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.StartIdentityApplianceRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.StartIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.StartIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.startIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, StartIdentityApplianceResponse.class);
    }

    public StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.StopIdentityApplianceRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.StopIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.StopIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.stopIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, StopIdentityApplianceResponse.class);
    }

    public DisposeIdentityApplianceResponse disposeIdentityAppliance(DisposeIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.DisposeIdentityApplianceRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.DisposeIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.DisposeIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.disposeIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, DisposeIdentityApplianceResponse.class);
    }

    public ExportIdentityApplianceResponse exportIdentityAppliance(ExportIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ExportIdentityApplianceRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ExportIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ExportIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.exportIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ExportIdentityApplianceResponse.class);
    }

    public ExportIdentityApplianceProjectResponse exportIdentityApplianceProject(ExportIdentityApplianceProjectRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ExportIdentityApplianceProjectRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ExportIdentityApplianceProjectRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ExportIdentityApplianceProjectResponse beRes = null;
        try {
            beRes = idApplianceManagementService.exportIdentityApplianceProject(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ExportIdentityApplianceProjectResponse.class);
    }

    public ImportIdentityApplianceResponse importIdentityApplianceProject(ImportIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ImportIdentityApplianceRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ImportIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ImportIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.importIdentityApplianceProject(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            if (e.getCause() instanceof ApplianceValidationException) {
                ImportIdentityApplianceResponse resp = new ImportIdentityApplianceResponse();
                List<String> validationErrors = new ArrayList<String>();
                for (ValidationError error : ((ApplianceValidationException) e.getCause()).getErrors()) {
                    validationErrors.add(error.getMsg());
                }
                resp.setValidationErrors(validationErrors);
                return resp;
            } else {
                throw new IdentityServerException(e);
            }
        }

        return dozerMapper.map(beRes, ImportIdentityApplianceResponse.class);
    }


    public ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ManageIdentityApplianceLifeCycleRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ManageIdentityApplianceLifeCycleRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ManageIdentityApplianceLifeCycleResponse beRes = null;
        try {
            beRes = idApplianceManagementService.manageIdentityApplianceLifeCycle(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ManageIdentityApplianceLifeCycleResponse.class);
    }

    public ActivateExecEnvResponse activateExecEnv(ActivateExecEnvRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ActivateExecEnvRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ActivateExecEnvRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ActivateExecEnvResponse beRes = null;
        try {
            beRes = idApplianceManagementService.activateExecEnv(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ActivateExecEnvResponse.class);
    }

    public CreateSimpleSsoResponse createSimpleSso(CreateSimpleSsoRequest req)
            throws IdentityServerException {

        IdentityApplianceDTO idAppliance = req.getIdentityAppliance();
        IdentityApplianceDefinitionDTO iad = idAppliance.getIdApplianceDefinition();

        idAppliance.setState(IdentityApplianceStateDTO.PROJECTED.toString());

        SamlR2ProviderConfigDTO config = null;
        //// providers that are currently in providers list are service providers and if they have config set, thay all have the same config object
        if(iad.getProviders() != null && iad.getProviders().size() > 0){
            config = (SamlR2ProviderConfigDTO)iad.getProviders().get(0).getConfig();
        }
        IdentityProviderDTO idp = createIdentityProvider(iad, config);

        // create josso activations and federated connections
        // providers that are currently in providers list are service providers
        for (ProviderDTO sp : iad.getProviders()) {
            if (sp.getRole().equals(ProviderRoleDTO.SSOServiceProvider)) {
                populateJOSSOActivation(iad, (ServiceProviderDTO)sp);
                createFederatedConnection(idp, (ServiceProviderDTO)sp);
            }
        }

        updateExecutionEnvNames(iad);

        iad.getProviders().add(idp);

        //connect all providers with created identity vault/source
        for (ProviderDTO tmpProvider : iad.getProviders()) {
            createIdentityLookup(iad, tmpProvider);
        }

        for (ProviderDTO p : idAppliance.getIdApplianceDefinition().getProviders()) {
            if (p instanceof ServiceProviderDTO) {
                populateServiceProvider((ServiceProviderDTO)p, iad);
            }
        }

        com.atricore.idbus.console.lifecycle.main.spi.request.AddIdentityApplianceRequest addIdApplianceReq =
                new com.atricore.idbus.console.lifecycle.main.spi.request.AddIdentityApplianceRequest();
        addIdApplianceReq.setIdentityAppliance(dozerMapper.map(idAppliance, IdentityAppliance.class));
        com.atricore.idbus.console.lifecycle.main.spi.response.AddIdentityApplianceResponse beRes = null;

        CreateSimpleSsoResponse response = new CreateSimpleSsoResponse();

        try {
            beRes = idApplianceManagementService.addIdentityAppliance(addIdApplianceReq);
            AddIdentityApplianceResponse res = dozerMapper.map(beRes, AddIdentityApplianceResponse.class);
            idAppliance = res.getAppliance();
            response.setAppliance(idAppliance);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            if (e.getCause() instanceof ApplianceValidationException) {
                List<String> validationErrors = new ArrayList<String>();
                for (ValidationError error : ((ApplianceValidationException) e.getCause()).getErrors()) {
                    validationErrors.add(error.getMsg());
                }
                response.setValidationErrors(validationErrors);
            } else {
                throw new IdentityServerException(e);
            }
        }

        return response;
    }

    public AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.AddIdentityApplianceRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.AddIdentityApplianceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.AddIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.addIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            if (e.getCause() instanceof ApplianceValidationException) {
                AddIdentityApplianceResponse resp = new AddIdentityApplianceResponse();
                List<String> validationErrors = new ArrayList<String>();
                for (ValidationError error : ((ApplianceValidationException) e.getCause()).getErrors()) {
                    validationErrors.add(error.getMsg());
                }
                resp.setValidationErrors(validationErrors);
                return resp;
            } else {
                throw new IdentityServerException(e);
            }
        }
        return dozerMapper.map(beRes, AddIdentityApplianceResponse.class);
    }

    public LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupIdentityApplianceByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupIdentityApplianceById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupIdentityApplianceByIdResponse.class);
    }

    public UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest req) throws IdentityServerException{
        //First find identity appliance in DB
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest beLookupReq =
                new  com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest();

        IdentityAppliance updatedAppliance = prepareApplianceForUpdate(req.getAppliance());

        //Finally call the update method
//        this.updateAppliance(updatedAppliance);
        return this.updateAppliance(updatedAppliance);
    }

    private IdentityAppliance prepareApplianceForUpdate(IdentityApplianceDTO updatedApplianceDto) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest beLookupReq =
                new  com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest();

        beLookupReq.setIdentityApplianceId(new Long(updatedApplianceDto.getId()).toString());

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupIdentityApplianceByIdResponse beLookupRes =
                null;
        try {
            beLookupRes = idApplianceManagementService.lookupIdentityApplianceById(beLookupReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }

        IdentityAppliance foundAppliance = beLookupRes.getIdentityAppliance();

        //Then, update the found identity appliance with data from DTO object
        dozerMapper.map(updatedApplianceDto, foundAppliance);
        return foundAppliance;
    }

    private UpdateIdentityApplianceResponse updateAppliance(IdentityAppliance appliance) throws IdentityServerException {
        //Prepare Request object for calling BE updateIdentityAppliance method
        com.atricore.idbus.console.lifecycle.main.spi.request.UpdateIdentityApplianceRequest beReq =
                new com.atricore.idbus.console.lifecycle.main.spi.request.UpdateIdentityApplianceRequest();

        beReq.setAppliance(appliance);

        com.atricore.idbus.console.lifecycle.main.spi.response.UpdateIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.updateIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            if (e.getCause() instanceof ApplianceValidationException) {
                UpdateIdentityApplianceResponse resp = new UpdateIdentityApplianceResponse();
                List<String> validationErrors = new ArrayList<String>();
                for (ValidationError error : ((ApplianceValidationException) e.getCause()).getErrors()) {
                    validationErrors.add(error.getMsg());
                }
                resp.setValidationErrors(validationErrors);
                return resp;
            } else {
                throw new IdentityServerException(e);
            }
        }
        return dozerMapper.map(beRes, UpdateIdentityApplianceResponse.class);
    }


    public RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException{
        //First find identity appliance in DB
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest beLookupReq =
                new  com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest();

        //Prepare Request object for calling BE updateIdentityAppliance method
        com.atricore.idbus.console.lifecycle.main.spi.request.RemoveIdentityApplianceRequest beReq =
                new com.atricore.idbus.console.lifecycle.main.spi.request.RemoveIdentityApplianceRequest();

        beReq.setApplianceId(new Long(req.getIdentityAppliance().getId()).toString());

        com.atricore.idbus.console.lifecycle.main.spi.response.RemoveIdentityApplianceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.removeIdentityAppliance(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, RemoveIdentityApplianceResponse.class);
    }

    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityAppliancesRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityAppliancesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListIdentityAppliancesResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listIdentityAppliances(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListIdentityAppliancesResponse.class);
    }

    public void setIdApplianceManagementService(IdentityApplianceManagementService idApplianceManagementService) {
        this.idApplianceManagementService = idApplianceManagementService;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }

    public AddResourceResponse addResource(AddResourceRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.AddResourceRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.AddResourceRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.AddResourceResponse beRes = null;
        try {
            beRes = idApplianceManagementService.addResource(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, AddResourceResponse.class);
    }

    public LookupResourceByIdResponse lookupResourceById(LookupResourceByIdRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupResourceByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupResourceByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupResourceByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupResourceById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupResourceByIdResponse.class);
    }

    public CheckInstallFolderExistenceResponse checkInstallFolderExistence(CheckInstallFolderExistenceRequest req) throws IdentityServerException {
        boolean folderExists = false;
        if(req.getInstallFolder() != null){
            folderExists = new File(req.getInstallFolder()).exists();
        }
        CheckInstallFolderExistenceResponse res = new CheckInstallFolderExistenceResponse();
        res.setFolderExists(folderExists);
        res.setEnvironmentName(req.getEnvironmentName());
        return res;
    }

    public CheckFoldersExistenceResponse checkFoldersExistence(CheckFoldersExistenceRequest req) throws IdentityServerException {
        List<String> invalidFolders = new ArrayList<String>();
        if (req.getFolders() != null) {
            for (String folder : req.getFolders()) {
                if (!new File(folder).exists()) {
                    invalidFolders.add(folder);
                }
            }
        }
        CheckFoldersExistenceResponse res = new CheckFoldersExistenceResponse();
        res.setInvalidFolders(invalidFolders);
        res.setEnvironmentName(req.getEnvironmentName());
        return res;
    }

    public ExportProviderCertificateResponse exportProviderCertificate(ExportProviderCertificateRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ExportProviderCertificateRequest ecReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ExportProviderCertificateRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ExportProviderCertificateResponse ecRes;
        try {
            ecRes = idApplianceManagementService.exportProviderCertificate(ecReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(ecRes, ExportProviderCertificateResponse.class);
    }

    public ExportMetadataResponse exportMetadata(ExportMetadataRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ExportMetadataRequest emReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ExportMetadataRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ExportMetadataResponse emRes;
        try {
            emRes = idApplianceManagementService.exportMetadata(emReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(emRes, ExportMetadataResponse.class);
    }
    
    /****************************
     * List methods
     ***************************/

    public ListAvailableJDBCDriversResponse listAvailableJDBCDrivers(ListAvailableJDBCDriversRequest request) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ListAvailableJDBCDriversRequest beReq =
                new com.atricore.idbus.console.lifecycle.main.spi.request.ListAvailableJDBCDriversRequest();

        com.atricore.idbus.console.lifecycle.main.spi.response.ListAvailableJDBCDriversResponse beRes = null;

        try {
            beRes = idApplianceManagementService.listAvailableJDBCDrivers(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }

        ListAvailableJDBCDriversResponse response = new ListAvailableJDBCDriversResponse();
        List<JDBCDriverDescriptorDTO> drivers = new ArrayList<JDBCDriverDescriptorDTO>();
        for (JDBCDriverDescriptor d: beRes.getDrivers()) {
            JDBCDriverDescriptorDTO dto = new JDBCDriverDescriptorDTO();
            dto.setName(d.getName());
            dto.setClassName(d.getClassName());
            dto.setDefaultUrl(d.getDefaultUrl());
            dto.setWebSiteUrl(d.getWebSiteUrl());
            drivers.add(dto);
        }
        response.setDrivers(drivers);
        return response;

    }




    public ListIdentityVaultsResponse listIdentityVaults(ListIdentityVaultsRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityVaultsRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityVaultsRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListIdentityVaultsResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listIdentityVaults(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListIdentityVaultsResponse.class);
    }

    public ListUserInformationLookupsResponse listUserInformationLookups(ListUserInformationLookupsRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListUserInformationLookupsRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListUserInformationLookupsRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListUserInformationLookupsResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listUserInformationLookups(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListUserInformationLookupsResponse.class);
    }

    public ListAccountLinkagePoliciesResponse listAccountLinkagePolicies(ListAccountLinkagePoliciesRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListAccountLinkagePoliciesRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListAccountLinkagePoliciesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListAccountLinkagePoliciesResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listAccountLinkagePolicies(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListAccountLinkagePoliciesResponse.class);
    }

    public ListAuthenticationContractsResponse listAuthenticationContracts(ListAuthenticationContractsRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListAuthenticationContractsRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListAuthenticationContractsRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListAuthenticationContractsResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listAuthenticationContracts(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListAuthenticationContractsResponse.class);
    }

    public ListAuthenticationMechanismsResponse listAuthenticationMechanisms(ListAuthenticationMechanismsRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListAuthenticationMechanismsRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListAuthenticationMechanismsRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListAuthenticationMechanismsResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listAuthenticationMechanisms(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListAuthenticationMechanismsResponse.class);
    }

    public ListAttributeProfilesResponse listAttributeProfiles(ListAttributeProfilesRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListAttributeProfilesRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListAttributeProfilesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListAttributeProfilesResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listAttributeProfiles(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListAttributeProfilesResponse.class);
    }

    public ListAuthAssertionEmissionPoliciesResponse listAuthAssertionEmissionPolicies(ListAuthAssertionEmissionPoliciesRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListAuthAssertionEmissionPoliciesRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListAuthAssertionEmissionPoliciesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListAuthAssertionEmissionPoliciesResponse beRes = null;
        try {
            beRes = idApplianceManagementService.listAuthAssertionEmissionPolicies(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListAuthAssertionEmissionPoliciesResponse.class);
    }

    public ListIdentityMappingPoliciesResponse listIdentityMappingPolicies(ListIdentityMappingPoliciesRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityMappingPoliciesRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityMappingPoliciesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListIdentityMappingPoliciesResponse beRes;
        try {
            beRes = idApplianceManagementService.listIdentityMappingPolicies(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListIdentityMappingPoliciesResponse.class);
    }

    public ListSubjectNameIDPoliciesResponse listSubjectNameIDPolicies(ListSubjectNameIDPoliciesRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ListSubjectNameIDPoliciesRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.ListSubjectNameIDPoliciesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListSubjectNameIDPoliciesResponse beRes;
        try {
            beRes = idApplianceManagementService.listSubjectNameIDPolicies(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListSubjectNameIDPoliciesResponse.class);
    }

    public ListImpersonateUserPoliciesResponse listImpersonateUserPolicies(ListImpersonateUserPoliciesRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ListImpersonateUserPoliciesRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.ListImpersonateUserPoliciesRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListImpersonateUserPoliciesResponse beRes;
        try {
            beRes = idApplianceManagementService.listImpersonateUserPolicies(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListImpersonateUserPoliciesResponse.class);
    }


    public ListUserDashboardBrandingsResponse listUserDashboardBrandings(ListUserDashboardBrandingsRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.ListUserDashboardBrandingsRequest beReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.ListUserDashboardBrandingsRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.ListUserDashboardBrandingsResponse beRes;
        try {
            beRes = idApplianceManagementService.listUserDashboardBrandings(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, ListUserDashboardBrandingsResponse.class);
    }

    /****************************
     * Lookup methods
     ***************************/
    public LookupIdentityVaultByIdResponse lookupIdentityVaultById(LookupIdentityVaultByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityVaultByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityVaultByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupIdentityVaultByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupIdentityVaultById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupIdentityVaultByIdResponse.class);
    }

    public LookupUserInformationLookupByIdResponse lookupUserInformationLookupById(LookupUserInformationLookupByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupUserInformationLookupByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupUserInformationLookupByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupUserInformationLookupByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupUserInformationLookupById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupUserInformationLookupByIdResponse.class);
    }

    public LookupAccountLinkagePolicyByIdResponse lookupAccountLinkagePolicyById(LookupAccountLinkagePolicyByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupAccountLinkagePolicyByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupAccountLinkagePolicyByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupAccountLinkagePolicyByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupAccountLinkagePolicyById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupAccountLinkagePolicyByIdResponse.class);
    }

    public LookupAuthenticationContractByIdResponse lookupAuthenticationContractById(LookupAuthenticationContractByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupAuthenticationContractByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupAuthenticationContractByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupAuthenticationContractByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupAuthenticationContractById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupAuthenticationContractByIdResponse.class);
    }

    public LookupAuthenticationMechanismByIdResponse lookupAuthenticationMechanismById(LookupAuthenticationMechanismByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupAuthenticationMechanismByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupAuthenticationMechanismByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupAuthenticationMechanismByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupAuthenticationMechanismById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupAuthenticationMechanismByIdResponse.class);
    }

    public LookupAttributeProfileByIdResponse lookupAttributeProfileById(LookupAttributeProfileByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupAttributeProfileByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupAttributeProfileByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupAttributeProfileByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupAttributeProfileById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupAttributeProfileByIdResponse.class);
    }

    public LookupAuthAssertionEmissionPolicyByIdResponse lookupAuthAssertionEmissionPolicyById(LookupAuthAssertionEmissionPolicyByIdRequest req) throws IdentityServerException{
        com.atricore.idbus.console.lifecycle.main.spi.request.LookupAuthAssertionEmissionPolicyByIdRequest beReq =
                dozerMapper.map(req,  com.atricore.idbus.console.lifecycle.main.spi.request.LookupAuthAssertionEmissionPolicyByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.LookupAuthAssertionEmissionPolicyByIdResponse beRes = null;
        try {
            beRes = idApplianceManagementService.lookupAuthAssertionEmissionPolicyById(beReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, LookupAuthAssertionEmissionPolicyByIdResponse.class);
    }

    public GetMetadataInfoResponse getMetadataInfo(GetMetadataInfoRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.GetMetadataInfoRequest gmiReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.GetMetadataInfoRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.GetMetadataInfoResponse gmiRes = null;
        try {
            gmiRes = idApplianceManagementService.getMetadataInfo(gmiReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(gmiRes, GetMetadataInfoResponse.class);
    }

    public GetCertificateInfoResponse getCertificateInfo(GetCertificateInfoRequest req) throws IdentityServerException {
        com.atricore.idbus.console.lifecycle.main.spi.request.GetCertificateInfoRequest gciReq =
                dozerMapper.map(req, com.atricore.idbus.console.lifecycle.main.spi.request.GetCertificateInfoRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.GetCertificateInfoResponse gciRes = null;
        try {
            gciRes = idApplianceManagementService.getCertificateInfo(gciReq);
        } catch (com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(gciRes, GetCertificateInfoResponse.class);
    }

    /******************************************************
     * Helper methods
     ******************************************************/

    private void populateServiceProvider(ServiceProviderDTO sp, IdentityApplianceDefinitionDTO iad) {
        sp.setIdentityAppliance(iad);
        sp.setDescription(sp.getName() + " description");

        // set location
        LocationDTO location = new LocationDTO();
        location.setProtocol(iad.getLocation().getProtocol());
        location.setHost(iad.getLocation().getHost());
        location.setPort(iad.getLocation().getPort());
        location.setContext(iad.getLocation().getContext());
        location.setUri(iad.getLocation().getUri() + "/" + createUrlSafeString(sp.getName()).toUpperCase());
        sp.setLocation(location);

        // set active bindings
        if (sp.getActiveBindings() == null) {
            sp.setActiveBindings(new HashSet<BindingDTO>());
        }
        sp.getActiveBindings().add(BindingDTO.SAMLR2_ARTIFACT);
        sp.getActiveBindings().add(BindingDTO.SAMLR2_SOAP);
        sp.getActiveBindings().add(BindingDTO.SAMLR2_HTTP_POST);

        // set active profiles
        if (sp.getActiveProfiles() == null) {
            sp.setActiveProfiles(new HashSet<ProfileDTO>());
        }
        sp.getActiveProfiles().add(ProfileDTO.SSO);
        sp.getActiveProfiles().add(ProfileDTO.SSO_SLO);

        // set saml config
        SamlR2SPConfigDTO spSamlConfig = new SamlR2SPConfigDTO();
        SamlR2ProviderConfigDTO originalConfig = (SamlR2ProviderConfigDTO)sp.getConfig();
        spSamlConfig.setName(sp.getName() + "-samlr2-config");
        spSamlConfig.setDescription("SAMLR2 " + sp.getName() + "Configuration");
        spSamlConfig.setUseSampleStore(originalConfig.isUseSampleStore());
        if (!spSamlConfig.isUseSampleStore() && originalConfig.getSigner() != null) {
            KeystoreDTO keystore = new KeystoreDTO();
            keystore.setName(originalConfig.getSigner().getName());
            keystore.setDisplayName(originalConfig.getSigner().getDisplayName());
            keystore.setType(originalConfig.getSigner().getType());
            keystore.setCertificateAlias(originalConfig.getSigner().getCertificateAlias());
            keystore.setPrivateKeyName(originalConfig.getSigner().getPrivateKeyName());
            keystore.setPrivateKeyPassword(originalConfig.getSigner().getPrivateKeyPassword());
            keystore.setPassword(originalConfig.getSigner().getPassword());
            ResourceDTO originalResource = originalConfig.getSigner().getStore();
            if (originalResource != null) {
                ResourceDTO resource = new ResourceDTO();
                resource.setName(originalResource.getName());
                resource.setDisplayName(originalResource.getDisplayName());
                resource.setUri(originalResource.getUri());
                resource.setValue(originalResource.getValue());
                keystore.setStore(resource);
            }
            spSamlConfig.setSigner(keystore);
            spSamlConfig.setEncrypter(keystore);
        }
        sp.setConfig(spSamlConfig);

        // set account linkage policy
        AccountLinkagePolicyDTO accountLinkagePolicy = new AccountLinkagePolicyDTO();
        accountLinkagePolicy.setName(AccountLinkEmitterTypeDTO.ONE_TO_ONE.getDisplayName());
        accountLinkagePolicy.setLinkEmitterType(AccountLinkEmitterTypeDTO.ONE_TO_ONE);
        sp.setAccountLinkagePolicy(accountLinkagePolicy);

        // set identity mapping policy
        IdentityMappingPolicyDTO identityMappingPolicy = new IdentityMappingPolicyDTO();
        identityMappingPolicy.setName(IdentityMappingTypeDTO.REMOTE.getDisplayName());
        identityMappingPolicy.setMappingType(IdentityMappingTypeDTO.REMOTE);
        sp.setIdentityMappingPolicy(identityMappingPolicy);

        sp.setSignAuthenticationRequests(true);
        sp.setWantAssertionSigned(true);
        sp.setSignRequests(true);
        sp.setWantSignedRequests(true);
    }

    private void createFederatedConnection(IdentityProviderDTO idp, ServiceProviderDTO sp){
        IdentityProviderChannelDTO idpChannel = new IdentityProviderChannelDTO();
        idpChannel.setName(sp.getName() + "-to-" + idp.getName() + "-default-channel");
        idpChannel.setDescription(sp.getName() + " Default Channel");
        idpChannel.setOverrideProviderSetup(false);
        idpChannel.setPreferred(true);
        LocationDTO idpChannelLocation = new LocationDTO();
        idpChannelLocation.setProtocol(sp.getLocation().getProtocol());
        idpChannelLocation.setHost(sp.getLocation().getHost());
        idpChannelLocation.setPort(sp.getLocation().getPort());
        idpChannelLocation.setContext(sp.getLocation().getContext());
        idpChannelLocation.setUri(sp.getLocation().getUri());
        idpChannel.setLocation(idpChannelLocation);
        idpChannel.setSignAuthenticationRequests(true);
        idpChannel.setWantAssertionSigned(true);

        ServiceProviderChannelDTO spChannel = new ServiceProviderChannelDTO();
        spChannel.setName(idp.getName() + "-to-" + sp.getName() + "-default-channel");
        spChannel.setDescription(sp.getName() + " Default Channel");
        spChannel.setOverrideProviderSetup(false);
        LocationDTO spChannelLocation = new LocationDTO();
        spChannelLocation.setProtocol(idp.getLocation().getProtocol());
        spChannelLocation.setHost(idp.getLocation().getHost());
        spChannelLocation.setPort(idp.getLocation().getPort());
        spChannelLocation.setContext(idp.getLocation().getContext());
        spChannelLocation.setUri(idp.getLocation().getUri());
        spChannel.setLocation(spChannelLocation);
        spChannel.setWantAuthnRequestsSigned(true);
        
        FederatedConnectionDTO fedConnection = new FederatedConnectionDTO();
        fedConnection.setName(idp.getName().toLowerCase() + "-" + sp.getName().toLowerCase());
        //SETTING ROLE A
        fedConnection.setRoleA(idp);
        fedConnection.setChannelA(spChannel);
        idp.getFederatedConnectionsA().add(fedConnection);

        //SETTING ROLE B
        fedConnection.setRoleB(sp);
        fedConnection.setChannelB(idpChannel);
        sp.getFederatedConnectionsB().add(fedConnection);
    }

    private void createIdentityLookup(IdentityApplianceDefinitionDTO iad, ProviderDTO provider){
        for(IdentitySourceDTO is : iad.getIdentitySources()){
            IdentityLookupDTO idLookup = new IdentityLookupDTO();
            idLookup.setName(provider.getName() + "-idlookup");
            idLookup.setDescription(provider.getName() + " Identity Lookup Definition");

            //set provider and lookup
            idLookup.setProvider(provider);
            provider.setIdentityLookup(idLookup);
            //set IdentitySource and lookup
            idLookup.setIdentitySource(is);
        }
    }

    private IdentityProviderDTO createIdentityProvider(IdentityApplianceDefinitionDTO iad, SamlR2ProviderConfigDTO config) {
        IdentityProviderDTO idp = new IdentityProviderDTO();
        idp.setName(createUrlSafeString(iad.getName()) + "-idp");
        idp.setIdentityAppliance(iad);

        idp.getActiveBindings().add(BindingDTO.SAMLR2_ARTIFACT);
        idp.getActiveBindings().add(BindingDTO.SAMLR2_HTTP_POST);
        idp.getActiveBindings().add(BindingDTO.SAMLR2_SOAP);

        idp.getActiveProfiles().add(ProfileDTO.SSO);
        idp.getActiveProfiles().add(ProfileDTO.SSO_SLO);

        idp.setWantAuthnRequestsSigned(true);
        idp.setSignRequests(true);
        idp.setWantSignedRequests(true);

        LocationDTO idpLocation = new LocationDTO();
        idpLocation.setProtocol(iad.getLocation().getProtocol());
        idpLocation.setHost(iad.getLocation().getHost());
        idpLocation.setPort(iad.getLocation().getPort());
        idpLocation.setContext(iad.getLocation().getContext());
        idpLocation.setUri(iad.getLocation().getUri() + "/" + createUrlSafeString(idp.getName()).toUpperCase());
        idp.setLocation(idpLocation);

//        SamlR2ProviderConfigDTO idpSamlConfig = new SamlR2ProviderConfigDTO();
        //SamlR2ProviderConfigDTO idpSamlConfig = config;
        //if(idpSamlConfig == null){
        //    idpSamlConfig = new SamlR2ProviderConfigDTO();
        //}
        SamlR2IDPConfigDTO idpSamlConfig = new SamlR2IDPConfigDTO();
        idpSamlConfig.setName(idp.getName() + "-samlr2-config");
        idpSamlConfig.setDescription("SAMLR2 " + idp.getName() + "Configuration");
        idpSamlConfig.setUseSampleStore(config.isUseSampleStore());
        if (!idpSamlConfig.isUseSampleStore() && config.getSigner() != null) {
            KeystoreDTO keystore = new KeystoreDTO();
            keystore.setName(config.getSigner().getName());
            keystore.setDisplayName(config.getSigner().getDisplayName());
            keystore.setType(config.getSigner().getType());
            keystore.setCertificateAlias(config.getSigner().getCertificateAlias());
            keystore.setPrivateKeyName(config.getSigner().getPrivateKeyName());
            keystore.setPrivateKeyPassword(config.getSigner().getPrivateKeyPassword());
            keystore.setPassword(config.getSigner().getPassword());
            ResourceDTO originalResource = config.getSigner().getStore();
            if (originalResource != null) {
                ResourceDTO resource = new ResourceDTO();
                resource.setName(originalResource.getName());
                resource.setDisplayName(originalResource.getDisplayName());
                resource.setUri(originalResource.getUri());
                resource.setValue(originalResource.getValue());
                keystore.setStore(resource);
            }
            idpSamlConfig.setSigner(keystore);
            idpSamlConfig.setEncrypter(keystore);
        }
        idp.setConfig(idpSamlConfig);

        if(idp.getAuthenticationMechanisms() == null){
            idp.setAuthenticationMechanisms(new HashSet<AuthenticationMechanismDTO>());
        }
        BasicAuthenticationDTO authMechanism = new BasicAuthenticationDTO();
        authMechanism.setName(createUrlSafeString(idp.getName()) + "-basic-authn");
        authMechanism.setDisplayName("basic");
        authMechanism.setEnabled(true);
        authMechanism.setPriority(1);
        authMechanism.setHashAlgorithm("MD5");
        authMechanism.setHashEncoding("HEX");
        authMechanism.setIgnoreUsernameCase(false);

        idp.getAuthenticationMechanisms().add(authMechanism);

        AuthenticationContractDTO authContract = new AuthenticationContractDTO();
        authContract.setName("Default");
        idp.setAuthenticationContract(authContract);

        AuthenticationAssertionEmissionPolicyDTO authAssertionEmissionPolicy = new AuthenticationAssertionEmissionPolicyDTO();
        authAssertionEmissionPolicy.setName("Default");
        idp.setEmissionPolicy(authAssertionEmissionPolicy);

        return idp;
    }

    private JOSSOActivationDTO populateJOSSOActivation(IdentityApplianceDefinitionDTO iad, ServiceProviderDTO sp) {
        JOSSOActivationDTO activation = (JOSSOActivationDTO)sp.getActivation();
        activation.setSp(sp);
        activation.getPartnerAppLocation().setUri("");

        if (iad.getExecutionEnvironments() == null) {
            iad.setExecutionEnvironments(new HashSet<ExecutionEnvironmentDTO>());
        }

        ExecutionEnvironmentDTO executionEnv = findExecutionEnvironment(iad, activation);

        if (executionEnv == null) {
            executionEnv = activation.getExecutionEnv();
            executionEnv.setName(createTempExecutionEnvName(activation));
            if (executionEnv.getActivations() == null) {
                executionEnv.setActivations(new HashSet<ActivationDTO>());
            }
            iad.getExecutionEnvironments().add(executionEnv);
        } else {
            activation.setExecutionEnv(executionEnv);
        }

        executionEnv.getActivations().add(activation);

        return activation;
    }

    private ExecutionEnvironmentDTO findExecutionEnvironment(IdentityApplianceDefinitionDTO iad,
                                                             JOSSOActivationDTO activation) {
        if (iad.getExecutionEnvironments() != null) {
            String execEnvName = createTempExecutionEnvName(activation);
            for (ExecutionEnvironmentDTO executionEnv : iad.getExecutionEnvironments()) {
                if (executionEnv.getName().equals(execEnvName)) {
                    return executionEnv;
                }
            }
        }
        return null;
    }

    private String createTempExecutionEnvName(JOSSOActivationDTO activation) {
        return activation.getExecutionEnv().getPlatformId() + "-" +
                activation.getPartnerAppLocation().getHost() + "-" + activation.getPartnerAppLocation().getPort();
    }

    private void updateExecutionEnvNames(IdentityApplianceDefinitionDTO iad) {
        Map<String, Integer> currentPlatformCount = new HashMap<String, Integer>();
        if (iad.getExecutionEnvironments() != null) {
            for (ExecutionEnvironmentDTO execEnv : iad.getExecutionEnvironments()) {
                int num = getNumberOfSameExecEnvPlatforms(iad, execEnv.getPlatformId());
                if (num == 1) {
                    execEnv.setName(execEnv.getPlatformId());
                } else {
                    Integer platformCount = currentPlatformCount.get(execEnv.getPlatformId());
                    if (platformCount == null) {
                        currentPlatformCount.put(execEnv.getPlatformId(), 1);
                    } else {
                        currentPlatformCount.put(execEnv.getPlatformId(), platformCount + 1);
                    }
                    execEnv.setName(execEnv.getPlatformId() + "-" + currentPlatformCount.get(execEnv.getPlatformId()));
                }
                execEnv.setDescription(execEnv.getName().replaceAll("-", " "));
            }
        }
    }

    private int getNumberOfSameExecEnvPlatforms(IdentityApplianceDefinitionDTO iad, String platformId) {
        int num = 0;
        if (iad.getExecutionEnvironments() != null) {
            for (ExecutionEnvironmentDTO executionEnv : iad.getExecutionEnvironments()) {
                if (executionEnv.getPlatformId().equals(platformId)) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Creates stringToCheck safe string. String will consist of letters, numbers, underscores and dashes
     * @param stringToCheck
     * @return url safe string
     */
    private String createUrlSafeString(String stringToCheck){
        String regex = "[^a-zA-Z0-9-_]";
        return stringToCheck.replaceAll(regex, "-").toLowerCase();
    }


}
