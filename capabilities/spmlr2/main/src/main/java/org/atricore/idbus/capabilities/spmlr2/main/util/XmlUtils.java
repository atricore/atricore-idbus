package org.atricore.idbus.capabilities.spmlr2.main.util;

import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.ResponseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: XmlUtils.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class XmlUtils {

    private static final Log logger = LogFactory.getLog(XmlUtils.class);

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
                requestType,
                new String[]{ SPMLR2Constants.SPML_PKG }
            );

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
                responseType,
                new String[]{ SPMLR2Constants.SPML_PKG }
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

    public static JAXBContext createSpmlR2JAXBContext(RequestType request) throws JAXBException {
        return createJAXBContext(new String[] {request.getClass().getPackage().getName()});
        /*
        return createJAXBContext(new String[]{ SPMLR2Constants.SPML_WSDL_PKG,
                SPMLR2Constants.SPML_IDBUS_PKG,
                SPMLR2Constants.SPML_ASSERTION_PKG,
                SPMLR2Constants.SPML_METADATA_PKG});
                */
    }

    public static JAXBContext createSpmlR2JAXBContext() throws JAXBException {
        return createJAXBContext(new String[]{
                SPMLR2Constants.SPML_PKG,
                SPMLR2Constants.SPML_ASYNC_PKG,
                SPMLR2Constants.SPML_ATRICORE_PKG,
                SPMLR2Constants.SPML_BATCH_PKG,
                SPMLR2Constants.SPML_BULK_PKG,
                SPMLR2Constants.SPML_DSML_CORE_PKG,
                SPMLR2Constants.SPML_DSML_CORE_PKG,
                SPMLR2Constants.SPML_DSML_PKG,
                SPMLR2Constants.SPML_PASSWORD_PKG,
                SPMLR2Constants.SPML_REFERENCE_PKG,
                SPMLR2Constants.SPML_SEARCH_PKG,
                SPMLR2Constants.SPML_SUSPEND_PKG,
                SPMLR2Constants.SPML_UPDATES_PKG});
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

    public static Object unmarshal(Node domMsg) throws Exception {
        JAXBContext jaxbContext = createSpmlR2JAXBContext();
        return jaxbContext.createUnmarshaller().unmarshal( domMsg );
    }
    public static Object unmarshal( String msg) throws Exception {
        JAXBContext jaxbContext = createSpmlR2JAXBContext();
        return jaxbContext.createUnmarshaller().unmarshal( new StringSource( msg ) );
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
        return JAXBContext.newInstance( packages.toString(), XmlUtils.class.getClassLoader());
    }


}
