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

package org.atricore.idbus.capabilities.sso.main.sp;

import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.federation.AccountLink;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.util.Locale;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SPSecurityContext implements Serializable {


    private String idpAlias;

    private Subject subject;

    private AccountLink acctLink;

    private String sessionIndex;

    private String idpSsoSession;

    private String requester;

    private Long lastIdPSessionHeartBeat;

    private AuthnCtxClass authnCtxClass;

    public SPSecurityContext() {
    }

    public String getIdpAlias() {
        return idpAlias;
    }

    public void setIdpAlias(String idpAlias) {
        this.idpAlias = idpAlias;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getSessionIndex() {
        return sessionIndex;
    }

    public void setSessionIndex(String sessionIndex) {
        this.sessionIndex = sessionIndex;
    }

    public AccountLink getAccountLink() {
        return acctLink;
    }

    public void setAccountLink(AccountLink acctLink) {
        this.acctLink = acctLink;
    }

    public AccountLink getAcctLink() {
        return acctLink;
    }

    public void setAcctLink(AccountLink acctLink) {
        this.acctLink = acctLink;
    }

    public Long getLastIdPSessionHeartBeat() {
        return lastIdPSessionHeartBeat;
    }

    public void setLastIdPSessionHeartBeat(Long lastIdPSessionHeartBeat) {
        this.lastIdPSessionHeartBeat = lastIdPSessionHeartBeat;
    }

    public void setIdpSsoSession(String idpSessionIndex) {
        this.idpSsoSession = idpSessionIndex;
    }

    public String getIdpSsoSession() {
        return idpSsoSession;
    }

    public void clear() {
        this.idpAlias = null;
        this.subject = null;
        this.acctLink = null;
        this.sessionIndex = null;
        this.lastIdPSessionHeartBeat = null;
    }

    @Override
    public String toString() {
        return super.toString() +
                "[" +
                "requester=" + requester +
                ",idpAlias=" + idpAlias +
                ",sessionIndex=" + sessionIndex +
                ",acctLink=" + acctLink +
                ",subject=" + subject +
                ",lastIdPSessionHeartBeat=" + lastIdPSessionHeartBeat +
                "]";
    }

    public void setAuthnCtxClass(AuthnCtxClass authnCtxClass) {
        this.authnCtxClass = authnCtxClass;
    }

    public AuthnCtxClass getAuthnCtxClass() {
        return authnCtxClass;
    }
}
