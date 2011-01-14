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

package com.atricore.liveservices.liveupdate._1_0.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.GregorianCalendar;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 1345 $ $Date: 2009-07-01 10:58:10 -0300 (Wed, 01 Jul 2009) $
 */
public class DateUtils {

    private static final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat
            ("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final java.util.TimeZone tz = java.util.TimeZone.getTimeZone("UTC");
    
    static {
        dateFormat.setTimeZone(tz);
    }

    private static DatatypeFactory newDatatypeFactory() {
        DatatypeFactory datatypeFactory;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to initialize subscription", e);
        }

        return datatypeFactory;
    }

    public static String toString(java.util.Date date) {
        final StringBuffer strDate;

        StringBuffer strDateBuffer = new StringBuffer();
        strDate = dateFormat.format(date, strDateBuffer, new FieldPosition(0));
        return strDate.toString();
    }

    public static java.util.Date toDate(String strDate) {

        try {
            java.util.Date date = dateFormat.parse(strDate.trim(),  new ParsePosition(0));
            return date;
        } catch (Exception e) {
        }

        return null;
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(long dateInMillis) {
        java.util.Date date = new java.util.Date(dateInMillis);
        return toXMLGregorianCalendar(date);
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(java.util.Date date) {

        DatatypeFactory datatypeFactory = newDatatypeFactory();
        GregorianCalendar gdate = new GregorianCalendar(tz);
        gdate.setTime(date);

        XMLGregorianCalendar calendar = datatypeFactory.newXMLGregorianCalendar(gdate);
        
//        XMLGregorianCalendar calendar = datatypeFactory.newXMLGregorianCalendar(
//                (date.getYear() + 1900),
//                (date.getMonth() + 1),
//                date.getDate(),
//                date.getHours(),
//                date.getMinutes(),
//                date.getSeconds(),
//                0,
//                0 /* set to zulu UTC time */
//        );

        return calendar;

    }


}
