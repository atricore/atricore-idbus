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

import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1040 $ $Date: 2009-03-04 22:56:52 -0200 (Wed, 04 Mar 2009) $
 */
public abstract class AbstractAccountLink implements AccountLink {

    private String id;

    private String localAccountNameIdentifier;
    private String localAccountNameFormat;

    private Subject idpSubject;

    private boolean enabled;
    private boolean deleted;
    private Map<String, String> context;

    protected AbstractAccountLink(){}

    protected AbstractAccountLink(Subject idpSubject, String localAccountNameIdentifier, String format,
                                  Map<String, String> context) {
        this.id = new UUIDGenerator().generateId();
        this.idpSubject = idpSubject;
        this.localAccountNameIdentifier = localAccountNameIdentifier;
        this.localAccountNameFormat = format;
        this.context = context == null ? new HashMap<String, String>() : context;

    }

    @Override
    public UserKey getUserKey() {
        return new SimpleUserKey(localAccountNameIdentifier);
    }

    public String getId() {
        return id;
    }

    public String getLocalAccountNameIdentifier() {
        return localAccountNameIdentifier;
    }

    private String getLocalAccountNameFormat() {
        return localAccountNameFormat;
    }

	public Subject getIdpSubject() {
		return idpSubject;
	}

	protected void setIdpSubject(Subject idpSubject) {
		this.idpSubject = idpSubject;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected void setLocalAccountNameIdentifier(String localAccountNameIdentifier) {
		this.localAccountNameIdentifier = localAccountNameIdentifier;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getContextProperty(String key) {
        return context.get(key);
    }


    @Override
    public String toString() {
        return super.toString() +
                "[" +
                "id=" + id +
                ",localAccountNameIdentifier" + localAccountNameIdentifier +
                ",enabled=" + enabled +
                ",deleted=" + deleted +
                ",idpSubject=" + idpSubject +
                "]";
    }
}
