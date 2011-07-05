package org.atricore.idbus.bundles.maven;


import java.util.List;

public interface MavenRuntimeExecutionOutcome
{
    List<Throwable> getExceptions();

    MavenRuntimeExecutionOutcome addException( Throwable e );

    boolean hasExceptions();
}