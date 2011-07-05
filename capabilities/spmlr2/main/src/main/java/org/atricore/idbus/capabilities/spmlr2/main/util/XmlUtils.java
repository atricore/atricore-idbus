package org.atricore.idbus.capabilities.spmlr2.main.util;

import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.ResponseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.databinding.JAXBUtils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlType;
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
/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: XmlUtils.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class XmlUtils {

    private static final Log logger = LogFactory.getLog(XmlUtils.class);

    private static final TreeSet<String> spmlContextPackages = new TreeSet<String>();

    private static final Holder<JAXBUtils.CONSTRUCTION_TYPE> constructionType = new Holder<JAXBUtils.CONSTRUCTION_TYPE>();

    private static final XMLInputFactory staxIF = XMLInputFactory.newInstance();
    private static final XMLOutputFactory staxOF = XMLOutputFactory.newInstance();

    static {
        spmlContextPackages.add(SPMLR2Constants.SPML_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_ASYNC_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_ATRICORE_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_BATCH_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_BULK_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_DSML_CORE_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_DSML_CORE_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_DSML_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_PASSWORD_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_REFERENCE_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_SEARCH_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_SUSPEND_PKG);
        spmlContextPackages.add(SPMLR2Constants.SPML_UPDATES_PKG);

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        javax.xml.parsers.SAXParserFactory saxf =
                SAXParserFactory.newInstance();


        try {
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


    //  -----------------------------------------------------------
    // JAXBUtils
    //  -----------------------------------------------------------

    // SPML 2.0 Request

    public static String marshallSpmlR2Request(RequestType request, boolean encode) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSpmlR2Request(request, type, encode);
    }

    public static String marshallSpmlR2Request(RequestType request, String requestType, boolean encode) throws Exception {

        String marshalledRequest = marshal(
                request,
                SPMLR2Constants.SPML_NS,
                requestType );

        return encode ? new String(new Base64().encode( marshalledRequest.getBytes())) : marshalledRequest;
    }

    public static RequestType unmarshallSpmlR2Request(String request, boolean decode) throws Exception {
        if (decode)
            request = decode(request);

        JAXBElement e = (JAXBElement) unmarshal(request, new String[]{ SPMLR2Constants.SPML_PKG });
        return (RequestType) e.getValue();
    }


    /**
     * This unmarshalls a Base64 SPML Request
     */
    public static RequestType unmarshallSpmlR2Request(String base64Request) throws Exception {
        return unmarshallSpmlR2Request(base64Request, true);
    }


    // SPML 2.0 Response

    public static String marshallSpmlR2Response(ResponseType response,
                                     boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSpmlR2Response(response, type, encode);
    }

    public static String marshallSpmlR2Response(ResponseType response,
                                     String responseType, boolean encode) throws Exception {

        String marshalledResponse = marshalledResponse = XmlUtils.marshal(
                response,
                SPMLR2Constants.SPML_NS,
                responseType
        );

        return encode ? new String( new Base64().encode( marshalledResponse.getBytes() ) ) : marshalledResponse;
    }

    /**
     * This unmarshalls a Base64 SPML Response
     */
    public static ResponseType unmarshallSpmlR2Response(String response, boolean decode) throws Exception {
        if (decode)
            response = decode(response);
        JAXBElement e = (JAXBElement) unmarshal(response,
                new String[]{ SPMLR2Constants.SPML_PKG});
        return (ResponseType) e.getValue();

    }

    public static ResponseType unmarshallSpmlR2Response(String base64Response) throws Exception {
        return unmarshallSpmlR2Response(base64Response, true);

    }

    public static String decode(String content) {
        return new String(new Base64().decode(content.getBytes()));
    }

    // SPML

    public static JAXBElement<RequestType> createJAXBelement(RequestType request) {

        Class<RequestType> clazz = (Class<RequestType>) request.getClass();

        // Remove the 'Type' suffix from the xml type name and use it as XML element!
        XmlType t = clazz.getAnnotation(XmlType.class);
        String element = t.name().substring(0, t.name().length() - 4);

        return new JAXBElement<RequestType>(new QName(SPMLR2Constants.SPML_WSDL_NS, element), clazz, request);
    }


    // JAXB Generic

    public static String marshal ( Object msg, String msgQName, String msgLocalName) throws Exception {

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(spmlContextPackages, constructionType,
                spmlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());

        Marshaller m = JAXBUtils.getJAXBMarshaller(jaxbContext);

        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                msg.getClass(),
                msg
        );

        Writer writer = new StringWriter();

        // Support XMLDsig
        m.marshal(jaxbRequest, writer);
        writer.flush();
        JAXBUtils.releaseJAXBMarshaller(jaxbContext, m);

        return writer.toString();
    }

    public static Object unmarshal(Node domMsg, String userPackages[]) throws Exception {
        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(domMsg);
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        if (o instanceof JAXBElement)
            return ((JAXBElement) o).getValue();

        return o;
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
        Object o = unmarshaller.unmarshal(staxIF.createXMLEventReader(new StringSource(msg)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        if (o instanceof JAXBElement)
            return ((JAXBElement) o).getValue();

        return o;

    }


}
