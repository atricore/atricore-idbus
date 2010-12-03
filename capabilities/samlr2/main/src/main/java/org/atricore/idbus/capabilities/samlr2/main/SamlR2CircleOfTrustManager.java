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

package org.atricore.idbus.capabilities.samlr2.main;

import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @org.apache.xbean.XBean element="cot-manager"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2CircleOfTrustManager.java 1337 2009-06-25 13:18:38Z sgonzalez $
 */
public class SamlR2CircleOfTrustManager extends AbstractCircleOfTrustManager {

    private static final Log logger = LogFactory.getLog(SamlR2CircleOfTrustManager.class);
    
    private static final String SAMLR2_MD_LOCAL_NS = "md";

    private static JAXBContext jaxbCtx;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        jaxbCtx = createJAXBContext();
    }

    protected MetadataDefinition createMetadataDefinition(Provider provider, FederationChannel channel) {


        // TODO : Use planning to create MD artifact

        // Create MD Header

        // Create MD Roles

        return null;

    }

    protected MetadataDefinition loadMetadataDefinition(CircleOfTrustMemberDescriptor memberDescriptor, Resource resource) throws CircleOfTrustManagerException {

        try {

            Unmarshaller um = jaxbCtx.createUnmarshaller();
            InputStream is = resource.getInputStream();
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
            md.setId(memberDescriptor.getId());

            return md;

        } catch (JAXBException e) {
            throw new CircleOfTrustManagerException(e);
        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    protected MetadataEntry searchEntityDefinition(MetadataDefinition def, String entityId)
            throws CircleOfTrustManagerException {
        try {

            Document doc = (Document) def.getDefinition();

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']";
            if (logger.isDebugEnabled())
                logger.debug("Looking entity descriptor using xpath: " + xpathExpr);

            List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

            if (elements.size() > 1)
                throw new CircleOfTrustManagerException("Too many entity descriptors found for entity ID: " + entityId + " (Definition: " + def.getId() + ")");

            if (elements.size() < 1)
                throw new CircleOfTrustManagerException("Entity descriptor not found for entityID: " + entityId + " (Definition: " + def.getId() + ")");

            EntityDescriptorType descriptor = (EntityDescriptorType) elements.get(0).getValue();
            return new MetadataEntryImpl<EntityDescriptorType>(descriptor.getEntityID(), descriptor);

        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    protected MetadataEntry searchEntityRoleDefinition(MetadataDefinition def, String entityId, String roleType)
            throws CircleOfTrustManagerException {

        try {

            Document doc = (Document) def.getDefinition();

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(roleType) + "[1]";
            if (logger.isDebugEnabled())
                logger.debug("Looking entity role descriptor using xpath: " + xpathExpr);

            List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

            if (elements.size() > 1)
                throw new CircleOfTrustManagerException("Too many entity descriptors found for entity ID: " + entityId);

            if (elements.size() < 1) {
                logger.debug("No entity role definition found in entity " + entityId);
                return null;
            }

            RoleDescriptorType descriptor = (RoleDescriptorType) elements.get(0).getValue();

            return new MetadataEntryImpl<RoleDescriptorType>(descriptor.getID(), descriptor);


        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    protected Collection<MetadataEntry> searchEndpointDescriptors(MetadataDefinition def,
                                                                  String entityId,
                                                                  String roleType,
                                                                  EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException {

        try {
            Document doc = (Document) def.getDefinition();

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(roleType) + "[1]/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(endpoint.getType()) +
                    "[@Binding='"+endpoint.getBinding()+"']";

            List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

            Collection<MetadataEntry> entries = new ArrayList<MetadataEntry>();
            for (JAXBElement element : elements) {
                EndpointType descriptor = (EndpointType) element.getValue();
                entries.add(new MetadataEntryImpl<EndpointType>(descriptor.getLocation(), descriptor));
            }

            return entries;

        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }

    }


    protected MetadataEntry searchEndpointDescriptor(MetadataDefinition def, String entityId, String roleType,
                                                       EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        try {
            Document doc = (Document) def.getDefinition();

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(roleType) + "[1]/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(endpoint.getType()) +
                    "[@Binding='"+endpoint.getBinding()+"']";
            List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

            if (elements.size() > 1)
                throw new CircleOfTrustManagerException("Too many endpoint descriptors found for entity ID: " +
                        entityId + " (" + roleType + "|" + endpoint + ")");

            if (elements.size() < 1) {
                logger.debug("Endpoint descriptor not found in entityID: " + entityId);
                return null;
            }

            EndpointType descriptor = (EndpointType) elements.get(0).getValue();

            return new MetadataEntryImpl<EndpointType>(descriptor.getLocation(), descriptor);

        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    // XML Utils

    protected JAXBContext createJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(SAMLR2Constants.SAML_METADATA_PKG);
    }

    protected Document marshalDefinition(JAXBElement jb) throws Exception {
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

        Marshaller m = jaxbCtx.createMarshaller();
        m.marshal(jb, doc);

        logger.debug(doc);

        return doc;
    }

    protected List<JAXBElement> searchMetadata(Document doc, String xpathExpr) throws CircleOfTrustManagerException {

        try {
            if (logger.isDebugEnabled())
                logger.debug("Looking metadata elements using xpath: " + xpathExpr);

            NodeList nodes = this.evaluateXPath(doc, xpathExpr);

            if (logger.isDebugEnabled())
                logger.debug("Found " + nodes.getLength() + " metadata element(s)");

            Unmarshaller u = jaxbCtx.createUnmarshaller();

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

    protected NodeList evaluateXPath(Document doc, String expression) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(getNamespaceContext());

        XPathExpression expr = xpath.compile(expression);

        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        return nl;
    }


    protected NamespaceContext getNamespaceContext() {

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

    protected String removePrefix(String str) {
        // Prefix can be:
        // 1- {prefix}value
        // 2- prefix:value
        
        int pos = str.lastIndexOf("}");
        if (pos >= 0)
            return str.substring(pos + 1);

        pos =str.lastIndexOf(":");
        if (pos >= 0)
            return str.substring(pos + 1);
        return str;

    }


}
