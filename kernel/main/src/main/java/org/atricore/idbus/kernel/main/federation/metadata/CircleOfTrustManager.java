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
     * Look for a COT Member descriptor that realizes the given role.
     *
     * @param provider
     * @param role
     * @return
     * @throws CircleOfTrustManagerException
     */
    CircleOfTrustMemberDescriptor lookupMemberForProvider(Provider provider, String role)
            throws CircleOfTrustManagerException;

    /**
     * Look for a COT Member descriptor in the destination provider.  If mustBeTarget is set to true, only the member
     * that targets the source provider is returned, otherwise the destination provider default target is returned.
     *
     * @param srcProvider
     * @param destProvider
     * @return
     * @throws CircleOfTrustManagerException
     */
    CircleOfTrustMemberDescriptor lookupMemberForProvider(Provider srcProvider, Provider destProvider)
            throws CircleOfTrustManagerException;

    /**
     * Lookup all members that realize the given role and can be used by the given provider.
     * @param provider
     * @param role
     * @return
     * @throws CircleOfTrustManagerException
     */
    Collection<CircleOfTrustMemberDescriptor> lookupMembersForProvider(Provider provider, String role)
        throws CircleOfTrustManagerException;

    /**
     * Returns the COT Member descriptor that matches the given alias.
     * @param memberAlias
     * @return
     */
    CircleOfTrustMemberDescriptor lookupMemberByAlias(String memberAlias);

    CircleOfTrustMemberDescriptor lookupMemberById(String hash);

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
