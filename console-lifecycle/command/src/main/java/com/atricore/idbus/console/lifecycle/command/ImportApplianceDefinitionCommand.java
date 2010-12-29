package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ImportApplianceDefinitionRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ImportApplianceDefinitionResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "import-definition", description = "Import Identity Appliance definition")
public class ImportApplianceDefinitionCommand extends ManagementCommandSupport {

    private static Log logger = LogFactory.getLog(ImportApplianceDefinitionCommand.class);

    @Option(name = "-i", aliases = "--input", description = "Identity Appliance export file", required = true, multiValued = false)
    private String input;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {


        FileSystemManager fs = VFS.getManager();
        FileObject inputFile = fs.resolveFile("file://" + input);
        if (!inputFile.exists())
            throw new FileNotFoundException(inputFile.getURL().toExternalForm());

        //System.out.println("Importing from " + inputFile.getURL().toExternalForm());

        final int BUFFER_SIZE = 2048;
        int count;
        InputStream is = null;

       try {

            is = inputFile.getContent().getInputStream();
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            byte[] buff =  new byte[BUFFER_SIZE];

            while ((count = is.read(buff, 0, BUFFER_SIZE)) != -1) {
                bOut.write(buff, 0, count);
            }
            bOut.flush();

            ImportApplianceDefinitionRequest req = new ImportApplianceDefinitionRequest ();
            req.setBytes(bOut.toByteArray());
            bOut.close();

            // Invoke service
            ImportApplianceDefinitionResponse res = svc.importApplianceDefinition(req);

            System.out.println("Created Identity Appliance " + res.getAppliance().getId() + " from " + input);

        } catch (ApplianceValidationException e) {
            cmdPrinter.printError(e);

        } finally {

           if (is != null) {
               try { is.close();  } catch (IOException e) {
                   logger.error("Unable to close stream for " + inputFile.getURL() + ". Error:" + e.getMessage());
                   if (logger.isDebugEnabled())
                       logger.debug("Unable to close stream for " + inputFile.getURL() + ". Error:" + e.getMessage(), e);
               }
           }

       }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
