package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.AddRequestType;
import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import oasis.names.tc.spml._2._0.batch.BatchRequestType;
import org.apache.commons.io.IOUtils;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.command.util.ExcelUserParser;
import org.atricore.idbus.capabilities.spmlr2.command.util.UserParser;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import java.io.*;
import java.util.Set;

@Command(scope = "spml", name = "usrimport", description = "SPML ADD operation")
public class UserImportCommand extends SpmlCommandSupport {


    @Option(name = "-i", aliases = "--input", description = "Input File ", required = false, multiValued = false)
    String input;

    @Option(name = "-e", aliases = "--extended-attributes", description = "Import unknown properties as extended attributes ", required = false, multiValued = false)
    boolean importUnknownColumnsAsAttributes = false;

    @Option(name = "-s", aliases = "--schema", description = "Print schema ", required = false, multiValued = false)
    boolean printSchema;

    @Option(name = "--schema-out",  description = "Save schema to output ", required = false, multiValued = false)
    String schemaOut;



    // TODO : Format CSV, EXCEL, etc

    // TODO : ON error ?


    @Override
    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        if (verbose)
            getCmdPrinter().printMsg("User parser: " + buildUserParser().getName());

        if (printSchema) {
            String schema = buildUserParser().getSchema();
            getCmdPrinter().printMsg(schema);
            if (schemaOut != null) {
                IOUtils.write(schema, new FileOutputStream(schemaOut));
            }
        }

        if (input != null)
            return super.doExecute(psp, pspChannel);

        return null;
    }

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
            newUsers = buildUserParser().fromStream(fis, importUnknownColumnsAsAttributes);
        } finally {
            fis.close();
        }

        if (newUsers == null)
            throw new RuntimeException("cannot read users from " + input);

        for(UserType newUser : newUsers) {
            AddRequestType req = new AddRequestType();
            req.setRequestID(uuidGenerator.generateId());
            req.setTargetID(targetId);
            req.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

            req.setData(newUser);

            // TODO : Wrap in JAXBElement
            request.getAny().add(req);
        }

        return request;

    }

    protected UserParser buildUserParser() {
        return new ExcelUserParser();

    }
}
