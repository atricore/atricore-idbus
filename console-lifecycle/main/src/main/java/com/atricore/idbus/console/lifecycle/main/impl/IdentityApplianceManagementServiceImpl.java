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

package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.activation._1_0.protocol.*;
import com.atricore.idbus.console.activation.main.client.ActivationClient;
import com.atricore.idbus.console.activation.main.client.ActivationClientFactory;
import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.ActivationService;
import com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.activation.main.spi.request.ConfigureAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.PlatformSupportedRequest;
import com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.ActivateSamplesResponse;
import com.atricore.idbus.console.activation.main.spi.response.ConfigureAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.PlatformSupportedResponse;
import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceState;
import com.atricore.idbus.console.lifecycle.main.domain.JDBCDriverDescriptor;
import com.atricore.idbus.console.lifecycle.main.domain.dao.*;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.exception.ExecEnvAlreadyActivated;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.*;
import com.atricore.idbus.console.lifecycle.main.spi.request.*;
import com.atricore.idbus.console.lifecycle.main.spi.response.*;
import com.atricore.idbus.console.lifecycle.main.util.MetadataUtil;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.common.support.jdbc.DriverDescriptor;
import org.atricore.idbus.kernel.common.support.jdbc.JDBCDriverManager;
import org.atricore.idbus.kernel.common.support.services.IdentityServiceLifecycle;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.w3._2000._09.xmldsig_.X509DataType;
import sun.security.provider.X509Factory;

import javax.jdo.FetchPlan;
import javax.xml.bind.JAXBElement;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IdentityApplianceManagementServiceImpl implements
        IdentityApplianceManagementService,
        InitializingBean,
        IdentityServiceLifecycle {

    private static final Log logger = LogFactory.getLog(IdentityApplianceManagementServiceImpl.class);

    // For local activations (faster)
    private ActivationService activationService;

    // For remote activations
    private ActivationClientFactory activationClientFactory;

    private ApplianceBuilder builder;

    private IdentityApplianceRegistry registry;

    private ApplianceDeployer deployer;

    private ApplianceValidator validator;

    private ApplianceMarshaller marshaller;

    private JDBCDriverManager jdbcDriverManager;

    private IdentityApplianceDAO identityApplianceDAO;

    private IdentityApplianceDefinitionDAO identityApplianceDefinitionDAO;

    private IdentityApplianceDeploymentDAO identityApplianceDeploymentDAO;

    private IdentityApplianceUnitDAO identityApplianceUnitDAO;

    private IdentitySourceDAO identitySourceDAO;

    private FederatedConnectionDAO federatedConnectionDAO;

    private UserInformationLookupDAO userInformationLookupDAO;

    private AccountLinkagePolicyDAO accountLinkagePolicyDAO;

    private AuthenticationContractDAO authenticationContractDAO;

    private AuthenticationMechanismDAO authenticationMechanismDAO;

    private AttributeProfileDAO attributeProfileDAO;

    private AuthenticationAssertionEmissionPolicyDAO authenticationAssertionEmissionPolicyDAO;

    private ResourceDAO resourceDAO;

    private boolean lazySyncAppliances = false;

    private boolean alreadySynchronizededAppliances = false;

    private boolean validateAppliances = true;

    private boolean enableDebugValidation = false;

    private Keystore sampleKeystore;

    private AccountLinkagePoliciesRegistry accountLinkagePoliciesRegistry;

    private IdentityMappingPoliciesRegistry identityMappingPoliciesRegistry;

    private ImpersonateUserPoliciesRegistry impersonateUserPoliciesRegistry;

    private BrandManager brandManger;

    private SubjectNameIdentifierPolicyRegistry  subjectNameIdentifierPolicyRegistry;

    public void afterPropertiesSet() throws Exception {
        if (sampleKeystore.getStore() != null &&
                (sampleKeystore.getStore().getValue() == null ||
                        sampleKeystore.getStore().getValue().length == 0)) {
            resolveResource(sampleKeystore.getStore());
        }

        if (sampleKeystore.getStore() == null &&
                sampleKeystore.getStore().getValue() == null ||
                sampleKeystore.getStore().getValue().length == 0) {
            logger.debug("Sample Keystore invalid or not found!");
        } else {
            logger.debug("Sample Keystore size " + sampleKeystore.getStore());
        }
    }


    @Transactional
    public void boot() throws IdentityServerException {
        logger.info("Initializing Identity Appliance Management serivce ....");
        try {
            syncAppliances();
        } catch (IdentityServerException e) {
            logger.error(e.getMessage(), e);
        }

        // Register buil-in Subject NameID Policies

        // Principal name
        SubjectNameIdentifierPolicy principalPolicy =
                new SubjectNameIdentifierPolicy ("samlr2-unspecified-nameidpolicy",
                        "Principal",
                        "samlr2.principal",
                        SubjectNameIDPolicyType.PRINCIPAL);
        this.subjectNameIdentifierPolicyRegistry.register(principalPolicy, null);

        // Email
        SubjectNameIdentifierPolicy emailPolicy =
                new SubjectNameIdentifierPolicy ("samlr2-email-nameidpolicy",
                        "Email Address",
                        "samlr2.email",
                        SubjectNameIDPolicyType.EMAIL);
        this.subjectNameIdentifierPolicyRegistry.register(emailPolicy, null);

        // Register built-in Account Linkage policies
        for (IdentityMappingType type : IdentityMappingType.values()) {
            if (type != IdentityMappingType.CUSTOM) {
                IdentityMappingPolicy policy = new IdentityMappingPolicy();
                policy.setName(type.getDisplayName());
                policy.setMappingType(type);

                this.identityMappingPoliciesRegistry.register(policy, null);
            }
        }

        for (AccountLinkEmitterType type : AccountLinkEmitterType.values()) {
            if (type != AccountLinkEmitterType.CUSTOM) {
                AccountLinkagePolicy policy = new AccountLinkagePolicy();
                policy.setName(type.getDisplayName());
                policy.setLinkEmitterType(type);

                this.accountLinkagePoliciesRegistry.register(policy, null);
            }
        }

        for (ImpersonateUserPolicyType type : ImpersonateUserPolicyType.values()) {
            if (type != ImpersonateUserPolicyType.CUSTOM) {
                ImpersonateUserPolicy policy = new ImpersonateUserPolicy();
                policy.setName(type.getDisplayName());
                policy.setImpersonateUserPolicyType(type);

                this.impersonateUserPoliciesRegistry.register(policy, null);
            }
        }



    }

    @Transactional
    public BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest request) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));
            appliance = buildAppliance(appliance, request.isDeploy());
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            return new BuildIdentityApplianceResponse(appliance);
        } catch (Exception e){
            logger.error("Error building identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    /**
     * Deploys an already existing Identity Appliance.
     * The appliance was previously created or imported and can by found in the list of appliances.
     */
    @Transactional
    public DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(req.getApplianceId()));
            appliance = deployAppliance(appliance,
                    req.getUsername(),
                    req.getPassword(),
                    req.getConfigureExecEnvs() != null ? req.getConfigureExecEnvs() : true);

            if (req.getStartAppliance()) {
                appliance = startAppliance(appliance);
            }
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            return new DeployIdentityApplianceResponse(appliance, true);
        } catch (Exception e){
            logger.error("Error deploying identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    /**
     * Undeploys an Identity Appliance.
     * The appliance was previously deployed, if the appliance is running this will first attempt to stop it.
     */
    @Transactional
    public UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(req.getApplianceId()));
            appliance = undeployAppliance(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            return new UndeployIdentityApplianceResponse (appliance);
        } catch (Exception e){
            logger.error("Error undeploying identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(req.getId()));
            appliance = startAppliance(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            return new StartIdentityApplianceResponse (appliance);
        } catch (Exception e){
            logger.error("Error starting identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(req.getId()));
            appliance = stopAppliance(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            return new StopIdentityApplianceResponse (appliance);
        } catch (Exception e){
            logger.error("Error stopping identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public DisposeIdentityApplianceResponse disposeIdentityAppliance(DisposeIdentityApplianceRequest req) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(req.getId()));
            appliance = disposeAppliance(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            return new DisposeIdentityApplianceResponse(appliance);
        } catch (Exception e){
            logger.error("Error disposing identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public ExportIdentityApplianceResponse exportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException {
        try {

            syncAppliances();

            if (logger.isTraceEnabled())
                logger.trace("Exporting appliance definition \n" + request.getApplianceId() + "\n");

            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));

            byte[] applianceBytes = marshaller.marshall(appliance);

            ExportIdentityApplianceResponse response = new  ExportIdentityApplianceResponse();
            response.setBytes(applianceBytes);
            response.setApplianceId(appliance.getId() + "");

            return response;
        } catch (Exception e) {
            logger.error("Error importing identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public ExportIdentityApplianceProjectResponse exportIdentityApplianceProject(ExportIdentityApplianceProjectRequest request) throws IdentityServerException {
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));
            byte[] zip = builder.exportProject(appliance);
            return new ExportIdentityApplianceProjectResponse(appliance.getName(),
                    appliance.getIdApplianceDefinition().getRevision(),
                    zip);

        } catch (Exception e){
            logger.error("Error exporting identity appliance project", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public ImportIdentityApplianceResponse importIdentityApplianceProject(ImportIdentityApplianceRequest request) throws IdentityServerException {

        try {
            syncAppliances();

            if (logger.isTraceEnabled())
                logger.trace("Importing appliance definition from zip file \n");

            final int BUFFER_SIZE = 2048;
            ByteArrayInputStream bIn = new ByteArrayInputStream(request.getBinaryAppliance());
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ZipInputStream zin = new ZipInputStream(bIn);
            ZipEntry entry;
            String appStr="";

            while((entry = zin.getNextEntry()) != null) {
                if (entry.getName() != null && entry.getName().endsWith("appliance.bin")) {
                    int count;
                    byte data[] = new byte[BUFFER_SIZE];
                    while ((count = zin.read(data, 0, BUFFER_SIZE)) != -1) {
                        bOut.write(data, 0, count);
                    }
                    bOut.flush();
                    appStr = bOut.toString();

                    bOut.close();
                    zin.close();
                    break;
                }
            }

            // 1. Unmarshall appliance
            IdentityAppliance appliance = new IdentityAppliance();
            appliance.setIdApplianceDefinitionBin(appStr);
            appliance = identityApplianceDAO.unmarshall(appliance);

            appliance.setNamespace(appliance.getIdApplianceDefinition().getNamespace());
            appliance.setDisplayName(appliance.getIdApplianceDefinition().getDisplayName());
            appliance.setDescription(appliance.getIdApplianceDefinition().getDescription());
            appliance.setState(IdentityApplianceState.PROJECTED.toString());
            validateAppliance(appliance, ApplianceValidator.Operation.IMPORT);
            debugAppliance(appliance, ApplianceValidator.Operation.IMPORT);
            appliance = identityApplianceDAO.save(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);

            if (logger.isTraceEnabled())
                logger.trace("Created Identity Appliance " + appliance.getId());

            // 4. Return the appliance
            ImportIdentityApplianceResponse response = new ImportIdentityApplianceResponse();
            response.setAppliance(appliance);

            return response;

        } catch (Exception e) {
            logger.error("Error importing identity appliance project from file", e);
            throw new IdentityServerException(e);
        }
    }

    @Deprecated
    @Transactional
    public ImportApplianceDefinitionResponse importApplianceDefinition(ImportApplianceDefinitionRequest request) throws IdentityServerException {

       try {
            syncAppliances();

            if (logger.isTraceEnabled())
                logger.trace("Importing appliance (console) definition from zip file \n");

            final int BUFFER_SIZE = 2048;
            ByteArrayInputStream bIn = new ByteArrayInputStream(request.getBytes());
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ZipInputStream zin = new ZipInputStream(bIn);
            ZipEntry entry;
            String appStr="";

            while((entry = zin.getNextEntry()) != null) {
                if (entry.getName() != null && entry.getName().endsWith("appliance.bin")) {
                    int count;
                    byte data[] = new byte[BUFFER_SIZE];
                    while ((count = zin.read(data, 0, BUFFER_SIZE)) != -1) {
                        bOut.write(data, 0, count);
                    }
                    bOut.flush();
                    appStr = bOut.toString();

                    bOut.close();
                    zin.close();
                    break;
                }
            }

            // 1. Unmarshall appliance
            IdentityAppliance appliance = new IdentityAppliance();
            appliance.setIdApplianceDefinitionBin(appStr);
            appliance = identityApplianceDAO.unmarshall(appliance);

            appliance.setNamespace(appliance.getIdApplianceDefinition().getNamespace());
            appliance.setDisplayName(appliance.getIdApplianceDefinition().getDisplayName());
            appliance.setDescription(appliance.getIdApplianceDefinition().getDescription());
            appliance.setState(IdentityApplianceState.PROJECTED.toString());
            validateAppliance(appliance, ApplianceValidator.Operation.IMPORT);
            debugAppliance(appliance, ApplianceValidator.Operation.IMPORT);
            appliance = identityApplianceDAO.save(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);

            if (logger.isTraceEnabled())
                logger.trace("Created Identity Appliance " + appliance.getId());

            // 4. Return the appliance
            ImportApplianceDefinitionResponse response = new ImportApplianceDefinitionResponse();
            response.setAppliance(appliance);

            return response;

        } catch(ApplianceValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error importing identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException {
        try {

            syncAppliances();

            String appId = req.getApplianceId();
            Long id = Long.parseLong(appId);

            IdentityAppliance appliance = identityApplianceDAO.findById(id);

            switch (req.getAction()) {
                case START:
                    startAppliance(appliance);
                    break;
                case STOP:
                    stopAppliance(appliance);
                    break;
                case RESTART:
                    restartAppliance(appliance);
                    break;
                case UNINSTALL:
                    undeployAppliance(appliance);
                    break;
                default:
                    throw new UnsupportedOperationException("Appliance lifecycle management action not supported: " + req.getAction());
            }

            debugAppliance(appliance, ApplianceValidator.Operation.ANY);

            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);

            ManageIdentityApplianceLifeCycleResponse response = new ManageIdentityApplianceLifeCycleResponse(req.getAction(), appliance);
            response.setStatusCode(StatusCode.STS_OK);
            return response;
        } catch (Exception e){
            logger.error("Error processing identity appliance lifecycle action", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public ActivateExecEnvResponse activateExecEnv(ActivateExecEnvRequest request) throws IdentityServerException {
        syncAppliances();

        try {
            ActivateExecEnvResponse response = new ActivateExecEnvResponse();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));

            if (logger.isDebugEnabled())
                logger.debug("Activating Execution Environment for appliance/exec-env " +
                        appliance.getId() + "/" + request.getExecEnvName());

            if (logger.isTraceEnabled())
                logger.trace("Looking for Execution Environment in " + appliance.getIdApplianceDefinition().getName());

            ExecutionEnvironment execEnv = null;
            for (ExecutionEnvironment e : appliance.getIdApplianceDefinition().getExecutionEnvironments()) {
                if (e.getName().equals(request.getExecEnvName())) {
                    execEnv = e;
                    break;
                }
            }

            if (execEnv == null)
                throw new IdentityServerException("Execution Environment "+request.getExecEnvName()+" not found in appliance " +
                        request.getApplianceId());

            execEnv.setInstallDemoApps(request.isActivateSamples());
            execEnv.setOverwriteOriginalSetup(request.isReplace());

            activateExecEnv(appliance, execEnv, request.isReactivate(), request.getUsername(), request.getPassword());
            debugAppliance(appliance, ApplianceValidator.Operation.ANY);

            return response;
        } catch (Exception e) {
            throw new IdentityServerException("Cannot activate Execution Environment for " +
                    request.getExecEnvName() + " : " + e.getMessage(), e);
        }
    }

    @Transactional
    public ValidateApplianceResponse validateApplinace(ValidateApplianceRequest request) throws IdentityServerException {

        ValidateApplianceResponse response = new ValidateApplianceResponse();

        IdentityAppliance appliance = request.getAppliance();
        if (appliance == null)
            appliance = this.identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));

        if (appliance == null)
            throw new ApplianceNotFoundException(Long.parseLong(request.getApplianceId()));

        if (logger.isDebugEnabled())
            logger.debug("Validating appliance " + appliance.getId());

        validator.validate(appliance, ApplianceValidator.Operation.ANY);
        return response;
    }

    @Transactional
    public AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException {
        AddIdentityApplianceResponse res = null;
        try {

            syncAppliances();

            IdentityAppliance appliance = req.getIdentityAppliance();

            if (appliance.getIdApplianceDefinition() == null)
                throw new IdentityServerException("Appliances must contain an appliance definition!");

            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
            if (logger.isTraceEnabled())
                logger.trace("Adding appliance " + applianceDef.getName());

            // Work on some defaults
            if (applianceDef.getDisplayName() == null)
                applianceDef.setDisplayName(applianceDef.getName());

            // Name
            if (appliance.getName() == null)
                appliance.setName(applianceDef.getName());
            else
                applianceDef.setName(appliance.getName());

            // Namespace
            if (appliance.getNamespace() == null)
                appliance.setNamespace(applianceDef.getNamespace());
            else
                applianceDef.setNamespace(appliance.getNamespace());

            // Displayname
            if (appliance.getDisplayName() == null)
                appliance.setDisplayName(applianceDef.getDisplayName());
            else
                applianceDef.setDisplayName(appliance.getDisplayName());

            // Description
            if (appliance.getDescription() == null)
                appliance.setDescription(applianceDef.getDescription());
            else
                applianceDef.setDescription(appliance.getDescription());

            for (Provider p : applianceDef.getProviders()) {
                if (p.getDisplayName() == null)
                    p.setDisplayName(p.getName());
            }

            for (ExecutionEnvironment ex : applianceDef.getExecutionEnvironments()) {
                if (ex.getDisplayName() == null)
                    ex.setDisplayName(ex.getName());
            }

            for (IdentitySource is : applianceDef.getIdentitySources()) {
                if (is.getDisplayName() == null)
                    is.setDisplayName(is.getName());
            }

            applianceDef.setRevision(1);
            applianceDef.setLastModification(new Date());
            appliance.setState(IdentityApplianceState.PROJECTED.toString());

            if (appliance.getIdApplianceDeployment() != null) {
                logger.warn("New appliances should not have deployment information!");
                appliance.setIdApplianceDeployment(null);
            }

            validateAppliance(appliance, ApplianceValidator.Operation.ADD);
            debugAppliance(appliance, ApplianceValidator.Operation.ADD);

            appliance = identityApplianceDAO.save(appliance);
            if (logger.isTraceEnabled())
                logger.trace("Added appliance " + appliance.getIdApplianceDefinition().getName() + " with ID:" + appliance.getId());

            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);

            res = new AddIdentityApplianceResponse();
            res.setAppliance(appliance);
        } catch (Exception e){
            logger.error("Error adding identity appliance", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException {
        UpdateIdentityApplianceResponse res = null;
        try {

            syncAppliances();
            IdentityAppliance appliance = request.getAppliance();

            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

            // We need to keep in sync appliance and appliance definition:
            if (applianceDef.getDisplayName() == null)
                applianceDef.setDisplayName(appliance.getName());

            if (applianceDef.getNamespace() == null)
                applianceDef.setNamespace(appliance.getNamespace());

            if (applianceDef.getDescription() == null)
                applianceDef.setDescription(appliance.getDescription());

            if (applianceDef.getName() == null)
                applianceDef.setName(appliance.getName());

            appliance.setName(applianceDef.getName());
            appliance.setDescription(applianceDef.getDescription());
            appliance.setNamespace(applianceDef.getNamespace());
            appliance.setDisplayName(applianceDef.getDisplayName());

            // Set some defaults
            for (Provider p : applianceDef.getProviders()) {
                if (p.getDisplayName() == null)
                    p.setDisplayName(p.getName());
            }

            for (ExecutionEnvironment ex : applianceDef.getExecutionEnvironments()) {
                if (ex.getDisplayName() == null)
                    ex.setDisplayName(ex.getName());
            }

            for (IdentitySource is : applianceDef.getIdentitySources()) {
                if (is.getDisplayName() == null)
                    is.setDisplayName(is.getName());
            }

            validateAppliance(appliance, ApplianceValidator.Operation.UPDATE);
            debugAppliance(appliance, ApplianceValidator.Operation.UPDATE);

            applianceDef.setLastModification(new Date());
            applianceDef.setRevision(applianceDef.getRevision() + 1);

            appliance = identityApplianceDAO.save(appliance);
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);

            res = new UpdateIdentityApplianceResponse(appliance);

        } catch (Exception e){
            logger.error("Error updating identity appliance", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException {
        LookupIdentityApplianceByIdResponse res = null;
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getIdentityApplianceId()));
            appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);
            res = new LookupIdentityApplianceByIdResponse();
            res.setIdentityAppliance(appliance);
        } catch (Exception e){
            logger.error("Error looking for identity appliance", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException{
        try {
            syncAppliances();
            if (!identityApplianceDAO.exists(Long.parseLong(req.getApplianceId())))
                throw new ApplianceNotFoundException(Long.parseLong(req.getApplianceId()));

            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(req.getApplianceId()));
            removeAppliance(appliance);

            RemoveIdentityApplianceResponse res = new RemoveIdentityApplianceResponse();
            return res;
        } catch (Exception e){
            logger.error("Error removing identity appliance", e);
            throw new IdentityServerException(e);
        }
    }

    @Transactional
    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException {



        ListIdentityAppliancesResponse res = null;
        try {
            syncAppliances();
            Collection<IdentityAppliance> appliances = identityApplianceDAO.list(req.isStartedOnly());
            appliances = identityApplianceDAO.detachCopyAll(appliances, FetchPlan.FETCH_SIZE_GREEDY);
            res = new ListIdentityAppliancesResponse();
            res.setIdentityAppliances(appliances);
        } catch (Exception e){
            logger.error("Error listing identity appliances", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListIdentityAppliancesByStateResponse listIdentityAppliancesByState(ListIdentityAppliancesByStateRequest req) throws IdentityServerException {
        syncAppliances();
        throw new UnsupportedOperationException("Not Supported!");
    }

    @Transactional
    public LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest request) throws IdentityServerException {
        LookupIdentityApplianceDefinitionByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding identity appliance definition by ID : "+ request.getIdentityApplianceDefinitionId());
            IdentityApplianceDefinition iad = identityApplianceDefinitionDAO.findById(Long.parseLong(request.getIdentityApplianceDefinitionId()));
            iad = identityApplianceDefinitionDAO.detachCopy(iad, FetchPlan.FETCH_SIZE_GREEDY);  //fetching providers and channels as well
            res = new LookupIdentityApplianceDefinitionByIdResponse();
            res.setIdentityApplianceDefinition(iad);
        } catch (Exception e){
            logger.error("Error retrieving identity appliance definition with id : " + request.getIdentityApplianceDefinitionId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException {
        ListIdentityApplianceDefinitionsResponse res = new ListIdentityApplianceDefinitionsResponse();
        try {
            syncAppliances();
            logger.debug("Listing all identity appliance definitions");
            Collection result = identityApplianceDefinitionDAO.findAll();
            res.getIdentityApplianceDefinitions().addAll(identityApplianceDefinitionDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));  //fetching providers and channels as well
        } catch (Exception e){
            logger.error("Error retrieving identity appliance definitions!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }


    /***************************************************************
     * List methods
     ***************************************************************/

    @Transactional
    public ListAvailableJDBCDriversResponse listAvailableJDBCDrivers(ListAvailableJDBCDriversRequest request) throws IdentityServerException {
        List<JDBCDriverDescriptor> jdbcDss = new ArrayList<JDBCDriverDescriptor>();
        for (DriverDescriptor ds : jdbcDriverManager.getRegisteredDrivers()) {
            JDBCDriverDescriptor jdbcDs = new JDBCDriverDescriptor();
            jdbcDs.setName(ds.getName());
            jdbcDs.setDefaultUrl(ds.getUrl());
            jdbcDs.setClassName(ds.getDriverclassName());
            jdbcDs.setWebSiteUrl(ds.getWebSiteUrl());
            jdbcDss.add(jdbcDs);
        }

        ListAvailableJDBCDriversResponse response = new ListAvailableJDBCDriversResponse();
        response.setDrivers(jdbcDss);
        return response;
    }


    @Transactional
    public ListIdentityVaultsResponse listIdentityVaults(ListIdentityVaultsRequest req) throws IdentityServerException {
        ListIdentityVaultsResponse res = new ListIdentityVaultsResponse();
        try {
            syncAppliances();
            logger.debug("Listing all identity vaults");
            Collection result = identitySourceDAO.findAll();
            res.getIdentityVaults().addAll(identitySourceDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));  //fetching user lookup information as well
        } catch (Exception e){
            logger.error("Error retrieving identity vaults!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListUserInformationLookupsResponse listUserInformationLookups(ListUserInformationLookupsRequest req) throws IdentityServerException {
        ListUserInformationLookupsResponse res = new ListUserInformationLookupsResponse();
        try {
            syncAppliances();
            logger.debug("Listing all user information lookups");
            Collection result = userInformationLookupDAO.findAll();
            res.getUserInfoLookups().addAll(userInformationLookupDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving user information lookups!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListAccountLinkagePoliciesResponse listAccountLinkagePolicies(ListAccountLinkagePoliciesRequest req) throws IdentityServerException {
        ListAccountLinkagePoliciesResponse res = new ListAccountLinkagePoliciesResponse();

        logger.debug("Listing all account linkage policies");

        // Add policies to response
        for (AccountLinkagePolicy policy : accountLinkagePoliciesRegistry.getPolicies()) {
            res.getAccountLinkagePolicies().add(policy);
        }

        return res;
    }

    @Transactional
    public ListAuthenticationContractsResponse listAuthenticationContracts(ListAuthenticationContractsRequest req) throws IdentityServerException {
        ListAuthenticationContractsResponse res = new ListAuthenticationContractsResponse();
        try {
            syncAppliances();
            logger.debug("Listing all authentication contracts");
            Collection result = authenticationContractDAO.findAll();
            res.getAuthContracts().addAll(authenticationContractDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving authentication contracts!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListAuthenticationMechanismsResponse listAuthenticationMechanisms(ListAuthenticationMechanismsRequest req) throws IdentityServerException {
        ListAuthenticationMechanismsResponse res = new ListAuthenticationMechanismsResponse();
        try {
            syncAppliances();
            logger.debug("Listing all authentication mechanisms");
            Collection result = authenticationMechanismDAO.findAll();
            res.getAuthMechanisms().addAll(authenticationMechanismDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving authentication mechanisms!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListAttributeProfilesResponse listAttributeProfiles(ListAttributeProfilesRequest req) throws IdentityServerException {
        ListAttributeProfilesResponse res = new ListAttributeProfilesResponse();
        try {
            syncAppliances();
            logger.debug("Listing all attribute profiles");
            Collection result = attributeProfileDAO.findAll();
            res.getAttributeProfiles().addAll(attributeProfileDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving attribute profiles!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListAuthAssertionEmissionPoliciesResponse listAuthAssertionEmissionPolicies(ListAuthAssertionEmissionPoliciesRequest req) throws IdentityServerException {
        ListAuthAssertionEmissionPoliciesResponse res = new ListAuthAssertionEmissionPoliciesResponse();
        try {
            syncAppliances();
            logger.debug("Listing all authentication assertion emission policies");
            Collection result = authenticationAssertionEmissionPolicyDAO.findAll();
            res.getAuthEmissionPolicies().addAll(authenticationAssertionEmissionPolicyDAO.detachCopyAll(result, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving authentication assertion emission policies!!!", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public ListIdentityMappingPoliciesResponse listIdentityMappingPolicies(ListIdentityMappingPoliciesRequest req) throws IdentityServerException {
        ListIdentityMappingPoliciesResponse res = new ListIdentityMappingPoliciesResponse();

        logger.debug("Listing all identity mapping policies");

        // Add policies to response
        for (IdentityMappingPolicy policy : identityMappingPoliciesRegistry.getPolicies()) {
            res.getIdentityMappingPolicies().add(policy);
        }


        return res;
    }

    @Transactional
    public ListSubjectNameIDPoliciesResponse listSubjectNameIDPolicies(ListSubjectNameIDPoliciesRequest req) throws IdentityServerException {
        ListSubjectNameIDPoliciesResponse res = new ListSubjectNameIDPoliciesResponse();

        for (SubjectNameIdentifierPolicy policy : subjectNameIdentifierPolicyRegistry.getPolicies()) {
            res.getPolicies().add(policy);
        }

        return res;
    }

    public ListImpersonateUserPoliciesResponse listImpersonateUserPolicies(ListImpersonateUserPoliciesRequest req) throws IdentityServerException {
        ListImpersonateUserPoliciesResponse res = new ListImpersonateUserPoliciesResponse();

        logger.debug("Listing all impersonate user policies");

        // Add policies to response
        for (ImpersonateUserPolicy policy : impersonateUserPoliciesRegistry.getPolicies()) {
            res.getImpersonateUserPolicies().add(policy);
        }

        return res;
    }

    public ListUserDashboardBrandingsResponse listUserDashboardBrandings(ListUserDashboardBrandingsRequest req) throws IdentityServerException {
        ListUserDashboardBrandingsResponse res = new ListUserDashboardBrandingsResponse();
        try {

            logger.debug("Listing all user dashboard brandings");

            // Add policies to response
            Collection<BrandingDefinition> brandings = brandManger.list();

            for (BrandingDefinition branding : brandings) {
                // Use the runtime ID here ...
                res.getBrandings().add(new UserDashboardBranding(branding.getWebBrandingId(), branding.getDescription()));
            }

            return res;
        } catch (BrandingServiceException e) {
            throw new IdentityServerException(e);
        }
    }

    /***************************************************************
     * Lookup methods
     ***************************************************************/

    @Transactional
    public LookupIdentityVaultByIdResponse lookupIdentityVaultById(LookupIdentityVaultByIdRequest req) throws IdentityServerException {
        LookupIdentityVaultByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding identity vault by ID : "+ req.getIdentityVaultId());
            IdentitySource identitySource = identitySourceDAO.findById(req.getIdentityVaultId());
            res = new LookupIdentityVaultByIdResponse();
            res.setIdentityVault(identitySourceDAO.detachCopy(identitySource, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving identity vault with id : " + req.getIdentityVaultId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupUserInformationLookupByIdResponse lookupUserInformationLookupById(LookupUserInformationLookupByIdRequest req) throws IdentityServerException {
        LookupUserInformationLookupByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding user information lookup by ID : "+ req.getUserInformationLookupId());
            UserInformationLookup userInformationLookup = userInformationLookupDAO.findById(req.getUserInformationLookupId());
            res = new LookupUserInformationLookupByIdResponse();
            res.setUserInfoLookup(userInformationLookupDAO.detachCopy(userInformationLookup, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving user information lookup with id : " + req.getUserInformationLookupId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupAccountLinkagePolicyByIdResponse lookupAccountLinkagePolicyById(LookupAccountLinkagePolicyByIdRequest req) throws IdentityServerException {
        LookupAccountLinkagePolicyByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding account linkage policy by ID : "+ req.getAccountLinkagePolicyId());
            AccountLinkagePolicy policy = accountLinkagePolicyDAO.findById(req.getAccountLinkagePolicyId());
            res = new LookupAccountLinkagePolicyByIdResponse();
            res.setAccountLinkagePolicy(accountLinkagePolicyDAO.detachCopy(policy, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving account linkage policy with id : " + req.getAccountLinkagePolicyId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupAuthenticationContractByIdResponse lookupAuthenticationContractById(LookupAuthenticationContractByIdRequest req) throws IdentityServerException {
        LookupAuthenticationContractByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding authentication contract by ID : "+ req.getAuthenticationContactId());
            AuthenticationContract authenticationContract = authenticationContractDAO.findById(req.getAuthenticationContactId());
            res = new LookupAuthenticationContractByIdResponse();
            res.setAuthenticationContract(authenticationContractDAO.detachCopy(authenticationContract, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving authentication contract with id : " + req.getAuthenticationContactId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupAuthenticationMechanismByIdResponse lookupAuthenticationMechanismById(LookupAuthenticationMechanismByIdRequest req) throws IdentityServerException {
        LookupAuthenticationMechanismByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding authentication mechanism by ID : "+ req.getAuthMechanismId());
            AuthenticationMechanism authenticationMechanism = authenticationMechanismDAO.findById(req.getAuthMechanismId());
            res = new LookupAuthenticationMechanismByIdResponse();
            res.setAuthenticationMechanism(authenticationMechanismDAO.detachCopy(authenticationMechanism, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving authentication mechanism with id : " + req.getAuthMechanismId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupAttributeProfileByIdResponse lookupAttributeProfileById(LookupAttributeProfileByIdRequest req) throws IdentityServerException {
        LookupAttributeProfileByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding attribute profile by ID : "+ req.getAttributeProfileId());
            AttributeProfile attributeProfile = attributeProfileDAO.findById(req.getAttributeProfileId());
            res = new LookupAttributeProfileByIdResponse();
            res.setAttributeProfile(attributeProfileDAO.detachCopy(attributeProfile, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving attribute profile with id : " + req.getAttributeProfileId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupAuthAssertionEmissionPolicyByIdResponse lookupAuthAssertionEmissionPolicyById(LookupAuthAssertionEmissionPolicyByIdRequest req) throws IdentityServerException {
        LookupAuthAssertionEmissionPolicyByIdResponse res = null;
        try {
            syncAppliances();
            logger.debug("Finding authentication assertion emission policy by ID : "+ req.getAuthAssertionEmissionPolicyId());
            AuthenticationAssertionEmissionPolicy policy = authenticationAssertionEmissionPolicyDAO.findById(req.getAuthAssertionEmissionPolicyId());
            res = new LookupAuthAssertionEmissionPolicyByIdResponse();
            res.setPolicy(authenticationAssertionEmissionPolicyDAO.detachCopy(policy, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error retrieving authentication assertion emission policy with id : " + req.getAuthAssertionEmissionPolicyId(), e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public AddResourceResponse addResource(AddResourceRequest req) throws IdentityServerException {
        AddResourceResponse res = null;
        try {
            syncAppliances();
            logger.debug("Persisting resource with name: " + req.getResource().getName());
            Resource resource = resourceDAO.save(req.getResource());
            res = new AddResourceResponse();
            res.setResource(resourceDAO.detachCopy(resource, FetchPlan.FETCH_SIZE_GREEDY));
        } catch (Exception e){
            logger.error("Error adding resource", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupResourceByIdResponse lookupResourceById(LookupResourceByIdRequest req) throws IdentityServerException {
        LookupResourceByIdResponse res = null;
        try {
            syncAppliances();
            Long id = Long.parseLong(req.getResourceId());
            Resource resource = resourceDAO.findById(id);
            resource = resourceDAO.detachCopy(resource, FetchPlan.FETCH_SIZE_GREEDY);
            res = new LookupResourceByIdResponse();
            res.setResource(resource);
        } catch (Exception e){
            logger.error("Error looking for resource", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    @Transactional
    public LookupIdentityMappingPolicyByIdResponse lookupIdentityMappingPolicyById(LookupIdentityMappingPolicyByIdRequest req) throws IdentityServerException {
        // TODO : Implement me!
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //

    public GetMetadataInfoResponse getMetadataInfo(GetMetadataInfoRequest req) throws IdentityServerException {
        GetMetadataInfoResponse res = null;
        try {
            MetadataDefinition md = MetadataUtil.loadMetadataDefinition(req.getMetadata());
            res = new GetMetadataInfoResponse();
            // entity id
            String entityId = MetadataUtil.findEntityId(md);
            res.setEntityId(entityId);
            SSODescriptorType ssoDescriptor = MetadataUtil.findSSODescriptor(md, req.getRole() + "Descriptor");
            if (ssoDescriptor != null) {
                // profiles
                if (ssoDescriptor.getSingleLogoutService().size() > 0) {
                    res.setSloEnabled(true);
                }
                if (ssoDescriptor instanceof IDPSSODescriptorType &&
                        ((IDPSSODescriptorType)ssoDescriptor).getSingleSignOnService().size() > 0) {
                    res.setSsoEnabled(true);
                }

                // bindings
                List<EndpointType> endpoints = new ArrayList<EndpointType>();
                endpoints.addAll(ssoDescriptor.getArtifactResolutionService());
                endpoints.addAll(ssoDescriptor.getSingleLogoutService());
                endpoints.addAll(ssoDescriptor.getManageNameIDService());
                if (ssoDescriptor instanceof IDPSSODescriptorType) {
                    endpoints.addAll(((IDPSSODescriptorType)ssoDescriptor).getSingleSignOnService());
                    endpoints.addAll(((IDPSSODescriptorType)ssoDescriptor).getAssertionIDRequestService());
                    endpoints.addAll(((IDPSSODescriptorType)ssoDescriptor).getNameIDMappingService());
                    if (((IDPSSODescriptorType)ssoDescriptor).isWantAuthnRequestsSigned() != null)
                        res.setWantAuthnRequestsSigned(((IDPSSODescriptorType)ssoDescriptor).isWantAuthnRequestsSigned());
                } else if (ssoDescriptor instanceof SPSSODescriptorType) {
                    endpoints.addAll(((SPSSODescriptorType)ssoDescriptor).getAssertionConsumerService());
                    if (((SPSSODescriptorType)ssoDescriptor).isWantAssertionsSigned() != null)
                        res.setWantAssertionSigned(((SPSSODescriptorType)ssoDescriptor).isWantAssertionsSigned());
                    if (((SPSSODescriptorType)ssoDescriptor).isAuthnRequestsSigned() != null)
                        res.setSignAuthnRequests(((SPSSODescriptorType)ssoDescriptor).isAuthnRequestsSigned());
                }
                for (EndpointType endpoint : endpoints) {
                    if (endpoint.getBinding().equals(SSOBinding.SAMLR2_POST.getValue())) {
                        res.setPostEnabled(true);
                    } else if (endpoint.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {
                        res.setRedirectEnabled(true);
                    } else if (endpoint.getBinding().equals(SSOBinding.SAMLR2_ARTIFACT.getValue())) {
                        res.setArtifactEnabled(true);
                    } else if (endpoint.getBinding().equals(SSOBinding.SAMLR2_SOAP.getValue())) {
                        res.setSoapEnabled(true);
                    }
                    if (res.isPostEnabled() && res.isRedirectEnabled() &&
                            res.isArtifactEnabled() && res.isSoapEnabled()) {
                        break;
                    }
                }

                // certificates
                for (KeyDescriptorType keyMd : ssoDescriptor.getKeyDescriptor()) {
                    X509Certificate x509Cert = getCertificate(keyMd);
                    if (x509Cert != null) {
                        if (KeyTypes.SIGNING.equals(keyMd.getUse())) {
                            res.setSigningCertIssuerDN(x509Cert.getIssuerX500Principal().getName());
                            res.setSigningCertSubjectDN(x509Cert.getSubjectX500Principal().getName());
                            res.setSigningCertNotBefore(x509Cert.getNotBefore());
                            res.setSigningCertNotAfter(x509Cert.getNotAfter());
                        } else if (KeyTypes.ENCRYPTION.equals(keyMd.getUse())) {
                            res.setEncryptionCertIssuerDN(x509Cert.getIssuerX500Principal().getName());
                            res.setEncryptionCertSubjectDN(x509Cert.getSubjectX500Principal().getName());
                            res.setEncryptionCertNotBefore(x509Cert.getNotBefore());
                            res.setEncryptionCertNotAfter(x509Cert.getNotAfter());
                        }
                    }
                }
            }
        } catch (Exception e){
            logger.error("Error retrieving metadata info", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    public GetCertificateInfoResponse getCertificateInfo(GetCertificateInfoRequest req) throws IdentityServerException {
        GetCertificateInfoResponse res = new GetCertificateInfoResponse();
        try {
            SamlR2ProviderConfig config = req.getConfig();

            // signing certificate
            Keystore signer = config.getSigner();
            if (signer == null && config.isUseSampleStore()) {
                signer = sampleKeystore;
            }
            if (signer != null) {
                byte[] keystore = signer.getStore().getValue();
                KeyStore jks = KeyStore.getInstance("PKCS#12".equals(signer.getType()) ? "PKCS12" : "JKS");
                jks.load(new ByteArrayInputStream(keystore), signer.getPassword().toCharArray());
                X509Certificate signerCertificate = (X509Certificate) jks.getCertificate(signer.getCertificateAlias());
                res.setSigningCertIssuerDN(signerCertificate.getIssuerX500Principal().getName());
                res.setSigningCertSubjectDN(signerCertificate.getSubjectX500Principal().getName());
                res.setSigningCertNotBefore(signerCertificate.getNotBefore());
                res.setSigningCertNotAfter(signerCertificate.getNotAfter());
            }

            // encryption certificate
            Keystore encrypter = config.getEncrypter();
            if (encrypter == null && config.isUseSampleStore()) {
                encrypter = sampleKeystore;
            }
            if (encrypter != null) {
                byte[] keystore = encrypter.getStore().getValue();
                KeyStore jks = KeyStore.getInstance("PKCS#12".equals(encrypter.getType()) ? "PKCS12" : "JKS");
                jks.load(new ByteArrayInputStream(keystore), encrypter.getPassword().toCharArray());
                X509Certificate encrypterCertificate = (X509Certificate) jks.getCertificate(encrypter.getCertificateAlias());
                res.setEncryptionCertIssuerDN(encrypterCertificate.getIssuerX500Principal().getName());
                res.setEncryptionCertSubjectDN(encrypterCertificate.getSubjectX500Principal().getName());
                res.setEncryptionCertNotBefore(encrypterCertificate.getNotBefore());
                res.setEncryptionCertNotAfter(encrypterCertificate.getNotAfter());
            }
        } catch (Exception e){
            logger.error("Error retrieving certificate info", e);
            throw new IdentityServerException(e);
        }
        return res;
    }

    public ExportProviderCertificateResponse exportProviderCertificate(ExportProviderCertificateRequest request) throws IdentityServerException {
        ExportProviderCertificateResponse response = new ExportProviderCertificateResponse();
        try {
            SamlR2ProviderConfig config = request.getConfig();

            // signer and encrypter are the same
            Keystore signer = config.getSigner();
            if (signer == null && config.isUseSampleStore()) {
                signer = sampleKeystore;
            }
            if (signer != null) {
                byte[] keystore = signer.getStore().getValue();
                KeyStore jks = KeyStore.getInstance("PKCS#12".equals(signer.getType()) ? "PKCS12" : "JKS");
                jks.load(new ByteArrayInputStream(keystore), signer.getPassword().toCharArray());
                X509Certificate signerCertificate = (X509Certificate) jks.getCertificate(signer.getCertificateAlias());
                if (signerCertificate != null) {
                    StringWriter stringWriter = new StringWriter();
                    BufferedWriter bufWriter = new BufferedWriter(stringWriter);

                    // write header
                    bufWriter.write(X509Factory.BEGIN_CERT);
                    bufWriter.newLine();

                    // write encoded
                    char[]  buf = new char[64];
                    byte[] encoded = Base64.encodeBase64(signerCertificate.getEncoded());

                    for (int i = 0; i < encoded.length; i += buf.length) {
                        int index = 0;

                        while (index != buf.length) {
                            if ((i + index) >= encoded.length) {
                                break;
                            }
                            buf[index] = (char) encoded[i + index];
                            index++;
                        }
                        bufWriter.write(buf, 0, index);
                        bufWriter.newLine();
                    }

                    // write footer
                    bufWriter.write(X509Factory.END_CERT);
                    bufWriter.newLine();

                    // flush and close
                    bufWriter.flush();
                    stringWriter.close();
                    bufWriter.close();

                    // set response
                    response.setCertificate(stringWriter.toString().getBytes());
                }
            }
        } catch (Exception e){
            logger.error("Error exporting provider certificate", e);
            throw new IdentityServerException(e);
        }
        return response;
    }

    public ExportMetadataResponse exportMetadata(ExportMetadataRequest request) throws IdentityServerException {
        ExportMetadataResponse response = new ExportMetadataResponse();
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));
            response.setMetadata(builder.exportMetadata(appliance, request.getProviderName(), request.getChannelName()));
        } catch (Exception e){
            logger.error("Error exporting SAML metadata", e);
            throw new IdentityServerException(e);
        }
        return response;
    }

    public ExportAgentConfigResponse exportAgentConfig(ExportAgentConfigRequest request) throws IdentityServerException {
        ExportAgentConfigResponse response = new ExportAgentConfigResponse();
        try {
            syncAppliances();
            IdentityAppliance appliance = identityApplianceDAO.findById(Long.parseLong(request.getApplianceId()));
            ExecutionEnvironment execEnv = null;
            for (ExecutionEnvironment executionEnvironment : appliance.getIdApplianceDefinition().getExecutionEnvironments()) {
                if (executionEnvironment.getName().equals(request.getExecEnvName())) {
                    execEnv = executionEnvironment;
                    break;
                }
            }
            if (execEnv != null) {
                String configFileName = "josso-agent-";
                if (execEnv.getPlatformId().startsWith("iis")) {
                    configFileName += "config.ini";
                } else {
                    configFileName += execEnv.getName().replaceAll("[ .]", "-").toLowerCase() + "-config.xml";
                }
                response.setFileName(configFileName);
                response.setAgentConfig(builder.exportJosso1Configuration(appliance, execEnv.getName()));
            } else {
                response.setStatusCode(StatusCode.STS_ERROR);
                response.setErrorMsg("Error exporting agent config: no execution environment with the given name!!!");
            }
        } catch (Exception e){
            logger.error("Error exporting agent config", e);
            throw new IdentityServerException(e);
        }
        return response;
    }

    private X509Certificate getCertificate(KeyDescriptorType keyMd) {
        X509Certificate x509Cert = null;
        byte[] x509CertificateBin = null;

        if (keyMd.getKeyInfo() != null) {

            // Get inside Key Info
            List contentMd = keyMd.getKeyInfo().getContent();
            if (contentMd != null && contentMd.size() > 0) {

                for (Object o : contentMd) {

                    if (o instanceof JAXBElement) {
                        JAXBElement e = (JAXBElement) o;
                        if (e.getValue() instanceof X509DataType) {

                            X509DataType x509Data = (X509DataType) e.getValue();

                            for (Object x509Content : x509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName()) {
                                if (x509Content instanceof JAXBElement) {
                                    JAXBElement x509Certificate = (JAXBElement) x509Content;

                                    if (x509Certificate.getName().getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") &&
                                            x509Certificate.getName().getLocalPart().equals("X509Certificate")) {

                                        x509CertificateBin = (byte[]) x509Certificate.getValue();
                                        break;
                                    }
                                }
                            }

                        }
                    }

                    if (x509CertificateBin != null)
                        break;
                }
            }
        } else {
            logger.debug("Metadata Key Descriptor does not have KeyInfo " + keyMd.toString());
        }

        if (x509CertificateBin != null) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                x509Cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(x509CertificateBin));
            } catch (CertificateException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return x509Cert;
    }

    protected void resolveResource(Resource resource) throws IOException {

        InputStream is = getClass().getResourceAsStream(resource.getUri());
        if (is != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
                byte[] buff = new byte[4096];
                int read = is.read(buff, 0, 4096);
                while (read > 0) {
                    baos.write(buff, 0, read);
                    read = is.read(buff, 0, 4096);
                }
                resource.setValue(baos.toByteArray());

            } finally {
                if (is != null) try { is.close(); } catch (IOException e) {/**/}
            }
        }

    }


// -------------------------------------------------< Properties >

    public ApplianceBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(ApplianceBuilder builder) {
        this.builder = builder;
    }

    public IdentityApplianceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(IdentityApplianceRegistry registry) {
        this.registry = registry;
    }

    public ApplianceDeployer getDeployer() {
        return deployer;
    }

    public void setDeployer(ApplianceDeployer deployer) {
        this.deployer = deployer;
    }

    public ApplianceValidator getValidator() {
        return validator;
    }

    public void setValidator(ApplianceValidator validator) {
        this.validator = validator;
    }

    public ApplianceMarshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(ApplianceMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public AccountLinkagePoliciesRegistry getAccountLinkagePoliciesRegistry() {
        return accountLinkagePoliciesRegistry;
    }

    public void setAccountLinkagePoliciesRegistry(AccountLinkagePoliciesRegistry accountLinkagePoliciesRegistry) {
        this.accountLinkagePoliciesRegistry = accountLinkagePoliciesRegistry;
    }

    public SubjectNameIdentifierPolicyRegistry getSubjectNameIdentifierPolicyRegistry() {
        return subjectNameIdentifierPolicyRegistry;
    }

    public void setSubjectNameIdentifierPolicyRegistry(SubjectNameIdentifierPolicyRegistry subjectNameIdentifierPolicyRegistry) {
        this.subjectNameIdentifierPolicyRegistry = subjectNameIdentifierPolicyRegistry;
    }

    public IdentityMappingPoliciesRegistry getIdentityMappingPoliciesRegistry() {
        return identityMappingPoliciesRegistry;
    }

    public void setIdentityMappingPoliciesRegistry(IdentityMappingPoliciesRegistry identityMappingPoliciesRegistry) {
        this.identityMappingPoliciesRegistry = identityMappingPoliciesRegistry;
    }

    public ImpersonateUserPoliciesRegistry getImpersonateUserPoliciesRegistry() {
        return impersonateUserPoliciesRegistry;
    }

    public void setImpersonateUserPoliciesRegistry(ImpersonateUserPoliciesRegistry impersonateUserPoliciesRegistry) {
        this.impersonateUserPoliciesRegistry = impersonateUserPoliciesRegistry;
    }

    public BrandManager getBrandManger() {
        return brandManger;
    }

    public void setBrandManger(BrandManager brandManger) {
        this.brandManger = brandManger;
    }

    public IdentityApplianceDAO getIdentityApplianceDAO() {
        return identityApplianceDAO;
    }

    public void setIdentityApplianceDAO(IdentityApplianceDAO identityApplianceDAO) {
        this.identityApplianceDAO = identityApplianceDAO;
    }

    public IdentityApplianceDefinitionDAO getIdentityApplianceDefinitionDAO() {
        return identityApplianceDefinitionDAO;
    }

    public void setIdentityApplianceDefinitionDAO(IdentityApplianceDefinitionDAO identityApplianceDefinitionDAO) {
        this.identityApplianceDefinitionDAO = identityApplianceDefinitionDAO;
    }

    public IdentityApplianceDeploymentDAO getIdentityApplianceDeploymentDAO() {
        return identityApplianceDeploymentDAO;
    }

    public void setIdentityApplianceDeploymentDAO(IdentityApplianceDeploymentDAO identityApplianceDeploymentDAO) {
        this.identityApplianceDeploymentDAO = identityApplianceDeploymentDAO;
    }

    public IdentityApplianceUnitDAO getIdentityApplianceUnitDAO() {
        return identityApplianceUnitDAO;
    }

    public void setIdentityApplianceUnitDAO(IdentityApplianceUnitDAO identityApplianceUnitDAO) {
        this.identityApplianceUnitDAO = identityApplianceUnitDAO;
    }

    public IdentitySourceDAO getIdentitySourceDAO() {
        return identitySourceDAO;
    }

    public void setIdentitySourceDAO(IdentitySourceDAO identitySourceDAO) {
        this.identitySourceDAO = identitySourceDAO;
    }

    public UserInformationLookupDAO getUserInformationLookupDAO() {
        return userInformationLookupDAO;
    }

    public void setUserInformationLookupDAO(UserInformationLookupDAO userInformationLookupDAO) {
        this.userInformationLookupDAO = userInformationLookupDAO;
    }

    public FederatedConnectionDAO getFederatedConnectionDAO() {
        return federatedConnectionDAO;
    }

    public void setFederatedConnectionDAO(FederatedConnectionDAO federatedConnectionDAO) {
        this.federatedConnectionDAO = federatedConnectionDAO;
    }

    public AccountLinkagePolicyDAO getAccountLinkagePolicyDAO() {
        return accountLinkagePolicyDAO;
    }

    public void setAccountLinkagePolicyDAO(AccountLinkagePolicyDAO accountLinkagePolicyDAO) {
        this.accountLinkagePolicyDAO = accountLinkagePolicyDAO;
    }

    public AuthenticationContractDAO getAuthenticationContractDAO() {
        return authenticationContractDAO;
    }

    public void setAuthenticationContractDAO(AuthenticationContractDAO authenticationContractDAO) {
        this.authenticationContractDAO = authenticationContractDAO;
    }

    public AuthenticationMechanismDAO getAuthenticationMechanismDAO() {
        return authenticationMechanismDAO;
    }

    public void setAuthenticationMechanismDAO(AuthenticationMechanismDAO authenticationMechanismDAO) {
        this.authenticationMechanismDAO = authenticationMechanismDAO;
    }

    public AttributeProfileDAO getAttributeProfileDAO() {
        return attributeProfileDAO;
    }

    public void setAttributeProfileDAO(AttributeProfileDAO attributeProfileDAO) {
        this.attributeProfileDAO = attributeProfileDAO;
    }

    public AuthenticationAssertionEmissionPolicyDAO getAuthenticationAssertionEmissionPolicyDAO() {
        return authenticationAssertionEmissionPolicyDAO;
    }

    public void setAuthenticationAssertionEmissionPolicyDAO(AuthenticationAssertionEmissionPolicyDAO authenticationAssertionEmissionPolicyDAO) {
        this.authenticationAssertionEmissionPolicyDAO = authenticationAssertionEmissionPolicyDAO;
    }

    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    public void setResourceDAO(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }

    public boolean isLazySyncAppliances() {
        return lazySyncAppliances;
    }

    public void setLazySyncAppliances(boolean lazySyncAppliances) {
        this.lazySyncAppliances = lazySyncAppliances;
    }

    public boolean isValidateAppliances() {
        return validateAppliances;
    }

    public void setValidateAppliances(boolean enableValidation) {
        this.validateAppliances = enableValidation;
    }

    public boolean isEnableDebugValidation() {
        return enableDebugValidation;
    }

    public void setEnableDebugValidation(boolean enableDebugValidation) {
        this.enableDebugValidation = enableDebugValidation;
    }

    public ActivationService getActivationService() {
        return activationService;
    }

    public void setActivationService(ActivationService activationService) {
        this.activationService = activationService;
    }

    public ActivationClientFactory getActivationClientFactory() {
        return activationClientFactory;
    }

    public void setActivationClientFactory(ActivationClientFactory activationClientFactory) {
        this.activationClientFactory = activationClientFactory;
    }

    public JDBCDriverManager getJdbcDriverManager() {
        return jdbcDriverManager;
    }

    public void setJdbcDriverManager(JDBCDriverManager jdbcDriverManager) {
        this.jdbcDriverManager = jdbcDriverManager;
    }

    public Keystore getSampleKeystore() {
        return sampleKeystore;
    }

    public void setSampleKeystore(Keystore sampleKeystore) {
        this.sampleKeystore = sampleKeystore;
    }
// -------------------------------------------------< Protected Utils , they need transactional context !>

    protected void validateAppliance(IdentityAppliance appliance, ApplianceValidator.Operation operation) throws ApplianceValidationException {

        IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

        if (appliance.getName() == null)
            appliance.setName(applianceDef.getName());

        if (!isValidateAppliances())
            return;

        try{
            validator.validate(appliance, operation);
        } catch (ApplianceValidationException e) {

            logger.error(e.getMessage());
            int i = 1;
            for (ValidationError ve : e.getErrors()) {
                logger.error(i + " : " + ve.getMsg());
                i++;
            }

            throw e;
        }
    }

    protected void debugAppliance(IdentityAppliance appliance, ApplianceValidator.Operation operation) {

        if (!isEnableDebugValidation())
            return ;

        try {
            validateAppliance(appliance, operation);
            logger.debug("Appliance " + appliance.getId() + " is valid");

        } catch (ApplianceValidationException e) {
            logger.error(e.getMessage(), e);

            for (ValidationError err : e.getErrors()) {
                if (err.getError() != null)
                    logger.debug(err.getMsg(), err.getError());
                else
                    logger.debug(err.getMsg());
            }
        }


    }

    protected void configureExecEnv(IdentityAppliance appliance,
                                    ExecutionEnvironment execEnv,
                                    String username,
                                    String password) throws IdentityServerException {

        try {


            String agentCfgLocation = appliance.getNamespace();
            agentCfgLocation = agentCfgLocation.replace('.', '/');

            agentCfgLocation += "/" + appliance.getIdApplianceDefinition().getName();
            agentCfgLocation += "/" + appliance.getNamespace() +
                    "." + appliance.getIdApplianceDefinition().getName() + ".idau";
            agentCfgLocation += "/1.0." + appliance.getIdApplianceDeployment().getDeployedRevision();

            String agentCfgName = appliance.getNamespace() + "." +
                    appliance.getIdApplianceDefinition().getName() + ".idau-1.0." +
                    appliance.getIdApplianceDeployment().getDeployedRevision() + "-" + execEnv.getName().toLowerCase();

            // Be carefull with this:
            if (appliance.getIdApplianceDeployment().getDeployedRevision() < appliance.getIdApplianceDefinition().getRevision())
                logger.warn("Activating undeployed appliance revision for " + appliance.getId());

            if (execEnv.getPlatformId().startsWith("iis"))
                agentCfgName += ".ini";
            else
                agentCfgName += ".xml";

            String agentCfg = agentCfgLocation + "/" + agentCfgName;
            agentCfg = agentCfg.toLowerCase();

            if (logger.isDebugEnabled())
                logger.debug("Activating Execution Environment " + execEnv.getName() + " using JOSSO Agent Config file  : " + agentCfg );

            switch (execEnv.getType()) {
                case LOCAL:
                    ConfigureAgentRequest activationReq = doMakeConfigureAgentRequest(execEnv);
                    activationReq.setJossoAgentConfigUri(agentCfg);
                    activationReq.setReplaceConfig(execEnv.isOverwriteOriginalSetup());

                    ConfigureAgentResponse actiationResponse = activationService.configureAgent(activationReq);
                    break;

                case REMOTE:

                    if (username != null && password != null) {
                        ConfigureAgentRequestType wsActivationReq = doMakeWsConfigureAgentRequest(execEnv, username, password);
                        ActivationClient wsClient = activationClientFactory.newActivationClient(execEnv.getLocation());

                        InputStream is = null;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);

                        try {

                            FileSystemManager fs = VFS.getManager();
                            FileObject homeDir = fs.resolveFile(getHomeDir());
                            FileObject appliancesDir = homeDir.resolveFile("appliances");
                            FileObject agentCfgFile = appliancesDir.resolveFile(agentCfg);

                            is = agentCfgFile.getContent().getInputStream();
                            IOUtils.copy(is, baos);

                            //  Attach activation resources to request
                            AgentConfigResourceType agentCfgResource = new AgentConfigResourceType ();

                            // Force resource name to josso-agent-config.
                            agentCfgResource.setName("josso-agent-config." + (execEnv.getPlatformId().startsWith("iis") ? "ini" : "xml"));
                            agentCfgResource.setConfigResourceContent(baos.toString());
                            agentCfgResource.setReplaceOriginal(execEnv.isOverwriteOriginalSetup());

                            wsActivationReq.getAgentConfigResource().add(agentCfgResource);

                        } catch (IOException e) {
                            logger.error("Cannot configure JOSSO agent : " + e.getMessage(), e);
                            IOUtils.closeQuietly(is);
                        }

                        wsActivationReq.setJossoAgentConfigUri(null);
                        wsActivationReq.setReplaceConfig(execEnv.isOverwriteOriginalSetup());
                        wsActivationReq.setUser(username);
                        wsActivationReq.setPassword(password);

                        ConfigureAgentResponseType wsActiationResponse = wsClient.configureAgent(wsActivationReq);
                    } else {
                        logger.warn("Cannot configure Agent for " + execEnv.getName() + ". No authentication information available");
                    }

                    break;
                default:
                    throw new IdentityServerException("Unknown Execution Environment type " + execEnv.getType().name());
            }

        } catch (ActivationException e) {
            throw new IdentityServerException(e);
        }


    }

    protected void activateExecEnv(IdentityAppliance appliance,
                                   ExecutionEnvironment execEnv,
                                   boolean reactivate,
                                   String username,
                                   String password) throws IdentityServerException {



        if (execEnv.isActive() && !reactivate) {
            throw new ExecEnvAlreadyActivated(execEnv);
        }

        if (!isActivationSupported(execEnv)) {
            logger.warn("Unsupported platform for activation in " + execEnv.getName() + " : "  + execEnv.getPlatformId());
            return;
        }

        try {

            switch (execEnv.getType()) {

                case LOCAL:
                    ActivateAgentRequest activationRequest = doMakeAgentActivationRequest(execEnv);
                    ActivateAgentResponse activationResponse = activationService.activateAgent(activationRequest);

                    // Only configure the appliance if we have deployment information
                    if (execEnv.isOverwriteOriginalSetup() && appliance.getIdApplianceDeployment() != null)
                        configureExecEnv(appliance, execEnv, username, password);

                    if (execEnv.isInstallDemoApps()) {

                        if (logger.isDebugEnabled())
                            logger.debug("Activating Samples in Execution Environment " + execEnv.getName());

                        ActivateSamplesRequest samplesActivationRequest =
                                doMakeAgentSamplesActivationRequest(execEnv);

                        ActivateSamplesResponse samplesActivationResponse =
                                activationService.activateSamples(samplesActivationRequest);
                    }
                    break;
                case REMOTE:

                    if (username != null && password != null) {
                        ActivationClient wsClient = activationClientFactory.newActivationClient(execEnv.getLocation());
                        ActivateAgentRequestType wsActivationRequest = doMakeWsAgentActivationRequest(execEnv, username, password);


                        ActivateAgentResponseType wsResponse = wsClient.activateAgent(wsActivationRequest);

                        // Only configure the appliance if we have deployment information
                        if (execEnv.isOverwriteOriginalSetup() && appliance.getIdApplianceDeployment() != null)
                            configureExecEnv(appliance, execEnv, username, password);


                        if (execEnv.isInstallDemoApps()) {

                            if (logger.isDebugEnabled())
                                logger.debug("Activating Samples in Execution Environment " + execEnv.getName());

                            ActivateSamplesRequestType wsSamplesActivationRequest =
                                    doMakeWsAgentSamplesActivationRequest(execEnv, username, password);

                            ActivateSamplesResponseType wsSamplesActivationResponse =
                                    wsClient.activateSamples(wsSamplesActivationRequest);
                        }
                    } else {
                        logger.warn("Cannot activate Agent for " + execEnv.getName() + ". No authentication information available");
                    }

                    break;
                default:
                    throw new IdentityServerException("Unknown execution environment type :  " + execEnv.getType());
            }

            // Mark activation as activated and save appliance.
            execEnv.setActive(true);
            identityApplianceDAO.save(appliance);
        } catch (ActivationException e) {
            throw new IdentityServerException(e);
        }


    }

    protected boolean isActivationSupported(ExecutionEnvironment execEnv) {
        try {
            PlatformSupportedRequest req = new PlatformSupportedRequest();
            req.setTargetPlatformId(execEnv.getPlatformId());

            PlatformSupportedResponse res = activationService.isSupported(req);
            return res.isSupported();


        } catch (ActivationException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    protected IdentityAppliance startAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Starting Identity Appliance " + appliance.getId());

        if (appliance.getState().equals(IdentityApplianceState.PROJECTED.toString()))
            appliance = buildAppliance(appliance, true);

        appliance = deployer.start(appliance);
        appliance = identityApplianceDAO.save(appliance);

        return appliance;
    }

    protected IdentityAppliance stopAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Stopping Identity Appliance " + appliance.getId());

        appliance = deployer.stop(appliance);
        appliance = identityApplianceDAO.save(appliance);

        return appliance;
    }

    protected IdentityAppliance restartAppliance(IdentityAppliance appliance) throws IdentityServerException {

        if (logger.isDebugEnabled())
            logger.debug("Restarting Identity Appliance " + appliance.getId());

        appliance = stopAppliance(appliance);
        appliance = startAppliance(appliance);

        return appliance;
    }

    protected IdentityAppliance undeployAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Undeploying Identity Appliance " + appliance.getId());

        if (appliance.getState().equals(IdentityApplianceState.STARTED.toString()))
            appliance = stopAppliance(appliance);

        // Install it
        appliance = deployer.undeploy(appliance);

        // Store it
        appliance = identityApplianceDAO.save(appliance);
        return appliance;

    }

    protected IdentityAppliance disposeAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Disposing Identity Appliance " + appliance.getId());

        if (appliance.getState().equals(IdentityApplianceState.STARTED.toString()))
            appliance = stopAppliance(appliance);

        if (appliance.getState().equals(IdentityApplianceState.DEPLOYED.toString()))
            appliance = undeployAppliance(appliance);

        appliance.setState(IdentityApplianceState.DISPOSED.toString());
        appliance = identityApplianceDAO.save(appliance);

        return appliance;
    }

    protected void removeAppliance(IdentityAppliance appliance) throws IdentityServerException {

        try {
            if (logger.isDebugEnabled())
                logger.debug("Deleting Identity Appliance " + appliance.getId());

            if (!appliance.getState().equals(IdentityApplianceState.DISPOSED.toString())
                    && !appliance.getState().equals(IdentityApplianceState.PROJECTED.toString()))
                throw new IllegalStateException("Appliance in state " + appliance.getState() + " cannot be deleted");

            /*
            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();

            if (applianceDep != null)
                identityApplianceDeploymentDAO.delete(applianceDep.getId());

            appliance.setIdApplianceDeployment(null);

            Set<Long> fcIds = new HashSet<Long>();
            for (Provider p : applianceDef.getProviders()) {

                if (p instanceof FederatedProvider) {
                    FederatedProvider fp = (FederatedProvider) p;
                    for (FederatedConnection fcA : fp.getFederatedConnectionsA()) {
                        fcIds.add(fcA.getId());
                    }

                    for (FederatedConnection fcB : fp.getFederatedConnectionsB()) {
                        fcIds.add(fcB.getId());
                    }
                    fp.getFederatedConnectionsA().clear();
                    fp.getFederatedConnectionsB().clear();
                }
            }
            */

            // TODO: fix jdo mapping so that idaus will be cascade deleted (currently only records from join table are removed)?
            /*List<Long> idauIDs = new ArrayList<Long>();
            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();
            if (applianceDep != null && applianceDep.getIdaus() != null) {
                for (IdentityApplianceUnit idaUnit : applianceDep.getIdaus()) {
                    idauIDs.add(idaUnit.getId());
                }
            }*/

            // some units are left unremoved, e.g. after appliance is deployed/undeployed/deployed, so we have to remove all
            // units with the given group
            String unitsGroup = appliance.getNamespace() + "." + appliance.getName();

            identityApplianceDAO.delete(appliance.getId());

            // identityApplianceUnitDAO.deleteUnitsByGroup(unitsGroup);

            /*for (Long idauID : idauIDs) {
                identityApplianceUnitDAO.delete(idauID);
            }*/

        } catch (Exception e) {
            logger.error("Cannot delete identity appliance " + appliance.getId());
            throw new IdentityServerException("Cannot delete identity appliance " + appliance.getId() + " : " + e.getMessage(), e);
        }

    }

    protected IdentityAppliance deployAppliance(IdentityAppliance appliance, String username, String password, boolean configureExecEnvs) throws IdentityServerException {

        if (logger.isDebugEnabled())
            logger.debug("Deploying Identity Appliance " + appliance.getId());

        if (appliance.getState().equals(IdentityApplianceState.STARTED.toString()))
            appliance = stopAppliance(appliance);

        if (appliance.getState().equals(IdentityApplianceState.DEPLOYED.toString()))
            appliance = undeployAppliance(appliance);

        if (appliance.getState().equals(IdentityApplianceState.PROJECTED.toString()) ||
                appliance.getIdApplianceDeployment() == null)
            appliance = buildAppliance(appliance, false);

        // Install it
        appliance = deployer.deploy(appliance);

        if (configureExecEnvs) {
            for (ExecutionEnvironment execEnv : appliance.getIdApplianceDefinition().getExecutionEnvironments()) {

                if (execEnv.isActive()) {
                    logger.debug("Execution environment is active, configuring " + execEnv.getName());
                    configureExecEnv(appliance, execEnv, username, password);
                } else {
                    logger.debug("Execution environment is not active, skip configuration " + execEnv.getName());
                }
            }
        }


        // Store it
        appliance = identityApplianceDAO.save(appliance);
        return appliance;
    }


    protected IdentityAppliance buildAppliance(IdentityAppliance appliance, boolean deploy) throws IdentityServerException {

        if (logger.isDebugEnabled())
            logger.debug("Building Identity Appliance [deploy:"+deploy+"]" + appliance.getId());

        // quick fix (sort providers: identity providers -> binding provider -> service providers -> binding provider -> service providers, ...)
        /*
        Set<Provider> providers = appliance.getIdApplianceDefinition().getProviders();
        List<Provider> sortedProviders = new ArrayList<Provider>();
        for (Provider provider : providers) {
            if (provider instanceof InternalSaml2ServiceProvider) {
                sortedProviders.add(provider);
            }
        }
        Collections.sort(sortedProviders, new ServiceProviderComparator());
        for (Provider provider : providers) {
            if (provider instanceof IdentityProvider) {
                sortedProviders.add(0, provider);
            }
        }

        appliance.getIdApplianceDefinition().setProviders(sortedProviders);
        */

        // Build the appliance
        appliance = builder.build(appliance);
        appliance.setState(IdentityApplianceState.STAGED.toString());

        // Install it
        if (deploy)
            appliance = deployAppliance(appliance, null, null, false);

        // Store it
        appliance = identityApplianceDAO.save(appliance);
        return appliance;
    }

    protected void syncAppliances() throws IdentityServerException {

        if (alreadySynchronizededAppliances)
            return;

        Collection<IdentityAppliance> appiances = identityApplianceDAO.findAll();

        for (IdentityAppliance appliance : appiances) {

            try {
                appliance = identityApplianceDAO.detachCopy(appliance, FetchPlan.FETCH_SIZE_GREEDY);

                if (logger.isDebugEnabled())
                    logger.debug("Synchronizing Appliance state for [" + appliance.getId() + "] " +
                            appliance.getIdApplianceDefinition().getName());

                if (appliance.getState().equals(IdentityApplianceState.STARTED.toString())) {

                    if (!deployer.isDeployed(appliance)) {
                        // STARTED in DB but not DEPLOYED in OSGI --> PROJECTED
                        appliance.setState(IdentityApplianceState.PROJECTED.toString());
                        if (logger.isDebugEnabled())
                            logger.debug("Synchronizing Appliance state : PROJECTED " + appliance.getId());
                        appliance = identityApplianceDAO.save(appliance);

                        if (logger.isDebugEnabled())
                            logger.debug("Automatically Starting appliance ... " + appliance.getId());
                        this.startAppliance(appliance);

                    } else if (!deployer.isStarted(appliance)) {

                        // STARTED in DB but not STARTED in OSGI --> STAGED
                        appliance.setState(IdentityApplianceState.DEPLOYED.toString());
                        if (logger.isDebugEnabled())
                            logger.debug("Synchronizing Appliance state : DEPLOYED " + appliance.getId());
                        appliance = identityApplianceDAO.save(appliance);

                        if (logger.isDebugEnabled())
                            logger.debug("Automatically Starting appliance ... " + appliance.getId());
                        this.startAppliance(appliance);

                    }

                } else if (appliance.getState().equals(IdentityApplianceState.DEPLOYED.toString())) {
                    // Appliance is marked as DEPLOYED

                    if (!deployer.isDeployed(appliance)) {
                        appliance.setState(IdentityApplianceState.PROJECTED.toString());
                        logger.debug("Synchronizing Appliance state : PROJECTED " + appliance.getId());
                        appliance = identityApplianceDAO.save(appliance);

                        logger.debug("Automatically Starting appliance ... " + appliance.getId());
                        this.deployAppliance(appliance, null, null, false);

                    }

                }
            } catch (Exception e) {
                logger.warn("Appliance " + appliance.getId() + " state synchronization failed : " + e.getMessage(), e);
            }
        }

    }


    protected ActivateAgentRequest doMakeAgentActivationRequest(ExecutionEnvironment execEnv ) {

        ActivateAgentRequest req = new ActivateAgentRequest ();
        req.setTarget(execEnv.getInstallUri());
        req.setTargetPlatformId(execEnv.getPlatformId());

        if (execEnv instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) execEnv;
            req.setJbossInstance(jbExecEnv.getInstance());
        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) execEnv;
            req.setWeblogicDomain(wlExecEnv.getDomain());
        } // TODO : Add support for Alfresco, Liferay, JBPortal, PHP, PHPBB, etc ...

        return req;
    }

    protected ActivateAgentRequestType doMakeWsAgentActivationRequest(ExecutionEnvironment execEnv,
                                                                      String username, String password ) {

        ActivateAgentRequestType req = new ActivateAgentRequestType();
        req.setTarget(execEnv.getInstallUri());
        req.setTargetPlatformId(execEnv.getPlatformId());
        req.setUser(username);
        req.setPassword(password);

        if (execEnv instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) execEnv;
            req.setJbossInstance(jbExecEnv.getInstance());
        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) execEnv;
            req.setWeblogicDomain(wlExecEnv.getDomain());
        }

        return req;
    }


    protected ConfigureAgentRequest doMakeConfigureAgentRequest(ExecutionEnvironment execEnv ) {

        ConfigureAgentRequest req = new ConfigureAgentRequest();
        req.setTarget(execEnv.getInstallUri());
        req.setTargetPlatformId(execEnv.getPlatformId());
        req.setReplaceConfig(execEnv.isOverwriteOriginalSetup());

        if (execEnv instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) execEnv;
            req.setJbossInstance(jbExecEnv.getInstance());
        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) execEnv;
            req.setWeblogicDomain(wlExecEnv.getDomain());
        } else if (execEnv instanceof LiferayExecutionEnvironment) {
            LiferayExecutionEnvironment liferayExecEnv = (LiferayExecutionEnvironment) execEnv;
            if ("tomcat".equals(liferayExecEnv.getContainerType())) {
                req.setTomcatInstallDir(liferayExecEnv.getContainerPath());
            } else if ("jboss".equals(liferayExecEnv.getContainerType())) {
                req.setJbossInstallDir(liferayExecEnv.getContainerPath());
            }
        } // TODO : Add support for JBPortal, Alfresco, PHP, PHPBB, etc ...

        return req;
    }

    protected ConfigureAgentRequestType doMakeWsConfigureAgentRequest(ExecutionEnvironment execEnv,
                                                                      String username,
                                                                      String password ) {

        ConfigureAgentRequestType req = new ConfigureAgentRequestType();
        req.setTarget(execEnv.getInstallUri());
        req.setTargetPlatformId(execEnv.getPlatformId());
        req.setReplaceConfig(execEnv.isOverwriteOriginalSetup());
        req.setUser(username);
        req.setPassword(password);

        if (execEnv instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) execEnv;
            req.setJbossInstance(jbExecEnv.getInstance());
        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) execEnv;
            req.setWeblogicDomain(wlExecEnv.getDomain());
        } else if (execEnv instanceof LiferayExecutionEnvironment) {
            LiferayExecutionEnvironment liferayExecEnv = (LiferayExecutionEnvironment) execEnv;
            if ("tomcat".equals(liferayExecEnv.getContainerType())) {
                req.setTomcatInstallDir(liferayExecEnv.getContainerPath());
            } else if ("jboss".equals(liferayExecEnv.getContainerType())) {
                req.setJbossInstallDir(liferayExecEnv.getContainerPath());
            }
        } // TODO : Add support for JBPortal, Alfresco, PHP, PHPBB, etc ...

        return req;
    }



    protected ActivateSamplesRequest doMakeAgentSamplesActivationRequest(ExecutionEnvironment execEnv) {

        ActivateSamplesRequest req = new ActivateSamplesRequest ();
        req.setTarget(execEnv.getInstallUri());
        req.setTargetPlatformId(execEnv.getPlatformId());

        if (execEnv instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) execEnv;
            req.setJbossInstance(jbExecEnv.getInstance());
        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) execEnv;
            req.setWeblogicDomain(wlExecEnv.getDomain());
        } else if (execEnv instanceof LiferayExecutionEnvironment) {
            LiferayExecutionEnvironment liferayExecEnv = (LiferayExecutionEnvironment) execEnv;
            if ("tomcat".equals(liferayExecEnv.getContainerType())) {
                req.setTomcatInstallDir(liferayExecEnv.getContainerPath());
            } else if ("jboss".equals(liferayExecEnv.getContainerType())) {
                req.setJbossInstallDir(liferayExecEnv.getContainerPath());
            }
        } // TODO : Add support for JBPortal, Alfresco, PHP, PHPBB, etc ...

        return req;
    }

    protected ActivateSamplesRequestType doMakeWsAgentSamplesActivationRequest(ExecutionEnvironment execEnv,
                                                                               String username, String password) {

        ActivateSamplesRequestType req = new ActivateSamplesRequestType ();
        req.setTarget(execEnv.getInstallUri());
        req.setTargetPlatformId(execEnv.getPlatformId());
        req.setUser(username);
        req.setPassword(password);

        if (execEnv instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) execEnv;
            req.setJbossInstance(jbExecEnv.getInstance());
        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) execEnv;
            req.setWeblogicDomain(wlExecEnv.getDomain());
        } else if (execEnv instanceof LiferayExecutionEnvironment) {
            LiferayExecutionEnvironment liferayExecEnv = (LiferayExecutionEnvironment) execEnv;
            if ("tomcat".equals(liferayExecEnv.getContainerType())) {
                req.setTomcatInstallDir(liferayExecEnv.getContainerPath());
            } else if ("jboss".equals(liferayExecEnv.getContainerType())) {
                req.setJbossInstallDir(liferayExecEnv.getContainerPath());
            }
        } // TODO : Add support for JBPortal, Alfresco, PHP, PHPBB, etc ...

        return req;
    }


    public class ServiceProviderComparator implements Comparator<Provider> {
        public int compare(Provider sp1, Provider sp2) {

            if (sp1 == sp2)
                return 0;

            // TODO : Is this OK  !!
            //if (((InternalSaml2ServiceProvider)sp1).getActivation().getSp().equals(((InternalSaml2ServiceProvider)sp2).getActivation().getSp())) return 0;
            if (((InternalSaml2ServiceProvider)sp1).getServiceConnection().getSp().equals(((InternalSaml2ServiceProvider)sp2).getServiceConnection().getSp())) return 0;

            return 1;
        }
    }


    protected String getHomeDir() {
        return System.getProperty("karaf.base");
    }

}
