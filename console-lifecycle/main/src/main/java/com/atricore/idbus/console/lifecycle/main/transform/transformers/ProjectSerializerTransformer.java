package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Now, we pass directly from PIM to Projected artifacts ... 
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProjectSerializerTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(ProjectSerializerTransformer.class);

    private String outputPath;

    private List<IdResourceSerializer> serializers = new ArrayList<IdResourceSerializer>();

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        try {

            IdApplianceProject prj = event.getContext().getProject();

            if (logger.isDebugEnabled())
                logger.debug("Serializing identity appliance project " + prj.getId());

            FileSystemManager fs = VFS.getManager();

            if (outputPath == null) {
                outputPath = System.getProperty("karaf.home", System.getProperty("java.io.tmpdir") + "/atricore-idbus");
            }

            FileObject workDir = fs.resolveFile("file://" + outputPath + "/data/work/maven/projects");
            if (!workDir.exists()) {
                workDir.createFolder();
            }

            FileObject prjDir = workDir.resolveFile(toFolderName(prj.getId()));
            if (prjDir.exists()) {
                if (logger.isTraceEnabled())
                    logger.trace("Removing pre-existing project work dir " + prjDir.getURL());
                prjDir.delete(Selectors.SELECT_ALL);
                prjDir.close();
            }

            prjDir = workDir.resolveFile(toFolderName(prj.getId()));
            prjDir.createFolder();
            if (logger.isDebugEnabled())
                logger.debug("Using root project directory " + prjDir.getURL());

            // -------------------------------------
            // Modules folders
            // -------------------------------------
            IdProjectModule module = prj.getRootModule();
            serializeProjectModule(prj, null, module, prjDir);

            return prj;
        } catch (FileSystemException e) {
            throw new TransformException(e);
        } catch (IdResourceSerializationException e) {
            throw new TransformException(e);
        }
    }

    protected void serializeProjectModule(IdApplianceProject prj, IdProjectModule parent, IdProjectModule module, FileObject workDir)
            throws FileSystemException, IdResourceSerializationException {

        FileObject moduleDir;
        FileObject moduleResourceDir = null;
        FileObject moduleSourceDir = null;

        if (module.getType().equals("Project")) {
            moduleDir = workDir.resolveFile(module.getId());

        } else if (module.getType().equals("Appliance")) {
            moduleDir = workDir.resolveFile(toFolderName(module.getId()));
            moduleSourceDir = moduleDir.resolveFile("src/main/java");
            moduleResourceDir = moduleDir.resolveFile("src/main/resources");

        } else {
            moduleDir = workDir.resolveFile(toFolderName(module.getId()));
            moduleResourceDir = moduleDir.resolveFile("src/main/resources");
            moduleSourceDir = moduleDir.resolveFile("src/main/java");
        }

        if (!moduleDir.exists())
            moduleDir.createFolder();

        if (moduleResourceDir != null)
            moduleResourceDir.createFolder();

        if (moduleSourceDir != null)
            moduleSourceDir.createFolder();

        if (logger.isDebugEnabled())
            logger.debug("Using module dir ["+module.getId()+"] " + moduleDir.getURL());

        if (logger.isDebugEnabled())
            logger.debug("Using sources dir ["+module.getId()+"] " + (moduleSourceDir != null ? moduleSourceDir.getURL() : "NONE"));

        if (logger.isDebugEnabled())
            logger.debug("Using resources dir ["+module.getId()+"] " + (moduleResourceDir != null ? moduleResourceDir.getURL() : "NONE"));

        ProjectModuleLayout layout = new ProjectModuleLayout(moduleDir,
                moduleSourceDir,
                moduleResourceDir);

        module.setLayout(layout);

        serializeSources(prj, module, layout);
        serializeResources(prj, module, layout);

        for (IdProjectModule child : module.getModules()) {
            serializeProjectModule(prj, module, child, moduleDir);
        }

    }

    protected void serializeSources(IdApplianceProject prj, IdProjectModule module, ProjectModuleLayout layout)
            throws IdResourceSerializationException {
        try {
            for (IdProjectSource source : module.getSources()) {

                IdResourceSerializerContext ctx = new VfsIdResourceSerializerContext (prj, module, layout);

                if (logger.isTraceEnabled())
                    logger.trace("Serializing source " + source);

                // Define resource locations
                for (IdResourceSerializer serializer : serializers) {
                    if (serializer.canHandle(source)) {

                        if (logger.isTraceEnabled())
                            logger.trace("Resolve source location ["+serializer.getClass().getSimpleName()+"] " + source);

                        serializer.resolveLocation(ctx, source);
                        break;
                    }
                }
            }

            for (IdProjectSource source : module.getSources()) {

                IdResourceSerializerContext ctx = new VfsIdResourceSerializerContext (prj, module, layout);
                boolean handled = false;

                // Serialize resources
                for (IdResourceSerializer serializer : serializers) {
                    if (serializer.canHandle(source)) {

                        if (logger.isTraceEnabled())
                            logger.trace("Serializing source ["+serializer.getClass().getSimpleName()+"] " + source);

                        serializer.serialize(ctx, source);
                        handled = true;
                        break;
                    }
                }


                if (!handled) {
                    logger.warn("Source was not serialized : " + source);
                }

            }


        } catch (Exception e) {
            throw new IdResourceSerializationException(e);
        }
    }


    protected void serializeResources(IdApplianceProject prj, IdProjectModule module, ProjectModuleLayout layout)
            throws IdResourceSerializationException {

        // ----------------------------------------
        // Create POM resource for module
        // ----------------------------------------

        // ----------------------------------------
        // Serialize resources
        // ----------------------------------------

        try {

            for (IdProjectResource resource : module.getResources()) {

                IdResourceSerializerContext ctx = new VfsIdResourceSerializerContext (prj, module, layout);

                if (logger.isTraceEnabled())
                    logger.trace("Serializing resource " + resource);

                // Define resource locations
                for (IdResourceSerializer serializer : serializers) {
                    if (serializer.canHandle(resource)) {

                        if (logger.isTraceEnabled())
                            logger.trace("Resolve resource location ["+serializer.getClass().getSimpleName()+"] " + resource);

                        serializer.resolveLocation(ctx, resource);
                        break;
                    }
                }
            }

            for (IdProjectResource resource : module.getResources()) {

                IdResourceSerializerContext ctx = new VfsIdResourceSerializerContext (prj, module, layout);
                boolean handled = false;

                // Serialize resources
                for (IdResourceSerializer serializer : serializers) {
                    if (serializer.canHandle(resource)) {

                        if (logger.isTraceEnabled())
                            logger.trace("Serializing resource ["+serializer.getClass().getSimpleName()+"] " + resource);                        

                        serializer.serialize(ctx, resource);
                        handled = true;
                        break;
                    }
                }


                if (!handled) {
                    logger.warn("Resource was not serialized : " + resource);
                }

            }

        } catch (Exception e) {
            throw new IdResourceSerializationException(e);
        }
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public List<IdResourceSerializer> getSerializers() {
        return serializers;
    }

    public void setSerializers(List<IdResourceSerializer> serializers) {
        this.serializers = serializers;
    }

    protected String toFolderName(String nameSpace) {
        if (nameSpace == null)
            return "";
        return nameSpace;
    }
}
