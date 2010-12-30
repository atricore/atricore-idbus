package com.atricore.idbus.console.licensing.main.util;

import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Author: Dejan Maric
 */
public class LicenseUtil {
    public static final String ATRICORE_LICENSE_PKG = "com.atricore.josso2.licensing._1_0.license";
    public static final String ATRICORE_LICENSE_NS = "urn:com:atricore:josso2:licensing:1.0:license";

    static Log logger = LogFactory.getLog(LicenseUtil.class);    

    public static void marshal(LicenseType license, String licensePath, ClassLoader cl) throws JAXBException {
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(ATRICORE_LICENSE_PKG);

            JAXBElement jaxbRequest = new JAXBElement(
                    new QName( ATRICORE_LICENSE_NS, "license" ),
                    license.getClass(), license);
            Marshaller marshaller = jaxbContext.createMarshaller();
            String outputFile = licensePath;
            marshaller.marshal(jaxbRequest, new FileOutputStream(outputFile, false));

        } catch (FileNotFoundException e) {
            logger.error("Output file doesn't exist");
        }

    }

    public static void marshal(LicenseType licence, String licensePath) throws JAXBException {
        marshal(licence, licensePath, Thread.currentThread().getContextClassLoader());
    }

    public static LicenseType unmarshal(InputStream is) throws JAXBException {
        return unmarshal(is, Thread.currentThread().getContextClassLoader());
    }

    public static LicenseType unmarshal(InputStream is, ClassLoader cl) throws JAXBException {

        JAXBContext ctx = JAXBContext.newInstance(LicenseUtil.ATRICORE_LICENSE_PKG, cl);

        Unmarshaller um  = ctx.createUnmarshaller();

        return (LicenseType) ((JAXBElement)um.unmarshal(is)).getValue();
    }
}
