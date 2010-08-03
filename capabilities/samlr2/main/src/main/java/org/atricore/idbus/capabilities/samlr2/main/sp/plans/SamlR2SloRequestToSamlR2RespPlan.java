package org.atricore.idbus.capabilities.samlr2.main.sp.plans;

import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SloRequestToSamlR2RespPlan extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "spsso-samlr2sloreq-to-samlr2response";
    }
}
