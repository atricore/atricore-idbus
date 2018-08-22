/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.capabilities.sso.main.common;

import oasis.names.tc.saml._2_0.metadata.EndpointType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.component.container.IdentityFlowContainer;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.IdRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AbstractSSOMediator.java 1245 2009-06-05 19:32:53Z sgonzalez $
 */
public abstract class AbstractSSOMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(AbstractSSOMediator.class);

    private boolean validateRequestsSignature;

    private boolean enableEncryption;

    private boolean signRequests;

    private boolean verifyUniqueIDs;

    // In milliseconds
    private long requestTimeToLive = 1000L * 60L * 60L * 24L; // Default to 24 hours  (TODO: once this is configured from console, set it back to 1h)

    // In milliseconds, represent the clock offset tolerance when validating time-stamps
    private long timestampValidationTolerance = 1000L * 60L * 5L; // Default to five minutes,

    private SamlR2Signer signer;

    private SamlR2Encrypter encrypter;

    private String metricsPrefix = "";

    private String auditCategory = "";

    private IdentityFlowContainer identityFlowContainer;

    private IdRegistry idRegistry;

    private Map<String, ChannelConfiguration> channelConfigs = new HashMap<String, ChannelConfiguration>();

    protected AbstractSSOMediator() {

    }

    /**
     * This util will create an EndpointDescriptor based on the received channel and endpoint information.
     * 
     * @param channel
     * @param endpoint
     * @return
     * @throws org.atricore.idbus.capabilities.sso.main.SSOException
     */
    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {

        // SAMLR2 Endpoint springmetadata definition
        String type = null;
        String location;
        String responseLocation;
        SSOBinding binding = null;

        if (endpoint.getMetadata() != null &&
                endpoint.getMetadata().getEntry() instanceof EndpointType) {

            EndpointType samlr2Endpoint = (EndpointType) endpoint.getMetadata().getEntry();
            logger.debug("Found SAMLR2 Endpoint metadata for endpoint " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding value.
            // ---------------------------------------------
            if (endpoint.getBinding() != null &&  samlr2Endpoint.getBinding() != null &&
                    !samlr2Endpoint.getBinding().equals(endpoint.getBinding())) {
                logger.warn("SAMLR2 Metadata Endpoint binding does not match binding for Identity Mediation Endpoint "
                        + endpoint.getName() + "IGNORING METADATA");
            }

            String b = endpoint.getBinding() != null ? endpoint.getBinding() : samlr2Endpoint.getBinding();
            if (b != null)
                binding = SSOBinding.asEnum(b);
            else
                logger.warn("No SSOBinding found in endpoint " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint location
            // ---------------------------------------------
            if (endpoint.getLocation() != null && samlr2Endpoint.getLocation() != null &&
                    !endpoint.getLocation().equals(samlr2Endpoint.getLocation())) {
                logger.warn("SAMLR2 Metadata Endpoint location does not match location for Identity Mediation Endpoint "
                        + endpoint.getName() + ", IGNORING METADATA!");
            }

            location = endpoint.getLocation() != null ? endpoint.getLocation() : samlr2Endpoint.getLocation();
            if (location == null)
                throw new IdentityMediationException("Endpoint location cannot be null.  " + endpoint);

            if (location.startsWith("/"))
                location = channel.getLocation() + location;

            // ---------------------------------------------
            // Resolve Endpoint response location
            // ---------------------------------------------
            if (endpoint.getResponseLocation() != null && samlr2Endpoint.getResponseLocation() != null &&
                    !endpoint.getResponseLocation().equals(samlr2Endpoint.getResponseLocation())) {
                logger.warn("SAMLR2 Metadata Endpoint response location does not match response location for Identity Mediation Endpoint "
                        + endpoint.getName() + "IGNORING METADATA");
            }

            responseLocation = endpoint.getResponseLocation() != null ?
                    endpoint.getResponseLocation() : samlr2Endpoint.getResponseLocation();

            if (responseLocation != null && responseLocation.startsWith("/"))
                responseLocation = channel.getLocation() + responseLocation;

            // ---------------------------------------------
            // Resolve Endpoint type
            // ---------------------------------------------
            // If no ':' is present, lastIndexOf should resturn -1 and the entire type is used.
            // Remove qualifier, format can be :
            // 1 - {qualifier}type
            // 2 - qualifier:type
            int bracketPos = endpoint.getType().lastIndexOf("}");
            if (bracketPos > 0)
                type = endpoint.getType().substring(bracketPos + 1);
            else
                type = endpoint.getType().substring(endpoint.getType().lastIndexOf(":") + 1);


        } else {

            logger.debug("Creating Endpoint Descriptor without SAMLR2 Metadata for : " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding
            // ---------------------------------------------
            if (endpoint.getBinding() != null)
                binding = SSOBinding.asEnum(endpoint.getBinding());
            else
                logger.warn("No SSOBinding found in endpoint " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint location
            // ---------------------------------------------
            location = endpoint.getLocation();
            if (location == null)
                throw new IdentityMediationException("Endpoint location cannot be null.  " + endpoint);
            
            if (location.startsWith("/"))
                location = channel.getLocation() + location;

            // ---------------------------------------------
            // Resolve Endpoint response location
            // ---------------------------------------------
            responseLocation = endpoint.getResponseLocation();
            if (responseLocation != null && responseLocation.startsWith("/"))
                responseLocation = channel.getLocation() + responseLocation;

            // ---------------------------------------------
            // Resolve Endpoint type
            // ---------------------------------------------

            // Remove qualifier, format can be :
            // 1 - {qualifier}type
            // 2 - qualifier:type
            int bracketPos = endpoint.getType().lastIndexOf("}");
            if (bracketPos > 0)
                type = endpoint.getType().substring(bracketPos + 1);
            else
                type = endpoint.getType().substring(endpoint.getType().lastIndexOf(":") + 1);

        }

        return new EndpointDescriptorImpl(endpoint.getName(),
                type,
                binding.getValue(),
                location,
                responseLocation);

    }

    public SamlR2Signer getSigner() {
        return signer;
    }

    public void setSigner(SamlR2Signer signer) {
        this.signer = signer;
    }


    public SamlR2Encrypter getEncrypter() {
        return encrypter;
    }

    public void setEncrypter(SamlR2Encrypter encrypter) {
        this.encrypter = encrypter;
    }

    public boolean isValidateRequestsSignature() {
        return validateRequestsSignature;
    }

    /**
     * TODO : Signature validation setup should be moved to channel
     * @return
     */
    public void setValidateRequestsSignature(boolean validateRequestsSignature) {
        this.validateRequestsSignature = validateRequestsSignature;
    }

    /**
     * TODO : Encryption setup should be moved to channel
     * @return
     */
    public boolean isEnableEncryption() {
        return enableEncryption;
    }

    public void setEnableEncryption(boolean enableEncryption) {
        this.enableEncryption = enableEncryption;
    }

    /**
     * TODO : Signature setup should be moved to channel
     * @return
     */
    public boolean isSignRequests() {
        return signRequests;
    }

    public void setSignRequests(boolean signRequests) {
        this.signRequests = signRequests;
    }

    public long getRequestTimeToLive() {
        return requestTimeToLive;
    }

    public void setRequestTimeToLive(long requestTimeToLive) {
        this.requestTimeToLive = requestTimeToLive;
    }

    public long getTimestampValidationTolerance() {
        return timestampValidationTolerance;
    }

    public void setTimestampValidationTolerance(long timestampValidationTolerance) {
        this.timestampValidationTolerance = timestampValidationTolerance;
    }

    public String getMetricsPrefix() {
        return metricsPrefix;
    }

    public void setMetricsPrefix(String metricsPrefix) {
        this.metricsPrefix = metricsPrefix;
    }

    public String getAuditCategory() {
        return auditCategory;
    }

    public void setAuditCategory(String auditCategory) {
        this.auditCategory = auditCategory;
    }

    public IdentityFlowContainer getIdentityFlowContainer() {
        return identityFlowContainer;
    }

    public void setIdentityFlowContainer(IdentityFlowContainer identityFlowContainer) {
        this.identityFlowContainer = identityFlowContainer;
    }

    public IdRegistry getIdRegistry() {
        return idRegistry;
    }

    public void setIdRegistry(IdRegistry idRegistry) {
        this.idRegistry = idRegistry;
    }

    public boolean isVerifyUniqueIDs() {
        return verifyUniqueIDs;
    }

    public void setVerifyUniqueIDs(boolean verifyUniqueIDs) {
        this.verifyUniqueIDs = verifyUniqueIDs;
    }

    public Map<String, ChannelConfiguration> getChannelConfigs() {
        return channelConfigs;
    }

    public void setChannelConfigs(Map<String, ChannelConfiguration> channelConfigs) {
        this.channelConfigs = channelConfigs;
    }
    public ChannelConfiguration getChannelConfig(String name) {
        return this.channelConfigs.get(name);
    }

}
