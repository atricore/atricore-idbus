package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import org.apache.felix.gogo.commands.Option;

public abstract class SignValidateCommandSupport extends LiveUpdateAdminCommandSupport {

    @Option(name = "-k", aliases = "--keystore", description = "Keystore file", required = true, multiValued = false)
    protected String keystoreFile;

    @Option(name = "-t", aliases = "--keystore-type", description = "Keystore type, default JKS", required = false, multiValued = false)
    protected String keystoreType = "JKS";

    @Option(name = "-c", aliases = "--cert-alias", description = "Private Key alias", required = true, multiValued = false)
    protected String certificateAlias;

    @Option(name = "-n", aliases = "--private-key-alias", description = "Private Key alias", required = true, multiValued = false)
    protected String privateKeyAlias;

    @Option(name = "-p", aliases = "--keystore-pass", description = "Keystore password", required = true, multiValued = false)
    protected String keystorePass;

    @Option(name = "-kp", aliases = "--private-key-pass", description = "Private Key password", required = false, multiValued = false)
    protected String privateKeyPass;
}
