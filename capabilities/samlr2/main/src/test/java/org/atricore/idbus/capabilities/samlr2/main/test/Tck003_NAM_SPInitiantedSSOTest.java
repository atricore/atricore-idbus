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

package org.atricore.idbus.capabilities.samlr2.main.test;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.Consent;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.w3._1999.xhtml.Div;
import org.w3._1999.xhtml.Form;
import org.w3._1999.xhtml.Html;
import org.w3._1999.xhtml.Input;

import java.util.Date;

/**
 *
 * TODO : Move to TCKs project, NAM section
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: Tck003_NAM_SPInitiantedSSOTest.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class Tck003_NAM_SPInitiantedSSOTest extends SamlR2TestSupport {

    private static final Log logger = LogFactory.getLog(Tck003_NAM_SPInitiantedSSOTest.class);

    private ApplicationContext applicationContext;

    private HttpClient client;

    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/josso2/samlr2/test/tck003/tck003-samlr2-nam-spinitiated-sso-test.xml"}
        );

        client = new HttpClient();
    }

    @After
    public void tearDown() {

        String stTimeout = System.getProperty("org.atricore.josso2.test.wait", "-1");
        long timeout = Long.parseLong(stTimeout);
        if (timeout >= 0 ) {
            synchronized (this) {
                logger.info("Waiting " + (timeout > 0 ? timeout + " millis"  : "forever"));
                try { wait(timeout); } catch (InterruptedException e) { /**/}
            }
        }

        if (applicationContext != null)
            ((ClassPathXmlApplicationContext)applicationContext).close();

    }

    public void sendAuthnReq() throws Exception {

        // String base64Req = "PD94bWwgdmVyc2lvbj0nMS4wJyBlbmNvZGluZz0nVVRGLTgnPz48bnMzOkF1dGhuUmVxdWVzdCB4bWxuczpuczQ9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jIyIgeG1sbnM6bnMzPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiIHhtbG5zOm5zMj0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyIgeG1sbnM9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iIEFzc2VydGlvbkNvbnN1bWVyU2VydmljZVVSTD0iaHR0cDovL2pvc3NvMDEuZGV2LmF0cmljb3JlLmNvbTo4MTgxL0pPU1NPMi9TUC0xL1NBTUwyL0FDUy9QT1NUIiBQcm90b2NvbEJpbmRpbmc9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpiaW5kaW5nczpIVFRQLVBPU1QiIElzUGFzc2l2ZT0iZmFsc2UiIEZvcmNlQXV0aG49ImZhbHNlIiBDb25zZW50PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6Y29uc2VudDp1bmF2YWlsYWJsZSIgSXNzdWVJbnN0YW50PSIyMDA5LTA2LTE5VDE3OjEzOjAzLjAwMFoiIFZlcnNpb249IjIuMCIgSUQ9ImlkNTM5QjkwMzU4REMyQzNCQSI+PElzc3VlciBGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpuYW1laWQtZm9ybWF0OmVudGl0eSI+aHR0cDovL2pvc3NvMDEuZGV2LmF0cmljb3JlLmNvbTo4MTgxL0pPU1NPMi9TQU1MMi9NRDwvSXNzdWVyPjxuczI6U2lnbmF0dXJlIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj48U2lnbmVkSW5mbz48Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnL1RSLzIwMDEvUkVDLXhtbC1jMTRuLTIwMDEwMzE1I1dpdGhDb21tZW50cyIgLz48U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIiAvPjxSZWZlcmVuY2UgVVJJPSIjaWQ1MzlCOTAzNThEQzJDM0JBIj48VHJhbnNmb3Jtcz48VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI2VudmVsb3BlZC1zaWduYXR1cmUiIC8+PC9UcmFuc2Zvcm1zPjxEaWdlc3RNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjc2hhMSIgLz48RGlnZXN0VmFsdWU+SUF6b0tZZ25XTWlRc3RXQUFCYW9VSVhyQlJFPTwvRGlnZXN0VmFsdWU+PC9SZWZlcmVuY2U+PC9TaWduZWRJbmZvPjxTaWduYXR1cmVWYWx1ZT5vaVM1c2JEN2I0bjBqd1I0ZDk1OUFDY1JKdHM0L0ZoWHNXd1IyVVhmZEZhVTBYTWdKRVdjWElXV1QxMWFlTlZYV2dReFJLc0IrVUZjUG1DcG1pUU1xd09ySS9vTXJKK0IxOHp6eUVIYXB6ekF3UXdiNENFc1hMNXNGRWFrRDJkaHlzKzlWcFFyOGhRL1NTdzd1SG93MjE4aDJveGh1QU8xVkdabkJlazRLeVk9PC9TaWduYXR1cmVWYWx1ZT48S2V5SW5mbz48WDUwOURhdGE+PFg1MDlDZXJ0aWZpY2F0ZT5NSUlDUmpDQ0FhOENCRW83MDBvd0RRWUpLb1pJaHZjTkFRRUVCUUF3YWpFTE1Ba0dBMVVFQmhNQ1Rsa3hFVEFQQmdOVkJBZ1RDRTVsZHlCWmIzSnJNUkV3RHdZRFZRUUhFd2hPWlhjZ1dXOXlhekVSTUE4R0ExVUVDaE1JWVhSeWFXTnZjbVV4RGpBTUJnTlZCQXNUQldwdmMzTnZNUkl3RUFZRFZRUURFd2xxYjNOemJ5MXpjREV3SGhjTk1Ea3dOakU1TVRnd05EVTRXaGNOTURrd09URTNNVGd3TkRVNFdqQnFNUXN3Q1FZRFZRUUdFd0pPV1RFUk1BOEdBMVVFQ0JNSVRtVjNJRmx2Y21zeEVUQVBCZ05WQkFjVENFNWxkeUJaYjNKck1SRXdEd1lEVlFRS0V3aGhkSEpwWTI5eVpURU9NQXdHQTFVRUN4TUZhbTl6YzI4eEVqQVFCZ05WQkFNVENXcHZjM052TFhOd01UQ0JuekFOQmdrcWhraUc5dzBCQVFFRkFBT0JqUUF3Z1lrQ2dZRUFzMG5uSXozaHk4Y25ONnNkRVR0WStjaEFXUWJpZVpJU1hSN2dlSzl4U3pnUUNwRC8za0ZQak1wODc1QjlIU09LV2haNk1lbjdOSEZjYk1zMWhTL29HejRFS3R0TTJuL1h3UytBZmllbUJiVjVPR3YxOXdDTHhuMnJ2Q1dsOUxpRnNBeXpKeEtWMWh6NE9IL0lVbXFSSG4xU1Nocm1KcG8rdUNXb2ZvMVR3RGNDQXdFQUFUQU5CZ2txaGtpRzl3MEJBUVFGQUFPQmdRQ0k0UVMwMjhVejM5NmdWaEZzMVdxK2FGdmN0KzJ6Vm5XRGRxeFVEb1MrNG05YTFpRi84MHFHdlhydmpsaTdyQTBibnU1YjNLNzJCVk1uZzI1bWhUL09LZHk3NmhPWG1OVTI4alhqUXp6Nk1zY3JjRDdiWitrdHg0QWxJR3BubFdxdFRLNTZ1d0pZQjhoSEZtb3poOTRCTk1rdXpNdGc0c1ZvdDhJOGJVLzJhQT09PC9YNTA5Q2VydGlmaWNhdGU+PC9YNTA5RGF0YT48L0tleUluZm8+PC9uczI6U2lnbmF0dXJlPjwvbnMzOkF1dGhuUmVxdWVzdD4=";

        AuthnRequestType req = new AuthnRequestType();

        NameIDType issuer = new NameIDType();
        issuer.setFormat(NameIDFormat.ENTITY.toString());
        issuer.setValue("http://josso01.dev.atricore.com:8181/IDBUS/SAML2/MD");
        req.setIssuer(issuer);

        req.setID("id123456");
        req.setIsPassive(false);
        req.setForceAuthn(false);

        req.setConsent(Consent.Unavailable.toString());
        req.setProtocolBinding(SamlR2Binding.SAMLR2_ARTIFACT.toString());
        req.setVersion("2.0");

        Date dateNow = new java.util.Date();
        req.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));

        NameIDPolicyType nameIdPolicy = new NameIDPolicyType ();
        nameIdPolicy.setFormat(NameIDFormat.PERSISTENT.toString());
        nameIdPolicy.setAllowCreate(true);
        req.setNameIDPolicy(nameIdPolicy);

        String base64Req = XmlUtils.marshalSamlR2Request(req, true);

        // POST 
        PostMethod post = new PostMethod("http://nam01.dev.atricore.com:8080/nidp/saml2/sso");
        post.setFollowRedirects(false);
        post.addParameter("SAMLRequest", base64Req);

        client.executeMethod(post);

        logger.debug(post.getResponseBodyAsString());

    }

    @Test
    public void spInitiatedSSOTest() throws Exception {

        // Access SP SSO Endpoint
        Html postAuthnReq = accessSPInitiantedSSOEndpoint();

        // POST SAML AuthnRequest to IDP
        String credentialsSubmittionUrl = postAuthnRequestToIDP(postAuthnReq);

        // POST Credentials to IDP
        String idpAuthenticatedUrl = postCredentialsToIDP(credentialsSubmittionUrl, "user1", "user1pwd");

        // Access IDP after authnetication
        String postResponse = accessIDPAuthenticated(idpAuthenticatedUrl);

        // POST SAML Response to SP
        postAuthnResponseToSP(postResponse);


    }

    /**
     *
     * @return the next location to access
     */
    protected Html accessSPInitiantedSSOEndpoint() throws Exception {

        // Access SP, asking for identity.
        String url = "http://josso01.dev.atricore.com:8181/IDBUS/SP-1/IDBUS/SSO/REDIR";

        logger.debug( "******************************************************************************" );
        logger.debug( "SP GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);

        int status = client.executeMethod( get );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        String response = get.getResponseBodyAsString();
        logger.debug( "******************************************************************************" );
        logger.debug( "SP RESPONSE: \n" + response);
        logger.debug( "******************************************************************************" );

        return unmarshallHtml(response);

    }

    protected String postAuthnRequestToIDP(Html postBindingHtml) throws Exception {

        Form jossoPostBindingForm = getForm(postBindingHtml);

        // Access SP, asking for identity.
        String url = jossoPostBindingForm.getAction();

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);
        post.setFollowRedirects(false);

        Div div = (Div) jossoPostBindingForm.getPOrH1OrH2().get(0);
        for (Object o :div.getContent()) {
            Input input = (Input) o;
            logger.debug("Adding POST param " + input.getName() + "=" + input.getValue());
            post.addParameter(input.getName(), input.getValue());
        }

        int status = client.executeMethod( post );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        String response = post.getResponseBodyAsString();
        logger.debug( "******************************************************************************" );
        logger.debug( "IDP RESPONSE : " + response);
        logger.debug( "******************************************************************************" );

        // TODO : Validate HTML and extract the right
        return "http://nam01.dev.atricore.com:8080/nidp/saml2/sso?sid=0";

    }

    protected String postCredentialsToIDP(String location, String usr, String pwd) throws Exception {
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);

        post.addParameter("option", "credential");
        post.addParameter("Ecom_User_ID", usr);
        post.addParameter("Ecom_Password", pwd);

        int status = client.executeMethod( post );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        String response = post.getResponseBodyAsString();
        logger.debug( "******************************************************************************" );
        logger.debug( "IDP RESPONSE: \n" + response);
        logger.debug( "******************************************************************************" );

        // Html html = super.unmarshallHtml(response);

        // TODO : Validate HTML and extract the right
        return "http://nam01.dev.atricore.com:8080/nidp/saml2/sso?sid=0";

    }

    protected String accessIDPAuthenticated(String location) throws Exception {
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);

        int status = client.executeMethod( get );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        String response = get.getResponseBodyAsString();
        logger.debug( "******************************************************************************" );
        logger.debug( "IDP RESPONSE: \n" + response);
        logger.debug( "******************************************************************************" );

        return response;

    }

    protected void postAuthnResponseToSP(String postAuthnResponse) throws Exception {

        // Access SP, asking for identity.

        // TODO : We need some kind of parser!! NAM does not generate XHTML !!!
        String url = "http://josso01.dev.atricore.com:8181/IDBUS/SP-1/SAML2/ACS/POST";
        String authnResponse = postAuthnResponse.replaceAll("\n", "");

        String sFrom = "<input type=\"hidden\" name=\"SAMLResponse\" value=\"";
        String sTo = "\"/>\t            </form>";

        int iFrom = authnResponse.indexOf(sFrom) + sFrom.length();
        int iTo = authnResponse.indexOf(sTo);

        String base64SamlResponse = authnResponse.substring(iFrom, iTo);

        logger.debug( "******************************************************************************" );
        logger.debug( "SP POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);
        post.setFollowRedirects(false);
        post.addParameter("SAMLResponse", base64SamlResponse);

        int status = client.executeMethod( post );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        String response = post.getResponseBodyAsString();
        logger.debug( "******************************************************************************" );
        logger.debug( "SP RESPONSE : " + response);
        logger.debug( "******************************************************************************" );

    }
}
