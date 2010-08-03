package org.atricore.idbus.bundles.maven;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface MavenRuntime {

    void doExecute() throws Exception;

    void destroy() throws Exception;

}
