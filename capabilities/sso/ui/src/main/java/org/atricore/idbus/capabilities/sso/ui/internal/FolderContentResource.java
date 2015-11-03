package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;

import java.io.File;
import java.util.*;

/**
 *
 */
public class FolderContentResource implements IResource {

    private final File rootFolder;
    public FolderContentResource(File rootFolder) {
        this.rootFolder = rootFolder;
    }


    public Collection<String> scan() {
        if (rootFolder == null || !rootFolder.exists())
            return Collections.EMPTY_LIST;

        int prefix = rootFolder.getAbsolutePath().length();

        Collection<File> resources = FileUtils.listFiles(rootFolder, null, true);
        List<String> resourcePaths = new ArrayList<String>(resources.size());
        for (File resource : resources) {
            if (resource.isDirectory())
                continue;

            resourcePaths.add(resource.getAbsolutePath().substring(prefix + 1));

        }
        return resourcePaths;
    }

    public void respond(Attributes attributes) {

        String url = attributes.getRequest().getUrl().getPath();
        File file = new File(rootFolder, url);
        if (!file.exists())
            return;

        FileResourceStream fileResourceStream = new FileResourceStream(file);
        ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
        resource.respond(attributes);
    }
}