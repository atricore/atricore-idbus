package org.atricore.idbus.bundles.maven;

import org.apache.maven.execution.MavenExecutionResult;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface MavenRuntime {

    MavenRuntimeExecutionOutcome doExecute() throws Exception;

    void destroy() throws Exception;

}
