package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.osgi.context.BundleContextAware;

/**
 * This operation will start the all OSGi bundles used as installers
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class StartInstallersOperation extends AbstractInstallOperation implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(StartInstallersOperation.class);

    private int installerRunLevel;

    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public int getInstallerRunLevel() {
        return installerRunLevel;
    }

    public void setInstallerRunLevel(int installerRunLevel) {
        this.installerRunLevel = installerRunLevel;
    }

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        for (InstallableUnitType iu : event.getContext().getIUs()) {
            for (ArtifactKeyType art : iu.getArtifact()) {

                if (art.getType().equals("bundle"))  {
                    // We assume this is the installer artifact, try to start it!
                    String location = "mvn:" + art.getGroup() + "/" + art.getName() + "/" + art.getVersion();

                    if (logger.isDebugEnabled())
                        logger.debug("Trying to install OSGi bundle "+ location);

                    Bundle installer = install(location);

                    if (logger.isDebugEnabled())
                        logger.debug("Installed OSGi bundle "+ installer.getBundleId());


                    if (logger.isDebugEnabled())
                        logger.debug("Trying to start OSGi bundle "+ installer.getBundleId());

                    start(installer);

                    if (logger.isDebugEnabled())
                        logger.debug("Started OSGi bundle "+ installer.getBundleId());

                }
            }
        }

        return OperationStatus.NEXT;
    }

    protected Bundle install(String location) throws LiveUpdateException {
        try {
            return getBundleContext().installBundle(location, null);
        } catch (Exception e) {
            throw new LiveUpdateException("Can't install bundle " + location + ". " + e.getMessage(), e);
        }
    }

    protected void start(Bundle b) throws LiveUpdateException {
        try {
            b.start(installerRunLevel);
        } catch (BundleException e) {
            throw new LiveUpdateException("Can't start bundle " + b.getBundleId() + ". " + e.getMessage(), e);
        }
    }

}
