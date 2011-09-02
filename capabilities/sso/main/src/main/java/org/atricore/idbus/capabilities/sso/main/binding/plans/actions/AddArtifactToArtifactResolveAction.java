package org.atricore.idbus.capabilities.sso.main.binding.plans.actions;

import oasis.names.tc.saml._2_0.protocol.ArtifactResolveType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AddArtifactToArtifactResolveAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(AddArtifactToArtifactResolveAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        ArtifactResolveType response = (ArtifactResolveType) out.getContent();

        // TODO : Encode artifact here in the future
        String samlArtifact = (String) in.getContent();

        if (logger.isTraceEnabled())
            logger.trace("Adding SAML Artifact to ArtifactResolve " + samlArtifact);
        
        response.setArtifact(samlArtifact);

    }
}