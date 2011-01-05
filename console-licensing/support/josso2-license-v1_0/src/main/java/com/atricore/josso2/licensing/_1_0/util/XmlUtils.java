package com.atricore.josso2.licensing._1_0.util;

import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class XmlUtils {

    public static final String ATRICORE_LICENSE_PKG = "com.atricore.josso2.licensing._1_0.license";
    public static final String ATRICORE_LICENSE_NS = "urn:com:atricore:josso2:licensing:1.0:license";

    public static LicenseType unmarshallLicense(InputStream is, boolean decode) throws Exception {
        return unmarshallLicense(new String(getByteArray(is)), decode);
    }

    public static LicenseType unmarshallLicense(String udIdxStr, boolean decode) throws Exception {
        if (decode)
            udIdxStr = new String(new Base64().decode(udIdxStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(udIdxStr, new String[]{ ATRICORE_LICENSE_PKG });
        return (LicenseType) e.getValue();
    }

    public static LicenseType unmarshallLicense(Document udIdx) throws Exception {
        JAXBElement e = (JAXBElement) unmarshal(udIdx, new String[]{ ATRICORE_LICENSE_PKG });
        return (LicenseType) e.getValue();
    }

    public static String marshalLicense(LicenseType udIdx, boolean encode) throws Exception {
        String type = udIdx.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = Character.toLowerCase(type.charAt(0)) + type.substring(1, type.length() - 4);

        return marshalLicense(udIdx, type, encode);

    }

    public static String marshalLicense(LicenseType udIdx, String requestType, boolean encode) throws Exception {

        String marshalled ;
        // Support IDBus SAMLR2 Extentions when marshalling
        marshalled = marshal(
            udIdx,
            ATRICORE_LICENSE_NS,
            requestType,
            new String[]{ ATRICORE_LICENSE_PKG }
        );

        return encode ? new String(new Base64().encode( marshalled.getBytes())) : marshalled;
    }

    public static Document marshalLicenseToDOM(LicenseType udIdx) throws Exception {
        String marshalled = marshalLicense(udIdx, false);

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(marshalled.getBytes()));
    }

    // JAXB Generic

    public static String marshal ( Object msg, String msgQName, String msgLocalName, String[] userPackages ) throws Exception {

        JAXBContext jaxbContext = createJAXBContext( userPackages );
        JAXBElement jaxbRequest = new JAXBElement( new QName( msgQName, msgLocalName ),
                msg.getClass(),
                msg
        );
        Writer writer = new StringWriter();

        // Support XMLDsig
        jaxbContext.createMarshaller().marshal( jaxbRequest, new NamespaceFilterXMLStreamWriter(writer) );

        return writer.toString();
    }

    public static Object unmarshal( String msg, String userPackages[] ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext( userPackages );
        return jaxbContext.createUnmarshaller().unmarshal( new StringSource( msg ) );
    }

    public static Object unmarshal( Document doc, String userPackages[] ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext( userPackages );
        return jaxbContext.createUnmarshaller().unmarshal( doc );
    }

    public static JAXBContext createJAXBContext ( String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();
        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }
        // Use our classloader to build JAXBContext so it can find binding classes.
        return JAXBContext.newInstance( packages.toString(), XmlUtils.class.getClassLoader());
    }

    private static byte[] getByteArray(InputStream is) throws Exception {
        return IOUtils.toByteArray(is);
    }
}