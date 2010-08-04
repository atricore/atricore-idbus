package com.atricore.idbus.console.lifecycle.main.transform.serializers;

import org.apache.commons.vfs.FileObject;
import com.atricore.idbus.console.lifecycle.main.transform.*;

import java.io.OutputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BinarySerializer extends VfsIdProjectResourceSerializer {

    @Override
    public boolean canHandle(IdProjectResource resource) {
        //return super.canHandle(resource);
        return resource.getType().equals("binary") && resource.getClassifier().equals("byte");
    }

    @Override
    public void serialize(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {
        // TODO : Resolve references to this resource in spring definitions

        byte[] in = (byte[])resource.getValue();

        VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;

        try {
            FileObject resourcesDir = resolveOutputDir(vfsCtx, resource);
            FileObject destFile = resourcesDir.resolveFile(resource.getNameSpace() + resource.getName());

            if (!destFile.exists()) {
                destFile.createFile();
            }

            OutputStream out = destFile.getContent().getOutputStream();
            out.write(in);
            out.close();
        } catch (Exception e) {
            throw new IdResourceSerializationException(e);
        }
    }
}
