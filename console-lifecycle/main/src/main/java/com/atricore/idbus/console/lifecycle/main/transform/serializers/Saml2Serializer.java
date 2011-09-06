package com.atricore.idbus.console.lifecycle.main.transform.serializers;

import com.atricore.idbus.console.lifecycle.main.transform.*;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class Saml2Serializer extends VfsIdProjectResourceSerializer {

    private static final Log logger = LogFactory.getLog(Saml2Serializer.class);

    @Override
    public boolean canHandle(IdProjectResource resource) {
        return resource.getType().equals("saml2") && resource.getClassifier().equals("jaxb");
    }

    @Override
    public void resolveLocation(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException {
        VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;

        try {
            FileObject rersourcesDir = resolveOutputDir(vfsCtx, resource);
            FileObject outputDir = rersourcesDir.resolveFile((resource.getNameSpace() != null ?
                    toFolderName(resource.getNameSpace()) + "/" : ""));

            if (logger.isTraceEnabled())
                logger.trace("Using serialization output dir ["+resource+"] : " + outputDir.getURL());


            FileObject outputFile =  outputDir.resolveFile(toFolderName(resource.getName() ) +
                    (resource.getNameSpace() != null ? "-samlr2-metadata" : "") + ".xml");

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
            logger.debug("Serializing resource " + resource + " using JAX-B SAML mappings");
        
        OutputStream os = null;
        
        try {

            VfsIdResourceSerializerContext vfsCtx = (VfsIdResourceSerializerContext) ctx;

            // Use the class classloader (bundle)
            JAXBContext jaxbCtx = JAXBContext.newInstance(SAMLR2Constants.SAML_METADATA_PKG,
                    getClass().getClassLoader());

            Marshaller m = jaxbCtx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            EntityDescriptorType entityDescriptor = (EntityDescriptorType) resource.getValue();
            JAXBElement jaxbEntityDescriptor = new JAXBElement(
                                    new QName("urn:oasis:names:tc:SAML:2.0:metadata", "EntityDescriptor"),
                                    entityDescriptor.getClass(), entityDescriptor);
            
            FileObject outputFile = vfsCtx.getLayout().getResourceFile(resource.getId());

            if (!outputFile.exists())
                outputFile.createFile();

            if (logger.isDebugEnabled())
                logger.debug("Serializing resource " + resource + " location : " + outputFile.getURL());

            os = outputFile.getContent().getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            m.marshal(jaxbEntityDescriptor, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new IdResourceSerializationException(e);
        } finally {
            if (os != null) try { os.close(); } catch (IOException e) { /**/}
        }
    }
}
