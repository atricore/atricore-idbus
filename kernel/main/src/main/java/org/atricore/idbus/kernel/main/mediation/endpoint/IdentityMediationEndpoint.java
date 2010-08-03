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

package org.atricore.idbus.kernel.main.mediation.endpoint;

import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.planning.IdentityPlan;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IdentityMediationEndpoint.java 1259 2009-06-09 20:08:04Z sgonzalez $
 */
public interface IdentityMediationEndpoint {

    /**
     * Endpoint type
     */
    String getType();

    /**
     * Endpoint name
     */
    String getName();

    /**
     * Protocol binding
     */
    String getBinding();

    /**
     * Channel relative location
     */
    String getLocation();

    /**
     * Channel relative response location
     */
    String getResponseLocation();

    /**
     * Endpoint springmetadata
     * @return
     */
    MetadataEntry getMetadata();

    /**
     * Configured plans for this endpoint.
     * @return
     */
    Collection<IdentityPlan> getIdentityPlans();

}
