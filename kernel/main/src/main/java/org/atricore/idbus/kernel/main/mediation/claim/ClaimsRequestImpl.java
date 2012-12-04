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

package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1290 $ $Date: 2009-06-17 09:52:17 -0300 (Wed, 17 Jun 2009) $
 */
public class ClaimsRequestImpl implements ClaimsRequest {

    private String id;
    private String relayState;
    private Channel issuerChannel;
    private IdentityMediationEndpoint issuerEndpoint;
    private ClaimChannel claimsChannel;
    private String lastErrorId;
    private String lastErrorMsg;
    private Set<SSOPolicyEnforcementStatement> ssoPolicyEnforcements = new HashSet<SSOPolicyEnforcementStatement>();
    private String skin;
    private String preauthenticationSecurityToken;
    
    public ClaimsRequestImpl(String id, Channel issuerChannel, IdentityMediationEndpoint issuerEndpoint, ClaimChannel claimsChannel) {
        this.id = id;
        this.issuerChannel = issuerChannel;
        this.claimsChannel = claimsChannel;
        this.issuerEndpoint = issuerEndpoint;
    }

    public ClaimsRequestImpl(String id, Channel issuerChannel, IdentityMediationEndpoint issuerEndpoint, ClaimChannel claimsChannel,
                             String relayState) {
        this.id = id;
        this.issuerChannel = issuerChannel;
        this.claimsChannel = claimsChannel;
        this.issuerEndpoint = issuerEndpoint;
        this.relayState = relayState;
    }

    public ClaimsRequestImpl(String id, Channel issuerChannel, IdentityMediationEndpoint issuerEndpoint, ClaimChannel claimsChannel,
                             String relayState, String preauthenticationSecurityToken) {
        this.id = id;
        this.issuerChannel = issuerChannel;
        this.claimsChannel = claimsChannel;
        this.issuerEndpoint = issuerEndpoint;
        this.relayState = relayState;
        this.preauthenticationSecurityToken = preauthenticationSecurityToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Channel getIssuerChannel() {
        return issuerChannel;
    }

    public void setIssuerChannel(Channel issuerChannel) {
        this.issuerChannel = issuerChannel;
    }

    public ClaimChannel getClaimsChannel() {
        return claimsChannel;
    }

    public void setClaimsChannel(ClaimChannel provider) {
        this.claimsChannel = provider;
    }

    public IdentityMediationEndpoint getIssuerEndpoint() {
        return issuerEndpoint;
    }

    public void setIssuerEndpoint(IdentityMediationEndpoint endpoint ) {
        this.issuerEndpoint = endpoint;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getLastErrorId() {
        return lastErrorId;
    }

    public void setLastErrorId(String lastErrorId) {
        this.lastErrorId = lastErrorId;
    }

    public Set<SSOPolicyEnforcementStatement> getSsoPolicyEnforcements() {
        return ssoPolicyEnforcements;
    }

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    public void setLastErrorMsg(String lastErrorMsg) {
        this.lastErrorMsg = lastErrorMsg;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getPreauthenticationSecurityToken() {
        return preauthenticationSecurityToken;
    }

    public void setPreauthenticationSecurityToken(String preauthenticationSecurityToken) {
        this.preauthenticationSecurityToken = preauthenticationSecurityToken;
    }

    @Override
    public String toString() {
        return super.toString() +
         "[id=" + id +
         ",relayState=" + relayState +
         ",issuerChannel=" + (issuerChannel != null ? issuerChannel.getName() : "null") +
         ",issuerEndpoint=" + (issuerEndpoint != null ? issuerEndpoint.getName() : "null]" +
         ",claimsChannel=" + (claimsChannel != null ? claimsChannel.getName() : "null"));

    }
}
