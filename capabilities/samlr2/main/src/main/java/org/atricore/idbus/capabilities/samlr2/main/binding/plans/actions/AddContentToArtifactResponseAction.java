package org.atricore.idbus.capabilities.samlr2.main.binding.plans.actions;

import oasis.names.tc.saml._2_0.protocol.ArtifactResolveType;
import oasis.names.tc.saml._2_0.protocol.ArtifactResponseType;
import oasis.names.tc.saml._2_0.protocol.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.actions.AbstractSamlR2Action;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AddContentToArtifactResponseAction  extends AbstractSamlR2Action {

    private static final Log logger = LogFactory.getLog(AddArtifactToArtifactResolveAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        ArtifactResponseType response = (ArtifactResponseType ) out.getContent();
        ArtifactResolveType request = (ArtifactResolveType) in.getContent();

        java.lang.Object content = executionContext.getContextInstance().getVariable(VAR_SAMLR2_ARTIFACT);
        if (logger.isTraceEnabled())
            logger.trace("Adding SAML 2.0 Content " + content + " to ArtifactResponse " + response.getID());

        ObjectFactory of = new ObjectFactory();

        // TODO : Use switch and leave reflection for unknown types ?
        String type = content.getClass().getSimpleName();
        String ofMethodName = "create" + type.substring(0, type.length() - "Type".length());
        Method m = of.getClass().getMethod(ofMethodName, content.getClass());

        JAXBElement samlXmlElemen = (JAXBElement) m.invoke(of, content);

        if (logger.isTraceEnabled())
            logger.trace("Adding SAML 2.0 JAXB Content " + content + " to ArtifactResponse " + response.getID());

        response.setAny(samlXmlElemen);


    }
}