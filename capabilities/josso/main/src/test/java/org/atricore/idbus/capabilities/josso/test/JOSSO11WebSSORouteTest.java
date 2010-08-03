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

package org.atricore.idbus.capabilities.josso.test;

import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxp.StringSource;
import org.apache.camel.impl.JndiRegistry;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.josso.DateUtils;
import org.atricore.idbus.capabilities.josso.UUIDGenerator;
import org.junit.After;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: JOSSO11WebSSORouteTest.java 1153 2009-04-10 05:21:39Z gbrigand $
 */
public class JOSSO11WebSSORouteTest extends ContextTestSupport {
    private static Log log = LogFactory.getLog(JOSSO11WebSSORouteTest.class);

    private static final String BASE64_SAMLR2_AUTHNREQUEST =
            "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI%2FPjxuczM6QXV0aG5SZXF1ZXN0" +
            "IFByb3RvY29sQmluZGluZz0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmJpbmRpbmdzOkhUVFAtUmVkaXJlY3QiIElzUG" +
            "Fzc2l2ZT0iZmFsc2UiIEZvcmNlQXV0aG49ImZhbHNlIiBJc3N1ZUluc3RhbnQ9IjIwMDktMDQtMDJUMTg6NDU6MDYuMDAwWiIg" +
            "VmVyc2lvbj0iMi4wIiBJRD0iOTNGMjY5QjgwQzgyMDVCNSIgeG1sbnM6bnM0PSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3" +
            "htbGVuYyMiIHhtbG5zOm5zMz0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIiB4bWxuczpuczI9Imh0dHA6" +
            "Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiIHhtbG5zPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW" +
            "9uIj48SXNzdWVyLz48L25zMzpBdXRoblJlcXVlc3Q%2B";

    protected ClassPathXmlApplicationContext applicationContext;

    protected void setUp() throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(
                "/org/atricore/idbus/capabilities/josso/test/josso-test-component.xml"
        );

        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testJOSSO11AuthNRequestToSAMLR2() throws Exception {
        String location = null;
        URL spUrl = new URL("http://localhost:8181/JOSSO11/BIND/CH1?cmd=login");
        HttpURLConnection urlConn = (HttpURLConnection) spUrl.openConnection();
        urlConn.setInstanceFollowRedirects(false);
        urlConn.connect();

        String headerName=null;
        String jossoSession = null;
        for (int i=1; (headerName = urlConn.getHeaderFieldKey(i))!=null; i++) {
            if (headerName.equals("Location")) {
                location = urlConn.getHeaderField(i);
            }
        }

        assert location != null;

     }


    public void testSAMLR2AuthnToJOSSO11() throws Exception {
        String location = null;
        URL spUrl = new URL("http://localhost:8181/JOSSO11/BIND/CH2?SAMLRequest=" + BASE64_SAMLR2_AUTHNREQUEST);
        HttpURLConnection urlConn = (HttpURLConnection) spUrl.openConnection();
        urlConn.setInstanceFollowRedirects(false);
        urlConn.connect();

        String headerName=null;
        String jossoSession = null;
        for (int i=1; (headerName = urlConn.getHeaderFieldKey(i))!=null; i++) {
            if (headerName.equals("Location")) {
                location = urlConn.getHeaderField(i);
            }
        }

        assert location != null;

     }

    public void testSAMLR2ResponseToJOSSO11() throws Exception {
        String location = null;

        ResponseType r = buildAuthnResponse(null, createAssertion());

        String marshalledResponse;

        marshalledResponse = marshal(
                oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType.class,
                r,
                "urn:oasis:names:tc:SAML:2.0:protocol",
                "Response",
                new String[]{}
        );

        String base64AuthnResponse;
        base64AuthnResponse = new String(new Base64().encode(marshalledResponse.getBytes()));

        String postResponse =
            executePost("http://localhost:8181/JOSSO11/BIND/ACS",
                    "SAMLResponse=" + URLEncoder.encode(base64AuthnResponse)
            );

     }

    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("applicationContext", applicationContext);
        return jndi;
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("jetty:http://localhost:8181/JOSSO11/BIND/CH1")
                    .to("josso-binding:JOSSO11AuthnRequestToSAMLR2?channelRef=binding_channel_1");

                from("jetty:http://localhost:8181/JOSSO11/BIND/CH2")
                    .to("josso-binding:RequestAuthnToJOSSO11IDP?channelRef=binding_channel_2");

                from("jetty:http://localhost:8181/JOSSO11/BIND/ACS")
                    .to("josso-binding:SAMLR2ResponseToJOSSO11?channelRef=binding_channel_2" +
                        "&josso11ACSUrl=http://localhost:8282/josso_security_check/");

            }
        };
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        if (applicationContext != null) {
            applicationContext.close();
        }
    }


    private String executePost(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected ResponseType buildAuthnResponse(AuthnRequestType authnRequest, AssertionType assertion) {


        ResponseType authnResponse = new ResponseType();
        UUIDGenerator uuidGenerator = new UUIDGenerator();
        ///TODO: String acs = authnRequest.getAssertionConsumerServiceURL();

        Date dateNow = new java.util.Date();
        Date dateOneWeekAhead = new java.util.Date(dateNow.getTime() + (1000 * 60 * 60 * 24 * 7));
        authnResponse.setID(uuidGenerator.generateId());
        authnResponse.setVersion("2.0");
        authnResponse.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));
        authnResponse.getAssertionOrEncryptedAssertion().add(assertion);

        return authnResponse;
    }

    public AssertionType createAssertion() {

        oasis.names.tc.saml._2_0.assertion.ObjectFactory samlObjectFactory;
        samlObjectFactory = new oasis.names.tc.saml._2_0.assertion.ObjectFactory();

        AssertionType assertion = samlObjectFactory.createAssertionType();

        UUIDGenerator uuidGenerator = new UUIDGenerator();

        // Prepare time stuff
        Date dateNow = new java.util.Date();
        Date dateOneWeekAhead = new java.util.Date(dateNow.getTime() + (1000 * 60 * 60 * 24 * 7));

        assertion.setID(uuidGenerator.generateId());
        assertion.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));
        assertion.setVersion("2.0");

        NameIDType issuer = new NameIDType();
        issuer.setValue("idp_atricore");
        assertion.setIssuer(issuer);

        SubjectType subject = new SubjectType();
        NameIDType subjectNameID = new NameIDType();
        subjectNameID.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");  // TODO: VP
        subjectNameID.setNameQualifier("idp_atricore");    // TODO: VP
        subjectNameID.setSPNameQualifier("sp_atricore");   // TODO: VP
        subjectNameID.setValue("user1");
        subject.getContent().add(samlObjectFactory.createNameID(subjectNameID));
        assertion.setSubject(subject);

        SubjectConfirmationType subjectConfirmation = new SubjectConfirmationType();
        subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer"); // TODO: VP
        SubjectConfirmationDataType subjectConfirmationData = new SubjectConfirmationDataType();
        subjectConfirmationData.setNotBefore(DateUtils.toXMLGregorianCalendar(dateNow));
        subjectConfirmationData.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(dateOneWeekAhead));
        subjectConfirmationData.setRecipient("sp_atricore"); // TODO: VP

        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        subject.getContent().add(samlObjectFactory.createSubjectConfirmation(subjectConfirmation));

        ConditionsType conditions = new ConditionsType();
        conditions.setNotBefore(DateUtils.toXMLGregorianCalendar(dateNow));
        conditions.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(dateOneWeekAhead));
        assertion.setConditions(conditions);

        AudienceRestrictionType audienceRestriction = new AudienceRestrictionType();
        audienceRestriction.getAudience().add("sp_test"); // TODO: VP
        audienceRestriction.getAudience().add("sp_atricore"); // TODO: VP
        conditions.getConditionOrAudienceRestrictionOrOneTimeUse().add(audienceRestriction);

        AuthnStatementType authnStatement;
        authnStatement = new AuthnStatementType();
        authnStatement.setAuthnInstant(DateUtils.toXMLGregorianCalendar(dateNow));
        authnStatement.setSessionIndex(uuidGenerator.generateId());
        authnStatement.setSessionNotOnOrAfter(DateUtils.toXMLGregorianCalendar(dateOneWeekAhead));
        AuthnContextType authnContext = new AuthnContextType();
        JAXBElement<String> passwordProtectedAuthnContext = samlObjectFactory.createAuthnContextClassRef(
                "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"
        ); // TODO: VP
        authnContext.getContent().add(passwordProtectedAuthnContext);
        authnStatement.setAuthnContext(authnContext);

        assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(authnStatement);

        // Compose attribute statements with roles

        AttributeStatementType groupMembershipStatement;
        groupMembershipStatement = new AttributeStatementType();
        AttributeType groupsAttr = new AttributeType();
        groupsAttr.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
        groupsAttr.setName("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups");
        groupsAttr.getAttributeValue().add("GROUP_A");
        groupMembershipStatement.getAttributeOrEncryptedAttribute().add(groupsAttr);
        assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(groupMembershipStatement);

        // Compose attribute statements with permissions
        AttributeStatementType privilegeStatement;
        privilegeStatement = new AttributeStatementType();
        AttributeType privAttr = new AttributeType();
        privAttr.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
        privAttr.setName("urn:att:names:csp:privileges");
        privAttr.getAttributeValue().add("PRIV_1");
        privilegeStatement.getAttributeOrEncryptedAttribute().add(privAttr);
        assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(privilegeStatement);

        return assertion;
    }

    protected String marshal(Class endpointInterface, Object msg, String msgQName, String msgLocalName, String[] userPackages) throws Exception {

        WebService ws = getWebServiceAnnotation(endpointInterface);
        JAXBContext jaxbContext = createJAXBContext(endpointInterface, userPackages);

        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                msg.getClass(),
                msg
        );
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(jaxbRequest, writer);

        return writer.toString();
    }

    protected String marshal(Object msg, String msgQName, String msgLocalName, String[] userPackages) throws Exception {

        JAXBContext jaxbContext = createJAXBContext(userPackages);

        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                msg.getClass(),
                msg
        );
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(jaxbRequest, writer);

        return writer.toString();
    }

    protected Object unmarshal(String msg, Class endpointInterface, String userPackages[]) throws Exception {

        JAXBContext jaxbContext = createJAXBContext(endpointInterface, userPackages);

        return jaxbContext.createUnmarshaller().unmarshal(new StringSource(msg));
    }


    protected JAXBContext createJAXBContext(Class interfaceClass, String[] userPackages) throws JAXBException {
        List<Class> classes = new ArrayList<Class>();
        //List<String> packages = new ArrayList<String>();
        StringBuilder packages = new StringBuilder();

        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];

            packages.append(userPackage + ":");
        }

        //classes.add(JbiFault.class);
        for (Method mth : interfaceClass.getMethods()) {
            WebMethod wm = (WebMethod) mth.getAnnotation(WebMethod.class);
            if (wm != null) {
                classes.add(mth.getReturnType());
                classes.addAll(Arrays.asList(mth.getParameterTypes()));
                packages.append(getPackages(mth.getParameterTypes()));
                packages.append(":");
            }
        }

        return JAXBContext.newInstance(packages.toString());
    }

    protected JAXBContext createJAXBContext(String[] userPackages) throws JAXBException {
        List<Class> classes = new ArrayList<Class>();
        //List<String> packages = new ArrayList<String>();
        StringBuilder packages = new StringBuilder();

        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];

            packages.append(userPackage + ":");
        }

        return JAXBContext.newInstance(packages.toString());
    }


    @SuppressWarnings("unchecked")
    protected WebService getWebServiceAnnotation(Class clazz) {
        for (Class cl = clazz; cl != null; cl = cl.getSuperclass()) {
            WebService ws = (WebService) cl.getAnnotation(WebService.class);
            if (ws != null) {
                return ws;
            }
        }
        return null;
    }


    private static String getPackages(Class[] classes) {
        StringBuilder pkgs = new StringBuilder();
        boolean first = true;

        for (int i = 0; i < classes.length; i++) {
            Class aClass = classes[i];

            if (!first) pkgs.append(":");
            else first = false;

            pkgs.append(getPackage(aClass.getName()));

        }

        return pkgs.toString();
    }

    private static String getPackage(String pckg) {
        int i = pckg.lastIndexOf(".");
        if (i != -1)
            pckg = pckg.substring(0, i);
        return pckg;
    }


}
