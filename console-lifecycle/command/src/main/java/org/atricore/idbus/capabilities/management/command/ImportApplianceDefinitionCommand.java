package org.atricore.idbus.capabilities.management.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.ImportApplianceDefinitionRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.ImportApplianceDefinitionResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "import-definition", description = "Import Identity Appliance definition")
public class ImportApplianceDefinitionCommand extends ManagementCommandSupport {

    private static Log logger = LogFactory.getLog(ImportApplianceDefinitionCommand.class);

    @Option(name = "-in", aliases = "--input", description = "Identity Appliance descriptor file", required = true, multiValued = false)
    private String input;
    
    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        FileSystemManager fs = VFS.getManager();

        FileObject inputFile = fs.resolveFile("file://" + input);
        if (!inputFile.exists())
            throw new FileNotFoundException(inputFile.getURL().toExternalForm());

        //System.out.println("Importing from " + inputFile.getURL().toExternalForm());

        InputStream is = inputFile.getContent().getInputStream();
        StringBuilder descriptor = new StringBuilder();

        byte[] buff =  new byte[1024];

        int read = is.read(buff, 0, 1024);
        while (read > 0) {
            descriptor.append(new String(buff, 0, read));
            read = is.read(buff);
        }

        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
                logger.info("Unable to close stream for " + inputFile.getURL() + ". Error:" + e.getMessage());
                if (logger.isDebugEnabled())
                    logger.debug("Unable to close stream for " + inputFile.getURL() + ". Error:" + e.getMessage(), e);
            }
        }

        ImportApplianceDefinitionRequest req = new ImportApplianceDefinitionRequest ();
        req.setDescriptor(descriptor.toString());
        ImportApplianceDefinitionResponse res = svc.importApplianceDefinition(req);
        System.out.println("Created Identity Appliance " + res.getAppliance().getId() + " from " + input);

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
