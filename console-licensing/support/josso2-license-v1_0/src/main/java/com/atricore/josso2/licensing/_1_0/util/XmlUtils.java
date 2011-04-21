package com.atricore.josso2.licensing._1_0.util;

import com.atricore.josso2.licensing._1_0.license.LicenseType;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class XmlUtils {

    public static final String ATRICORE_LICENSE_PKG = "com.atricore.josso2.licensing._1_0.license";
    public static final String ATRICORE_LICENSE_NS = "urn:com:atricore:josso2:licensing:1.0:license";

    public static LicenseType unmarshalLicense(InputStream is, boolean decode) throws Exception {
        return unmarshalLicense(new String(getByteArray(is)), decode);
    }

    public static LicenseType unmarshalLicense(String udIdxStr, boolean decode) throws Exception {
        if (decode)
            udIdxStr = new String(new Base64().decode(udIdxStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(udIdxStr, new String[]{ ATRICORE_LICENSE_PKG });
        return (LicenseType) e.getValue();
    }

    public static LicenseType unmarshalLicense(Document udIdx) throws Exception {
        JAXBElement e = (JAXBElement) unmarshal(udIdx, new String[]{ ATRICORE_LICENSE_PKG });
        return (LicenseType) e.getValue();
    }

    public static String marshalLicense(LicenseType udIdx, boolean encode) throws Exception {

        String type = udIdx.getClass().getSimpleName();

        if (type.endsWith("Type"))
            type = Character.toLowerCase(type.charAt(0)) + type.substring(1, type.length() - 4);

        return marshalLicense(udIdx, type, encode);

    }

    public static String marshalLicense(LicenseType udIdx, String license, boolean encode) throws Exception {

        String marshalled ;
        // Support IDBus SAMLR2 Extentions when marshalling
        marshalled = marshal(
            udIdx,
            ATRICORE_LICENSE_NS,
            license,
            new String[]{ ATRICORE_LICENSE_PKG }
        );

        return encode ? new String(new Base64().encode( marshalled.getBytes())) : marshalled;
    }

    public static Document marshalLicenseToDOM(LicenseType udIdx) throws Exception {
        String marshaled = marshalLicense(udIdx, false);

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(marshaled.getBytes()));
    }

    // JAXB Generic

    public static String marshal ( Object lic, String licQName, String licLocalName, String[] userPackages ) throws Exception {

        JAXBContext jaxbContext = createJAXBContext( userPackages );

        JAXBElement jaxbElement = new JAXBElement( new QName( licQName, licLocalName ),
                lic.getClass(),
                lic
        );

        Marshaller m = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new NamespaceFilterXMLStreamWriter(writer);

        m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
                new NamespacePrefixMapper() {

                    @Override
                    public String[] getPreDeclaredNamespaceUris() {
                        return new String[] {
                                "urn:com:atricore:josso2:licensing:1.0:license",
                                "http://www.w3.org/2000/09/xmldsig#",
                                "http://www.w3.org/2001/04/xmlenc#",
                                "http://www.w3.org/2001/XMLSchema"
                        };
                    }

                    @Override
                    public String getPreferredPrefix(String nsUri, String suggestion, boolean requirePrefix) {

                        if (nsUri.equals("urn:com:atricore:josso2:licensing:1.0:license"))
                            return "lic";
                        if (nsUri.equals("http://www.w3.org/2000/09/xmldsig#"))
                            return "ds";
                        else if (nsUri.equals("http://www.w3.org/2001/04/xmlenc#"))
                            return "enc";
                        else if (nsUri.equals("http://www.w3.org/2001/XMLSchema"))
                            return "xsd";


                        return suggestion;
                    }
                });

        // Support XMLDsig
        m.marshal(jaxbElement, xmlStreamWriter);

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