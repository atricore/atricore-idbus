package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ExportIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ExportIdentityApplianceResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.OutputStream;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "export-definition", description = "Export Identity Appliance definition")
public class ExportApplianceDefinitionCommand extends ManagementCommandSupport {

    private static Log logger = LogFactory.getLog(ExportApplianceDefinitionCommand.class);

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Option(name = "-o", aliases = "--output", description = "Identity Appliance descriptor destination file", required = true, multiValued = false)
    private String output;

    @Option(name = "-r", aliases = "--replace", description = "Replace destination file", required = false, multiValued = false)
    private boolean replace;


    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        ExportIdentityApplianceRequest req = new ExportIdentityApplianceRequest ();
        req.setApplianceId(id);
        ExportIdentityApplianceResponse res = svc.exportIdentityAppliance(req);

        FileSystemManager fs = VFS.getManager();
        FileObject outputFile = fs.resolveFile("file://" + output);
        if (!outputFile.exists())
            outputFile.createFile();
        else if (!replace) {
            throw new Exception("Output file already exists, use --replace option. " + outputFile.getURL().toExternalForm());
        }

        FileContent fc = outputFile.getContent();
        OutputStream os = fc.getOutputStream(false);
        os.write(res.getBytes(), 0, res.getBytes().length);
        os.flush();
        os.close();

        System.out.println("Appliance " + id + " exported to " + outputFile.getURL().toExternalForm());

        return null;

    }
}
