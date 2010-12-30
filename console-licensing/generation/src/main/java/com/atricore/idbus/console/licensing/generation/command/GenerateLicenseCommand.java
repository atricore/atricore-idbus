package com.atricore.idbus.console.licensing.generation.command;

import com.atricore.idbus.console.licensing.generation.main.LicenseGenerator;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "license", name = "generate", description = "Generate license file")
public class GenerateLicenseCommand  extends OsgiCommandSupport {
        // TODO : options should be:
        // * License file
        // * Keystore file
        // * Keystore pass
        // * Private Key name (in keystore)
        // * Private key pass
    @Option(name = "-l", aliases = "--license", description = "License file", required = true, multiValued = false)
    private String license;

    @Option(name = "-s", aliases = "--keystore", description = "Keystore file", required = true, multiValued = false)
    private String keystore;

    @Option(name = "-p", aliases = "--password", description = "Keystore password", required = true, multiValued = false)
    private String keystorePass;

    @Option(name = "-k", aliases = "--keyname", description = "Private key alias", required = true, multiValued = false)
    private String keyAlias;

    @Option(name = "-s", aliases = "--keypass", description = "Private key password", required = true, multiValued = false)
    private String keyPass;

    @Option(name = "-c", aliases = "--cert", description = "Certificate alias", required = true, multiValued = false)
    private String certAlias;

    private LicenseGenerator generator;

    @Override
    protected Object doExecute() throws Exception {
        generator.generate(license, keystore, keystorePass, keyAlias, keyPass, certAlias);        
        return null;
    }

    public LicenseGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(LicenseGenerator generator) {
        this.generator = generator;
    }
}
