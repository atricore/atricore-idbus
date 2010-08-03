package org.atricore.idbus.capabilities.josso.main.util;

import org.apache.commons.codec.binary.Base64;
import org.atricore.idbus.capabilities.josso.main.JossoConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class XmlUtils {

    public static String marshall(Object content , boolean encode) throws Exception {
        String type = content.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshall(content, type, encode);
    }


   public static String marshall(Object content,
                                     String contentType, boolean encode) throws Exception {

        String marshalled;

        marshalled = XmlUtils.marshal(content,
                JossoConstants.JOSSO_PROTOCOL_NS,
                contentType,
                new String[]{ JossoConstants.JOSSO_PROTOCOL_PKG }
        );


        return encode ? new String( new Base64().encode( marshalled.getBytes() ) ) : marshalled;
    }

   public static JAXBElement createJAXBelement(Object content) {

        Class clazz = (Class) content.getClass();

        // Remove the 'Type' suffix from the xml type name and use it as XML element!
        XmlType t = (XmlType) clazz.getAnnotation(XmlType.class);

        String element = t.name().substring(0, t.name().length() - 4);

        return new JAXBElement(new QName(JossoConstants.JOSSO_PROTOCOL_NS, element), clazz, content);

   }

   public static JAXBContext createJOSSOJAXBContext() throws JAXBException {
        return createJAXBContext(new String[]{ JossoConstants.JOSSO_PROTOCOL_PKG});
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
        jaxbContext.createMarshaller().marshal( jaxbRequest, writer);

        return writer.toString();
    }

    public static Object unmarshal( String msg, String userPackages[] ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext( userPackages );
        // TODO : Verify !
        return jaxbContext.createUnmarshaller().unmarshal( new ByteArrayInputStream(msg.getBytes()));

    }

    public static JAXBContext createJAXBContext ( String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();
        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }
        // Use our classloader to build JAXBContext so it can find binding classes.
        return JAXBContext.newInstance( packages.toString(), XmlUtils.class.getClassLoader());
    }
}
