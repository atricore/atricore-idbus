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

package org.atricore.idbus.kernel.main.federation.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.util.*;

/**
 *
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: CircleOfTrustManagerImpl.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class CircleOfTrustManagerImpl implements CircleOfTrustManager, InitializingBean, DisposableBean  {

    private static final Log logger = LogFactory.getLog(CircleOfTrustManagerImpl.class);

    private CircleOfTrust cot;

    private Map<String, MetadataDefinition> definitions = new HashMap<String, MetadataDefinition>();

    boolean init = false;

    public CircleOfTrust getCot() {
        return cot;
    }

    public void setCot(CircleOfTrust cot) {
        this.cot = cot;
    }

    public void afterPropertiesSet() throws Exception {
        // Nothing to do ..
    }

    public void init() throws CircleOfTrustManagerException {

        if (init)
            return;

        synchronized (this) {

            if (init)
                return;

            logger.info("Initializing Circle Of Trust (COT) " + cot.getName());

            try {

                for (FederatedProvider provider : cot.getProviders()) {


                    for (CircleOfTrustMemberDescriptor member : provider.getAllMembers()) {

                        if (logger.isDebugEnabled())
                            logger.debug("Initializing Provider Member information " + provider.getName() + ", member " + member.getAlias());

                        registerMember(member);
                    }

                    if (provider instanceof AbstractFederatedProvider) {
                        AbstractFederatedProvider localProvider = (AbstractFederatedProvider) provider;
                        localProvider.setCircleOfTrust(cot);

                        if (localProvider.getChannel() != null && localProvider.getChannel().getClaimProviders() != null) {
                            // Sort claim providers, the collection MUST be a list ....
                            if (logger.isTraceEnabled())
                                logger.trace("Sorting Claims providers ");

                            Collections.sort((List<ClaimChannel>) localProvider.getChannel().getClaimProviders(), new ClaimChannelPriorityComparator());

                            if (logger.isTraceEnabled()) {
                                int order = 0;
                                for (ClaimChannel c : localProvider.getChannel().getClaimProviders()) {
                                    logger.trace("claim channel priority [" + order + "] for " + c.getName());
                                    order ++;
                                }
                            }
                        }

                    } else {
                        logger.debug("Unknown provider type " + provider + ", cannot inject COT");
                    }


                }

                init = true;

                logger.info("Initializing COT " + cot.getName() + " OK");
                
            } catch (Exception e) {
                logger.error("Initializing COT Manager : " + e.getMessage());
                throw new CircleOfTrustManagerException(e);
            }


        }
    }

    protected void registerMember(CircleOfTrustMemberDescriptor member) throws CircleOfTrustManagerException {

        if (logger.isDebugEnabled())
            logger.debug("Registering COT Member descriptor " + member);

        MetadataDefinition def = null;
        // resource-based metadata
        if (member instanceof ResourceCircleOfTrustMemberDescriptorImpl) {

            // Resolve Metadata Definition
            ResourceCircleOfTrustMemberDescriptorImpl m = (ResourceCircleOfTrustMemberDescriptorImpl) member;
            if (logger.isDebugEnabled())
                logger.debug("Loading resource-based metadata for member " + member.getId() + " [" + member.getAlias() + "]");

            if (m.getResource() != null)
                def = loadMetadataDefinition(member, m.getResource());

        } else { // non-resource-based metadata
            if (logger.isDebugEnabled())
                logger.debug("Loading non resource-based metadata for member " + member.getId() + " [" + member.getAlias() + "]");

            def = loadMetadataDefinition(member);
        }

        if (def != null) {
            MetadataDefinition old = definitions.put(member.getAlias(), def);

            MetadataEntry md = findEntityMetadata(member.getAlias());
            if (md == null)
                logger.warn("No metadata found for COT Member " + member.getAlias());

            member.setMetadata(md);

            if (old != null)
                throw new CircleOfTrustManagerException("Duplicated COT Member descriptor for alias : " +
                        member.getAlias());
        }

    }

    public void destroy() throws Exception {

    }

    public void exportCircleOfTrustMetadataDefinition(String memberAlias) {
        throw new UnsupportedOperationException("Not implemented");
    }


    public Collection<CircleOfTrustMemberDescriptor> getMembers() {
        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();
        for (FederatedProvider p : cot.getProviders()) {
            members.addAll(p.getAllMembers());
        }
        return members;
    }

    /**
     * This will look in all registered providers that realize the given role for the channel that targets the source
     * provider and return its COT Member descriptor.
     *
     */
    public CircleOfTrustMemberDescriptor lookupMemberForProvider(Provider srcProvider, String role) throws CircleOfTrustManagerException {

        if (logger.isDebugEnabled())
            logger.debug("Looking for COT Member descriptor for source provider " + srcProvider.getName() +
                    ", role " + role);


        for (Provider destProvider : cot.getProviders()) {
            if (destProvider.getRole().equals(role)) {
                if (logger.isDebugEnabled())
                    logger.debug("Provider " + destProvider .getName() + " has role " + role);

                CircleOfTrustMemberDescriptor selectedMember = lookupMemberForProvider(srcProvider, destProvider);

                if (selectedMember != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Selected COT Member '" + selectedMember + "' for provider '" + srcProvider.getName() +
                                "' with role '" + role + "' in COT " + cot.getName());
                    return selectedMember;
                }


            }
        }

        if (logger.isDebugEnabled())
            logger.debug("NO COT Member found for provider '" + srcProvider.getName() +
                    "' with role '" + role + "' in COT " + cot.getName());

        return null;

    }

    public CircleOfTrustMemberDescriptor lookupMemberForProvider(Provider srcProvider, Provider destProvider) throws CircleOfTrustManagerException {

        CircleOfTrustMemberDescriptor targetingMember = null;

        if (destProvider instanceof FederatedLocalProvider) {
            FederatedLocalProvider federatedDestProvider = (FederatedLocalProvider) destProvider;

            for (FederationChannel channel : federatedDestProvider.getChannels()) {
                if (channel.getTargetProvider().equals(srcProvider)) {
                    targetingMember = channel.getMember();
                    if (logger.isDebugEnabled())
                        logger.debug("Selected targeting member : " + targetingMember.getAlias());

                }
            }
        }


        if (targetingMember == null) {
            logger.debug("No Selected between source " + srcProvider.getName() + " and destination " +  destProvider.getName());
        }

        return targetingMember;


    }

    public boolean isLocalMember(String alias) throws CircleOfTrustManagerException {
        for (FederatedProvider p : cot.getProviders()) {

            for (CircleOfTrustMemberDescriptor m : p.getAllMembers()) {
                if (m.getAlias().equals(alias)) {
                    return !(p instanceof FederatedRemoteProvider);
                }
            }
        }
        throw new CircleOfTrustManagerException("Unknown entity " + alias);
    }

    /**
     * @param role the role realized by the provider, it's protocol specific: i.e. saml 2.0 sp
     */
    public Collection<CircleOfTrustMemberDescriptor> lookupMembersForProvider(Provider provider, String role)
            throws CircleOfTrustManagerException {

        Set<CircleOfTrustMemberDescriptor> members = new HashSet<CircleOfTrustMemberDescriptor>();

        for (Provider destProvider : cot.getProviders()) {
            if (destProvider.getRole().equals(role)) {

                if (logger.isDebugEnabled())
                    logger.debug("Provider " + destProvider .getName() + " has role " + role);

                // See if this local provider can be used with our provider
                if (destProvider instanceof FederatedLocalProvider) {
                    FederatedLocalProvider federatedDestProvider = (FederatedLocalProvider) destProvider;

                    boolean useOverrideChannel = false;
                    for (FederationChannel channel : federatedDestProvider.getChannels()) {

                        // The received provider is the target of the channel
                        if (channel.getTargetProvider().equals(provider)) {
                            useOverrideChannel = true;
                            members.add(channel.getMember());
                            if (logger.isDebugEnabled())
                                logger.debug("Selected targeting member : " + channel.getMember().getAlias());
                        }
                    }

                    // Use the default channel
                    if (!useOverrideChannel)
                        members.add(federatedDestProvider.getChannel().getMember());
                } else {
                    FederatedRemoteProvider destRemoteProvider = (FederatedRemoteProvider) destProvider;
                    for(CircleOfTrustMemberDescriptor m : destRemoteProvider.getAllMembers()) {
                        if (logger.isDebugEnabled())
                            logger.debug("Selected member : " + m.getAlias());

                        members.add(m);
                    }
                }



            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Found " + members.size() + " members for " + provider.getName() + " with role " + role);

        return members;
    }

    public CircleOfTrustMemberDescriptor lookupMemberByAlias(String alias) {

        for (FederatedProvider provider : cot.getProviders()) {

            for (CircleOfTrustMemberDescriptor member : provider.getAllMembers()) {
                if (member.getAlias().equals(alias)) {
                    if (logger.isDebugEnabled())
                        logger.debug("Specific COT Member found for " + alias + " in provider " + provider.getName());
                    return member;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("No COT Member registered with alias " + alias);

        // Not found !?
        return null;

    }

    public CircleOfTrustMemberDescriptor lookupMemberById(String id) {
        for (FederatedProvider provider : cot.getProviders()) {

            for (CircleOfTrustMemberDescriptor member : provider.getAllMembers()) {
                if (member.getId().equals(id)) {
                    if (logger.isDebugEnabled())
                        logger.debug("Specific COT Member found for " + id + " in provider " + provider.getName());
                    return member;
                }
            }
        }


        if (logger.isDebugEnabled())
            logger.debug("No COT Member registered with ID " + id);

        // Not found !?
        return null;
    }

    /**
     * The member alias must match a MD Definition ID.
     */
    public MetadataEntry findEntityMetadata(String memberAlias) throws CircleOfTrustManagerException {

        if (memberAlias == null)
            throw new NullPointerException("Member Alias cannot be null");

        CircleOfTrustMemberDescriptor member = lookupMemberByAlias(memberAlias);
        if (member == null) {
            throw new CircleOfTrustManagerException("Entity ID is not a COT member alias : " + memberAlias + " in COT " + cot.getName());
        }

        MetadataDefinition md = definitions.get(member.getAlias());
        return this.searchEntityDefinition(member, md, memberAlias);
    }

    public MetadataEntry findEntityRoleMetadata(String memberAlias, String entityRole) throws CircleOfTrustManagerException {

        if (memberAlias == null)
            throw new NullPointerException("Member Alias cannot be null");
        if (entityRole == null)
            throw new NullPointerException("Entity Role cannot be null");

        CircleOfTrustMemberDescriptor member = lookupMemberByAlias(memberAlias);
        if (member == null) {
            throw new CircleOfTrustManagerException("Entity ID is not a COT member alias : " + memberAlias + " in COT " + cot.getName());
        }

        MetadataDefinition md = definitions.get(member.getAlias());
        if (md == null) {
            logger.debug("Entity Role metadata not found for '" + memberAlias + "', role '"+entityRole+"'");
            return null;
        }

        return this.searchEntityRoleDefinition(member, md, memberAlias, entityRole);
    }

    public MetadataEntry findEndpointMetadata(String memberAlias, String entityRole, EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        if (memberAlias == null)
            throw new NullPointerException("Member Alias cannot be null");
        if (entityRole == null)
            throw new NullPointerException("Entity Role cannot be null");
        if (endpoint == null)
            throw new NullPointerException("IdentityMediationEndpoint cannot be null");

        CircleOfTrustMemberDescriptor member = lookupMemberByAlias(memberAlias);
        if (member == null) {
            throw new CircleOfTrustManagerException("Entity ID is not a COT member alias : " + memberAlias + " in COT " + cot.getName());
        }

        MetadataDefinition md = definitions.get(member.getAlias());
        if (md == null) {
            logger.debug("Identity Mediation Endpoint metadata not found for '" + memberAlias +
                    "', role '" + entityRole +
                    "', endpoint='" + endpoint + "'");
            return null;
        }

        return this.searchEndpointDescriptor(member, md, memberAlias, entityRole, endpoint);
    }

    public Collection<MetadataEntry> findEndpointsMetadata(String memberAlias, String entityRole, EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        if (memberAlias == null)
            throw new NullPointerException("Member Alias cannot be null");
        if (entityRole == null)
            throw new NullPointerException("Entity Role cannot be null");
        if (endpoint == null)
            throw new NullPointerException("IdentityMediationEndpoint cannot be null");
        
        CircleOfTrustMemberDescriptor member = lookupMemberByAlias(memberAlias);
        if (member == null) {
            throw new CircleOfTrustManagerException("Entity ID is not a COT member alias : " + memberAlias + " in COT " + cot.getName());
        }

        MetadataDefinition md = definitions.get(member.getAlias());
        if (md == null) {
            logger.debug("Endpoints metadata not found for '" + memberAlias +
                    "', role '" + entityRole +
                    "', endpoint '" + endpoint + "'");
            return null;
        }

        return this.searchEndpointDescriptors(member, md, memberAlias, entityRole, endpoint);
    }

    protected MetadataDefinition loadMetadataDefinition(CircleOfTrustMemberDescriptor member)
            throws CircleOfTrustManagerException {

        if (member.getMetadataIntrospector() != null)
            return member.getMetadataIntrospector().load(member);

        return null;
    }

    protected MetadataDefinition loadMetadataDefinition(CircleOfTrustMemberDescriptor member,
                                                        Resource resource)
            throws CircleOfTrustManagerException {

        return member.getMetadataIntrospector().load(member, resource);
    }

    protected MetadataEntry searchEntityDefinition(CircleOfTrustMemberDescriptor member,
                                                   MetadataDefinition metadataDefinition,
                                                            String memberAlias)
            throws CircleOfTrustManagerException {

            return member.getMetadataIntrospector().searchEntityDefinition(metadataDefinition, memberAlias);

    }

    protected MetadataEntry searchEntityRoleDefinition(CircleOfTrustMemberDescriptor member,
                                                                MetadataDefinition metadataDefinition,
                                                                String memberAlias,
                                                                String roleType)
            throws CircleOfTrustManagerException {

            return member.getMetadataIntrospector().searchEntityRoleDefinition(metadataDefinition, memberAlias, roleType);
    }

    protected MetadataEntry searchEndpointDescriptor(CircleOfTrustMemberDescriptor member,
                                                     MetadataDefinition metadataDefinition,
                                                      String memberAlias,
                                                      String roleType,
                                                      EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException {

            return member.getMetadataIntrospector().searchEndpointDescriptor(
                                                            metadataDefinition,
                                                            memberAlias,
                                                            roleType,
                                                            endpoint);
    }

    protected Collection<MetadataEntry> searchEndpointDescriptors(CircleOfTrustMemberDescriptor member,
                                                                  MetadataDefinition metadataDefinition,
                                                                  String memberAlias,
                                                                  String roleType,
                                                                  EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException {

        return member.getMetadataIntrospector().searchEndpointDescriptors(
                metadataDefinition,
                memberAlias,
                roleType,
                endpoint);

    }

    protected class ClaimChannelPriorityComparator implements Comparator<ClaimChannel> {
        public int compare(ClaimChannel o1, ClaimChannel o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }





}
