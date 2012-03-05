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
package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;


/**
 * SSO-specific implementation of the wicket web session. Used to persist the claim request.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class SSOWebSession extends WebSession {

    private ClaimsRequest claimsRequest;

    public SSOWebSession(Request request) {
        super(request);
    }

    /**
     * @return Current authenticated web session
     */
    public static SSOWebSession get()
    {
        return (SSOWebSession) Session.get();
    }

    public void setClaimsRequest(ClaimsRequest claimsRequest) {
        this.claimsRequest = claimsRequest;
    }

    public ClaimsRequest getClaimsRequest() {
        return claimsRequest;
    }



}
