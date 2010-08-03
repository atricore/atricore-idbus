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



/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1040 $ $Date: 2009-03-04 22:56:52 -0200 (Wed, 04 Mar 2009) $
 */
public class SubjectNameID extends AbstractPrincipal {
	
    private String name;

    private String format;

    private String localNameQualifier;

    private String nameQualifier;

    private String localName;
	
	protected SubjectNameID() {	
	}

    public SubjectNameID(String name, String format) {
        this.name = name;
        this.format = format;
    }

    public SubjectNameID(String name, String format, String nameQualifier, String localNameQualifier) {
        this.name = name;
        this.format = format;
        this.nameQualifier = nameQualifier;
        this.localNameQualifier = localNameQualifier;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public String getLocalNameQualifier() {
        return localNameQualifier;
    }

    public String getNameQualifier() {
        return nameQualifier;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String toString() {
        return "[Subject ID: " + name + "=" + format + "]";
    }

	protected void setName(String name) {
		this.name = name;
	}

	protected void setFormat(String format) {
		this.format = format;
	}

	protected void setLocalNameQualifier(String localNameQualifier) {
		this.localNameQualifier = localNameQualifier;
	}

	protected void setNameQualifier(String nameQualifier) {
		this.nameQualifier = nameQualifier;
	}

}
