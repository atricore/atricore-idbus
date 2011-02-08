package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.util.FilePathUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PropertiesEMailNotificationSchemeStore implements NotificationSchemeStore {

    private static final Log logger = LogFactory.getLog(PropertiesEMailNotificationSchemeStore.class);

    private String baseFolder;

    private URI baseUri;
    
    public void init() throws LiveUpdateException {
        try {
            if (baseFolder == null)
                baseFolder = System.getProperty("karaf.data",
                        System.getProperty("java.io.tmpdir")) +
                        "/liveservices/liveupdate/notifications/email";

            if (logger.isDebugEnabled())
                logger.debug("Using baseFolder : " + baseFolder);

            baseUri = new URI(FilePathUtil.fixFilePath(baseFolder));

            File f = new File(baseUri);
            if (!f.exists()) {
                if (!f.mkdirs())
                    throw new LiveUpdateException("Cannot create folder " + baseFolder);
            } else if (!f.isDirectory()) {
                throw new LiveUpdateException("Configured folder is not a directory : " + baseFolder);
            }
        } catch (URISyntaxException e) {
            throw new LiveUpdateException("Invalid base folder : " + e.getMessage(), e);
        }
    }
    
    public Collection<NotificationScheme> loadAll() throws LiveUpdateException {
        File baseFolderFile = new File(baseUri);

        List<NotificationScheme> schemes = new ArrayList<NotificationScheme>();

        if (baseFolderFile.listFiles() == null)
            return schemes;

        for (File child : baseFolderFile.listFiles()) {
            if (child.getName().endsWith(".properties") &&
                    !child.getName().endsWith("-processed.properties")) {
                InputStream in = null;
                try {
                    Properties props = new Properties();
                    in = new FileInputStream(child);
                    props.load(in);

                    schemes.add(unmarshall(props));

                } catch (Exception e) {
                    throw new LiveUpdateException("Cannot load email notification schemes. " + e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        }

        return schemes;
    }

    public NotificationScheme load(String name) throws LiveUpdateException {
        URI file = buildFileURI(name);
        InputStream in = null;
        try {
            in = new FileInputStream(new File(file));

            if (logger.isDebugEnabled())
                logger.debug("Loading email notification scheme from " + file);

            Properties props = new Properties();
            props.load(in);

            return unmarshall(props);

        } catch (FileNotFoundException e) {
            if (logger.isDebugEnabled())
                logger.debug("There is no email notification scheme: " + name);
            return null;
        } catch (Exception e) {
            throw new LiveUpdateException("Cannot load " + file + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void store(NotificationScheme scheme) throws LiveUpdateException {
        Properties props = marshall(scheme);

        OutputStream out = null;
        OutputStream pfOut = null;
        try {
            URI file = buildFileURI(scheme.getName());
            out = new FileOutputStream(new File(file), false);
            props.store(out, "Email notification scheme [" + scheme.getName() + "]");

            URI processedFile = buildProcessedFileURI(scheme.getName());
            File pf = new File(processedFile);
            if (!pf.exists()) {
                Properties processedUpdatesProps = new Properties();
                processedUpdatesProps.setProperty("processed", "");
                pfOut = new FileOutputStream(pf, false);
                processedUpdatesProps.store(pfOut, "Processed updates for scheme [" + scheme.getName() + "]");
            }
        } catch (Exception e) {
            throw new LiveUpdateException("Cannot persist email notification scheme " +
                    scheme.getName() + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(pfOut);
        }
    }

    public void remove(String name) throws LiveUpdateException {
        URI file = buildFileURI(name);
        File f = new File(file);
        if (f.exists() && !f.isDirectory()) {
            if (!f.delete()) {
                throw new LiveUpdateException("Cannot delete email notification scheme " + name);
            }
        }

        URI processedFile = buildProcessedFileURI(name);
        File pf = new File(processedFile);
        if (pf.exists() && !pf.isDirectory()) {
            if (!pf.delete()) {
                logger.warn("Cannot delete processed updates file " + processedFile.toString());
            }
        }
    }

    public String[] getProcessedUpdates(String name) throws LiveUpdateException {
        URI processedFile = buildProcessedFileURI(name);
        InputStream in = null;
        try {
            in = new FileInputStream(new File(processedFile));

            if (logger.isDebugEnabled())
                logger.debug("Loading processed updates from " + processedFile);

            Properties props = new Properties();
            props.load(in);

            String processedUpdates = props.getProperty("processed");
            return StringUtils.split(processedUpdates, ",");
            
        } catch (Exception e) {
            throw new LiveUpdateException("Cannot load " + processedFile + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void addProcessedUpdates(String name, String[] updates) throws LiveUpdateException {
        URI processedFile = buildProcessedFileURI(name);
        InputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(processedFile);
            in = new FileInputStream(file);

            if (logger.isDebugEnabled())
                logger.debug("Loading processed updates from " + processedFile);

            Properties props = new Properties();
            props.load(in);

            String processedUpdates = props.getProperty("processed");
            if (processedUpdates != null && !processedUpdates.equals("")) {
                processedUpdates += ",";
            }
            processedUpdates += StringUtils.join(updates, ",");

            props.setProperty("processed", processedUpdates);

            out = new FileOutputStream(file, false);
            props.store(out, "Processed updates for scheme [" + name + "]");

        } catch (Exception e) {
            throw new LiveUpdateException("Cannot load " + processedFile + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    public Properties marshall(NotificationScheme scheme) {
        // Convert scheme to properties
        EMailNotificationScheme emailScheme = (EMailNotificationScheme) scheme;
        Properties props = new Properties();
        props.setProperty("name", emailScheme.getName());
        props.setProperty("enabled", String.valueOf(emailScheme.isEnabled()));
        props.setProperty("threshold", emailScheme.getThreshold());
        props.setProperty("smtp.host", emailScheme.getSmtpHost());
        props.setProperty("smtp.username", emailScheme.getSmtpUsername());
        props.setProperty("smtp.password", emailScheme.getSmtpPassword());
        props.setProperty("smtp.port", String.valueOf(emailScheme.getSmtpPort()));
        String addresses = StringUtils.join(emailScheme.getAddresses(), ",");
        props.setProperty("addresses", addresses);
        return props;
    }

    public NotificationScheme unmarshall(Properties props) {
        // Convert properties to scheme
        EMailNotificationScheme scheme = new EMailNotificationScheme();
        scheme.setName(props.getProperty("name"));
        scheme.setEnabled(BooleanUtils.toBoolean(props.getProperty("enabled")));
        scheme.setThreshold(props.getProperty("threshold"));
        scheme.setSmtpHost(props.getProperty("smtp.host"));
        scheme.setSmtpUsername(props.getProperty("smtp.username"));
        scheme.setSmtpPassword(props.getProperty("smtp.password"));
        scheme.setSmtpPort(Integer.valueOf(props.getProperty("smtp.port")));
        scheme.setAddresses(StringUtils.split(props.getProperty("addresses"), ","));
        return scheme;
    }

    protected URI buildFileURI(String name) throws LiveUpdateException {
        String n = baseFolder + "/" + name.replace(" ", "_").toLowerCase() + ".properties";
        try {
            return new URI(FilePathUtil.fixFilePath(n));
        } catch (URISyntaxException e) {
            throw new LiveUpdateException("Invalid file name " + n);
        }
    }

    protected URI buildProcessedFileURI(String name) throws LiveUpdateException {
        String n = baseFolder + "/" + name.replace(" ", "_").toLowerCase() + "-processed.properties";
        try {
            return new URI(FilePathUtil.fixFilePath(n));
        } catch (URISyntaxException e) {
            throw new LiveUpdateException("Invalid file name " + n);
        }
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }
}
