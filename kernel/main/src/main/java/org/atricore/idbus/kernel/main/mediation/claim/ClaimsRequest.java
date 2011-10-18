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

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1278 $ $Date: 2009-06-14 03:14:41 -0300 (Sun, 14 Jun 2009) $
 */
public interface ClaimsRequest extends Serializable {

    String getId();

    String getRelayState();

    Channel getIssuerChannel();

    IdentityMediationEndpoint getIssuerEndpoint();

    Channel getClaimsChannel();

    String getLastErrorId();

    String getLastErrorMsg();

    Set<SSOPolicyEnforcementStatement> getSsoPolicyEnforcements();

    String getSkin();
}