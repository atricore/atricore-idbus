package com.atricore.idbus.console.lifecycle.main.transform.serializers;

import com.atricore.idbus.console.lifecycle.main.transform.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class  VelocitySerializer extends VfsIdProjectResourceSerializer {

    private static final Log logger = LogFactory.getLog(VelocitySerializer.class);

    private VelocityEngine velocityEngine;
                               
    private String basePath = "/com/atricore/idbus/console/lifecycle/main/transformers/templates/";

    public VelocitySerializer() {
        try {
            velocityEngine = new VelocityEngine();

            // Setup classpath resource loader  (Actually not used!)
            velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "classpath");
            
            velocityEngine.addProperty(
                    "classpath." + Velocity.RESOURCE_LOADER + ".class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

            velocityEngine.setProperty(
                    "classpath." + Velocity.RESOURCE_LOADER + ".cache", "false");

            velocityEngine.setProperty(
                    "classpath." + Velocity.RESOURCE_LOADER + ".modificationCheckInterval",
                    "2");

            velocityEngine.init();

        } catch (Exception e) {
            logger.error("Cannot initialize serializer, velocity error: " + e.getMessage(), e);
        }
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public boolean canHandle(IdProjectResource resource) {
        return resource.getClassifier() != null &&
                resource.getClassifier().equals("velocity");
    }

    @Override
    public void resolveLocation(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {

        try {
            VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;
            FileObject outputDir = resolveOutputDir(vfsCtx, resource);

            if (outputDir == null)
                outputDir = vfsCtx.getLayout().getWorkDir();

            if (resource.getNameSpace() != null) {
                outputDir = outputDir.resolveFile(toFolderName(resource.getNameSpace()));
            }

            if (logger.isTraceEnabled())
                logger.trace("Using serialization output dir ["+resource+"] : " + outputDir.getURL());

            FileObject outputFile = outputDir.resolveFile(
                    resource.getName() + resolveOutputFileExtension(resource));

            if (!outputFile.exists())
                outputFile.createFile();

            if (logger.isTraceEnabled())
                logger.trace("Using serialization output file ["+resource+"] : " + outputFile.getURL());


            vfsCtx.getLayout().addResourceFile(resource.getId(), outputFile);

        } catch (FileSystemException e) {
            throw new IdResourceSerializationException(e);
        }

    }

    @Override
    public void serialize(IdResourceSerializerContext ctx, IdProjectResource resource)
        throws IdResourceSerializationException {

        OutputStream os = null;
        InputStream is = null;
        String templateName = resource.getType() + "-" + resource.getValue() + ".vm";
        if (logger.isDebugEnabled())
            logger.debug("Serializing resource " + resource + " using velocity template " + templateName);

        // Fully Qualified template name
        String fqtn = basePath + (basePath.endsWith("/") ? "" : "/") + templateName;
        try {

            // Create output file
            VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;
            FileObject outputFile = vfsCtx.getLayout().getResourceFile(resource.getId());

            // Resolve the template

            VelocityContext veCtx = new VelocityContext();
            veCtx.put("project", ctx.getProject());
            veCtx.put("module", ctx.getModule());
            veCtx.put("rootModule", ctx.getProject().getRootModule());
            veCtx.put("applianceDef", ctx.getProject().getDefinition());
            veCtx.put("karafHome", System.getProperty("karaf.home"));
            veCtx.put("hasEmbeddedDependencies", ctx.getModule().getEmbeddedDependencies().size() > 0);
            if (resource.getParams() != null) {
                for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) resource.getParams().entrySet()) {
                    veCtx.put(entry.getKey(), entry.getValue());
                }
            }

            // Load the template file because velocity is not handling OSGi classloader very well.
            is = getClass().getResourceAsStream(fqtn);

            if (is == null) {
                logger.error("Velocity template not found: " + fqtn);
                throw new IdResourceSerializationException("Velocity template not found: " + fqtn);
            }

            Reader reader = new InputStreamReader(is);

            os = outputFile.getContent().getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);

            // Template t = velocityEngine.getTemplate(fqtn);
            // t.merge(veCtx, writer);
            
            velocityEngine.evaluate(veCtx, writer, "TransformBuilder", reader);

            writer.flush();
            writer.close();

            // Prepare velocity context

            // Serialize
        } catch (Exception e) {
            throw new IdResourceSerializationException ("Cannot serialize resource: " + e.getMessage(), e);
        } finally {
            if (os != null) try { os.close(); } catch (IOException e) { /**/}
            if (is != null) try { is.close(); } catch (IOException e) { /**/}
        }
    }


}
