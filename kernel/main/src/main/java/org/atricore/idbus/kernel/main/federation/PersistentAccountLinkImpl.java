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

import javax.security.auth.Subject;
import java.util.Map;


public class PersistentAccountLinkImpl extends AbstractAccountLink implements PersistentAccountLink {

	private Long persistanceId;

	protected PersistentAccountLinkImpl(){
		this.setEnabled(true);
		this.setDeleted(false);
	}

	public PersistentAccountLinkImpl(Subject idpSubject, String localAccountNameIdentifier, String localAccountNameFormat,
                                     Map<String, String> context) {
        super(idpSubject, localAccountNameIdentifier, localAccountNameFormat, context);
        this.setEnabled(true);
        this.setDeleted(false);
    }


	public Long getPersistanceId() {
		return persistanceId;
	}

	private void setPersistanceId(Long persistanceId) {
		this.persistanceId = persistanceId;
	}

	public class Builder extends BaseAccountLinkBuilder {

	    protected Long persisenceId;

        public void setPersisenceId(Long persisenceId) {
            this.persisenceId = persisenceId;
        }

        @Override
        public AccountLink build() {
            PersistentAccountLinkImpl p = new PersistentAccountLinkImpl(this.idpSubject, this.localAccount, this.accountFormat, this.props);
            p.setPersistanceId(this.persisenceId);
            p.setDeleted(this.isDeleted);
            p.setEnabled(this.isEnabled);
            return p;
        }
    }
}
