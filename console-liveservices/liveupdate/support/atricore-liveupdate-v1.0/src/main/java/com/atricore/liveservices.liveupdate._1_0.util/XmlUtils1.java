package com.atricore.liveservices.liveupdate._1_0.util;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class XmlUtils1 {


    public static UpdatesIndexType unmarshallUpdatesIndex(InputStream is, boolean decode) throws Exception {
        return unmarshallUpdatesIndex(new String(getByteArray(is)), decode);
    }

    public static UpdatesIndexType unmarshallUpdatesIndex(String udIdxStr, boolean decode) throws Exception {
        if (decode)
            udIdxStr = new String(new Base64().decode(udIdxStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(udIdxStr, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (UpdatesIndexType) e.getValue();
    }

    public static UpdatesIndexType unmarshallUpdatesIndex(Document udIdx) throws Exception {
        JAXBElement e = (JAXBElement) unmarshal(udIdx, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (UpdatesIndexType) e.getValue();
    }

    public static UpdateDescriptorType unmarshallUpdateDescriptor(String udStr, boolean decode) throws Exception {
        if (decode)
            udStr = new String(new Base64().decode(udStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(udStr, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (UpdateDescriptorType) e.getValue();
    }

    public static ArtifactDescriptorType unmarshallArtifactDescriptor(InputStream is, boolean decode) throws Exception {
        return unmarshallArtifactDescriptor(new String(getByteArray(is)), decode);
    }

    public static ArtifactDescriptorType unmarshallArtifactDescriptor(String adStr, boolean decode) throws Exception {
        if (decode)
            adStr = new String(new Base64().decode(adStr.getBytes()));

        JAXBElement e = (JAXBElement) unmarshal(adStr, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (ArtifactDescriptorType) e.getValue();
    }

    public static ArtifactDescriptorType unmarshallArtifactDescriptor(Document ad) throws Exception {
        JAXBElement e = (JAXBElement) unmarshal(ad, new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" });
        return (ArtifactDescriptorType) e.getValue();
    }

    public static String marshalUpdatesIndex(UpdatesIndexType udIdx, boolean encode) throws Exception {
        String type = udIdx.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = Character.toLowerCase(type.charAt(0)) + type.substring(1, type.length() - 4);

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

    public static Document marshalUpdatesIndexToDOM(UpdatesIndexType udIdx) throws Exception {
        String marshalled = marshalUpdatesIndex(udIdx, false);

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(marshalled.getBytes()));
    }

    public static String marshalUpdateDescriptor(UpdateDescriptorType ud, boolean encode) throws Exception {
        String type = ud.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = Character.toLowerCase(type.charAt(0)) + type.substring(1, type.length() - 4);

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

    public static String marshalArtifactDescriptor(ArtifactDescriptorType ad, boolean encode) throws Exception {
        String type = ad.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = Character.toLowerCase(type.charAt(0)) + type.substring(1, type.length() - 4);

        return marshalArtifactDescriptor(ad, type, encode);

    }

    public static String marshalArtifactDescriptor(ArtifactDescriptorType ad, String requestType, boolean encode) throws Exception {

        String marshalled ;
        // Support IDBus SAMLR2 Extentions when marshalling
        marshalled = marshal(
            ad,
            "urn:com:atricore:liveservices:liveupdate:1.0:md",
            requestType,
            new String[]{ "com.atricore.liveservices.liveupdate._1_0.md" }
        );

        return encode ? new String(new Base64().encode( marshalled.getBytes())) : marshalled;
    }

    public static Document marshalArtifactDescriptorToDOM(ArtifactDescriptorType ad) throws Exception {
        String marshalled = marshalArtifactDescriptor(ad, false);

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
        return JAXBContext.newInstance( packages.toString(), XmlUtils1.class.getClassLoader());
    }

    private static byte[] getByteArray(InputStream is) throws Exception {
        return IOUtils.toByteArray(is);
    }
}
