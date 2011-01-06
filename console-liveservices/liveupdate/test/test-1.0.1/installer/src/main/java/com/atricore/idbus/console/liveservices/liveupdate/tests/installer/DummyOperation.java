package com.atricore.idbus.console.liveservices.liveupdate.tests.installer;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class DummyOperation extends AbstractInstallOperation {

    private static final Log logger = LogFactory.getLog(DummyOperation.class);

    private String groupId;
    private String artifactId;
    private String version;

    private static final String CONFIG_PROPERTIES = "/com/atricore/idbus/console/liveservices/liveupdate/test/installer/installer.properties";

    public DummyOperation() throws Exception {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(CONFIG_PROPERTIES));
        groupId = props.getProperty("groupId");
        artifactId = props.getProperty("artifactId");
        version = props.getProperty("version");
    }

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {

        if (logger.isDebugEnabled())
            logger.debug("DummyOp:execute:" + groupId + "/" + artifactId + "/" + version);
        return OperationStatus.NEXT;
    }

}
