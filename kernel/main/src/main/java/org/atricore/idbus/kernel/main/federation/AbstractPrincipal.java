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

package org.atricore.idbus.kernel.main.federation;

import java.io.Serializable;
import java.security.Principal;

public abstract class AbstractPrincipal implements Serializable, Principal {

	private Long id;
	
	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}
	
    /**
     * Compare this BaseRole's name against another BaseRole
     *
     * @return true if name equals another.getName();
     */
    public boolean equals(Object another) {

        if (!another.getClass().isAssignableFrom(getClass()))
            return false;

        String anotherName = ((AbstractPrincipal) another).getName();
        boolean equals = false;
        if (getName() == null)
            equals = anotherName == null;
        else
            equals = getName().equals(anotherName);
        return equals;
    }

    public int hashCode() {
        return (getName() == null ? 0 : getName().hashCode());
    }

    abstract public String getName();

    abstract protected void setName(String name);

}
