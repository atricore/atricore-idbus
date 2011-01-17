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

import com.atricore.idbus.console.services.dto.UpdateDescriptorTypeDTO;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import java.util.Collection;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 1/13/11 - 2:50 PM
 */

public class CheckForUpdatesResponse {

    private Collection<UpdateDescriptorTypeDTO> updateDescriptors;

    public Collection<UpdateDescriptorTypeDTO> getUpdateDescriptors() {
        return updateDescriptors;
    }

    public void setUpdateDescriptors(Collection<UpdateDescriptorTypeDTO> updateDescriptors) {
        this.updateDescriptors = updateDescriptors;
    }
}
