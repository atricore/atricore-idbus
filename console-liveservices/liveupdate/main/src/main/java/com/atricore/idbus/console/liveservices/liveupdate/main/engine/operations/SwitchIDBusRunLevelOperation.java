package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.util.FilePathUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import java.io.*;
import java.net.URI;
import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SwitchIDBusRunLevelOperation extends AbstractInstallOperation implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(SwitchIDBusRunLevelOperation.class);

    private BundleContext bundleContext;

    private int runLevel;
    private String configProperties;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        // Get package admin service.
        ServiceReference ref = getBundleContext().getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
        if (ref == null) {
            throw new LiveUpdateException("StartLevel service is unavailable.");
        }

        try {
            org.osgi.service.startlevel.StartLevel sl = (org.osgi.service.startlevel.StartLevel) getBundleContext().getService(ref);
            if (sl == null) {
                throw new LiveUpdateException("StartLevel service is unavailable.");
            }


            logger.info("Setting RUN-LEVEL to " + runLevel);
            System.out.println("Setting RUN-LEVEL to " + runLevel);

            // Switch to maintenance run-level
            sl.setStartLevel(runLevel);

            updateConfigRunLevel(runLevel);

            return OperationStatus.NEXT;
        }
        finally {
            getBundleContext().ungetService(ref);
        }


    }

    // --------------------------------------------------< Utilities >

    protected void updateConfigRunLevel(int runLevel) throws LiveUpdateException {


        try {
            Properties props = loadConfig();
            int cfgRunLevel = Integer.parseInt(props.getProperty("org.osgi.framework.startlevel.beginning"));

            if (cfgRunLevel != runLevel) {
                backupProperties(true);
                props.setProperty("org.osgi.framework.startlevel.beginning", runLevel + "");
                storeProperties(props, "Switched to RunLevel : " + runLevel);
            }

        } catch (Exception e) {
            throw new LiveUpdateException(e.getMessage(), e);
        }

    }

    protected Properties loadConfig() throws Exception {
        return loadConfigFromURI(buildConfigPropertiesURI());
    }

    protected Properties loadConfigBackup() throws Exception {
        return loadConfigFromURI(buildConfigPropertiesBkpURI());
    }


    protected Properties loadConfigFromURI(URI cfgUri) throws Exception {
        InputStream in = null;

        try {
            File cfg = new File(cfgUri);
            if (!cfg.exists())
                return null;

            in = new FileInputStream(cfg);

            Properties props = new Properties();
            props.load(in);

            return props;
        } finally {
            IOUtils.closeQuietly(in);
        }

    }

    protected void storeProperties(Properties props, String comments) throws Exception {
        storeProperties(props, buildConfigPropertiesURI(), comments);
    }

    protected void storeProperties(Properties props, URI cfgUri, String comments) throws Exception {

        OutputStream out = null;
        try {
            File cfg = new File(cfgUri);

            if (logger.isDebugEnabled())
                logger.debug("Storing configuration properties : "  + cfg);

            out = new FileOutputStream(cfg);
            props.store(out, comments);

        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    protected void backupProperties(boolean replace) throws Exception {

        File f = new File(buildConfigPropertiesBkpURI());

        if (!replace && f.exists()) {
            if (logger.isDebugEnabled())
                logger.debug("Backup already exists : " + f.getAbsolutePath());

            return;
        }

        if (logger.isDebugEnabled())
            logger.debug("Backing up configuration properties to " + f.getAbsolutePath());

        if (!f.exists()) {
            if (!f.createNewFile())
                logger.error("Config properties backup file creation failure");
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(new File(buildConfigPropertiesURI()));
            out = new FileOutputStream(f, false);
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    protected void restoreProperties() throws Exception {

        File f = new File(buildConfigPropertiesBkpURI());

        if (!f.exists())
            return;

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(f);
            out = new FileOutputStream(new File(buildConfigPropertiesURI()), false);
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    protected URI buildConfigPropertiesURI() throws Exception {
        return new URI(FilePathUtil.fixFilePath(configProperties));
    }

    protected URI buildConfigPropertiesBkpURI() throws Exception {
        return new URI(FilePathUtil.fixFilePath(configProperties + ".bkp"));
    }

    // --------------------------------------------------< Properties >

    public int getRunLevel() {
        return runLevel;
    }

    public void setRunLevel(int runLevel) {
        this.runLevel = runLevel;
    }

    public void setConfigProperties(String configProperties) {
        this.configProperties = configProperties;
    }

    public String getConfigProperties() {
        return configProperties;
    }
}
