package org.atricore.idbus.bundles.maven;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MavenRuntimeExecutionOutcomeImpl
        implements MavenRuntimeExecutionOutcome {

    private List<Throwable> exceptions;


    public List<Throwable> getExceptions() {
        return exceptions == null ? Collections.<Throwable>emptyList() : exceptions;
    }

    public MavenRuntimeExecutionOutcome addException(Throwable t) {
        if (exceptions == null) {
            exceptions = new ArrayList<Throwable>();
        }

        exceptions.add(t);

        return this;
    }

    public boolean hasExceptions() {
        return !getExceptions().isEmpty();
    }

}