package org.atricore.idbus.capabilities.sso.support.core.util;

import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import oasis.names.tc.saml._2_0.idbus.ExtendedAttributeType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ExtensionsType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.AbstractPrincipal;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProtocolUtils {

    private static final Log logger = LogFactory.getLog(ProtocolUtils.class);

    public static SubjectType toSubjectType(Subject s) {

        SubjectType st = new SubjectType();

        for (SubjectNameID p : s.getPrincipals(SubjectNameID.class )) {
            SubjectNameIDType a = new SubjectNameIDType ();
            a.setName(p.getName());
            a.setFormat(p.getFormat());
            a.setLocalName(p.getLocalName());
            a.setNameQualifier(p.getNameQualifier());
            a.setLocalNameQualifier(p.getLocalNameQualifier());

            st.getAbstractPrincipal().add(a);
        }

        for (SubjectAttribute p : s.getPrincipals(SubjectAttribute.class )) {
            SubjectAttributeType a = new SubjectAttributeType();
            a.setName(p.getName());
            a.setValue(p.getValue());
            st.getAbstractPrincipal().add(a);
        }

        for (SubjectRole p : s.getPrincipals(SubjectRole.class )) {
            SubjectRoleType a = new SubjectRoleType();
            a.setName(p.getName());
            st.getAbstractPrincipal().add(a);
        }

        return st;
    }

    public static Subject toSubject(SubjectType s) {
        return toSubject(s, null);
    }


    public static Subject toSubject(SubjectType s, Collection<AbstractPrincipalType> additionalAttrs) {

        Set<Principal> principals = new HashSet<Principal>();

        for (int i = 0; i < s.getAbstractPrincipal().size(); i++) {
            AbstractPrincipalType pt = s.getAbstractPrincipal().get(i);

            if (pt instanceof SubjectNameIDType) {

                SubjectNameIDType st = (SubjectNameIDType) pt;
                SubjectNameID p = new SubjectNameID (st.getName(),
                        st.getFormat(),
                        st.getNameQualifier(),
                        st.getLocalNameQualifier());
                p.setLocalName(st.getLocalName());

                principals.add(p);

            } else if (pt instanceof SubjectAttributeType) {
                SubjectAttributeType st = (SubjectAttributeType) pt;
                SubjectAttribute p = new SubjectAttribute(st.getName(), st.getValue());
                principals.add(p);

            } else if (pt instanceof SubjectRoleType) {
                SubjectRoleType st = (SubjectRoleType) pt;
                SubjectRole p = new SubjectRole(st.getName());
                principals.add(p);

            } else {
                logger.warn("Unknown principal type " + pt.getClass().getSimpleName());
            }
        }

        if (additionalAttrs != null) {
            // TODO : Should we replace old attributes or duplicate them ?
            for (AbstractPrincipalType pt : additionalAttrs) {
                if (pt instanceof SubjectAttributeType) {
                    SubjectAttributeType st = (SubjectAttributeType) pt;
                    SubjectAttribute p = new SubjectAttribute(st.getName(), st.getValue());
                    principals.add(p);

                } else if (pt instanceof SubjectRoleType) {
                    SubjectRoleType st = (SubjectRoleType) pt;
                    SubjectRole p = new SubjectRole(st.getName());
                    principals.add(p);

                } else {
                    logger.warn("Unknown principal type " + pt.getClass().getSimpleName());
                }
            }
        }

        return new Subject(true, principals, new HashSet(), new HashSet());
    }

    public static final String getRequestExtAttribute(AuthnRequestType authnRequest, String name) {
        ExtensionsType extensions = authnRequest.getExtensions();
        if (extensions == null)
            return null;

        for (Object o : extensions.getAny()) {

            if (o instanceof JAXBElement) {

                JAXBElement e = (JAXBElement) o;
                if (e.getValue() instanceof ExtAttributeListType) {
                    ExtAttributeListType attrs = (ExtAttributeListType) e.getValue();
                    for (ExtendedAttributeType attr : attrs.getExtendedAttribute()) {
                        if (attr.getName().equals(name))
                            return attr.getValue();
                    }
                }
            }
        }
        return null;
    }


}
