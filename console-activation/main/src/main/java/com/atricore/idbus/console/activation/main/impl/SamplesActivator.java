package com.atricore.idbus.console.activation.main.impl;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.request.AbstractActivationRequest;
import com.atricore.idbus.console.activation.main.spi.response.AbstractActivationResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.josso.tooling.gshell.core.support.MessagePrinter;
import org.josso.tooling.gshell.install.JOSSOScope;
import org.josso.tooling.gshell.install.installer.Installer;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamplesActivator extends ActivatorSupport {

    private static final Log log = LogFactory.getLog(SamplesActivator.class);

    // -----------------------------------------------------------------------
    protected FileObject homeDir;
    protected FileObject jossoDistDir;
    protected FileObject appDir;


    protected SamplesActivator(List<Installer> installers, MessagePrinter printer, AbstractActivationRequest request, AbstractActivationResponse response) {
        super(installers, printer, request, response);
    }

    protected void init() throws Exception {
        getInstaller(request).init();
    }

    protected void verifyTarget() throws Exception {
        if (!request.isForceInstall())
            getInstaller(request).validatePlatform();
    }


    protected void validate() throws Exception {
        if (!isTargetPlatformIdValid(request.getTargetPlatformId()))
            throw new Exception("Invalid id [" + request.getTargetPlatformId() + "] specified!");
    }

    protected void setup() throws Exception {
        // -----------------------------------------------------------------------
        // TODO : We could use a remote repository to get our artifacts instead of the vfs or we could use vfs providers.
        FileSystemManager fs = VFS.getManager();
        homeDir = fs.resolveFile(getHomeDir());
        jossoDistDir = homeDir.resolveFile("josso"); 
        appDir = jossoDistDir.resolveFile("dist/samples/apps");
    }


    protected void installConfig() throws Exception {

    }

    protected void deployWar() throws Exception {

        for (FileObject child : appDir.getChildren()) {
            getInstaller(request).installApplication(createArtifact(appDir.getURL().toString(), JOSSOScope.AGENT, child.getName().getBaseName()), true);
        }

    }

    public void doActivate() throws ActivationException {

        try {

            init();

            validate();

            setup();

            printer.printMsg();
            printer.printMsg("@|bold Deploying " + getInstaller(request).getPlatformName() + " " + getInstaller(request).getPlatformVersion() + " JOSSO Gateway v." + getJossoVersion() + "|");
            printer.printMsg();

            printer.printMsg("Verifying Target " + getInstaller(request).getPlatformDescription());
            verifyTarget();
            printer.printMsg();

            printer.printMsg("Install JOSSO Samples Configuration");
            installConfig();
            printer.printMsg();

            printer.printMsg("Deploy JOSSO Samples Applications");
            deployWar();
            printer.printMsg();

            // -----------------------------------------------------------------------
            // 6. Inform outcome
            printer.printMsg(getInstaller(request).getPlatformDescription() + " JOSSO Samples v." + getJossoVersion());
            printer.printOkStatus("Overall Installation", "Successful!");
            printer.printMsg();

            printer.printMsg("@|bold Congratulations!| You've successfully installed the samples.");
            printer.printMsg();

            // Clear terminal!
            printer.getOut().write("\u0001[0m");



        } catch (Exception e) {
            // 5. Inform outcome (error)
            printer.printMsg();
            printer.printMsg(getInstaller(request).getPlatformDescription() + " JOSSO Samples v." + getJossoVersion());
            printer.printErrStatus("Overall Installation", e.getMessage());
            printer.printMsg();
            printer.printMsg("See ../log/gshell.log for details");
            log.error(e.getMessage(), e);

        }

    }

}
