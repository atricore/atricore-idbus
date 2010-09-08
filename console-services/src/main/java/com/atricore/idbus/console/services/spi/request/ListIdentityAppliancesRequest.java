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



/**
 * Author: Dejan Maric
 */
public class ListIdentityAppliancesRequest extends AbstractManagementRequest {

    boolean startedOnly;
    boolean projectedOnly;

    int fetchDepth;

    public ListIdentityAppliancesRequest() {
        this.startedOnly = false;
        this.projectedOnly = false;
        this.fetchDepth = 3;
    }

    public boolean isStartedOnly() {
        return startedOnly;
    }

    public void setStartedOnly(boolean startedOnly) {
        this.startedOnly = startedOnly;
    }

    public boolean isProjectedOnly() {
        return projectedOnly;
    }

    public void setProjectedOnly(boolean projectedOnly) {
        this.projectedOnly = projectedOnly;
    }

    public int getFetchDepth() {
        return fetchDepth;
    }

    public void setFetchDepth(int fetchDepth) {
        if(fetchDepth < 0){
            this.fetchDepth = 1;
        } else if(fetchDepth == 0) {
            this.fetchDepth = 3; //workaround to set default value. after the constructor, flex calls setFetchDepth(0) when fetchDepth is not initialized in flex.
        }
        else {
            this.fetchDepth = fetchDepth;   
        }
    }
}
