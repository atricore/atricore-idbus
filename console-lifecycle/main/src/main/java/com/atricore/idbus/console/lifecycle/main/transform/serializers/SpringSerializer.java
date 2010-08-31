package com.atricore.idbus.console.lifecycle.main.transform.serializers;

import com.atricore.idbus.console.lifecycle.main.transform.*;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Import;
import com.atricore.idbus.console.lifecycle.support.springmetadata.util.XmlApplicationContextEnhancer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SpringSerializer extends VfsIdProjectResourceSerializer {

    private static final Log logger = LogFactory.getLog(SpringSerializer.class);

    @Override
    public boolean canHandle(IdProjectResource resource) {

        // We can handle spring beans definitions using JAXB
        return resource.getType().equals("spring-beans") &&
                resource.getClassifier().equals("jaxb");
    }

    @Override
    public void resolveLocation(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {
        VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;

        try {
            FileObject rersourcesDir = resolveOutputDir(vfsCtx, resource);
            FileObject outputDir = rersourcesDir.resolveFile("META-INF/spring/" +
                    (resource.getNameSpace() != null ? toFolderName(resource.getNameSpace()) + "/" : ""));

            if (logger.isTraceEnabled())
                logger.trace("Using serialization output dir ["+resource+"] : " + outputDir.getURL());


            FileObject outputFile =  outputDir.resolveFile(toFolderName(resource.getName() ) +
                    (resource.getNameSpace() != null ? "-config" : "") + ".xml");

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
    public void serialize(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {

        if (logger.isDebugEnabled())
            logger.debug("Serializing resource " + resource + " using JAX-B Spring mapping");

        OutputStream os = null;

        try {

            VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;

            // User the class classloader (bundle)
            JAXBContext jaxbCtx = JAXBContext.newInstance("com.atricore.idbus.console.lifecycle.support.springmetadata.model:" +
                    "com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi:" +
                    "com.atricore.idbus.console.lifecycle.support.springmetadata.model.tool",
                    getClass().getClassLoader());

            Marshaller m = jaxbCtx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Beans beans = (Beans) resource.getValue();

            // Translate imports paths from 'logical' to deployment :)

            for (Object o : beans.getImportsAndAliasAndBeen()) {

                if (o instanceof Import) {
                    Import beansImport = (Import) o;

                    // Resource ID is ${<ID-VALUE>}
                    String resourceId = beansImport.getResource();

                    if (logger.isTraceEnabled())
                        logger.trace("Resolving import for resource ["+beansImport.getResource()+"]");

                    FileObject resourceFile = vfsCtx.getLayout().getResourceFile(resourceId);
                    if (resourceFile == null)
                        throw new IdResourceSerializationException("Unresolved resource file for id " + resourceId); 

                    String path = resolveRelativePath(vfsCtx.getLayout().getResourcesDir().resolveFile("META-INF/spring"), resourceFile);

                    if (logger.isDebugEnabled())
                        logger.debug("Resolved import for resource ["+beansImport.getResource()+"] " + path);

                    beansImport.setResource(path);

                }
            }

            FileObject outputFile = vfsCtx.getLayout().getResourceFile(resource.getId());

            if (!outputFile.exists())
                outputFile.createFile();

            if (logger.isDebugEnabled())
                logger.debug("Serializing resource " + resource + " location : " + outputFile.getURL());

            os = outputFile.getContent().getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            XmlApplicationContextEnhancer x = new XmlApplicationContextEnhancer(writer);
            m.marshal(beans, x);
            x.flush();
            writer.flush();
            writer.close();

        } catch (Exception e) {
            throw new IdResourceSerializationException(e);
        } finally {
            if (os != null) try { os.close(); } catch (IOException e) { /**/}
        }
    }
}
