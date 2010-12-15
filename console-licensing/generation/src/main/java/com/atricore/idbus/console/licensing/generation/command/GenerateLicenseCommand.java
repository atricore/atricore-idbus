package com.atricore.idbus.console.licensing.generation.command;

import com.atricore.idbus.console.licensing.generation.main.LicenseGenerator;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class GenerateLicenseCommand  extends OsgiCommandSupport {
        // TODO : options should be:
        // * License file
        // * Keystore file
        // * Keystore pass
        // * Private Key name (in keystore)
        // * Private key pass

    private LicenseGenerator generator;

    @Override
    protected Object doExecute() throws Exception {
        return null;
    }

    public LicenseGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(LicenseGenerator generator) {
        this.generator = generator;
    }
}
