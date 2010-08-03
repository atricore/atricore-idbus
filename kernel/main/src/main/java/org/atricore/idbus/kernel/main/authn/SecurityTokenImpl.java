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

package org.atricore.idbus.kernel.main.authn;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: JOSSO11BindingRouteTest.java 1077 2009-03-20 22:27:50Z ajadzinsky $
 */
public class SecurityTokenImpl<T> implements SecurityToken<T> {
    private String id;
    private T content;
    private String nameIdentifier;

    public SecurityTokenImpl(String id, T content) {
        this(id, null, content);
    }

    public SecurityTokenImpl(String id, String nameIdentifier, T content) {
        this.id = id;
        this.nameIdentifier = nameIdentifier;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public T getContent() {
        return content;
    }

    public String getNameIdentifier() {
        return nameIdentifier;
    }

}
