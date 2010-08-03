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

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1040 $ $Date: 2009-03-04 22:56:52 -0200 (Wed, 04 Mar 2009) $
 */
public interface AccountLinkLifecycle {

    PersistentAccountLink establishPersistent(Subject idpSubject, String localSubjectNameIdentifier ) throws AccountLinkageException;

    TransientAccountLink establishTransient(Subject idpSubject, String localSubjectNameIdentifier ) throws AccountLinkageException;

    boolean persistentForIDPSubjectExists(Subject idpSubject) throws AccountLinkageException;

    boolean transientForIDPSubjectExists(Subject idpSubject) throws AccountLinkageException;

    AccountLink findByIDPAccount(Subject idpSubject) throws AccountLinkageException;

    AccountLink findByLocalAccount(Subject localSubject) throws AccountLinkageException;

    Subject resolve(AccountLink accountLink) throws AccountLinkageException;

    AccountLink disable(AccountLink accountLink) throws AccountLinkageException;

    AccountLink enable(AccountLink accountLink) throws AccountLinkageException;

    AccountLink dispose(AccountLink accountLink) throws AccountLinkageException;

}
