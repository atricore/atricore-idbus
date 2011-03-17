package com.atricore.idbus.console.activation.main.impl;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.request.ConfigureAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.AbstractActivationRequest;
import com.atricore.idbus.console.activation.main.spi.response.AbstractActivationResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileUtil;
import org.apache.commons.vfs.VFS;
import org.josso.tooling.gshell.core.support.MessagePrinter;
import org.josso.tooling.gshell.install.JOSSOScope;
import org.josso.tooling.gshell.install.installer.Installer;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AgentConfigActivator extends ActivatorSupport {

    private static final Log log = LogFactory.getLog(AgentConfigActivator.class);

    protected FileObject homeDir;

    protected FileObject tmpDir;
    protected FileObject appliancesDir;

    protected FileObject jossoDistDir;
    protected FileObject confDir;

    protected AgentConfigActivator(List<Installer> installers,
                                   MessagePrinter printer,
                                   AbstractActivationRequest request,
                                   AbstractActivationResponse response) {
        super(installers, printer, request, response);
    }

    /**
     * Template method
     */
    public void doActivate() throws ActivationException {

        try {

            init();

            validate();

            setup();

            printer.printMsg();
            printer.printMsg("@|bold Installing " + getInstaller(request).getPlatformName() + " " + getInstaller(request).getPlatformVersion() + " JOSSO Agent v." + getJossoVersion() + "|");
            printer.printMsg();

            printer.printMsg("Verifying Target " + getInstaller(request).getPlatformDescription());
            verifyTarget();
            printer.printMsg();

            printer.printMsg("Backing up and removing old JOSSO artifacts");
            backupAndRemoveOldArtifacts();
            printer.printMsg();
            // -----------------------------------------------------------------------

            // 1. Agent configuration files
            printer.printMsg("Installing JOSSO Agent Configuration files");
            installJOSSOAgentConfig();
            printer.printMsg();

            // 2. Inform outcome
            printer.printMsg(getInstaller(request).getPlatformDescription() + " JOSSO Agent v." + getJossoVersion());
            printer.printOkStatus("Overall Installation", "Successful.");
            printer.printMsg();

            printer.printMsg("@|bold Congratulations!| You've successfully installed the agent.");
            printer.printMsg("Now Follow the @|bold JOSSO Agent Configuration guide| for SSO-enabling applications.");
            printer.printMsg();

            // Clear terminal!
            printer.getOut().write("\u0001[0m");

        } catch (Exception e) {
            // 3. Inform outcome (error)
            printer.printMsg();
            printer.printErrStatus("Overall Installation", e.getMessage());
            printer.printMsg();
            printer.printMsg("See log file for details");
            log.error(e.getMessage(), e);
        }


    }

    protected void init() throws Exception {
        getInstaller(request).init();
    }

    protected void validate() throws Exception {
        if (!isTargetPlatformIdValid(request.getTargetPlatformId()))
            throw new Exception("Invalid id [" + request.getTargetPlatformId() + "] specified!");
    }

    protected void setup() throws Exception {
        // -----------------------------------------------------------------------
        // TODO : We could use a remote repository to get our artifacts instead of the vfs or we could use vfs providers.
        // TODO : In OSGI , we can use Classloader URL resolvers
        FileSystemManager fs = VFS.getManager();

        homeDir = fs.resolveFile(getHomeDir());


        tmpDir = homeDir.resolveFile("data/work/tmp");
        if (!tmpDir.exists())
            tmpDir.createFolder();

        jossoDistDir = homeDir.resolveFile("josso");
        appliancesDir = homeDir.resolveFile("appliances");
        confDir = jossoDistDir.resolveFile("dist/agents/config/" + request.getTargetPlatformId());
    }

    protected void verifyTarget() throws Exception {
        if (!request.isForceInstall())
            getInstaller(request).validatePlatform();
    }

    protected void backupAndRemoveOldArtifacts() throws Exception {

    }

    protected void installJOSSOAgentConfig() throws Exception {
        ConfigureAgentRequest ar = (ConfigureAgentRequest) request;

        FileObject[] cfgFiles = confDir.getChildren();
        for (int i = 0; i < confDir.getChildren().length; i++) {
            FileObject cfgFile = cfgFiles[i];
            String fileName = cfgFile.getName().getBaseName();
            getInstaller(request).installConfiguration(createArtifact(confDir.getURL().toString(), JOSSOScope.AGENT, fileName), ar.isReplaceConfig());
        }

        if (ar.getJossoAgentConfigUri() != null) {
            FileObject agentCfg = appliancesDir.resolveFile(ar.getJossoAgentConfigUri());
            if (agentCfg.exists()) {
                // Rename file
                FileObject finalAgentCfg = tmpDir.resolveFile("josso-agent-config.xml");
                FileUtil.copyContent(agentCfg, finalAgentCfg);
                getInstaller(request).installConfiguration(createArtifact(tmpDir.getURL().toString(), JOSSOScope.AGENT, "josso-agent-config.xml"), ar.isReplaceConfig());
                finalAgentCfg.delete();
            }
        }
    }

}
