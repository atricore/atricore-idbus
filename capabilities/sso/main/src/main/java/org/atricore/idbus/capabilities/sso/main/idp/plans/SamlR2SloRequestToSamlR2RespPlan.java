package org.atricore.idbus.capabilities.sso.main.idp.plans;

import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SloRequestToSamlR2RespPlan extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "idpsso-samlr2sloreq-to-samlr2response";
    }

    @Override
    public IdentityPlanExecutionExchange prepare(IdentityPlanExecutionExchange exchange) throws IdentityPlanningException {
        return super.prepare(exchange);
    }
}
