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

package org.atricore.idbus.kernel.main.mediation;

import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.camel.logging.MediationLogger;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: IdentityMediator.java 1492 2009-09-02 21:58:23Z sgonzalez $
 */
public interface IdentityMediator {

    void init(IdentityMediationUnitContainer unitContainer) throws IdentityMediationException;

    void start() throws IdentityMediationException;

    void stop() throws IdentityMediationException;

    void setupEndpoints(Channel channel) throws IdentityMediationException;

    EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException;

    MediationBindingFactory getBindingFactory();

    String getErrorUrl();

    String getWarningUrl();

    MediationLogger getLogger();

    boolean isLogMessages();

    Object sendMessage(MediationMessage message, Channel channel) throws IdentityMediationException;

    Object sendMessage(Object content, EndpointDescriptor destination, Channel channel) throws IdentityMediationException;


}
