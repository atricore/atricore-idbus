package com.atricore.idbus.console.liveservices.liveupdate.tests.services.spi;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface DistributableTestService {

    String getGroupId();

    String getArtifactId();

    String getVersion();
    
}
