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

package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.AclEntry;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;

/**
 * Created by IntelliJ IDEA.
 * User: eugenia
 * Date: 27-oct-2009
 * Time: 15:42:54
 * To change this template use File | Settings | File Templates.
 */
public class FindAclEntryByApprovalTokenResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -1798476899156498718L;

    private AclEntry aclEntry;

    public AclEntry getAclEntry() {
        return aclEntry;
    }

    public void setAclEntry(AclEntry aclEntry) {
        this.aclEntry = aclEntry;
    }

}
