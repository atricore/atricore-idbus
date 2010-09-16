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

package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.lifecycle.main.exception.SignOnException;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;

/**
 * @author <a href=mailto:dfisic@atricore.org>Dusan Fisic</a>
 */

public interface SpmlAjaxClient {

    String getPspTargetId();

    void setPspTargetId(String pspTargetId);

    void setSpmlService(SpmlR2Client svc);
}