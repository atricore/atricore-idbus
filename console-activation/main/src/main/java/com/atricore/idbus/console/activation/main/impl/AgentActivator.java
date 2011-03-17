package com.atricore.idbus.console.activation.main.impl;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.*;
import org.josso.tooling.gshell.core.support.MessagePrinter;
import org.josso.tooling.gshell.install.JOSSOScope;
import org.josso.tooling.gshell.install.installer.Installer;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AgentActivator extends ActivatorSupport {
    
    private static final Log log = LogFactory.getLog(AgentActivator.class);

    protected FileObject homeDir;

    protected FileObject tmpDir;
    protected FileObject appliancesDir;

    protected FileObject jossoDistDir;
    protected FileObject libsDir;
    protected FileObject srcsDir;
    protected FileObject trdpartyDir;
    protected FileObject confDir;
    protected FileObject iis32Dir;
    protected FileObject iis64Dir;

    public AgentActivator(List<Installer> installers,
                          MessagePrinter printer,
                          ActivateAgentRequest request,
                          ActivateAgentResponse response) {
        super(installers, printer, request, response);
    }

    protected void init() throws Exception {
        getInstaller(request).init();
    }

    protected void validate() throws Exception {
        if (!isTargetPlatformIdValid(request.getTargetPlatformId()))
            throw new Exception("Invalid id ["+request.getTargetPlatformId()+"] specified!");
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
        libsDir = jossoDistDir.resolveFile("dist/agents/bin");
        srcsDir = jossoDistDir.resolveFile("dist/agents/src");
        trdpartyDir = libsDir.resolveFile("3rdparty");
        confDir = jossoDistDir.resolveFile("dist/agents/config/" + request.getTargetPlatformId());
        iis32Dir = libsDir.resolveFile("Win32");
        iis64Dir = libsDir.resolveFile("Win64");
    }

    protected void verifyTarget() throws Exception {
        if (!request.isForceInstall())
            getInstaller(request).validatePlatform();
    }

    protected void processDir(FileObject dir, boolean recursive) throws Exception {
        FileObject[] children = dir.getChildren();
        for (FileObject subfile : children) {
            if (subfile.getType() == FileType.FOLDER) {
                if (recursive)
                    processDir(subfile, recursive);
            } else {
                getInstaller(request).installComponent(createArtifact(subfile.getParent().getURL().toString(), JOSSOScope.AGENT, subfile.getName().getBaseName()), true);
            }
        }
    }

    protected void installJOSSOAgentJars() throws Exception {
        processDir(libsDir, false);
        processDir(iis32Dir, true);
        processDir(iis64Dir, true);
    }
    
    protected void installJOSSOAgentJarsFromSrc() throws Exception {

        if (!srcsDir.exists())
            return;

        FileObject[] agentBins = srcsDir.getChildren();
        for (int i = 0; i < agentBins.length; i++) {
            FileObject agentBin = agentBins[i];
            getInstaller(request).installComponentFromSrc(createArtifact(srcsDir.getURL().toString(), JOSSOScope.AGENT, agentBin.getName().getBaseName()), true);
        }

    }

    protected void installJOSSOAgentConfig() throws Exception {
        FileObject[] cfgFiles = confDir.getChildren();
        for (int i = 0 ; i < confDir.getChildren().length ; i ++) {
            FileObject cfgFile = cfgFiles[i];
            String fileName = cfgFile.getName().getBaseName();
            getInstaller(request).installConfiguration(createArtifact(confDir.getURL().toString(), JOSSOScope.AGENT, fileName), false);
        }

        getInstaller(request).updateAgentConfiguration(request.getIdpHostName(), request.getIdpPort(), request.getIdpType());
    }

    public void install3rdParty() throws Exception {
        FileObject[] libs = trdpartyDir.getChildren();
        for (int i = 0 ; i < trdpartyDir.getChildren().length ; i ++) {
            FileObject trdPartyFile = libs[i];
            String fileName = trdPartyFile.getName().getBaseName();
            getInstaller(request).install3rdPartyComponent(createArtifact(trdpartyDir.getURL().toString(), JOSSOScope.AGENT, fileName), false);
        }
    }

    protected void configureContainer() throws Exception {
        // TODO : work on this, we could have primitives
        getInstaller(request).configureAgent();
    }

    protected void backupAndRemoveOldArtifacts() throws Exception {
        getInstaller(request).removeOldComponents(true);
    }

    protected void performAdditionalTasks() throws Exception {
        getInstaller(request).performAdditionalTasks(libsDir);
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

            // 1. 3rd party
            printer.printMsg("Installing JOSSO 3rd party JARs");
            install3rdParty();
            printer.printMsg();

            // 2. Install agent jars
            printer.printMsg("Installing JOSSO Agent JARs");
            installJOSSOAgentJars();
            printer.printMsg();

            // 3. Agent configuration files
            printer.printMsg("Installing JOSSO Agent JARs from Source");
            installJOSSOAgentJarsFromSrc();
            printer.printMsg();

            // 4. Container configuration files
            printer.printMsg("Configuring Container");
            configureContainer();
            printer.printMsg();

            // 5. Agent configuration files
            printer.printMsg("Installing JOSSO Agent Configuration files");
            installJOSSOAgentConfig();
            printer.printMsg();

            performAdditionalTasks();

            // 6. Inform outcome
            printer.printMsg(getInstaller(request).getPlatformDescription() + " JOSSO Agent v." + getJossoVersion());
            printer.printOkStatus("Overall Installation", "Successful.");
            printer.printMsg();

            printer.printMsg("@|bold Congratulations!| You've successfully installed the agent.");
            printer.printMsg("Now Follow the @|bold JOSSO Agent Configuration guide| for SSO-enabling applications.");
            printer.printMsg();

            // Clear terminal!
            printer.getOut().write("\u0001[0m");

        } catch (Exception e) {
            // 5. Inform outcome (error)
            printer.printMsg();
            printer.printErrStatus("Overall Installation", e.getMessage());
            printer.printMsg();
            printer.printMsg("See log file for details");


            log.error(e.getMessage(), e);

            // Clear terminal!
        } finally {


        }


    }
}
