package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.Selectors;

import java.util.*;

/**
 * File system backed MD Repository impl.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class VFSMetadataRepositoryImpl extends AbstractVFSRepository<UpdateDescriptorType> implements MetadataRepository {

    private static final Log logger = LogFactory.getLog(VFSMetadataRepositoryImpl.class);

    private Map<String, UpdateDescriptorType> updates = new HashMap<String, UpdateDescriptorType>();

    public VFSMetadataRepositoryImpl() {

    }

    public void init() throws LiveUpdateException {
        super.init();
        // load updates
        List<UpdateDescriptorType> descrs = loadDescriptors();
        for (UpdateDescriptorType descr : descrs) {
            updates.put(descr.getID(), descr);
        }
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() {
        return updates.values();
    }

    public void clear() throws LiveUpdateException {
        try {
            repo.delete(Selectors.EXCLUDE_SELF);
            updates.clear();
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        }
    }

    public void addUpdatesIndex(UpdatesIndexType newUpdates) throws LiveUpdateException {
        try {

            String updateStr = XmlUtils1.marshalUpdatesIndex(newUpdates, false);
            byte[] updateBin  = updateStr.getBytes();

            FileObject updateFile = repo.resolveFile(newUpdates.getID() + ".liveupdate");
            if (!updateFile.exists())
                updateFile.createFile();

            writeContent(updateFile, updateBin, false);

            for (UpdateDescriptorType ud : newUpdates.getUpdateDescriptor()) {
                updates.put(ud.getID(), ud);
            }
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }


    }

    public void removeUpdate(String id) {
        // Delete an update stored in this repo.
        updates.remove(id);
        // TODO: remove update descriptor element from update file?
    }

    public boolean hasUpdate(String id) {
        return this.updates.containsKey(id);
    }

    public UpdatesIndexType getUpdates() {
        // TODO : Implement me
        return null;
    }

    // ------------------------------< Utilities >

    protected List<UpdateDescriptorType> loadDescriptors() throws LiveUpdateException {

        List<UpdateDescriptorType> descrs = new ArrayList<UpdateDescriptorType>();
        try {
            for (FileObject f : repo.getChildren()) {
                byte[] descrBin = readContent(f);
                UpdatesIndexType idx = XmlUtils1.unmarshallUpdatesIndex(new String(descrBin), false);
                descrs.addAll(idx.getUpdateDescriptor());
            }
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }

        return descrs;
    }
}
