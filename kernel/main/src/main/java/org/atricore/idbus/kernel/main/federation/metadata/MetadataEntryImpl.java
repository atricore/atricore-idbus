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

/**
 *
 */
public class MetadataEntryImpl<E> implements MetadataEntry {

    private String name;

    private E metadata;

    public MetadataEntryImpl(String name, E metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public E getEntry() {
        return metadata;
    }

    @Override
    public int hashCode() {
        return metadata.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        MetadataEntry me = (MetadataEntry) obj;
        return metadata.equals(me);
    }

    @Override
    public String toString() {
        return metadata.toString();
    }

}
