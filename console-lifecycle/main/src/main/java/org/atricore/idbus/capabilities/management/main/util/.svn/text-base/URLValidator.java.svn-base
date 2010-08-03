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

package org.atricore.idbus.capabilities.management.main.util;

/**
 * Created by IntelliJ IDEA.
 * User: cdbirge
 * Date: Nov 11, 2009
 * Time: 7:02:40 PM
 * Email: cbirge@atricore.org
 */
public class URLValidator {

    public static String REGEXP_URL_VALIDATOR = "^(http(s?))://(www.)?[\\w.-]+(:[\\d]{1,4})?(/([\\w.-])*)*\\b$";

    public static String REGEXP_URL_PART_VALIDATOR = "^(([\\w.-])*)*\\b$";


    public static boolean validateUrl(String url){
        return url.matches(REGEXP_URL_VALIDATOR);
    }

    public static boolean validateUrlPart(String urlPart){
        return urlPart.matches(REGEXP_URL_PART_VALIDATOR);
    }
}
