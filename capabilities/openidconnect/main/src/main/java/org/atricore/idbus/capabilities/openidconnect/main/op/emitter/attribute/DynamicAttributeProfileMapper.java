package org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.IDTokenEmitter;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectRoleType;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.io.*;
import java.util.*;

public class DynamicAttributeProfileMapper implements OIDCAttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(DynamicAttributeProfileMapper.class);

    private String name;

    private Map<String, AttributeMapping> attributeMaps = new HashMap<String, AttributeMapping>();

    private VelocityEngine velocityEngine;

    private boolean includeNonMappedProperties;

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
    public IDTokenClaimsSet toAttributes(Object rstCtx, Subject subject, List<AbstractPrincipalType> proxyPrincipals, IDTokenClaimsSet claimsSet) {

        init();

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() < 1) {
            logger.error("Can't build ID Token for SimplePrincipal.  Try attaching an ID vault to your IDP/VP");
            return null;
        }
        SSOUser user = ssoUsers.iterator().next();

        VelocityContext veCtx = new VelocityContext();

        veCtx.put("principal", user.getName());

        // Additional claims
        Set<String> usedProps = new HashSet<String>();
        if (user.getProperties() != null) {
            for (SSONameValuePair property : user.getProperties()) {
                usedProps.add(property.getName());
                veCtx.put(property.getName(), property.getValue());

                // Map name, if null property must be ignored.
                String name = mapName(property.getName());
                if (name != null)
                    claimsSet.setClaim(name, property.getValue());

            }
        }

        // Groups
        Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);
        Set<String> usedRoles = new HashSet<String>();

        for (SSORole ssoRole : ssoRoles) {
            usedRoles.add(ssoRole.getName());
        }

        // Add proxy principals (principals received from a proxied provider), but only if we don't have such a principal yet.
        if (proxyPrincipals != null) {
            for (AbstractPrincipalType principal : proxyPrincipals) {
                if (principal instanceof SubjectAttributeType) {
                    SubjectAttributeType attr = (SubjectAttributeType) principal;
                    String name = attr.getName();
                    if (name != null) {
                        int idx = name.lastIndexOf(':');
                        if (idx >= 0) name = name.substring(idx + 1);
                    }

                    String value = attr.getValue();
                    if (!usedProps.contains(name)) {
                        usedProps.add(name);
                        veCtx.put(name, value);

                        String mappedName = mapName(name);
                        if (mappedName != null)
                            claimsSet.setClaim(mappedName, value);
                    }
                } else if (principal instanceof SubjectRoleType) {
                    SubjectRoleType role = (SubjectRoleType) principal;
                    if (!usedRoles.contains(role.getName())) {
                        usedRoles.add(role.getName());
                    }
                }
            }
        }

        String mappedGroups = mapName("groups");

        if (mappedGroups != null) {
            JWTClaimsSet previousClaims = IDTokenEmitter.getPreviousIdTokenClaims(rstCtx);
            if (usedRoles.size() < 1) {
                if (previousClaims != null && previousClaims.getClaim(mappedGroups) != null)
                    claimsSet.setClaim(mappedGroups, previousClaims.getClaim(mappedGroups));
            } else {
                // Create role claim with used roles
                claimsSet.setClaim(mappedGroups, usedRoles);
            }
        }

        // Add constants and expressions!
        for (AttributeMapping attributeMapping : attributeMaps.values()) {

            String attrName = attributeMapping.getAttrName().trim();
            if (attrName.st./atartsWith("\"") && attrName.endsWith("\"") &&
                    attributeMapping.getReportedAttrName() != null && !attributeMapping.getReportedAttrName().equals("")) {

                String name = attributeMapping.getReportedAttrName();
                String value = attrName.substring(1, attrName.length() - 2);
                claimsSet.setClaim(name, value);
            } else if (attrName.startsWith("vt:")) {
                String vtExpr = attributeMapping.getAttrName().substring("vt:".length());
                OutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(baos);

                InputStream is = new ByteArrayInputStream(vtExpr.getBytes());
                Reader in = new InputStreamReader(is);

                // Support scripting!
                try {
                    if (velocityEngine.evaluate(veCtx, out, attributeMapping.getReportedAttrName(), in)) {
                        out.flush();
                        String name = attributeMapping.getReportedAttrName();
                        String tokens = baos.toString().trim();
                        claimsSet.setClaim(name, tokens); // TODO : Support multiple values
                    } else {
                        logger.error("Invalid expression ["+vtExpr+"] for " + attributeMapping.getReportedAttrName());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return claimsSet;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    protected String mapName(String name) {

        AttributeMapping attributeMapping = getAttributeMapping(name);
        String mappedName = null;
        if (attributeMapping != null) {
            mappedName = (attributeMapping.getReportedAttrName() != null &&
                    !attributeMapping.getReportedAttrName().equals("")) ?
                    attributeMapping.getReportedAttrName() : name;

        } else if (includeNonMappedProperties) {
            mappedName = name;
        }

        return mappedName;

    }

}
