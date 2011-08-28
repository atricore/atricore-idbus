package org.atricore.idbus.capabilities.sso.main.binding.plans;

import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2ArtifactToSamlR2ArtifactResolvePlan extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "bind-samlr2art-to-samlr2artresolve";
    }

}

