package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.Step;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdatePlan;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class InstallEventImpl implements InstallEvent {

    private Step step;

    private UpdateContext ctx;

    public InstallEventImpl(Step step, UpdateContext ctx) {
        this.step = step;
        this.ctx = ctx;
    }

    public Step getStep() {
        return step;
    }

    public UpdateContext getContext() {
        return ctx;
    }
}
