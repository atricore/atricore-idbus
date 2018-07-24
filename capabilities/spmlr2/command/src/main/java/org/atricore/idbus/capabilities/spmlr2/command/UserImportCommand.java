package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.AddRequestType;
import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import oasis.names.tc.spml._2._0.batch.BatchRequestType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.poi.util.IOUtils;
import org.atricore.idbus.capabilities.spmlr2.command.util.ExcelUserParser;
import org.atricore.idbus.capabilities.spmlr2.command.util.UserImporter;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

@Command(scope = "spml", name = "usrimport", description = "SPML ADD operation")
public class UserImportCommand extends SpmlCommandSupport {


    @Option(name = "-i", aliases = "--input", description = "Input File ", required = true, multiValued = false)
    String input;

    // TODO : Format CSV, EXCEL, etc

    // TODO : ON error ?

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        BatchRequestType request = new BatchRequestType();

        request.setRequestID(uuidGenerator.generateId());


        File fileIn = new File(input);

        if (!fileIn.exists())
            throw new FileNotFoundException(input);

        InputStream fis = new FileInputStream(fileIn);
        Set<UserType> newUsers = null;
        try {
            newUsers = buildImporter().fromStream(fis);
        } finally {
            fis.close();
        }

        if (newUsers == null)
            throw new RuntimeException("cannot read users from " + input);

        for(UserType newUser : newUsers) {
            AddRequestType req = new AddRequestType();
            req.setRequestID(uuidGenerator.generateId());
            req.setTargetID(targetId);

            req.setData(newUser);

            // TODO : Wrap in JAXBElement
            request.getAny().add(req);
        }

        return request;

    }

    protected UserImporter buildImporter() {
        return new ExcelUserParser();

    }
}
