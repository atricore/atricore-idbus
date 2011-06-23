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

package org.atricore.idbus.capabilities.sts.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;

import javax.security.auth.Subject;

/**
 *
 *  @org.apache.xbean.XBean element="security-token-authenticator"
 *
 * This implementation supports built-in authentication schemes.
 * This will probably be replaced once the provisioning infrastructure is in place.
 *
 * For now, the component will addapt authenticaiton requests and redirect them to legacy authenticator component.
 *
 * TODO : Replace with pluggable authentication infrastructure!
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: DefaultSecurityTokenAuthenticator.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
@Deprecated
public class DefaultSecurityTokenAuthenticator extends AbstractSecurityTokenAuthenticator {

    private static Log logger = LogFactory.getLog(DefaultSecurityTokenAuthenticator.class);

    public boolean canAuthenticate(Object requestToken) {
        return false;
    }

    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {
        throw new SecurityTokenEmissionException("Cannot use deprecated authenticator!");
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        throw new UnsupportedOperationException("Not required by this implementation!");
    }
}
