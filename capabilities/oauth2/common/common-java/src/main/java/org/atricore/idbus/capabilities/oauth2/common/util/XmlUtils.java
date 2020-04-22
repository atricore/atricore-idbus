package org.atricore.idbus.capabilities.oauth2.common.util;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Constants;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthRequestAbstractType;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthResponseAbstractType;
import org.atricore.idbus.kernel.main.databinding.JAXBUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.Holder;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class XmlUtils {

    /**
     * List of tokens that may be in an xpath expression.
     */
    private static String[] xpath = {
            "/", "..", "@", "*", "[", "]", "(", "(", "{", "}", "?", "$", "#", "|", "*", "=", "!=", "<", "<=", ">", ">=",
            "node", "ancestor", "descendant", "following", "attribute", "child", "namespace", "parent", "preceding", "self",
            "document-node", "text", "comment", "namespace-code", "processing-instruction",
    };;


    private static final Log logger = LogFactory.getLog(XmlUtils.class);

    private static final TreeSet<String> oauthContextPackages = new TreeSet<String>();

    private static final Holder<JAXBUtils.CONSTRUCTION_TYPE> constructionType = new Holder<JAXBUtils.CONSTRUCTION_TYPE>();

    private static final XMLInputFactory staxIF = XMLInputFactory.newInstance();
    private static final XMLOutputFactory staxOF = XMLOutputFactory.newInstance();

    static {
        oauthContextPackages.add(OAuth2Constants.OAUTH2_PROTOCOL_PKG);

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

    public static String marshalOAuth2Request(OAuthRequestAbstractType request, boolean encode) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalOAuth2Request(request, type, encode);

    }

    public static String marshalOAuth2Request(OAuthRequestAbstractType request, String requestType, boolean encode) throws Exception {

        String marshaledRequest = marshalOAuth2(
                request,
                OAuth2Constants.OAUTH2_PROTOCOL_NS,
                requestType
        );

        return encode ? new String(new Base64().encode(marshaledRequest.getBytes())) : marshaledRequest;
    }


    public static String marshalOAuth2Response(OAuthResponseAbstractType response, boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalOAuth2Response(response, type, encode);

    }

    public static String marshalOAuth2Response(OAuthResponseAbstractType response,
                                                   String responseType, boolean encode) throws Exception {

        String marshaledResponse = XmlUtils.marshalOAuth2(
                response,
                OAuth2Constants.OAUTH2_PROTOCOL_NS,
                responseType
        );

        return encode ? new String(new Base64().encode(marshaledResponse.getBytes())) : marshaledResponse;


    }


    public static String marshalOAuth2(Object msg,
                                       String msgQName,
                                       String msgLocalName) throws Exception {

        //JAXBContext jaxbContext = createJAXBContext(userPackages);
        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(oauthContextPackages, constructionType,
                oauthContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Marshaller m = JAXBUtils.getJAXBMarshaller(jaxbContext);

        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                msg.getClass(),
                msg
        );

        Writer writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new NamespaceFilterXMLStreamWriter(writer);

        // Support XMLDsig

        // TODO : What about non-sun XML Bind stacks!
        m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
                new NamespacePrefixMapper() {

                    @Override
                    public String[] getPreDeclaredNamespaceUris() {
                        return new String[] {
                                OAuth2Constants.OAUTH2_PROTOCOL_NS,
                                "http://www.w3.org/2000/09/xmldsig#",
                                "http://www.w3.org/2001/04/xmlenc#",
                                "http://www.w3.org/2001/XMLSchema"
                        };
                    }

                    @Override
                    public String getPreferredPrefix(String nsUri, String suggestion, boolean requirePrefix) {

                        if (nsUri.equals(OAuth2Constants.OAUTH2_PROTOCOL_NS))
                            return "oauth2p";
                        else if (nsUri.equals("http://www.w3.org/2000/09/xmldsig#"))
                            return "ds";
                        else if (nsUri.equals("http://www.w3.org/2001/04/xmlenc#"))
                            return "enc";
                        else if (nsUri.equals("http://www.w3.org/2001/XMLSchema"))
                            return "xsd";


                        return suggestion;
                    }
                });

        m.marshal(jaxbRequest, xmlStreamWriter);
        xmlStreamWriter.flush();
        JAXBUtils.releaseJAXBMarshaller(jaxbContext, m);

        return writer.toString();
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
