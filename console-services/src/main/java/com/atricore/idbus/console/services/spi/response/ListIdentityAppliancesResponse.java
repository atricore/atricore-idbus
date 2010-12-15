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

package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Dejan Maric
 */
public class ListIdentityAppliancesResponse extends AbstractManagementResponse {

    private Collection<IdentityApplianceDTO> identityAppliances;

    public Collection<IdentityApplianceDTO> getIdentityAppliances() {
        if(identityAppliances == null){
            identityAppliances = new ArrayList<IdentityApplianceDTO>();
        }
        return identityAppliances;
    }

    public void setIdentityAppliances(Collection<IdentityApplianceDTO> identityAppliances) {
        this.identityAppliances = identityAppliances;
    }
}