package com.atricore.idbus.console.liveservices.liveupdate.tests.services.impl;

import com.atricore.idbus.console.liveservices.liveupdate.tests.services.spi.DistributableTestService;

import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class DistributableTestServiceImpl implements DistributableTestService {

    private static final String CONFIG_PROPERTIES = "/com/atricore/idbus/console/liveservices/liveupdate/tests/distributable.properties";

    private String groupId;

    private String artifactId;

    private String version;

    public void init() throws Exception {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(CONFIG_PROPERTIES));
        groupId = props.getProperty("groupId");
        artifactId = props.getProperty("artifactId");
        version = props.getProperty("version");
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

}
