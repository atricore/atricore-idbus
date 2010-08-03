package org.atricore.idbus.kernel.main.mediation;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface MediationEngine {

    Collection<IdentityMediationUnit> getAllUnits();

    IdentityMediationUnit lookupUnit(String name);

}
