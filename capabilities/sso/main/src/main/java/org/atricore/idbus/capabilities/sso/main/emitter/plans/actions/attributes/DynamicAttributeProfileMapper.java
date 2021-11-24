package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;
import org.atricore.idbus.kernel.main.authn.*;

import javax.security.auth.Subject;
import java.io.*;
import java.util.*;

public class DynamicAttributeProfileMapper extends BaseAttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(DynamicAttributeProfileMapper.class);

    // TODO : Add other special attributes (i.e. idpAlias)
    private static final String PRINCIPAL_ATTR_NAME = "_principal";

    private static final String GROUPS_ATTR_NAME = "_groups";

    private static final String AUTHN_CTX_CLASS_ATTR_NAME = "_authnContextClass";

    private static final String IDP_SSO_SESSION = "_idpSsoSession";

    private Map<String, AttributeMapping> attributeMaps = new HashMap<String, AttributeMapping>();

    private boolean includeNonMappedProperties = true;

    public DynamicAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.CUSTOM);
    }

    private VelocityEngine velocityEngine;

    private boolean init = false;

    public void init() {
        if (init)
            return;

        try {
            velocityEngine = new VelocityEngine();

            // Setup classpath resource loader  (Actually not used!)
            velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "classpath");

            velocityEngine.addProperty(
                    "classpath." + Velocity.RESOURCE_LOADER + ".class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

            velocityEngine.setProperty(
                    "classpath." + Velocity.RESOURCE_LOADER + ".cache", "false");

            velocityEngine.setProperty(
                    "classpath." + Velocity.RESOURCE_LOADER + ".modificationCheckInterval",
                    "2");

            velocityEngine.init();

            init = true;

        } catch (Exception e) {
            logger.error("Cannot initialize serializer, velocity error: " + e.getMessage(), e);
        }

    }

    @Override
    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext) {

        init();

        List<AttributeType> userAttrs = new ArrayList<AttributeType>();

        VelocityContext veCtx = new VelocityContext();

        // Principal
        AttributeMapping principalAttributeMapping = getAttributeMapping(PRINCIPAL_ATTR_NAME);
        if (principalAttributeMapping != null) {
            // Add an attribute for the principal
            AttributeType attrPrincipal = new AttributeType();
            attrPrincipal.setName((principalAttributeMapping.getReportedAttrName() != null &&
                                  !principalAttributeMapping.getReportedAttrName().equals("")) ?
                    principalAttributeMapping.getReportedAttrName() : "principal");
            attrPrincipal.setNameFormat(principalAttributeMapping.getReportedAttrNameFormat());
            attrPrincipal.getAttributeValue().add(ssoUser.getName());

            veCtx.put("principal", principalAttributeMapping.getReportedAttrNameFormat());
        }

        // IdP SSO Session
        AttributeMapping idpSsoSessionAttributeMapping = getAttributeMapping(IDP_SSO_SESSION);
        if (idpSsoSessionAttributeMapping != null) {
            // Add an attribute for the principal
            AttributeType idpSsoSession = new AttributeType();
            idpSsoSession.setName((idpSsoSessionAttributeMapping.getReportedAttrName() != null &&
                    !idpSsoSessionAttributeMapping.getReportedAttrName().equals("")) ?
                    idpSsoSessionAttributeMapping.getReportedAttrName() : "idpSsoSession");
            idpSsoSession.setNameFormat(idpSsoSessionAttributeMapping.getReportedAttrNameFormat());
            idpSsoSession.getAttributeValue().add(emissionContext.getSessionIndex());

            veCtx.put("idpSsoSession", emissionContext.getSessionIndex());
        }


        // This will add SSO User properties as attribute statements.
        if (ssoUser.getProperties() != null && ssoUser.getProperties().length > 0) {

            // Keep attributes simple, if they are URIs, remove prefixes
            Map<String, AttributeType> at = new HashMap<>();
            for (SSONameValuePair property : ssoUser.getProperties()) {

                veCtx.put(property.getName(), property.getValue());

                AttributeMapping attributeMapping = getAttributeMapping(property.getName());

                String name = null;
                String format = null;
                String friendlyName = null;
                if (attributeMapping != null) {

                    name = (attributeMapping.getReportedAttrName() != null &&
                            !attributeMapping.getReportedAttrName().equals("")) ?
                            attributeMapping.getReportedAttrName() : property.getName();

                    friendlyName = name;
                    format = attributeMapping.getReportedAttrNameFormat();


                } else if (includeNonMappedProperties) {

                    name = property.getName();

                    // Only qualify property names if needed
                    if (property.getName().indexOf(':') >= 0) {
                        format = AttributeNameFormat.URI.getValue();
                    } else {
                        format = AttributeNameFormat.BASIC.getValue();
                    }

                }

                AttributeType attrProp = at.get(name);
                if (attrProp == null) {
                    attrProp = new AttributeType();
                    attrProp.setName(name);
                    attrProp.setNameFormat(format);
                    attrProp.setFriendlyName(friendlyName);
                    at.put(name, attrProp);
                    userAttrs.add(attrProp);
                }

                boolean found = false;
                for (Object value : attrProp.getAttributeValue()) {
                    if (value.equals(property.getValue())) {
                        found = true;
                        break;
                    }
                }

                if (!found)
                    attrProp.getAttributeValue().add(property.getValue());


            }

        }

        // Add constants and expressions!
        for (AttributeMapping attributeMapping : attributeMaps.values()) {
            String attrName = attributeMapping.getAttrName().trim();
            if (attrName.startsWith("\"") && attrName.endsWith("\"") &&
                    attributeMapping.getReportedAttrName() != null && !attributeMapping.getReportedAttrName().equals("")) {
                AttributeType attrProp = new AttributeType();
                attrProp.setName(attributeMapping.getReportedAttrName());
                attrProp.setFriendlyName(attrProp.getName());
                attrProp.setNameFormat(attributeMapping.getReportedAttrNameFormat());
                attrProp.getAttributeValue().add(attrName.substring(1, attrName.length() - 1));
                userAttrs.add(attrProp);

            } else if (attrName.startsWith("vt:")) {

                String vtExpr = attributeMapping.getAttrName().substring("vt:".length());
                OutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter out  = new OutputStreamWriter(baos);

                InputStream is = new ByteArrayInputStream(vtExpr.getBytes());
                Reader in = new InputStreamReader(is);


                // Support scripting!
                try {
                    if (velocityEngine.evaluate(veCtx, out, attributeMapping.getReportedAttrName(), in)) {
                        out.flush();

                        AttributeType attrProp = new AttributeType();
                        attrProp.setName(attributeMapping.getReportedAttrName());
                        attrProp.setFriendlyName(attrProp.getName());
                        attrProp.setNameFormat(attributeMapping.getReportedAttrNameFormat());
                        String tokens = baos.toString().trim();

                        attrProp.getAttributeValue().add(tokens); // TODO : Support multiple values

                        userAttrs.add(attrProp);
                    } else {
                        logger.error("Invalid expression ["+vtExpr+"] for " + attributeMapping.getReportedAttrName());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }


            }
        }

        return userAttrs;
    }

    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {
        init();

        List<AttributeType> attrRoles = new ArrayList<AttributeType>();

        // TODO : Allow groups expression group-vt:
        AttributeMapping groupsAttributeMapping = getAttributeMapping(GROUPS_ATTR_NAME);
        if (groupsAttributeMapping != null || includeNonMappedProperties) {
            AttributeType attrRole = new AttributeType();

            attrRole.setName((groupsAttributeMapping!= null && groupsAttributeMapping.getReportedAttrName() != null &&
                    !groupsAttributeMapping.getReportedAttrName().equals("")) ?
                    groupsAttributeMapping.getReportedAttrName() : "groups");
            attrRole.setFriendlyName(attrRole.getName());
            attrRole.setNameFormat(AttributeNameFormat.BASIC.getValue());
            for(SSORole role : ssoRoles)
                attrRole.getAttributeValue().add( role.getName() );

            attrRoles.add(attrRole);
        }

        return attrRoles;
    }

    @Override
    protected Collection<AttributeType> policiesToAttributes(Set<PolicyEnforcementStatement> ssoPolicies) {
        init();
        // SSO Enforced policies
        // TODO : Can we use SAML Authn context information ?!
        List<AttributeType> attrPolicies = new ArrayList<AttributeType>();

        for (PolicyEnforcementStatement ssoPolicy : ssoPolicies) {
            AttributeType attrPolicy = new AttributeType();

            attrPolicy.setFriendlyName(ssoPolicy.getName());
            attrPolicy.setName(ssoPolicy.getNs() + ":" + ssoPolicy.getName());
            attrPolicy.setNameFormat(AttributeNameFormat.URI.getValue());

            if (ssoPolicy.getValues().size() > 0) {
                for (Object v : ssoPolicy.getValues())
                    attrPolicy.getAttributeValue().add(v);
            }

            attrPolicies.add(attrPolicy);
        }

        return attrPolicies;
    }

    @Override
    public AuthnCtxClass toAuthnCtxClass(Subject ssoSubject, AuthnCtxClass original) {
        init();
        AttributeMapping authnCtxClassAttributeMapping = getAttributeMapping(AUTHN_CTX_CLASS_ATTR_NAME);
        if (authnCtxClassAttributeMapping != null && authnCtxClassAttributeMapping.getReportedAttrName() != null &&
                !authnCtxClassAttributeMapping.getReportedAttrName().equals("")) {
            return AuthnCtxClass.asEnum(authnCtxClassAttributeMapping.getReportedAttrName());
        }
        return original;
    }

    private AttributeMapping getAttributeMapping(String attrName) {
        return attributeMaps.get(attrName);
    }

    public void setAttributeMaps(List<AttributeMapping> attributeMaps) {
        for (AttributeMapping attributeMapping : attributeMaps) {
            this.attributeMaps.put(attributeMapping.getAttrName(), attributeMapping);
        }
    }

    public boolean isIncludeNonMappedProperties() {
        return includeNonMappedProperties;
    }

    public void setIncludeNonMappedProperties(boolean includeNonMappedProperties) {
        this.includeNonMappedProperties = includeNonMappedProperties;
    }
}
