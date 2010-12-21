package com.atricore.liveservices.liveupdate._1_0.util;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class XmlUtils1 {


    public static UpdatesIndexType unmarshallUpdatesIndex(InputStream is, boolean decode) throws Exception {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

        int read = is.read(buf);
        while (read > 0) {
            baos.write(buf, 0, read);
            read = is.read(buf);
        }

        return unmarshallUpdatesIndex(new String(baos.toByteArray()), decode);
    }

    public static UpdatesIndexType unmarshallUpdatesIndex(String udIdxStr, boolean decode) throws Exception {
        if (decode)
            udIdxStr = new String(new Base64().decode(udIdxStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(udIdxStr, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (UpdatesIndexType) e.getValue();
    }

    public static UpdateDescriptorType unmarshallUpdateDescriptor(String udStr, boolean decode) throws Exception {
        if (decode)
            udStr = new String(new Base64().decode(udStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(udStr, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (UpdateDescriptorType) e.getValue();
    }


    public static String marshalUpdatesIndex(UpdatesIndexType udIdx, boolean encode) throws Exception {
        String type = udIdx.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalUpdatesIndex(udIdx, type, encode);

    }

    public static String marshalUpdatesIndex(UpdatesIndexType udIdx, String requestType, boolean encode) throws Exception {

        String marshalled ;
        // Support IDBus SAMLR2 Extentions when marshalling
        marshalled = marshal(
            udIdx,
            "urn:com:atricore:liveservices:liveupdate:1.0:md",
            requestType,
            new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" }
        );

        return encode ? new String(new Base64().encode( marshalled.getBytes())) : marshalled;
    }

    public static String marshalUpdateDescriptor(UpdateDescriptorType ud, boolean encode) throws Exception {
        String type = ud.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalUpdateDescriptor(ud, type, encode);

    }

    public static String marshalUpdateDescriptor(UpdateDescriptorType ud, String requestType, boolean encode) throws Exception {

        String marshalled ;
        // Support IDBus SAMLR2 Extentions when marshalling
        marshalled = marshal(
            ud,
            "urn:com:atricore:liveservices:liveupdate:1.0:md",
            requestType,
            new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" }
        );

        return encode ? new String(new Base64().encode( marshalled.getBytes())) : marshalled;
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

    public static JAXBContext createJAXBContext ( String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();
        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }
        // Use our classloader to build JAXBContext so it can find binding classes.
        return JAXBContext.newInstance( packages.toString(), XmlUtils1.class.getClassLoader());
    }

}
