package com.atricore.idbus.console.activation.main.impl;

import com.atricore.idbus.console.activation.main.spi.Activator;
import com.atricore.idbus.console.activation.main.spi.request.AbstractActivationRequest;
import com.atricore.idbus.console.activation.main.spi.response.AbstractActivationResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.josso.tooling.gshell.core.support.MessagePrinter;
import org.josso.tooling.gshell.install.JOSSOArtifact;
import org.josso.tooling.gshell.install.JOSSOScope;
import org.josso.tooling.gshell.install.installer.InstallException;
import org.josso.tooling.gshell.install.installer.Installer;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class ActivatorSupport implements Activator {

    private static final Log log = LogFactory.getLog(ActivatorSupport.class);

    private String jossoVersion;

    private Installer installer;

    private List<Installer> installers;

    protected MessagePrinter printer;

    protected AbstractActivationRequest request;

    protected AbstractActivationResponse response;



    protected ActivatorSupport(List<Installer> installers, MessagePrinter printer,
                               AbstractActivationRequest request,
                               AbstractActivationResponse response) {
        this.installers = installers;
        this.printer = printer;
        this.request = request;
        this.response = response;

    }

    public String getJossoVersion() {
        return jossoVersion;
    }

    public void setJossoVersion(String jossoVersion) {
        this.jossoVersion = jossoVersion;
    }

    public List<Installer> getInstallers() {
        return installers;
    }

    public void setInstallers(List<Installer> installers) {
        this.installers = installers;
    }

    public MessagePrinter getPrinter() {
        return printer;
    }

    public void setPrinter(MessagePrinter printer) {
        this.printer = printer;
    }

    public boolean isTargetPlatformIdValid(String targetPlatformId) {
        try {

            for (Installer i : installers) {
                if (i.getPlatformId().equals(targetPlatformId))
                    return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public Installer getInstaller(AbstractActivationRequest request) {

        if (installer == null) {
            installer = createInstaller(request);
        }

        return installer;
    }

    protected Installer createInstaller(AbstractActivationRequest request) {
        try {

            log.debug("Creating installer for " + request.getTargetPlatformId());

            if (!(request.getIdpType().equals("josso") || request.getIdpType().equals("atricore-idbus"))) {
                throw new RuntimeException("idp-type should be 'josso' or 'atricore-idbus'");
            }

            int idpPort = 0;
            try {
                idpPort = Integer.valueOf(request.getIdpPort());
            } catch (NumberFormatException e) {
                throw new RuntimeException("idp-port should be a number");
            }
            if (idpPort < 1) {
                throw new RuntimeException("idp-port should be a positive number");
            }

            for (Installer i : installers) {

                if (i.getPlatformId().equals(request.getTargetPlatformId())) {

                    installer = i.createInstaller();

                    if (request.getJbossInstance() != null) {
                        log.debug("Using 'jbossInstance' " + request.getJbossInstance());
                        installer.setProperty("jbossInstance", request.getJbossInstance());
                    }

                    if (request.getWeblogicDomain() != null) {
                        log.debug("Using 'weblogicDomain' " + request.getWeblogicDomain());
                        installer.setProperty("weblogicDomain", request.getWeblogicDomain());
                    }

                    if (request.getUser() != null) {
                        log.debug("Using 'user' " + request.getUser());
                        installer.setProperty("user", request.getUser());
                    }

                    if (request.getPassword() != null) {
                        log.debug("Using 'password' " + request.getPassword());
                        installer.setProperty("password", request.getPassword());
                    }

                    if (request.getTarget() != null) {
                        String target = normalizePath(request.getTarget());
                        log.debug("Using 'target' " + request.getTarget() + "["+target+"]");
                        installer.setProperty("target", target);
                    }

                    if (request.getTomcatInstallDir() != null) {
                        String tomcatInstallDir = normalizePath(request.getTomcatInstallDir());
                        log.debug("Using 'tomcatInstallDir' " + request.getTomcatInstallDir() + " ["+tomcatInstallDir+"]");
                        installer.setProperty("tomcatInstallDir", tomcatInstallDir);
                    }

                    if (request.getJbossInstallDir() != null) {
                        String jbossInstallDir = normalizePath(request.getJbossInstallDir());
                        log.debug("Using 'jbossInstallDir' " + request.getJbossInstallDir() + " ["+jbossInstallDir+"]");
                        installer.setProperty("jbossInstallDir", jbossInstallDir);
                    }

                    return installer;
                }
            }

            throw new RuntimeException("No installer found for " + request.getTargetPlatformId() + " (see list-platforms command)");

        } catch (InstallException e) {
            throw new RuntimeException("Cannot create installer : " + e.getMessage(), e);
        }

    }

    public void setInstaller(Installer installer) {
        this.installer = installer;
    }


    protected String getHomeDir() {
        return System.getProperty("karaf.base");
    }

    protected JOSSOArtifact createAgentArtifact(String baseUrl, String artifactId, String type) {
        return createArtifact(baseUrl, JOSSOScope.AGENT, artifactId, getJossoVersion(), type);
    }

    protected JOSSOArtifact createSampleArtifact(String baseUrl, String artifactId, String type) {
        return createArtifact(baseUrl, JOSSOScope.SAMPLE, artifactId, getJossoVersion(), type);
    }


    protected JOSSOArtifact createArtifact(String baseUrl, JOSSOScope scope, String finalName) {
        JOSSOArtifact artifact = new JOSSOArtifact(scope, baseUrl + (baseUrl.endsWith("/") ? "" : "/") + finalName);
        if (log.isDebugEnabled())
            log.debug("Created artifact representation : " + artifact.toString());
        return artifact;
    }

    protected JOSSOArtifact createArtifact(String baseUrl, JOSSOScope scope, String artifactId, String version, String type) {
        JOSSOArtifact artifact = new JOSSOArtifact(artifactId, version, type, scope, baseUrl);
        if (log.isDebugEnabled())
            log.debug("Created artifact representation : " + artifact.toString());
        return artifact;
    }

    protected String normalizePath (String path) {
        return path.replace("\\", "/");
    }

}
