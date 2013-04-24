/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
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

package org.atricore.idbus.kernel.main.mediation.confirmation;

import org.atricore.idbus.kernel.main.mediation.AbstractChannel;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityConfirmationProvider;
import org.atricore.idbus.kernel.main.mediation.provider.StatefulProvider;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

/**
 * Channel implementation of identity confirmation providers.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class IdentityConfirmationChannelImpl extends AbstractChannel implements IdentityConfirmationChannel {

    private IdentityConfirmationProvider identityConfirmationProvider;

    public StatefulProvider getProvider() {
        return identityConfirmationProvider;
    }

    public void setProvider(IdentityConfirmationProvider identityConfirmationProvider) {
        this.identityConfirmationProvider = identityConfirmationProvider;
    }
}
