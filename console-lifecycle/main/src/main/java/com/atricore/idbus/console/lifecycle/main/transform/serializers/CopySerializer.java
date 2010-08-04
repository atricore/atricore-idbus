package com.atricore.idbus.console.lifecycle.main.transform.serializers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import com.atricore.idbus.console.lifecycle.main.transform.*;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @version $Id$
 */
public class CopySerializer extends VfsIdProjectResourceSerializer {

    private static final Log logger = LogFactory.getLog(CopySerializer.class);

    @Override
    public boolean canHandle(IdProjectResource resource) {
        return resource.getClassifier() != null &&
                resource.getClassifier().equals("copy");
    }

    @Override
    public void serialize(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {
        //FileObject srcFile = (FileObject)resource.getValue();
        InputStream in = (InputStream)resource.getValue();
        
        VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;

        try {
            FileObject resourcesDir = resolveOutputDir(vfsCtx, resource);
            FileObject destFile = resourcesDir.resolveFile(resource.getNameSpace() + resource.getName());

            if (!destFile.exists()) {
                destFile.createFile();
            }
            
            OutputStream out = destFile.getContent().getOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            //FileUtil.copyContent(srcFile, destFile);
        } catch (Exception e) {
            throw new IdResourceSerializationException(e);
        }
    }
}