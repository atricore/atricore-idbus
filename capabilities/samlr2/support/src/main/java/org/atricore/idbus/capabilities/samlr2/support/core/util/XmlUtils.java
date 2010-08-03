/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.support.core.util;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR11Constants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.SSOConstants;
import org.atricore.idbus.common.sso._1_0.protocol.SSORequestAbstractType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: XmlUtils.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class XmlUtils {

    private static final Log logger = LogFactory.getLog(XmlUtils.class);

    //  -----------------------------------------------------------
    // JAXBUtils
    //  -----------------------------------------------------------

    // SAML 2.0 Request

    public static String marshallSamlR2Request(RequestAbstractType request, boolean encode) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSamlR2Request(request, type, encode);
    }

    public static String marshallSamlR2Request(RequestAbstractType request, String requestType, boolean encode) throws Exception {

        String marshalledRequest ;
        // Support IDBus SAMLR2 Extentions when marshalling
        if (request.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG)) {

            marshalledRequest = marshal(
                request,
                SAMLR2Constants.SAML_IDBUS_NS,
                requestType,
                new String[]{ SAMLR2Constants.SAML_IDBUS_PKG }
            );

        } else {

            marshalledRequest = marshal(
                request,
                SAMLR2Constants.SAML_PROTOCOL_NS,
                requestType,
                new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG }
            );
        }

        return encode ? new String(new Base64().encode( marshalledRequest.getBytes())) : marshalledRequest;
    }

    public static RequestAbstractType unmarshallSamlR2Request(String request, boolean decode) throws Exception {
        if (decode)
            request = decode(request);

        JAXBElement e = (JAXBElement) unmarshal(request, new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG });
        return (RequestAbstractType) e.getValue();
    }


    /**
     * This unmarshalls a Base64 SAML Request
     */
    public static RequestAbstractType unmarshallSamlR2Request(String base64Request) throws Exception {
        return unmarshallSamlR2Request(base64Request, true);
    }


    // SAML 2.0 Response

    public static String marshallSamlR2Response(StatusResponseType response,
                                     boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSamlR2Response(response, type, encode);
    }

    public static String marshallSamlR2Response(StatusResponseType response,
                                     String responseType, boolean encode) throws Exception {

        String marshalledResponse ;
        // Support IDBus SAMLR2 Extentions when marshalling
        if (response.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG)) {

                marshalledResponse = XmlUtils.marshal(
                    response,
                    SAMLR2Constants.SAML_IDBUS_NS,
                    responseType,
                    new String[]{ SAMLR2Constants.SAML_IDBUS_PKG }
            );
        } else {
            marshalledResponse = XmlUtils.marshal(
                response,
                SAMLR2Constants.SAML_PROTOCOL_NS,
                responseType,
                new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG }
        );

        }

        return encode ? new String( new Base64().encode( marshalledResponse.getBytes() ) ) : marshalledResponse;
    }

    /**
     * This unmarshalls a Base64 SAML Response
     */
    public static StatusResponseType unmarshallSamlR2Response(String response, boolean decode) throws Exception {
        if (decode)
            response = decode(response);
        JAXBElement e = (JAXBElement) unmarshal(response,
                new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG, SAMLR2Constants.SAML_IDBUS_PKG});
        return (StatusResponseType) e.getValue();

    }

    public static StatusResponseType unmarshallSamlR2Response(String base64Response) throws Exception {
        return unmarshallSamlR2Response(base64Response, true);

    }

    // SAML EX Request

    public static String marshallSSORequest(SSORequestAbstractType request, boolean encode) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSSORequest(request, type, encode);
    }

    public static String marshallSSORequest(SSORequestAbstractType request, String requestType, boolean encode) throws Exception {

        String marshalledRequest = marshal(
                request,
                SSOConstants.SSO_PROTOCOL_NS,
                requestType,
                new String[]{ SSOConstants.SSO_PROTOCOL_PKG}
        );

        return encode ? new String(new Base64().encode( marshalledRequest.getBytes())) : marshalledRequest;
    }

    public static SSORequestAbstractType unmarshallSSORequest(String request, boolean decode) throws Exception {
        if (decode)
            request = decode(request);

        JAXBElement e = (JAXBElement) unmarshal(request, new String[]{ SSOConstants.SSO_PROTOCOL_PKG});
        return (SSORequestAbstractType) e.getValue();
    }


    /**
     * This unmarshalls a Base64 SAML Request
     */
    public static SSORequestAbstractType unmarshallSSORequest(String base64Request) throws Exception {
        return unmarshallSSORequest(base64Request, true);
    }


    // SAM EX Response

    public static String marshallSSOResponse(SSOResponseType response,
                                     boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSSOResponse(response, type, encode);
    }

    public static String marshallSSOResponse(SSOResponseType response,
                                     String responseType, boolean encode) throws Exception {
        String marshalledResponse = XmlUtils.marshal(
                response,
                SSOConstants.SSO_PROTOCOL_NS,
                responseType,
                new String[]{ SSOConstants.SSO_PROTOCOL_PKG}
        );

        return encode ? new String( new Base64().encode( marshalledResponse.getBytes() ) ) : marshalledResponse;
    }

    /**
     * This unmarshalls a Base64 SAML Response
     */
    public static SSOResponseType unmarshallSSOResponse(String response, boolean decode) throws Exception {
        if (decode)
            response = decode(response);
        JAXBElement e = (JAXBElement) unmarshal(response, new String[]{ SSOConstants.SSO_PROTOCOL_PKG});
        return (SSOResponseType) e.getValue();

    }

    public static StatusResponseType unmarshallSSOResponse(String base64Response) throws Exception {
        return unmarshallSamlR2Response(base64Response, true);

    }

    // SAML 1.1 Response
    public static String marshallSamlR11Response(oasis.names.tc.saml._1_0.protocol.ResponseType response,
                                     boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshallSamlR11Response(response, type, encode);
    }

    public static String marshallSamlR11Response(oasis.names.tc.saml._1_0.protocol.ResponseType response,
                                     String responseType, boolean encode) throws Exception {

        String marshalledResponse ;
        marshalledResponse = XmlUtils.marshal(
                response,
                SAMLR11Constants.SAML_PROTOCOL_NS,
                responseType,
                new String[]{ SAMLR11Constants.SAML_PROTOCOL_PKG }
        );


        return encode ? new String( new Base64().encode( marshalledResponse.getBytes() ) ) : marshalledResponse;
    }

    public static String decode(String content) {
        return new String(new Base64().decode(content.getBytes()));
    }


    // SAML

    public static JAXBElement<RequestAbstractType> createJAXBelement(RequestAbstractType request) {

        Class<RequestAbstractType> clazz = (Class<RequestAbstractType>) request.getClass();

        // Remove the 'Type' suffix from the xml type name and use it as XML element!
        XmlType t = clazz.getAnnotation(XmlType.class);
        String element = t.name().substring(0, t.name().length() - 4);

        if (request.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG))
            return new JAXBElement<RequestAbstractType>(new QName(SAMLR2Constants.SAML_IDBUS_NS, element), clazz, request);
        else
            return new JAXBElement<RequestAbstractType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, element), clazz, request);
    }

    public static JAXBContext createSamlR2JAXBContext(RequestAbstractType request) throws JAXBException {
        return createJAXBContext(new String[] {request.getClass().getPackage().getName()});
        /*
        return createJAXBContext(new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG,
                SAMLR2Constants.SAML_ASSERTION_PKG,
                SAMLR2Constants.SAML_METADATA_PKG});
                */
    }

    public static JAXBContext createSamlR2JAXBContext() throws JAXBException {
        return createJAXBContext(new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG,
                SAMLR2Constants.SAML_ASSERTION_PKG,
                SAMLR2Constants.SAML_METADATA_PKG});
    }


    public static JAXBContext createSSOJAXBContext() throws JAXBException {
        return createJAXBContext(new String[]{ SSOConstants.SSO_PROTOCOL_PKG});
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
        return JAXBContext.newInstance( packages.toString(), XmlUtils.class.getClassLoader());
    }


}
