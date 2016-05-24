package org.atricore.idbus.examples.subjectauthnpolicy;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;

public class DayOfWeekAuthnStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:examples:policy:day-of-week";

    public static final String NAME = "dayOfWeek";

    public DayOfWeekAuthnStatement() {
        super(NAMESPACE, NAME);
    }
}
