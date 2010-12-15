package org.atricore.idbus.capabilities.spmlr2.main.common;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.GregorianCalendar;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DateUtils {

    private static final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat
            ("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final java.util.TimeZone tz = java.util.TimeZone.getTimeZone("UTC");
    private static final ParsePosition startParsePos = new ParsePosition(0);
    private static final FieldPosition firstFieldPos = new FieldPosition(0);

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
        strDate = dateFormat.format(date, strDateBuffer, firstFieldPos);
        return strDate.toString();
    }

    public static java.util.Date toDate(String strDate) {

        try {
            java.util.Date date = dateFormat.parse(strDate.trim(), startParsePos);
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
