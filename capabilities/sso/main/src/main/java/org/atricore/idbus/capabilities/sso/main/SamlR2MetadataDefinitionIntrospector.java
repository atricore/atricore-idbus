package org.atricore.idbus.capabilities.sso.main;

import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SamlR2MetadataDefinitionIntrospector implements MetadataDefinitionIntrospector, InitializingBean {

    private static final Log logger = LogFactory.getLog(SamlR2MetadataDefinitionIntrospector.class);

    private static final String SAMLR2_MD_LOCAL_NS = "md";

    // Use a private instance context insted:
    // private static JAXBContext jaxbCtx;
    private JAXBContext jaxbCtx;

    private Map<String , Object> cache  = new ConcurrentHashMap<String, Object>();

    public void afterPropertiesSet() throws Exception {
        jaxbCtx = createJAXBContext();
        cache.clear();
    }

    protected MetadataDefinition createMetadataDefinition(Provider provider, FederationChannel channel) {
        // TODO : Use planning to create MD artifact
        // Create MD Header
        // Create MD Roles
        return null;

    }

    public MetadataDefinition load(CircleOfTrustMemberDescriptor memberDescriptor) throws CircleOfTrustManagerException {

            throw new CircleOfTrustManagerException("SAMLR2 Metadata Definition needs to be stored in an external resource");
    }

    public MetadataDefinition load(CircleOfTrustMemberDescriptor memberDescriptor, Resource resource) throws CircleOfTrustManagerException {

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

            Document strictDoc = marshalDefinition(je);
            md.setStrictDefinition(strictDoc);

            MetadataEntry entry = searchEntityDefinition(md, memberDescriptor.getAlias(), true);

            EntityDescriptorType samlMd = (EntityDescriptorType) entry.getEntry();

            for (RoleDescriptorType roleDescriptor : samlMd.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {
                if (roleDescriptor instanceof SPSSODescriptorType) {
                    SPSSODescriptorType spDescriptor = (SPSSODescriptorType) roleDescriptor;
                    prepareSPDescriptor(entry, spDescriptor);
                } else if (roleDescriptor instanceof IDPSSODescriptorType) {
                    IDPSSODescriptorType idpDescriptor = (IDPSSODescriptorType) roleDescriptor;
                    prepareIdPDescriptor(entry, idpDescriptor);
                }
            }

            return md;

        } catch (JAXBException e) {
            throw new CircleOfTrustManagerException(e);
        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    public MetadataEntry searchEntityDefinition(MetadataDefinition def, String entityId)
            throws CircleOfTrustManagerException {
        return searchEntityDefinition(def, entityId, false);
    }



    public MetadataEntry searchEntityDefinition(MetadataDefinition def, String entityId, boolean strict)
            throws CircleOfTrustManagerException {
        try {

            Document doc = (Document) (strict ? def.getStrictDefinition() : def.getDefinition());

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']";
            if (logger.isDebugEnabled())
                logger.debug("Looking entity descriptor using xpath: " + xpathExpr);

            String key = def.getId() + ":" + entityId + ":" + xpathExpr + ":" + strict;

            // See if we have this cached
            MetadataEntry e = (MetadataEntry) cache.get(key);
            if (e != null) {
                if (logger.isTraceEnabled())
                    logger.trace("Using entity descriptor from cache, key : " + key);

                return e;
            }

            synchronized (this) {

                List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

                if (elements.size() > 1)
                    throw new CircleOfTrustManagerException("Too many entity descriptors found for entity ID: " + entityId + " (Definition: " + def.getId() + ")");

                if (elements.size() < 1)
                    throw new CircleOfTrustManagerException("Entity descriptor not found for entityID: " + entityId + " (Definition: " + def.getId() + ")");

                EntityDescriptorType descriptor = (EntityDescriptorType) elements.get(0).getValue();
                e = new MetadataEntryImpl<EntityDescriptorType>(descriptor.getEntityID(), descriptor);
            }

            if (logger.isTraceEnabled())
                logger.trace("Storing entity descriptor in cache, key : " + key);
            cache.put(key, e);

            return e;

        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    public MetadataEntry searchEntityRoleDefinition(MetadataDefinition def, String entityId, String roleType)
            throws CircleOfTrustManagerException {

        try {

            Document doc = (Document) def.getDefinition();

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(roleType) + "[1]";
            if (logger.isDebugEnabled())
                logger.debug("Looking entity role descriptor using xpath: " + xpathExpr);

            String key = def.getId() + ":" + entityId + ":" + xpathExpr;

            // See if we have this cached
            MetadataEntry e = (MetadataEntry) cache.get(key);
            if (e != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Using entity role descriptor from cache, key : " + key);
                return e;
            }

            synchronized (this) {

                List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

                if (elements.size() > 1)
                    throw new CircleOfTrustManagerException("Too many entity descriptors found for entity ID: " + entityId);

                if (elements.size() < 1) {
                    logger.debug("No entity role definition found in entity " + entityId);
                    return null;
                }

                RoleDescriptorType descriptor = (RoleDescriptorType) elements.get(0).getValue();

                e = new MetadataEntryImpl<RoleDescriptorType>(descriptor.getID(), descriptor);
            }

            if (logger.isTraceEnabled())
                logger.trace("Storing entity role descriptor in cache, key : " + key);

            cache.put(key, e);
            return e;




        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }
    }

    public Collection<MetadataEntry> searchEndpointDescriptors(   MetadataDefinition def,
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

            String key = def.getId() + ":" + entityId + ":" + roleType + ":" + xpathExpr;

            // See if we have this cached
            Collection<MetadataEntry> e = (Collection<MetadataEntry>) cache.get(key);

            if (e != null) {

                if (logger.isTraceEnabled())
                    logger.trace("Using endpoint descriptor from cache, key : " + key);

                return e;
            }

            synchronized (this) {

                List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

                Collection<MetadataEntry> entries = new ArrayList<MetadataEntry>();
                for (JAXBElement element : elements) {
                    EndpointType descriptor = (EndpointType) element.getValue();
                    entries.add(new MetadataEntryImpl<EndpointType>(descriptor.getLocation(), descriptor));
                }

                if (logger.isTraceEnabled())
                    logger.trace("Storing endpoint descriptor in cache, key : " + key);
                cache.put(key, entries);

                return entries;
            }



        } catch (Exception e) {
            throw new CircleOfTrustManagerException(e);
        }

    }


    public MetadataEntry searchEndpointDescriptor(MetadataDefinition def, String entityId, String roleType,
                                                       EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        try {
            Document doc = (Document) def.getDefinition();

            String xpathExpr = "//"+SAMLR2_MD_LOCAL_NS+":EntityDescriptor[@entityID='"+entityId+"']/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(roleType) + "[1]/" +
                    SAMLR2_MD_LOCAL_NS + ":" + removePrefix(endpoint.getType()) +
                    "[@Binding='"+endpoint.getBinding()+"']";

            String key = def.getId() + ":" + entityId + ":" + roleType + ":" + endpoint + ":" + xpathExpr;

            // See if we have this cached
            MetadataEntry e = (MetadataEntry) cache.get(key);
            if (e != null) {
                if (logger.isTraceEnabled())
                    logger.trace("Using endpoint descriptor from cache, key : " + key);
                return e;
            }

            synchronized (this) {

                List<JAXBElement> elements = this.searchMetadata(doc, xpathExpr);

                if (elements.size() > 1)
                    throw new CircleOfTrustManagerException("Too many endpoint descriptors found for entity ID: " +
                            entityId + " (" + roleType + "|" + endpoint + ")");

                if (elements.size() < 1) {
                    logger.debug("Endpoint descriptor not found in entityID: " + entityId);
                    return null;
                }

                EndpointType descriptor = (EndpointType) elements.get(0).getValue();

                e = new MetadataEntryImpl<EndpointType>(descriptor.getLocation(), descriptor);
            }

            if (logger.isTraceEnabled())
                logger.trace("Storing endpoint descriptor in cache, key : " + key);

            cache.put(key, e);
            return e;

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


    protected void prepareIdPDescriptor(MetadataEntry mdEntry, IDPSSODescriptorType mdIdP) {

        // Remove services using non-normative protocols (i.e. local:)
        // We assume that we're altering the actual list!
        synchronized (mdIdP) {
            removeNonNormativeEndpoints(mdIdP.getAssertionIDRequestService());
            removeNonNormativeEndpoints(mdIdP.getSingleSignOnService());
            removeNonNormativeEndpoints(mdIdP.getNameIDMappingService());
            removeNonNormativeEndpoints(mdIdP.getManageNameIDService());
            removeNonNormativeEndpoints(mdIdP.getSingleLogoutService());
            removeNonNormativeIndexedEndpoints(mdIdP.getArtifactResolutionService());
        }

    }

    protected void prepareSPDescriptor(MetadataEntry mdEntry, SPSSODescriptorType mdSP) {
        // Remove services using non-normative protocols (i.e. local:)
        // We assume that we're altering the actual list!
        synchronized (mdSP) {
            removeNonNormativeEndpoints(mdSP.getManageNameIDService());
            removeNonNormativeEndpoints(mdSP.getSingleLogoutService());
            removeNonNormativeIndexedEndpoints(mdSP.getAssertionConsumerService());
            removeNonNormativeIndexedEndpoints(mdSP.getArtifactResolutionService());
        }
    }

    protected void removeNonNormativeIndexedEndpoints(List<IndexedEndpointType> endpoints) {
        List<IndexedEndpointType> validEdpoints = new ArrayList<IndexedEndpointType>();

        for (IndexedEndpointType endpoint : endpoints) {
            if (endpoint.getBinding() == null) {
                continue;
            }

            SSOBinding b = SSOBinding.asEnum(endpoint.getBinding());
            if (!b.isNormative())
                continue;

            validEdpoints.add(endpoint);
        }

        endpoints.clear();
        endpoints.addAll(validEdpoints);
    }

    protected void removeNonNormativeEndpoints(List<EndpointType> endpoints) {
        List<EndpointType> validEdpoints = new ArrayList<EndpointType>();

        for (EndpointType endpoint : endpoints) {
            if (endpoint.getBinding() == null) {
                continue;
            }

            SSOBinding b = SSOBinding.asEnum(endpoint.getBinding());
            if (!b.isNormative())
                continue;

            validEdpoints.add(endpoint);
        }

        endpoints.clear();
        endpoints.addAll(validEdpoints);
    }


}
