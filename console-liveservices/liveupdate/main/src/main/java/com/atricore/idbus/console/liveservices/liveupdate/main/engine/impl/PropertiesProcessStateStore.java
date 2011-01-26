package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.ProcessStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.ProcessStore;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Simple process state store
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class PropertiesProcessStateStore implements ProcessStore {

    private static final Log logger = LogFactory.getLog(PropertiesProcessStateStore.class);

    private String baseFolder;

//    private URI baseUri;

    public PropertiesProcessStateStore() {

    }

    public void init() throws LiveUpdateException {

//        try {

            if (baseFolder == null){
                String separator = System.getProperty("file.separator");
//                separator = (separator.equals("\\") ? "\\\\" : separator);
                baseFolder = System.getProperty("karaf.data",
                        System.getProperty("java.io.tmpdir")) +
//                        "liveservices/liveupdate/processes";
                        separator +
                        "liveservices" + separator + "liveupdate" + separator + "processes";
//                baseFolder = baseFolder.replace("\\", "/");
            }

            if (logger.isDebugEnabled())
                logger.debug("Using baseFolder : " + baseFolder);
//            baseUri = new URI(baseFolder);

            File f = new File(baseFolder);
            if (!f.exists()) {
                if (!f.mkdirs())
                    throw new LiveUpdateException("Cannot create folder " + baseFolder);
            } else if (!f.isDirectory()) {
                throw new LiveUpdateException("Configured folder is not a directory : " + baseFolder);
            }
//        } catch (URISyntaxException e) {
//            throw new LiveUpdateException("Invalid base folder : " + e.getMessage(), e);
//        }
    }



    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    public void save(UpdateProcessState state) throws LiveUpdateException {
        Properties props = new Properties();

        props.setProperty("id", state.getId());
        props.setProperty("plan", state.getPlan());
        props.setProperty("status", state.getStatus().name());
        if (state.getOperation() != null)
            props.setProperty("operation", state.getOperation());

//        URI profileUri = buildProfileFileURI(state.getId());
        String profileUri = buildProfileFilePath(state.getId());
        
        props.setProperty("updateProfile", profileUri.toString());

        OutputStream out = null;
        OutputStream profileOut = null;
        try {

//            URI file = buildFileURI(state.getId());
            String file = buildFilePath(state.getId());
            out = new FileOutputStream(new File(file), false);
            props.store(out, "LiveUpdate process state " + state.getId());

            String updateProfile = XmlUtils1.marshalProfile(state.getUpdateProfile(), "profile", false);

            InputStream profileIn = new ByteArrayInputStream(updateProfile.getBytes());
            File profileFile = new File (new URI(props.getProperty("updateProfile")));
            profileOut = new FileOutputStream(profileFile, false);

            IOUtils.copy(profileIn, profileOut);

        } catch (Exception e) {
            throw new LiveUpdateException("Cannot persist process state for " +
                    state.getId() + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(profileOut);
        }


    }

    public UpdateProcessState load(String id) throws LiveUpdateException {
//        URI file = buildFileURI(id);
        String file = buildFilePath(id);
        InputStream in = null;
        try {
            in = new FileInputStream(new File(file));

            if (logger.isDebugEnabled())
                logger.debug("Loading process from " + file);

            Properties props = new Properties();
            props.load(in);

            return buildStateInstance(props);

        } catch (IOException e) {
            throw new LiveUpdateException("Cannot load " + file + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public Collection<UpdateProcessState> load() throws LiveUpdateException {
//        File baseFolderFile = new File(baseUri);
        File baseFolderFile = new File(baseFolder);

        List<UpdateProcessState> states = new ArrayList<UpdateProcessState>();

        if (baseFolderFile.listFiles() == null)
            return states;

        for (File child : baseFolderFile.listFiles()) {
            if (child.getName().endsWith("-proc.bin")) {
                InputStream in = null;
                try {
                    Properties props = new Properties();
                    in = new FileInputStream(child);
                    props.load(in);

                    states.add(buildStateInstance(props));

                } catch (Exception e) {
                    throw new LiveUpdateException("Cannot load processes. " + e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        }

        return states;
    }

    public void remove(String id) throws LiveUpdateException {
//        URI file = buildFileURI(id);
        String file = buildFilePath(id);

        File f = new File(file);
        if (f.exists() && !f.isDirectory()) {
            if (!f.delete()) {
                throw new LiveUpdateException("Cannot delete process state " + id);
            }
        }

//        URI profileFile = buildProfileFileURI(id);
        String profileFile = buildProfileFilePath(id);
        File pf = new File(profileFile);
        if (pf.exists() && !pf.isDirectory()) {
            if (!pf.delete()) {
                logger.warn("Cannot delete process resource " + profileFile.toString());
            }
        }


    }

    protected UpdateProcessState buildStateInstance(Properties props) throws LiveUpdateException {

        UpdateProcessState state = new UpdateProcessState();
        state.setId(props.getProperty("id"));
        state.setPlan(props.getProperty("plan"));
        state.setStatus(ProcessStatus.valueOf(props.getProperty("status")));
        state.setOperation(props.getProperty("operation"));


        InputStream in = null;
        try {

            URI profileUri = new URI(props.getProperty("updateProfile"));
            in = new FileInputStream(new File(profileUri));
            ProfileType profile = XmlUtils1.unmarshallProfile(in, false);
            state.setUpdateProfile(profile);
        } catch (FileNotFoundException e) {
            throw new LiveUpdateException("Cannot file profile " + props.getProperty("updateProfile") + ". " + e.getMessage(), e);
        } catch (Exception e) {
            throw new LiveUpdateException("Cannot file profile " + props.getProperty("updateProfile") + ". " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return state;

    }

//    protected URI buildFileURI(String id) throws LiveUpdateException {
//        String n = baseFolder + "/" + id + "-proc.bin";
//        try {
//            return new URI(n);
//        } catch (URISyntaxException e) {
//            throw new LiveUpdateException("Invalid file name " + n);
//        }
//    }
//
//    protected URI buildProfileFileURI(String id) throws LiveUpdateException {
//        String n = baseFolder + "/" + id + "-prof.bin";
//        try {
//            return new URI(n);
//        } catch (URISyntaxException e) {
//            throw new LiveUpdateException("Invalid file name " + n);
//        }
//    }

    protected String buildFilePath(String id) throws LiveUpdateException {
        String n = baseFolder + "/" + id + "-proc.bin";
        return n;
    }

    protected String buildProfileFilePath(String id) throws LiveUpdateException {
        String n = baseFolder + "/" + id + "-prof.bin";
        return n;
    }    

}
