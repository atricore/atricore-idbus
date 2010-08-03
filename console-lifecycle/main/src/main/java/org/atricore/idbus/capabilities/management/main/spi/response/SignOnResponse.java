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

package org.atricore.idbus.capabilities.management.main.spi.response;


/**
 * User: cdbirge
 * Date: Nov 3, 2009
 * Time: 9:04:49 AM
 * email: cbirge@atricore.org
 */
public class SignOnResponse extends AbstractManagementResponse {

    private String assertion;
    private String signOnStatusCode;

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

    public String getSignOnStatusCode() {
        return signOnStatusCode;
    }

    public void setSignOnStatusCode(String signOnStatusCode) {
        this.signOnStatusCode = signOnStatusCode;
    }
}
