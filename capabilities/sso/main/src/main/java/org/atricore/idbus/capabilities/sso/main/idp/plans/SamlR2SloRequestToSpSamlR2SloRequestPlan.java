package org.atricore.idbus.capabilities.sso.main.idp.plans;

import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SubjectNameIDBuilder;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SloRequestToSpSamlR2SloRequestPlan extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "idpsso-samlr2sloreq-to-samlr2sloreq";
    }

    private Set<SubjectNameIDBuilder> nameIDBuilders;

    private SubjectNameIDBuilder defaultNameIDBuilder;

    private boolean ignoreRequestedNameIDPolicy = false;

    public Set<SubjectNameIDBuilder> getNameIDBuilders() {
        return nameIDBuilders;
    }

    public void setNameIDBuilders(Set<SubjectNameIDBuilder> nameIDBuilders) {
        this.nameIDBuilders = nameIDBuilders;
    }

    public SubjectNameIDBuilder getDefaultNameIDBuilder() {
        return defaultNameIDBuilder;
    }

    public void setDefaultNameIDBuilder(SubjectNameIDBuilder defaultNameIDBuilder) {
        this.defaultNameIDBuilder = defaultNameIDBuilder;
    }

    public boolean isIgnoreRequestedNameIDPolicy() {
        return ignoreRequestedNameIDPolicy;
    }

    public void setIgnoreRequestedNameIDPolicy(boolean ignoreRequestedNameIDPolicy) {
        this.ignoreRequestedNameIDPolicy = ignoreRequestedNameIDPolicy;
    }

    @Override
    public IdentityPlanExecutionExchange prepare(IdentityPlanExecutionExchange ex) throws IdentityPlanningException {
        ex.setTransientProperty(SSOPlanningConstants.VAR_IGNORE_REQUESTED_NAMEID_POLICY, new Boolean(this.isIgnoreRequestedNameIDPolicy()));
        ex.setTransientProperty(SSOPlanningConstants.VAR_DEFAULT_NAMEID_BUILDER, getDefaultNameIDBuilder());
        ex.setTransientProperty(SSOPlanningConstants.VAR_NAMEID_BUILDERS, getNameIDBuilders());

        return super.prepare(ex);

    }
}
