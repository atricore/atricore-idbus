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

package org.atricore.idbus.capabilities.sso.main.idp.plans.actions;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SetAuthnResponseStatusAction extends AbstractSSOAction {

    public static final Log logger = LogFactory.getLog(SetAuthnResponseStatusAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        ResponseType response = (ResponseType) out.getContent();
        AuthnRequestType request = (AuthnRequestType) in.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Setting SAMLR2 Status Code");

        StatusCodeType statusCode = new StatusCodeType();
        AssertionType assertion = (AssertionType) executionContext.getContextInstance().getVariable(VAR_SAMLR2_ASSERTION);

        // TODO : Check error variable to provide different status codes!
        if (assertion != null) {
            if (logger.isDebugEnabled())
                logger.debug("Assertion found, authentication succeeded");

            statusCode.setValue(StatusCode.TOP_SUCCESS.getValue());
        } else {

            if (logger.isDebugEnabled())
                logger.debug("Assertion NOT found, authentication failed");

            // Authentication failed, send propper status code!
            statusCode.setValue(StatusCode.TOP_RESPONDER.getValue());

            if (request != null && request.getIsPassive()) {

                if (logger.isDebugEnabled())
                    logger.debug("Setting secondary status code to NO PASSIVE");

                StatusCodeType secStatusCode = new StatusCodeType();
                secStatusCode.setValue(StatusCode.NO_PASSIVE.getValue());
                statusCode.setStatusCode(secStatusCode);

            } else {

                if (logger.isDebugEnabled())
                    logger.debug("Setting secondary status code to AUTHN FAILED");

                StatusCodeType secStatusCode = new StatusCodeType();
                secStatusCode.setValue(StatusCode.AUTHN_FAILED.getValue());
                statusCode.setStatusCode(secStatusCode);
            }

        }

        StatusType status = new StatusType();
        status.setStatusCode(statusCode);

        response.setStatus(status);

    }
}
