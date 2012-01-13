/*
 * Atricore IDBus
 *
 * Copyright (c) 2011, Atricore Inc.
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

package org.atricore.idbus.applications.server.ui.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CollectUsernamePasswordClaims implements java.io.Serializable {

    private static final Log logger = LogFactory.getLog(CollectUsernamePasswordClaims.class);

    private ClaimsRequest claimsRequest;
    private String username;
    private String password;
    private boolean rememberMe;

    public CollectUsernamePasswordClaims() {
        logger.debug("Creating new CollectUsernamePasswordClaims instance");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public ClaimsRequest getClaimsRequest() {
        return claimsRequest;
    }

    public void setClaimsRequest(ClaimsRequest request) {
        this.claimsRequest = request;
    }

    @Override
    public String toString() {
        return super.toString() + "[username=" + username +
                ",password=" + (password != null ? password : "********" ) +
                ",rememberMe=" + rememberMe + "]";
    }
}
