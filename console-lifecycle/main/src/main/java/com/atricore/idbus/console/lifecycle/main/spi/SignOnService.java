/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.exception.SignOnServiceException;
import com.atricore.idbus.console.lifecycle.main.spi.request.SignOnRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.SignOutRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.UserLoggedRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.SignOnResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.SignOutResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.UserLoggedResponse;

/**
 * User: cdbirge
 * Date: Nov 3, 2009
 * Time: 8:44:43 AM
 * email: cbirge@atricore.org
 */
public interface SignOnService {

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnServiceException;

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnServiceException;

    UserLoggedResponse userLogged(UserLoggedRequest userLoggedRequest) throws SignOnServiceException;
}
