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

package org.atricore.idbus.capabilities.samlr2.support.test;

import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.junit.Test;
import org.w3._1999.xhtml.Html;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class XmlUtilsTest {

    public static final Log logger = LogFactory.getLog(XmlUtilsTest.class); 

    @Test
    public void testUnmarshallXhtml() throws Exception {

        String xhtmlStr1 = "<?xml version=\"1.0\" ?><ns2:html xmlns:ns2=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><body onload=\"document.forms[0].submit();\"><noscript><p title=\"Note: Since your browser does not support JavaScript, you must press the Continue button once to proceed.\"></p></noscript><form method=\"post\" enctype=\"application/x-www-form-urlencoded\" action=\"http://localhost:8080/IDBUS/IDP-1/SAML2/SSO/POST\"><div><input value=\"PD94bWwgdmVyc2lvbj0iMS4wIiA/PjxBdXRoblJlcXVlc3QgeG1sbnM6bnM0PSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGVuYyMiIHhtbG5zOm5zMz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyIgeG1sbnM6bnMyPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9uIiB4bWxucz0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIiBBc3NlcnRpb25Db25zdW1lclNlcnZpY2VVUkw9Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9JREJVUy9TUC0xL1NBTUwyL0FDUy9QT1NUIiBQcm90b2NvbEJpbmRpbmc9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpiaW5kaW5nczpIVFRQLVBPU1QiIElzUGFzc2l2ZT0iZmFsc2UiIEZvcmNlQXV0aG49ImZhbHNlIiBDb25zZW50PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6Y29uc2VudDp1bmF2YWlsYWJsZSIgRGVzdGluYXRpb249Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9JREJVUy9JRFAtMS9TQU1MMi9TU08vUE9TVCIgSXNzdWVJbnN0YW50PSIyMDA5LTEwLTAyVDE4OjQ2OjA0LjA2NFoiIFZlcnNpb249IjIuMCIgSUQ9ImlkRDVEMUJDNjhBNTk0MDQ4OSI+PG5zMjpJc3N1ZXIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDplbnRpdHkiPmh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9JREJVUy9TQU1MMi9NRDwvbnMyOklzc3Vlcj48bnMzOlNpZ25hdHVyZSB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+PFNpZ25lZEluZm8+PENhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy9UUi8yMDAxL1JFQy14bWwtYzE0bi0yMDAxMDMxNSNXaXRoQ29tbWVudHMiPjwvQ2Fub25pY2FsaXphdGlvbk1ldGhvZD48U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIj48L1NpZ25hdHVyZU1ldGhvZD48UmVmZXJlbmNlIFVSST0iI2lkRDVEMUJDNjhBNTk0MDQ4OSI+PFRyYW5zZm9ybXM+PFRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIj48L1RyYW5zZm9ybT48L1RyYW5zZm9ybXM+PERpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIj48L0RpZ2VzdE1ldGhvZD48RGlnZXN0VmFsdWU+bUJnMzdPK2ZoMzJjdXlwdUY3TVNpcXVSaWpZPTwvRGlnZXN0VmFsdWU+PC9SZWZlcmVuY2U+PC9TaWduZWRJbmZvPjxTaWduYXR1cmVWYWx1ZT5QQzk3bGdJUnJ1OFFOQmpHTjlGWGNpZDBJWkM3RmltNFRHK2xPaE1LS21WSU13ekFBbWxDWXh6T3R0ZXRpemYyS1VQVW54eWRRcGVrNlcxY1lFQllxcjZ1OUhOUjg2elFvMG5URFZKRGNmM2FqK1BJNUhFN2tyQ3c5NzQ2SmlLdWNlYUtQNnVjWGRaOXpnQ1dlbGx0N0FEZnBzcnNEMGJCK0IyRHRSVlFVN3c9PC9TaWduYXR1cmVWYWx1ZT48S2V5SW5mbz48WDUwOURhdGE+PFg1MDlDZXJ0aWZpY2F0ZT5NSUlFMHpDQ0E3dWdBd0lCQWdJa0Fod1IvNlRWMTNTQ1FmbHF2K0xkVnZKMDVkbVlmWEYzWHJOY2VKZ1BBZ0lFWFFLOU1BMEdDU3FHU0liM0RRRUJCUVVBTURFeEdqQVlCZ05WQkFzVEVVOXlaMkZ1YVhwaGRHbHZibUZzSUVOQk1STXdFUVlEVlFRS0ZBcHVZVzB3TVY5MGNtVmxNQjRYRFRBNU1EWXlNREF4TWpJeE5Gb1hEVEV4TURZeU1EQXhNakl4TkZvd1pURVNNQkFHQTFVRUF4TUphbTl6YzI4dGMzQXhNUTR3REFZRFZRUUxFd1ZxYjNOemJ6RVJNQThHQTFVRUNoTUlZWFJ5YVdOdmNtVXhFVEFQQmdOVkJBY1RDRTVsZHlCWmIzSnJNUXN3Q1FZRFZRUUlFd0pPV1RFTU1Bb0dBMVVFQmhNRFZWTkJNSUdmTUEwR0NTcUdTSWIzRFFFQkFRVUFBNEdOQURDQmlRS0JnUURFblVoOWd1V0F4QjBBSTUxcEd0Q1J3Q0VKQUZnRm9LWmxYd0xHRlBhNysxTCs0am81T2loNVJaZFk3YlJZcHY1NlE1amxDaVJQSGJXM29ZRGxleGQrQ2NmWE1oS2p1TmF5YXU2bC9aSkQySUNmSVJTU3RKZ2dlR21iQThyWktVbURUaW0wbTVIU2NIcFdFUWQvbGxVdXlDcmd4L2ZVRGt3SjFDU2tPR3lPSFFJREFRQUJvNElDSVRDQ0FoMHdIUVlEVlIwT0JCWUVGRkRzTUJ5UVdTUFF5N2ZDQ2pBOWVDK2IyM0dUTUI4R0ExVWRJd1FZTUJhQUZFYldmaFZwNnovaVVLVzZpcHdHOVgzaW10TUhNQXNHQTFVZER3UUVBd0lFc0RDQ0Fjd0dDMkNHU0FHRytEY0JDUVFCQklJQnV6Q0NBYmNFQWdFQUFRSC9FeDFPYjNabGJHd2dVMlZqZFhKcGRIa2dRWFIwY21saWRYUmxLSFJ0S1JaRGFIUjBjRG92TDJSbGRtVnNiM0JsY2k1dWIzWmxiR3d1WTI5dEwzSmxjRzl6YVhSdmNua3ZZWFIwY21saWRYUmxjeTlqWlhKMFlYUjBjbk5mZGpFd0xtaDBiVENDQVVpZ0dnRUJBREFJTUFZQ0FRRUNBVVl3Q0RBR0FnRUJBZ0VLQWdGcG9Sb0JBUUF3Q0RBR0FnRUJBZ0VBTUFnd0JnSUJBUUlCQUFJQkFLSUdBZ0VYQVFIL280SUJCS0JZQWdFQ0FnSUEvd0lCQUFNTkFJQUFBQUFBQUFBQUFBQUFBQU1KQUlBQUFBQUFBQUFBTUJnd0VBSUJBQUlJZi8vLy8vLy8vLzhCQVFBQ0JBYnczMGd3R0RBUUFnRUFBZ2gvLy8vLy8vLy8vd0VCQUFJRUJ2RGZTS0ZZQWdFQ0FnSUEvd0lCQUFNTkFFQUFBQUFBQUFBQUFBQUFBQU1KQUVBQUFBQUFBQUFBTUJnd0VBSUJBQUlJZi8vLy8vLy8vLzhCQVFBQ0JCSC9wTlV3R0RBUUFnRUFBZ2gvLy8vLy8vLy8vd0VCQUFJRUVmK2sxYUpPTUV3Q0FRSUNBUUFDQWdEL0F3MEFnQUFBQUFBQUFBQUFBQUFBQXdrQWdBQUFBQUFBQUFBd0VqQVFBZ0VBQWdoLy8vLy8vLy8vL3dFQkFEQVNNQkFDQVFBQ0NILy8vLy8vLy8vL0FRRUFNQTBHQ1NxR1NJYjNEUUVCQlFVQUE0SUJBUUNXNHN2QVRvY09SSWphQllWZkZvcENvWHZqWG0wR0NhcWpscFNJU0VUU09aWEtEbUIxOGFjZEg4QkU0aEJSMzhhdk40VHJOT1JzZC9hb29vL0Q2MlB4YlpVaWJ4bHEzZlZWeEJnaTBXd1crMXRrU2NHa09JQ1JUSXg5cVo0RlowYkhrblBEVkxHSXpza2VpSE5SMWVSVzJ1anJ0YTF2V2pydCtKTkxPcC9iaDh0eGhDemx0eWIzbkRweUtjNWI2UjN6YnZ2czlVLzNmbTVoODNGbXphbnNjanBsRndaY3JKeExIK0JlWXIrM0krVDBVU2Z0Vi8razFkTVdLelU5WUc0NGVKeVdYZ3FGZ2xnRzVWb21QaU8vV2xsaU10U05qNjd4ZWZsVVJXWmlhU1g4TjRxTklNMTAzMGpCbUdnM1ZoZmI5emx0YXJCVGpTeCtYV2NZQUZtVDwvWDUwOUNlcnRpZmljYXRlPjwvWDUwOURhdGE+PC9LZXlJbmZvPjwvbnMzOlNpZ25hdHVyZT48TmFtZUlEUG9saWN5IEFsbG93Q3JlYXRlPSJ0cnVlIiBGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMTpuYW1laWQtZm9ybWF0OnVuc3BlY2lmaWVkIj48L05hbWVJRFBvbGljeT48L0F1dGhuUmVxdWVzdD4=\" type=\"hidden\" name=\"SAMLRequest\"></input></div><noscript><div><input value=\"Continue\" type=\"submit\"></input></div></noscript></form></body></ns2:html>";
        logger.debug("Unmarshalling : \n" + xhtmlStr1);
        Object o = XmlUtils.unmarshal(xhtmlStr1, new String[] {"org.w3._1999.xhtml"});

        assert o instanceof Html;
        Html html = (Html) o; 

    }

    @Test
    public void testGoogleApps() throws Exception {
        String authnReq = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"nnimlmjomkgcpjibdiclihekphalnghohekcdocf\"\n" +
                "                    Version=\"2.0\" IssueInstant=\"2011-01-10T19:43:52Z\"\n" +
                "                    ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" ProviderName=\"google.com\"\n" +
                "                    IsPassive=\"false\" AssertionConsumerServiceURL=\"https://www.google.com/a/atricore.com/acs\">\n" +
                "    <saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">google.com/a/atricore.com</saml:Issuer>\n" +
                "    <samlp:NameIDPolicy AllowCreate=\"true\" Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\"/>\n" +
                "</samlp:AuthnRequest>";

        logger.debug("Unmarshalling : \n" + authnReq);

        Object o = XmlUtils.unmarshalSamlR2Request(authnReq, false);

        assert o != null;
        assert o instanceof AuthnRequestType;
    }
}
