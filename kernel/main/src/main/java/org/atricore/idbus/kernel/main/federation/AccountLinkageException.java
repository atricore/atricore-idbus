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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1040 $ $Date: 2009-03-04 22:56:52 -0200 (Wed, 04 Mar 2009) $
 */
public class AccountLinkageException extends Exception {


    // ID already exists for a user
    public static final int DUPLICATE_ID = 1;

    // ID Already used for another social service, ID source
    public static final int USED_ID = 2;

    // No account link found.
    public static final int NO_ACCOUNT_LINK = 3;



    private String errorDetails;

    private int error = NO_ACCOUNT_LINK;


    public AccountLinkageException(String message) {
        super(message);
    }

    public AccountLinkageException(int error, String message, String details) {
        super(message);
        this.errorDetails = details;
        this.error = error;
    }

    public AccountLinkageException(int error, String message, String details, Throwable cause) {
        super(message, cause);
        this.errorDetails = details;
        this.error = error;
    }

    public AccountLinkageException(String message, String details, Throwable cause) {
        super(message, cause);
        this.errorDetails = details;
    }

    public AccountLinkageException(String message, Throwable cause) {
        super(message, cause);
    }


    public String getErrorDetails() {
        return errorDetails;
    }

    public int getError() {
        return error;
    }
}
