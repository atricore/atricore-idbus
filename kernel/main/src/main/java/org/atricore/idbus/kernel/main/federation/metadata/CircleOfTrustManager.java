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

import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;

import java.util.Collection;

/**
 * This component manages circle of trust members' springmetadata information.
 *
 * // TODO : create operations to registration / unregister / verify MD
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: CircleOfTrustManager.java 1259 2009-06-09 20:08:04Z sgonzalez $
 */
public interface CircleOfTrustManager {

    /**
     * Resolves all springmetadata required by the Circle Of Trust members.
     *
     * @throws CircleOfTrustManagerException          ;
     */
    void init() throws CircleOfTrustManagerException;

    /**
     * COT Definition
     * @return
     */
    CircleOfTrust getCot();

    void exportCircleOfTrustMetadataDefinition(String memberAlias)
            throws CircleOfTrustManagerException;

    /**
     * This will look the first registered providers that realize the given role targets the
     * provider
     * @return the COT Member descriptor for the selected provider
     *
     */
    CircleOfTrustMemberDescriptor lookupMemberForProvider(Provider provider, String role)
            throws CircleOfTrustManagerException;

    /**
     * This will look for FederationChannel associated with the source provider, that targets the destination provider and
     * return the COT Member descriptor.
     *
     * @return the COT Member descriptor for the selected provider
     *
     */
    CircleOfTrustMemberDescriptor lookupMemberForProvider(Provider srcProvider, Provider destProvider)
            throws CircleOfTrustManagerException;

    /**
     * This will look the all registered providers that realize the given role and target the
     * provider 
     * @return the COT Member descriptor for the selected provider
     *
     */
    Collection<CircleOfTrustMemberDescriptor> lookupMembersForProvider(Provider provider, String role)
        throws CircleOfTrustManagerException;

    /**
     * Returns the COT Member descriptor that matches the given alias.
     * @param memberAlias
     * @return
     */
    CircleOfTrustMemberDescriptor lookupMemberByAlias(String memberAlias);

    /**
     * Look up COT Member descriptor by ID
     * @param hash
     * @return
     */
    CircleOfTrustMemberDescriptor lookupMemberById(String hash);

    /**
     * Lookup federated provider by alias
     * @param providerAlias
     * @return
     */
    FederatedProvider lookupFederatedProviderByAlias(String providerAlias);

    /**
     * True if the COT member is running in the same identity appliance..
     * @param alias
     * @return
     * @throws CircleOfTrustManagerException
     */
    boolean isLocalMember(String alias) throws CircleOfTrustManagerException;

    MetadataEntry findEntityMetadata(String memberAlias)
            throws CircleOfTrustManagerException;

    MetadataEntry findEntityRoleMetadata(String memberAlias, String role)
            throws CircleOfTrustManagerException;

    MetadataEntry findEndpointMetadata(String memberAlias, String role, EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException;

    Collection<MetadataEntry> findEndpointsMetadata(String memberAlias, String role, EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException;

}
