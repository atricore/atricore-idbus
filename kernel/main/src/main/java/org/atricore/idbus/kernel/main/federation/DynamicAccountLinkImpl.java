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
import java.util.HashMap;

public class DynamicAccountLinkImpl extends AbstractAccountLink implements DynamicAccountLink {

    public DynamicAccountLinkImpl(Subject idpSubject,
                                  String localAccountNameIdentifier,
                                  String localAccountNameFormat,
                                  Map<String, String> context) {
        super(idpSubject, localAccountNameIdentifier, localAccountNameFormat, context);
    }

    public DynamicAccountLinkImpl(Subject idpSubject,
                                  String localAccountNameIdentifier,
                                  String localAccountNameFormat) {
        super(idpSubject, localAccountNameIdentifier, localAccountNameFormat, new HashMap<String, String>());
    }

    public static class Builder extends BaseAccountLinkBuilder<Builder> {

        @Override
        public AccountLink build() {
            DynamicAccountLinkImpl l = new DynamicAccountLinkImpl(this.idpSubject, this.localAccount, this.accountFormat, this.props);
            l.setDeleted(this.isDeleted);
            l.setEnabled(this.isEnabled);
            return l;
        }
    }

}
