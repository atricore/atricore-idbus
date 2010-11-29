/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.util;

import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.SSODescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManagerException;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MetadataUtil {

    private static final Log logger = LogFactory.getLog(MetadataUtil.class);

    private static final String SAMLR2_MD_LOCAL_NS = "md";

    public static MetadataDefinition loadMetadataDefinition(byte[] definition) throws Exception {
        try {

            Unmarshaller um = createSamlR2JAXBContext().createUnmarshaller();
            ByteArrayInputStream is = new ByteArrayInputStream(definition);
            JAXBElement je = (JAXBElement) um.unmarshal(is);

            if (logger.isDebugEnabled()) {
                logger.debug(je.getName());

                if (je.getValue() instanceof EntityDescriptorType) {
                    EntityDescriptorType ed = (EntityDescriptorType) je.getValue();
                    logger.debug(ed.getEntityID());
                }
            }

            Document doc = marshalDefinition(je);

            MetadataDefinition<Document> md = new MetadataDefinition<Document>();
            md.setDefinition(doc);
            
            return md;

        } catch (JAXBException e) {
            throw new CircleOfTrustManagerException(e);
        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    protected static Document marshalDefinition(JAXBElement jb) throws Exception {
        // Marshall the Assertion object as a DOM tree:
        if (logger.isDebugEnabled())
            logger.debug("Marshalling SAMLR2 Metadata to DOM Tree [" + jb.getName()+ "]");

        // Instantiate the document to be signed
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        // XML Signature needs to be namespace aware
        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();

        Marshaller m = createSamlR2JAXBContext().createMarshaller();
        m.marshal(jb, doc);

        logger.debug(doc);

        return doc;
    }

    public static JAXBContext createSamlR2JAXBContext() throws JAXBException {
        return createJAXBContext(new String[]{ SAMLR2Constants.SAML_PROTOCOL_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG,
                SAMLR2Constants.SAML_ASSERTION_PKG,
                SAMLR2Constants.SAML_METADATA_PKG});
    }

    public static JAXBContext createJAXBContext ( String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();
        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }
        // Use our classloader to build JAXBContext so it can find binding classes.
        return JAXBContext.newInstance( packages.toString(), XmlUtils.class.getClassLoader());
    }
    
    public static String findEntityId(MetadataDefinition md) throws Exception {
        Document doc = (Document) md.getDefinition();

        String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor";
        if (logger.isDebugEnabled())
            logger.debug("Looking entity ID using xpath: " + xpathExpr);

        List<JAXBElement> elements = searchMetadata(doc, xpathExpr);

        if (elements.size() > 1)
            throw new Exception("Too many entity descriptors found");

        if (elements.size() < 1) {
            throw new Exception("No entity descriptors found");
        }

        EntityDescriptorType descriptor = (EntityDescriptorType) elements.get(0).getValue();

        return descriptor.getEntityID();
    }

    public static SSODescriptorType findSSODescriptor(MetadataDefinition md, String descriptorName) throws Exception {
        Document doc = (Document) md.getDefinition();

        String xpathExpr = "//" + SAMLR2_MD_LOCAL_NS + ":EntityDescriptor/" + SAMLR2_MD_LOCAL_NS + ":" + descriptorName;
        if (logger.isDebugEnabled())
            logger.debug("Looking descriptor using xpath: " + xpathExpr);

        List<JAXBElement> elements = searchMetadata(doc, xpathExpr);

        if (elements.size() > 1)
            throw new Exception("Too many SSO descriptors found");

        if (elements.size() < 1) {
            throw new Exception("No SSO descriptor found");
        }

        SSODescriptorType descriptor = (SSODescriptorType) elements.get(0).getValue();

        return descriptor;
    }

    protected static List<JAXBElement> searchMetadata(Document doc, String xpathExpr) throws CircleOfTrustManagerException {

        try {
            if (logger.isDebugEnabled())
                logger.debug("Looking metadata elements using xpath: " + xpathExpr);

            NodeList nodes = evaluateXPath(doc, xpathExpr);

            if (logger.isDebugEnabled())
                logger.debug("Found " + nodes.getLength() + " metadata element(s)");

            Unmarshaller u = createSamlR2JAXBContext().createUnmarshaller();

            List<JAXBElement> entries = new ArrayList<JAXBElement>();
            for (int i = 0 ; i < nodes.getLength() ; i++) {
                Node node = nodes.item(i);
                JAXBElement e = (JAXBElement) u.unmarshal(node);
                entries.add(e);
            }

            return entries;


        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }

    }

    protected static NodeList evaluateXPath(Document doc, String expression) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(getNamespaceContext());

        XPathExpression expr = xpath.compile(expression);

        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        return nl;
    }

    protected static NamespaceContext getNamespaceContext() {

        return new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                if (prefix.equals(SAMLR2_MD_LOCAL_NS))
                    return SAMLR2Constants.SAML_METADATA_NS;

                return null;
            }

            // Dummy implementation - not used!
            public Iterator getPrefixes(String val) {
                return null;
            }


            // Dummy implemenation - not used!
            public String getPrefix(String uri) {
                if (uri.equals(SAMLR2Constants.SAML_METADATA_NS))
                    return SAMLR2_MD_LOCAL_NS;

                return null;
            }
            
        };

    }
}
