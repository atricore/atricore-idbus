package org.atricore.idbus.kernel.main.mediation;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MediationUnitLifecycleEventImpl implements MediationUnitLifecycleEvent {

    private String type;

    private String unitId;

    public MediationUnitLifecycleEventImpl(String type, String unitId) {
        this.type = type;
        this.unitId = unitId;
    }

    public String getType() {
        return type;
    }

    public String getUnitName() {
        return unitId;
    }
}
