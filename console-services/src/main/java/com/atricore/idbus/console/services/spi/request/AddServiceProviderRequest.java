/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.spi.request;

import org.atricore.idbus.capabilities.management.main.domain.metadata.ServiceProvider;
import org.atricore.idbus.capabilities.management.main.spi.request.AbstractManagementRequest;

/**
 * User: cdbirge
 * Date: Oct 21, 2009
 * Time: 9:16:36 AM
 * email: cbirge@atricore.org
 */
public class AddServiceProviderRequest extends AbstractManagementRequest {

    private long parentApplianceId;
    private ServiceProvider provider;

    public long getParentApplianceId() {
        return parentApplianceId;
    }

    public void setParentApplianceId(long parentApplianceId) {
        this.parentApplianceId = parentApplianceId;
    }

    public ServiceProvider getProvider() {
        return provider;
    }

    public void setProvider(ServiceProvider provider) {
        this.provider = provider;
    }
}