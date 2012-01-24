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

package org.atricore.idbus.kernel.main.mediation.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class FederatedRemoteProviderImpl extends AbstractFederatedProvider implements FederatedRemoteProvider {

    private static final Log logger = LogFactory.getLog(FederatedRemoteProviderImpl.class);

    private CircleOfTrustMemberDescriptor member;

    private List<CircleOfTrustMemberDescriptor > members = new ArrayList<CircleOfTrustMemberDescriptor>();

    public CircleOfTrustMemberDescriptor getMember() {
        return member;
    }

    public void setMember(CircleOfTrustMemberDescriptor member) {
        this.members.clear();
        this.members.add(member);
        this.member = member;
    }


    public void setMembers(List<CircleOfTrustMemberDescriptor> members) {
        if (members.size() != 1)
            throw new RuntimeException("Cannot set " + members.size() + " COT Members to a Federated Remote Provdier");

        this.members.clear();
        this.members.addAll(members);
        this.member = members.get(0);

    }

    @Override
    public List<CircleOfTrustMemberDescriptor> getMembers() {
        return members;
    }

    @Override
    public List<CircleOfTrustMemberDescriptor> getMembers(String configurationKey) {
        logger.warn("Remote provider has only one member, ignoring configuration key " + configurationKey);
        return members;
    }

    @Override
    public List<CircleOfTrustMemberDescriptor> getAllMembers() {
        return members;
    }


}
