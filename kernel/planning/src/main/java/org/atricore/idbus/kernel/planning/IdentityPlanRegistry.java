package org.atricore.idbus.kernel.planning;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdentityPlanRegistry {

    IdentityPlan lookup(String name);


}
