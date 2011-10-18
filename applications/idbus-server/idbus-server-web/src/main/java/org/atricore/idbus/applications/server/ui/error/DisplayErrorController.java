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

package org.atricore.idbus.applications.server.ui.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class DisplayErrorController extends AbstractController {

    private static final Log logger = LogFactory.getLog(DisplayErrorController.class);

    private MessageQueueManager artifactQueueManager;

    protected ModelAndView handleRequestInternal(HttpServletRequest hreq, HttpServletResponse hrews) throws Exception {

        String artifactId = hreq.getParameter("IDBusErrArt");

        try {
            MediationMessage fault = artifactId != null ? getFault(artifactId) : null;

            if (fault != null) {

                String details = fault.getFaultDetails();
                IdentityMediationFault err = fault.getFault();
                List<String> causes = buildCauses(err);
                ErrorData f = new ErrorData(err.getFaultCode(),
                        err.getSecFaultCode(),
                        err.getStatusDetails(),
                        details,
                        causes);
                hreq.setAttribute("IDBusError", f);

            }

        } catch (Exception e) {
            logger.error("Cannot display error information:" + e.getMessage(), e);
            ErrorData f = new ErrorData("urn:org:atricore:idbus:samlr2:status:InternalError",
                    null,
                    null,
                    e.getMessage(),
                    buildCauses(e));

            hreq.setAttribute("IDBusError", f);
        }

        // No error information available
        return new ModelAndView("error");

    }

    protected MediationMessage getFault(String artifactId) throws Exception {
        return (MediationMessage) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));
    }

    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }

    protected List<String> buildCauses(Throwable cause) {

        List<String> causes = new ArrayList<String>();

        while (cause != null) {

            Writer errorWriter = new StringWriter();
            PrintWriter errorPrintWriter = new PrintWriter(errorWriter);

            cause.printStackTrace(errorPrintWriter);
            causes.add(errorWriter.toString());

            cause = cause.getCause();
        }
        return causes;

    }
}
