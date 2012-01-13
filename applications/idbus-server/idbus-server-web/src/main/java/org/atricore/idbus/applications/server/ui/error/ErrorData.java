/*
 * Atricore IDBus
 *
 * Copyright (c) 2011, Atricore Inc.
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

package org.atricore.idbus.applications.server.ui.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ErrorData {

    private String status;
    private String secStatus;
    private String details;
    private String errDetails;

    private List<String> causes = new ArrayList<String>();

    public ErrorData() {
    }

    public ErrorData(String status,
                     String secStatus,
                     String details,
                     String errDetails,
                     List<String> causes) {

        this.status = status;
        this.secStatus = secStatus;
        this.errDetails = errDetails;
        this.details = details;
        this.causes = causes;
    }

    public String getStatus() {
        return status;
    }

    public String getSecStatus() {
        return secStatus;
    }

    public String getDetails() {
        return details;
    }

    public String getErrDetails() {
        return errDetails;
    }

    public Collection<String> getCauses() {
        return causes;
    }
}
