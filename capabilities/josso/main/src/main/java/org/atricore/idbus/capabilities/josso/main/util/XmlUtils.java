package org.atricore.idbus.capabilities.josso.main.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoConstants;
import org.atricore.idbus.capabilities.sso.support.core.InvalidXMLException;
import org.atricore.idbus.capabilities.sso.support.core.util.StringSource;
import org.atricore.idbus.kernel.main.databinding.JAXBUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.Holder;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class XmlUtils {

    /**
     * List of tokens that may be in an xpath expression.
     */
    private static String[] xpath = {
            "/", "..", "@", "*", "[", "]", "(", "(", "{", "}", "?", "$", "#", "|", "*", "div", "=", "!=", "<", "<=", ">", ">=", "or", "and",
            "mod", "node", "ancestor", "ancestor-or-self", "descendant", "descendant-or-self", "following",
            "following-sibling", "attribute", "child", "namespace", "parent", "preceding", "preceding-sibling", "self", "node",
            "document-node", "text", "comment", "namespace-code", "processing-instruction", "attribute", "schema-attribute"
    };


    private static final Log logger = LogFactory.getLog(XmlUtils.class);

    private static final TreeSet<String> ssoContextPackages = new TreeSet<String>();

    private static final Holder<JAXBUtils.CONSTRUCTION_TYPE> constructionType = new Holder<JAXBUtils.CONSTRUCTION_TYPE>();

    private static final XMLInputFactory staxIF = XMLInputFactory.newInstance();
    private static final XMLOutputFactory staxOF = XMLOutputFactory.newInstance();

    static {
        ssoContextPackages.add(JossoConstants.JOSSO_PROTOCOL_PKG);

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        javax.xml.parsers.SAXParserFactory saxf =
                SAXParserFactory.newInstance();

        String FEATURE = null;

        try {
            // -----------------------------------------------------------------------------
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all
            // XML entity attacks are prevented
            // -----------------------------------------------------------------------------
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);

            // -----------------------------------------------------------------------------
            // If you can't completely disable DTDs, then at least do the following:
            // -----------------------------------------------------------------------------
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);

            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);

            // -----------------------------------------------------------------------------
            // Disable external DTDs as well
            // -----------------------------------------------------------------------------
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);

            // -----------------------------------------------------------------------------
            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            // -----------------------------------------------------------------------------
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            logger.debug("DocumentBuilder = " + dbf.newDocumentBuilder());
            logger.debug("SAXParser = " + saxf.newSAXParser());
            logger.debug("XMLEventReader = " + staxIF.createXMLEventReader(new StringSource("<a>Hello</a>")));
            logger.debug("XMLEventWriter = " + staxOF.createXMLEventWriter(new ByteArrayOutputStream()));
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
        } catch (SAXException e) {
            logger.error(e.getMessage(), e);
        } catch (XMLStreamException e) {
            logger.error(e.getMessage(), e);
        }

    }

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

    // JAXB Generic

    public static String marshal ( Object msg, String msgQName, String msgLocalName, String[] userPackages ) throws Exception {

        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Marshaller marshaller = JAXBUtils.getJAXBMarshaller(jaxbContext);

        JAXBElement jaxbRequest = new JAXBElement( new QName( msgQName, msgLocalName ),
                msg.getClass(),
                msg
        );
        Writer writer = new StringWriter();

        // Support XMLDsig
        XMLEventWriter xmlWriter = staxOF.createXMLEventWriter(writer);
        marshaller.marshal( jaxbRequest, xmlWriter);
        xmlWriter.flush();
        JAXBUtils.releaseJAXBMarshaller(jaxbContext, marshaller);

        return writer.toString();
    }

    public static Object unmarshal(String msg, String userPackages[]) throws Exception {
        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(staxIF.createXMLStreamReader(new StringSource(msg)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        if (o instanceof JAXBElement)
            return ((JAXBElement) o).getValue();

        return o;

    }

    /**
     * Verifh that IDs do not have an XPath expression that the digital signature tool may try to resolve.
     *
     * @param ID
     * @throws Exception
     */
    public static void verifyID(String ID) throws Exception {

        for (String s : xpath) {
            if (ID.contains(s))
                throw new InvalidXMLException("Invalid ID " + ID + " [" + s + "]");

        }

    }
}
