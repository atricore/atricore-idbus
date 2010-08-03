package org.atricore.idbus.capabilities.management.main.transform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import java.net.URL;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class VfsIdProjectResourceSerializer implements IdResourceSerializer {

    private static final Log logger = LogFactory.getLog(VfsIdProjectResourceSerializer.class);

    public boolean canHandle(IdProjectResource resource) {
        return false;
    }


    public void resolveLocation(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {
        
    }

    public void serialize(IdResourceSerializerContext ctx, IdProjectResource resource)
            throws IdResourceSerializationException {
        resource.getNameSpace();
    }

    protected FileObject resolveOutputDir(VfsIdResourceSerializerContext ctx, IdProjectResource resource) {

        ProjectModuleLayout layout = ctx.getLayout();

        if (resource.getScope() == null)
            return layout.getResourcesDir();

        switch (resource.getScope()) {
            case PROJECT:
                return layout.getWorkDir();
            case RESOURCE:
                return layout.getResourcesDir();
            case SOURCE:
                return layout.getSourcesDir();
        }

        return layout.getResourcesDir();

    }

    protected String resolveRelativePath(FileObject basePath, FileObject file) throws IdResourceSerializationException {
        try {
            URL baseUrl = basePath.getURL();
            URL fileUrl = file.getURL();

            String baseStr = baseUrl.toExternalForm();
            String fileStr = fileUrl.toExternalForm();

            if (logger.isTraceEnabled())
                logger.trace("Resolving relative path from ["+fileStr+"] in ["+baseStr+"]");

            if (fileStr.length() < baseStr.length())
                throw new IdResourceSerializationException("File " + fileStr + " is not child of " + baseStr);

            if (!fileStr.startsWith(baseStr))
                throw new IdResourceSerializationException("File " + fileStr + " is not child of " + baseStr);

            return fileStr.substring(baseStr.length() + 1);

        } catch (FileSystemException e) {
            throw new IdResourceSerializationException(e);
        }

    }

    protected String resolveOutputFileExtension(IdProjectResource resource) {
        // TODO : Support other extensions
        return ".xml";
    }

    protected String toFolderName(String nameSpace) {
        // TODO
        if (nameSpace == null)
            return "";
        
        return nameSpace;
    }
}
