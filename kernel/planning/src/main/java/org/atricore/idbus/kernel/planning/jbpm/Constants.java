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

package org.atricore.idbus.kernel.planning.jbpm;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: Constants.java 1220 2009-05-28 03:57:34Z sgonzalez $
 */
public interface Constants {

    static final String VAR_IN_IDENTITY_ARTIFACT = "urn:org:atricore:idbus:id-plan:InIdentityArtifact";

    static final String VAR_OUT_IDENTITY_ARTIFACT = "urn:org:atricore:idbus:idplan:OutIdentityArtifact";

    /**
     * Process Fragment Registry variable name.
     */
    static final String VAR_PFR = "urn:org:atricore:idbus:bpm:ProcessFragmentRegistry";

    /**
     * Process Descriptor Name variable name.
     */
    static final String VAR_PDN = "urn:org:atricore:idbus:bpm:ProcessDescriptorName";

    /**
     * Application context
     */
    static final String VAR_APP_CTX = "urn:org:atricore:idbus:bpm:SpringAppContext";

    /**
     * Identity Plan Execution Exchange
     */
    static final String VAR_IPEE = "urn:org:atricore:idbus:bpm:IdentityPlanExecutionExchange";


}
