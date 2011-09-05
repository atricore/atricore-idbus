package org.atricore.idbus.capabilities.sso.main.idp.plans;

import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SloRequestToSpSamlR2SloRequestPlan extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "idpsso-samlr2sloreq-to-samlr2sloreq";
    }

}
