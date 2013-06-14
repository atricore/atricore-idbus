/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
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

package org.atricore.idbus.kernel.main.mediation.confirmation;

import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.Claim;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Default implementation of an identity confirmation request.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class IdentityConfirmationRequestImpl implements IdentityConfirmationRequest {
    private String id;
    private FederationChannel issuerChannel;
    private String spAlias;
    private Collection<Claim> claims = new ArrayList<Claim>();
    private String relayState;
    private String lastErrorId;
    private String lastErrorMsg;

    public IdentityConfirmationRequestImpl(FederationChannel issuerChannel, String spAlias) {
        this.issuerChannel = issuerChannel;
        this.spAlias = spAlias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FederationChannel getIssuerChannel() {
        return issuerChannel;
    }

    public void setIssuerChannel(FederationChannel issuerChannel) {
        this.issuerChannel = issuerChannel;
    }

    public String getSpAlias() {
        return spAlias;
    }

    public void setSpAlias(String spAlias) {
        this.spAlias = spAlias;
    }

    public Collection<Claim> getClaims() {
        return claims;
    }

    public void addClaim(Claim claim) {
        claims.add(claim);
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

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    public void setLastErrorMsg(String lastErrorMsg) {
        this.lastErrorMsg = lastErrorMsg;
    }


}
