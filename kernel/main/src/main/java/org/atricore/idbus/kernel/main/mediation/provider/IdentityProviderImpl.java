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

package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityProviderImpl extends AbstractFederatedLocalProvider implements IdentityProvider {

    private transient ProvisioningTarget provisioningTarget;
    private boolean identityConfirmationEnabled;
    private String identityConfirmationPolicy;

    public ProvisioningTarget getProvisioningTarget() {
        return provisioningTarget;
    }

    public void setProvisioningTarget(ProvisioningTarget provisioningTarget) {
        this.provisioningTarget = provisioningTarget;
    }

    public boolean isIdentityConfirmationEnabled() {
        return identityConfirmationEnabled;
    }

    public void setIdentityConfirmationEnabled(boolean identityConfirmationEnabled) {
        this.identityConfirmationEnabled = identityConfirmationEnabled;
    }

    public String getIdentityConfirmationPolicy() {
        return identityConfirmationPolicy;
    }

    public void setIdentityConfirmationPolicy(String identityConfirmationPolicy) {
        this.identityConfirmationPolicy = identityConfirmationPolicy;
    }


}
