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

import org.atricore.idbus.kernel.main.mediation.Channel;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1278 $ $Date: 2009-06-14 03:14:41 -0300 (Sun, 14 Jun 2009) $
 */
public class ClaimsResponseImpl implements ClaimsResponse {
    private String id;
    private String relayState;
    private Channel issuer;
    private String inResponseTo;
    private ClaimSet claimSet;

    public ClaimsResponseImpl(String id, Channel issuer, String inResponseTo, ClaimSet claimSet) {
        this.id = id;
        this.issuer = issuer;
        this.inResponseTo = inResponseTo;
        this.claimSet = claimSet;
    }

    public ClaimsResponseImpl(String id, Channel issuer, String inResponseTo, ClaimSet claimSet, String relayState) {
        this.id = id;
        this.issuer = issuer;
        this.inResponseTo = inResponseTo;
        this.claimSet = claimSet;
        this.relayState = relayState;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Channel getIssuer() {
        return issuer;
    }

    public void setIssuer(Channel issuer) {
        this.issuer = issuer;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(String inResponseTo) {
        this.inResponseTo = inResponseTo;
    }

    public ClaimSet getClaimSet() {
        return claimSet;
    }

    public void setClaimSet(ClaimSet claimSet) {
        this.claimSet = claimSet;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    @Override
    public String toString() {
        return super.toString() +
         "[id=" + id +
         ",relayState=" + relayState +
         ",issuer-channel=" + (issuer != null ? issuer.getName() : "null") +
         ",inResponseTp=" + inResponseTo +
         ",claimSet=" + claimSet;
    }

}
