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

package com.atricore.idbus.console.services.dto;

import java.util.HashSet;
import java.util.Set;

public class JOSSO1ResourceDTO extends ServiceResourceDTO {

    private static final long serialVersionUID = 8404812833078161141L;

    private String partnerAppId;

    private LocationDTO partnerAppLocation;

    private Set<String> ignoredWebResources;

    // TODO : Add other properties used to create JOSSO Agent config (PHP, ISAPI, Java, etc)

    public String getPartnerAppId() {
        return partnerAppId;
    }

    public void setPartnerAppId(String partnerAppId) {
        this.partnerAppId = partnerAppId;
    }

    public LocationDTO getPartnerAppLocation() {
        return partnerAppLocation;
    }

    public void setPartnerAppLocation(LocationDTO partnerAppLocation) {
        this.partnerAppLocation = partnerAppLocation;
    }

    public Set<String> getIgnoredWebResources() {
        if (ignoredWebResources == null) {
            ignoredWebResources = new HashSet<String>();
        }
        return ignoredWebResources;
    }

    public void setIgnoredWebResources(Set<String> ignoredWebResources) {
        this.ignoredWebResources = ignoredWebResources;
    }
}
