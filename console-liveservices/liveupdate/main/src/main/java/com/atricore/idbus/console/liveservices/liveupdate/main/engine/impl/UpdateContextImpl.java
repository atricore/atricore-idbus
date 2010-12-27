package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateContextImpl implements UpdateContext {

    private Collection<InstallableUnitType> ius;

    public UpdateContextImpl(Collection<InstallableUnitType> ius) {
        this.ius = ius;
    }

    public Collection<InstallableUnitType> getIUs() {
        return ius;
    }
}
