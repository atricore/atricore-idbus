package org.atricore.idbus.capabilities.management.main.transform;

import org.apache.commons.vfs.FileObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProjectModuleLayout {

    FileObject workDir;

    FileObject resourcesDir;

    FileObject sourcesDir;

    Map<String, FileObject> resourceFiles;

    public ProjectModuleLayout(FileObject workDir, FileObject sourcesDir, FileObject resourcesDir) {
        this.workDir = workDir;
        this.resourcesDir = resourcesDir;
        this.sourcesDir = sourcesDir;
        this.resourceFiles = new HashMap<String, FileObject>();
    }

    public FileObject getWorkDir() {
        return workDir;
    }

    public FileObject getResourcesDir() {
        return resourcesDir;
    }

    public FileObject getSourcesDir() {
        return sourcesDir;
    }

    public FileObject getResourceFile(String id) {
        return resourceFiles.get(id);
    }

    public void addResourceFile(String id, FileObject resource) {
        this.resourceFiles.put(id, resource);
    }

}
