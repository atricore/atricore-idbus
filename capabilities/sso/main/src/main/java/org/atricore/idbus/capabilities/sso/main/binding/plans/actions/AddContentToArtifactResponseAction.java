package org.atricore.idbus.capabilities.sso.main.binding.plans.actions;

import oasis.names.tc.saml._2_0.protocol.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AddContentToArtifactResponseAction  extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(AddArtifactToArtifactResolveAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        ArtifactResponseType response = (ArtifactResponseType ) out.getContent();
        ArtifactResolveType request = (ArtifactResolveType) in.getContent();

        java.lang.Object content = executionContext.getContextInstance().getVariable(VAR_SAMLR2_ARTIFACT);
        String samlType = (String) executionContext.getContextInstance().getVariable(VAR_SAMLR2_ARTIFACT_TYPE);

        if (content == null) {
            logger.error("No SAML 2.0 Content found for artifact response : " + response.getID());
            return;
        }

        if (logger.isTraceEnabled())
            logger.trace("Adding SAML 2.0 Content " + content + " to ArtifactResponse " + response.getID());

        try {


            JAXBElement samlXmlElement = null;

            // SAML Type may be any request or response ... AuthnRequest

            String type = content.getClass().getName();
            //String ofMethodName = "create" + type.substring(0, type.length() - "Type".length());
            String ofMethodName = "create" + samlType;

            if (logger.isTraceEnabled())
                logger.trace("Creating SAML 2.0 JAXB Content with ObjectFactory method : " + ofMethodName);

            Object of = resolveObjectFactory(type);

            Method m = of.getClass().getMethod(ofMethodName, content.getClass());
            samlXmlElement = (JAXBElement) m.invoke(of, content);

            if (logger.isTraceEnabled())
                logger.trace("Adding SAML 2.0 JAXB Content " + content + " to ArtifactResponse " + response.getID());

            response.setAny(samlXmlElement);

        } catch (Exception e) {
            logger.error("Cannot create SAML 2.0 Content " + e.getMessage(), e);
            throw new IdentityMediationException(e);
        }


    }

    protected Object resolveObjectFactory(String type) {

        if (type.startsWith(SAMLR2Constants.SAML_PROTOCOL_PKG))
            return new oasis.names.tc.saml._2_0.protocol.ObjectFactory();
        else if (type.startsWith(SAMLR2Constants.SAML_IDBUS_PKG))
            return new oasis.names.tc.saml._2_0.idbus.ObjectFactory();
        else if (type.startsWith(SAMLR2Constants.SAML_ASSERTION_PKG))
            return new oasis.names.tc.saml._2_0.assertion.ObjectFactory();
        else
            throw new IllegalArgumentException("Unsupported SAML type : " + type);
    }
}