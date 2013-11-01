package org.atricore.idbus.idojos.virtualidentitystore;

import java.util.Collection;

/**
 * BaseUserExistsMappingRule.
 */
public class BaseUserExistsMappingRule {

    public Collection<UserExistsOutcome> select(Collection<UserExistsOutcome> sourceOutcomes) {
        return null;
    }

    public UserExistsOutcome join(Collection<UserExistsOutcome> selectedOutcomes) {
        return null;
    }

    public UserExistsOutcome transform(UserExistsOutcome jointOutcome) {
        return null;
    }

    public void validate(UserExistsOutcome transformedOutcome) {

    }

}
