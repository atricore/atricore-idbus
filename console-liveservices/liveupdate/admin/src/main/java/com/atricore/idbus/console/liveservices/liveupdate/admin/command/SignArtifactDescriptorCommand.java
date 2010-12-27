package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
// cmd : liveupdate-admin/sign-artifact-descriptor
public class SignArtifactDescriptorCommand extends LiveUpdateAdminCommandSupport {

    // Option -k --keystore
    private String keystoreFile;

    // Opition -n --key-name
    private String keyName;

    // Option -p --keystore-pass
    private String keyPass;

    // Argument
    private String updatesIndexFile;

    protected Object doExecute(LiveUpdateAdminService svc) {
        // TODO
        return null;
    }


}
