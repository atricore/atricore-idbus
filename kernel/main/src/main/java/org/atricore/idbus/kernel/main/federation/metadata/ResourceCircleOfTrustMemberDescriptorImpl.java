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

package org.atricore.idbus.kernel.main.federation.metadata;

import org.springframework.core.io.Resource;

/**
 * Circle of Trust Member descriptor referencing springmetadata definition as a resource.
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: ResourceCircleOfTrustMemberDescriptorImpl.java 1269 2009-06-11 16:28:55Z sgonzalez $
 */
public class ResourceCircleOfTrustMemberDescriptorImpl extends CircleOfTrustMemberDescriptorImpl {

    // Metadata resource, file, stream, etc
    transient private Resource resource;

    public Resource getResource() {
        return resource;
    }

    /**
     * @org.apache.xbean.Property alias="springmetadata-resource"
     *
     * @param resource
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return super.toString() + "[resource="+resource+"]";
    }
}
