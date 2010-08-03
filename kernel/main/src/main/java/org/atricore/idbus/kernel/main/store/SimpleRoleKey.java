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

package org.atricore.idbus.kernel.main.store;


/**
 * This role key implementation uses a String as identifier.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SimpleRoleKey.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public class SimpleRoleKey implements RoleKey {

    private String _id;

    public SimpleRoleKey(String id) {
        _id = id;
    }

    public String getId() {
        return _id;
    }

    /**
     * Compare this SimpleRokeKey name against another SimpleRoleKey name
     *
     * @return true if name equals another.getRegion()
     */
    public boolean equals(Object another) {
        if (!(another instanceof SimpleRoleKey))
            return false;

        String anotherId = ((SimpleRoleKey) another).getId();

        boolean equals = false;
        if (_id == null) {
            equals = anotherId == null;
        } else {
            equals = _id.equals(anotherId);
        }

        return equals;
    }

    /**
     * Returns the hashcode of the name
     */
    public int hashCode() {
        return (_id == null ? 0 : _id.hashCode());
    }

    public String toString() {
        return _id;
    }


}
